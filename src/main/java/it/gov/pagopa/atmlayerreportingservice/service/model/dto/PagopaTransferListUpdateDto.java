package it.gov.pagopa.atmlayerreportingservice.service.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagopaTransferListUpdateDto {
    @NotNull
    public String transactionId;

    @NotNull
    public Integer transferId;

    @NotNull
    public BigDecimal transferAmount;

    @Size(max = 35)
    public String transferCro;

    @Size(max = 35)
    public String flowId;

    public LocalDate transferExecutionDt;

    @NotNull
    public String iuv;

    @NotNull
    @Size(max = 140)
    public String rmtInfo;

    public PagopaTransferListUpdateDto() {
    }
}
