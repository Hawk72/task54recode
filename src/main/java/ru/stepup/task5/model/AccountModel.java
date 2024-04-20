package ru.stepup.task5.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountModel {
    @NotNull(message = "Поле <instanceId> не заполнено")
    private Long instanceId;
    private String registryTypeCode;
    private String accountType;
    private String currencyCode;
    private String branchCode;
    private String priorityCode;
    private String mdmCode;
    private String clientCode;
    private String trainRegion;
    private String counter;
    private String salesCode;

    @Override
    public String toString() {
        return "CorporateSettlementAccountModel{" +
                "instanceId=" + instanceId +
                ", registryTypeCode='" + registryTypeCode + '\'' +
                ", accountType='" + accountType + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", branchCode='" + branchCode + '\'' +
                ", priorityCode='" + priorityCode + '\'' +
                ", mdmCode='" + mdmCode + '\'' +
                ", clientCode='" + clientCode + '\'' +
                ", trainRegion='" + trainRegion + '\'' +
                ", counter='" + counter + '\'' +
                ", salesCode='" + salesCode + '\'' +
                '}';
    }
}
