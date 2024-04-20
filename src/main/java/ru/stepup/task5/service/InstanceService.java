package ru.stepup.task5.service;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.stepup.task5.entity.*;
import ru.stepup.task5.model.Arrangement;
import ru.stepup.task5.model.InstanceModel;
import ru.stepup.task5.model.enums.StateType;
import ru.stepup.task5.repo.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Getter
public class InstanceService {
    @Getter
    @Setter
    private class ResultField{
        private String instanceId;
        private List<String> registerId = new ArrayList<>();
        private List<String> supplementaryAgreementId = new ArrayList<>();
    }
    @Getter
    private class Result{
        private ResultField data;
    }

    private final TppProductRepo tppProductRepo;
    private final AgreementRepo agreementRepo;
    private final TppRefProductRegisterTypeRepo tppRefProductRegisterTypeRepo;
    private final TppRefProductClassRepo tppRefProductClassRepo;
    private final TppProductRegisterRepo tppProductRegisterRepo;
    private final AccountPoolRepo accountPoolRepo;
    private List<TppRefProductRegisterType> productRegisterTypes;
    private TppProduct product;
    private Account account;
    private Result result;

    public InstanceService(TppProductRepo tppProductRepo, AgreementRepo agreementRepo, TppRefProductRegisterTypeRepo tppRefProductRegisterTypeRepo, TppRefProductClassRepo tppRefProductClassRepo, TppProductRegisterRepo tppProductRegisterRepo, AccountPoolRepo accountPoolRepo) {
        this.tppProductRepo = tppProductRepo;
        this.agreementRepo = agreementRepo;
        this.tppRefProductRegisterTypeRepo = tppRefProductRegisterTypeRepo;
        this.tppRefProductClassRepo = tppRefProductClassRepo;
        this.tppProductRegisterRepo = tppProductRegisterRepo;
        this.accountPoolRepo = accountPoolRepo;
    }

    private void checkDoubleProduct(InstanceModel model) {
        List<TppProduct> tppProductList = tppProductRepo.findByNumber(model.getContractNumber());
        if (!tppProductList.isEmpty()) {
            throw new IllegalArgumentException("Параметр ContractNumber № договора <" + model.getContractNumber() + "> уже существует для ЭП с ИД  <" + tppProductList.get(0).getId() + ">");
        }
    }

    private void checkDoubleAgreement(InstanceModel model) {
        if (!model.getInstanceArrangement().isEmpty()) {
            for (Arrangement a: model.getInstanceArrangement()) {
                List<Agreement> agreementList = agreementRepo.findByNumber(a.getNumber());
                if (!agreementList.isEmpty()) {
                    throw new IllegalArgumentException("Параметр № Дополнительного соглашения (сделки) Number <" + a.getNumber() + "> уже существует для ЭП с ИД  <" + agreementList.get(0).getId() + ">");
                }
            }
        }
    }

    private void findRegisterType(InstanceModel model) {
        TppRefProductClass productClass = tppRefProductClassRepo.findByValue(model.getProductCode());
        productRegisterTypes = tppRefProductRegisterTypeRepo.findByTppRefProductClass(productClass);

        for (Iterator<TppRefProductRegisterType> iterator = productRegisterTypes.iterator(); iterator.hasNext(); ) {
            TppRefProductRegisterType rtype = iterator.next();
            if (rtype.getTppRefAccountType() == null || rtype.getTppRefAccountType().getValue() == null ||!rtype.getTppRefAccountType().getValue().equals("Клиентский")) {
                iterator.remove();
            }
        }

        if (productRegisterTypes.isEmpty()) {
            throw new NotFoundException("Код Продукта <" + model.getProductCode() + "> не найдено в Каталоге продуктов tpp_ref_product_class");
        }
    }

    private void findAccount(InstanceModel model) {
        AccountPool accountPool = accountPoolRepo.findAccountPool(model.getBranchCode(), model.getIsoCurrencyCode(), model.getMdmCode(), model.getUrgencyCode(), model.getRegisterType());
        if (accountPool == null) {
            throw new NotFoundException("Не найден пул счетов по указанным параметрам");
        }
        account = accountPool.getAccounts().get(0);
    }

