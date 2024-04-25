package ru.stepup.task5.service;

import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.stepup.task5.entity.Account;
import ru.stepup.task5.entity.AccountPool;
import ru.stepup.task5.entity.TppProductRegister;
import ru.stepup.task5.entity.TppRefProductRegisterType;
import ru.stepup.task5.model.AccountModel;
import ru.stepup.task5.model.enums.StateType;
import ru.stepup.task5.repo.*;

import java.util.List;


@Service
@Getter
public class AccountService {
    @Getter
    @AllArgsConstructor
    private class ResultField{
        private String accountId;
    }
    @Getter
    private class Result{
        ResultField data;
    }

    private Long accountId;
    private TppRefProductRegisterType tppRefProductRegisterType;
    private Account account;
    private final TppProductRegisterRepo tppProductRegisterRepo;
    private final TppRefProductRegisterTypeRepo tppRefProductRegisterTypeRepo;
    private final AccountPoolRepo accountPoolRepo;
    private Result result;

    public AccountService(TppProductRegisterRepo tppProductRegisterRepo, TppRefProductRegisterTypeRepo tppRefProductRegisterTypeRepo, AccountPoolRepo accountPoolRepo) {
        this.tppProductRegisterRepo = tppProductRegisterRepo;
        this.tppRefProductRegisterTypeRepo = tppRefProductRegisterTypeRepo;
        this.accountPoolRepo = accountPoolRepo;
    }

    private void checkDouble(AccountModel model) {
        List<TppProductRegister> registerList = tppProductRegisterRepo.findByProductId(model.getInstanceId());
        if (registerList.stream().anyMatch(s -> s.getTppRefProductRegisterType().getValue().equals(model.getRegistryTypeCode()))) {
            throw new IllegalArgumentException("Параметр registryTypeCode тип регистра <" + model.getRegistryTypeCode()
                    + "> уже существует для ЭП с ИД  <" + model.getInstanceId() + ">");
        }
    }

    private void findRefProductRegisterType(AccountModel model) {
        tppRefProductRegisterType = tppRefProductRegisterTypeRepo.findByValue(model.getRegistryTypeCode());
        if (tppRefProductRegisterType == null) {
            throw new NotFoundException("Код Продукта <" + model.getRegistryTypeCode() + "> не найдено в Каталоге продуктов <" + tppRefProductRegisterTypeRepo.getSchema()
                    + "." + tppRefProductRegisterTypeRepo.getTableName() + "> для данного типа Регистра");
        }
    }

    private void findAccount(AccountModel model) {
        AccountPool accountPool = accountPoolRepo.findAccountPool(model.getBranchCode(), model.getCurrencyCode(), model.getMdmCode(), model.getPriorityCode(), model.getRegistryTypeCode());
        if (accountPool == null) {
            throw new NotFoundException("Не найден пул счетов по указанным параметрам");
        }
        account = accountPool.getAccounts().get(0);
    }

    private Long saveProductRegister(AccountModel model) {
        TppProductRegister tppProductRegister = new TppProductRegister();
        tppProductRegister.setProductId(model.getInstanceId());
        tppProductRegister.setTppRefProductRegisterType(tppRefProductRegisterType);
        tppProductRegister.setAccount(account.getId());
        tppProductRegister.setCurrencyCode(model.getCurrencyCode());
        tppProductRegister.setState(StateType.OPEN.name());
        tppProductRegister.setAccountNumber(account.getAccountNumber());
        tppProductRegisterRepo.save(tppProductRegister);
        return tppProductRegister.getId();
    }

    private void init() {
        accountId = null;
        tppRefProductRegisterType = null;
        account = null;
    }

    public void processModel(AccountModel model) {
//        System.out.println("*** AccountModel processModel");
        init();
        checkDouble(model);
        findRefProductRegisterType(model);
        findAccount(model);
        result = new Result();
        result.data = new ResultField(String.valueOf(saveProductRegister(model)));
    }
}
