package ru.stepup.task5.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.stepup.task5.model.enums.ProductType;
import ru.stepup.task5.model.enums.RateType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class InstanceModel {
    private Long instanceId;
    @NotNull(message = "Поле <productType> не заполнено")
    private ProductType productType;
    @NotBlank(message = "Поле <productCode> не заполнено")
    private String productCode;
    @NotBlank(message = "Поле <registerType> не заполнено")
    private String registerType;
    @NotBlank(message = "Поле <mdmCode> не заполнено")
    private String mdmCode;
    @NotBlank(message = "Поле <contractNumber> не заполнено")
    private String contractNumber;
    @NotNull(message = "Поле <contractDate> не заполнено")
    private Date contractDate;
    @NotNull(message = "Поле <priority> не заполнено")
    private Integer priority;
    private BigDecimal interestRatePenalty;
    private BigDecimal minimalBalance;
    private BigDecimal thresholdAmount;
    private String accountingDetails;
    private RateType rateType;
    private BigDecimal taxPercentageRate;
    private BigDecimal technicalOverdraftLimitAmount;
    @NotNull(message = "Поле <contractId> не заполнено")
    private Long contractId;
    @NotBlank(message = "Поле <branchCode> не заполнено")
    private String branchCode;
    @NotBlank(message = "Поле <isoCurrencyCode> не заполнено")
    private String isoCurrencyCode;
    @NotBlank(message = "Поле <urgencyCode> не заполнено")
    private String urgencyCode;
    private Integer referenceCode;
    private Data additionalPropertiesVip;
    @Valid
    private List<Arrangement> instanceArrangement = new ArrayList<>();

    @Override
    public String toString() {
        return "InstanceModel{" +
                "instanceId=" + instanceId +
                ", productType=" + productType +
                ", productCode='" + productCode + '\'' +
                ", registerType='" + registerType + '\'' +
                ", mdmCode='" + mdmCode + '\'' +
                ", contractNumber='" + contractNumber + '\'' +
                ", contractDate=" + contractDate +
                ", priority=" + priority +
                ", interestRatePenalty=" + interestRatePenalty +
                ", minimalBalance=" + minimalBalance +
                ", thresholdAmount=" + thresholdAmount +
                ", accountingDetails='" + accountingDetails + '\'' +
                ", rateType=" + rateType +
                ", taxPercentageRate=" + taxPercentageRate +
                ", technicalOverdraftLimitAmount=" + technicalOverdraftLimitAmount +
                ", contractId=" + contractId +
                ", branchCode='" + branchCode + '\'' +
                ", isoCurrencyCode='" + isoCurrencyCode + '\'' +
                ", urgencyCode='" + urgencyCode + '\'' +
                ", referenceCode=" + referenceCode +
                ", additionalPropertiesVip=" + additionalPropertiesVip +
                ", instanceArrangement=" + instanceArrangement +
                '}';
    }
}