    private void saveProduct(InstanceModel model) {
        product = new TppProduct();
        product.setProductCodeId(productRegisterTypes.get(0).getTppRefAccountType().getId().longValue());
        product.setNumber(model.getContractNumber());
        product.setPriority(model.getPriority().longValue());
        product.setDateOfConclusion(new Timestamp(model.getContractDate().getTime()));
        product.setInterestRateType(((model.getRateType() == null) ? null : model.getRateType().name()));
        product.setTaxRate(model.getTaxPercentageRate());
        product.setThresholdAmount(model.getThresholdAmount());
        product.setNso(model.getMinimalBalance());
        tppProductRepo.save(product);
        result.data.setInstanceId(String.valueOf(product.getId()));
    }

    private void saveProductRegisters(InstanceModel model) {
        for (TppRefProductRegisterType rtype: productRegisterTypes) {
            TppProductRegister tppProductRegister = new TppProductRegister();
            tppProductRegister.setProductId(product.getId());
            tppProductRegister.setTppRefProductRegisterType(rtype);
            tppProductRegister.setState(StateType.OPEN.name());
            tppProductRegister.setCurrencyCode(model.getIsoCurrencyCode());
            tppProductRegister.setAccount(account.getId());
            tppProductRegister.setAccountNumber(account.getAccountNumber());

            tppProductRegisterRepo.save(tppProductRegister);
            result.data.getRegisterId().add(String.valueOf(tppProductRegister.getId()));
        }
    }

    private void findProduct(InstanceModel model) {
        product = tppProductRepo.findById(model.getInstanceId()).orElse(null);
        if (product == null) {
            throw new NotFoundException("Экземпляр продукта с параметром <" + model.getInstanceId() + "> не найден");
        }
    }

    private void saveAgreements(InstanceModel model) {
        if (!model.getInstanceArrangement().isEmpty()) {
            for (Arrangement a: model.getInstanceArrangement()) {
                Agreement agreement = new Agreement();
                agreement.setGeneralAgreementId(a.getGeneralAgreementId());
                agreement.setSupplementaryAgreementId(a.getSupplementaryAgreementId());
                agreement.setArrangementType(((a.getArrangementType() == null) ? null : a.getArrangementType().name()));
                agreement.setShedulerJobId(a.getShedulerJobId());
                agreement.setNumber(a.getNumber());
                agreement.setOpeningDate(((a.getOpeningDate() == null) ? null : new Timestamp(a.getOpeningDate().getTime())));
                agreement.setClosingDate(((a.getClosingDate() == null) ? null : new Timestamp(a.getClosingDate().getTime())));
                agreement.setCancelDate(((a.getCancelDate() == null) ? null : new Timestamp(a.getCancelDate().getTime())));
                agreement.setValidityDuration(a.getValidityDuration());
                agreement.setCancellationReason(a.getCancellationReason());
                agreement.setStatus(a.getStatus());
                agreement.setInterestCalculationDate(((a.getInterestCalculationDate() == null) ? null : new Timestamp(a.getInterestCalculationDate().getTime())));
                agreement.setInterestRate(a.getInterestRate());
                agreement.setCoefficient(a.getCoefficient());
                agreement.setCoefficientAction(((a.getCoefficientAction() == null) ? null : a.getCoefficientAction().name()));
                agreement.setMinimumInterestRate(a.getMinimumInterestRate());
                agreement.setMinimumInterestRateCoefficient(a.getMinimumInterestRateCoefficient());
                agreement.setMinimumInterestRateCoefficientAction(((a.getMinimumInterestRateCoefficientAction() == null) ? null : a.getMinimumInterestRateCoefficientAction().name()));
                agreement.setMaximalInterestRate(a.getMaximalnterestRate());
                agreement.setMaximalInterestRateCoefficient(a.getMaximalnterestRateCoefficient());
                agreement.setMaximalInterestRateCoefficientAction(((a.getMaximalnterestRateCoefficientAction() == null) ? null : a.getMaximalnterestRateCoefficientAction().name()));

                product.getAgreements().add(agreement);
                agreementRepo.save(agreement);
                result.data.getSupplementaryAgreementId().add(String.valueOf(agreement.getId()));

            }
            tppProductRepo.save(product);
        }
    }

    private void init() {
        productRegisterTypes = null;
        product = null;
    }
    @Transactional
    public void processModel(InstanceModel model) {
        init();

        if (model.getInstanceId() == null) {
            checkDoubleProduct(model);
            checkDoubleAgreement(model);
            findRegisterType(model);
            findAccount(model);

            result = new Result();
            result.data = new ResultField();
            saveProduct(model);
            saveProductRegisters(model);
        } else {
            findProduct(model);
            checkDoubleAgreement(model);
            result = new Result();
            result.data = new ResultField();
            result.data.setInstanceId(String.valueOf(product.getId()));
            saveAgreements(model);
        }
    }

}
