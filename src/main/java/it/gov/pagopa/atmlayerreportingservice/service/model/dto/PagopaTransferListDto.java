package it.gov.pagopa.atmlayerreportingservice.service.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PagopaTransferListDto {
    @NotNull
    public Long id;

    @NotNull
    public Long transactionId;

    @NotNull
    public Integer transferId;

    @NotNull
    public BigDecimal transferAmount;

    @Size(max = 35)
    public String transferCro;

    @Size(max = 35)
    public String flowId;

    @NotNull
    public Boolean pagopaReported;

    public LocalDate transferExecutionDt;

    @NotNull
    @Size(max = 11)
    public String paFiscalCode;

    @Size(max = 140)
    public String paName;

    @NotNull
    @Size(max = 34)
    public String paIban;

    @NotNull
    @Size(max = 140)
    public String rmtInfo;

    public PagopaTransferListDto() {
    }
}
