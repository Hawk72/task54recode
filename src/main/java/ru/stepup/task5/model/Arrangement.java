package ru.stepup.task5.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.stepup.task5.model.enums.PlusMinusType;
import ru.stepup.task5.model.enums.ProductType;

import java.math.BigDecimal;
import java.util.Date;
@Getter
@Setter
public class Arrangement {
    private String generalAgreementId;
    private String supplementaryAgreementId;
    private ProductType arrangementType;
    private Long shedulerJobId;
    @NotBlank(message = "Поле <number> не заполнено")
    private String number;
    @NotNull(message = "Поле <openingDate> не заполнено")
    private Date openingDate;
    private Date closingDate;
    private Date cancelDate;
    private Long validityDuration;
    private String cancellationReason;
    private String status;
    private Date interestCalculationDate;
    private BigDecimal interestRate;
    private BigDecimal coefficient;
    private PlusMinusType coefficientAction;
    private BigDecimal minimumInterestRate;
    private BigDecimal minimumInterestRateCoefficient;
    private PlusMinusType minimumInterestRateCoefficientAction;
    private BigDecimal maximalnterestRate;
    private BigDecimal maximalnterestRateCoefficient;
    private PlusMinusType maximalnterestRateCoefficientAction;

    @Override
    public String toString() {
        return "Arrangement{" +
                "generalAgreementId='" + generalAgreementId + '\'' +
                ", supplementaryAgreementId='" + supplementaryAgreementId + '\'' +
                ", arrangementType='" + arrangementType + '\'' +
                ", shedulerJobId=" + shedulerJobId +
                ", number='" + number + '\'' +
                ", openingDate=" + openingDate +
                ", closingDate=" + closingDate +
                ", cancelDate=" + cancelDate +
                ", validityDuration=" + validityDuration +
                ", cancellationReason='" + cancellationReason + '\'' +
                ", status='" + status + '\'' +
                ", interestCalculationDate=" + interestCalculationDate +
                ", interestRate=" + interestRate +
                ", coefficient=" + coefficient +
                ", coefficientAction='" + coefficientAction + '\'' +
                ", minimumInterestRate=" + minimumInterestRate +
                ", minimumInterestRateCoefficient='" + minimumInterestRateCoefficient + '\'' +
                ", minimumInterestRateCoefficientAction='" + minimumInterestRateCoefficientAction + '\'' +
                ", maximalnterestRate=" + maximalnterestRate +
                ", maximalnterestRateCoefficient=" + maximalnterestRateCoefficient +
                ", maximalnterestRateCoefficientAction='" + maximalnterestRateCoefficientAction + '\'' +
                '}';
    }
}
