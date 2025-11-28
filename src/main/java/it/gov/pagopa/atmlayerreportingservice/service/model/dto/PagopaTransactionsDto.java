package it.gov.pagopa.atmlayerreportingservice.service.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;

public class PagopaTransactionsDto {
    @NotNull
    public Long id;

    @NotNull
    @Size(max = 36)
    public String transactionId;

    @NotNull
    @Size(max = 1)
    public String status;

    @NotNull
    @Size(max = 36)
    public String billAccountId;

    @NotNull
    public BigDecimal billAmount;

    @NotNull
    @Size(max = 5)
    public String senderBank;

    @NotNull
    public Instant payDate;

    @NotNull
    public Boolean reported;

    @NotNull
    @Size(max = 27)
    public String billerIban;

    @NotNull
    @Size(max = 18)
    public String billId;

    @NotNull
    public BigDecimal billerCommission;

    @NotNull
    public BigDecimal bankCommission;

    @NotNull
    @Size(max = 22)
    public String idempotencyKey;

    @NotNull
    @Size(max = 11)
    public String billerFiscalCode;

    @NotNull
    @Size(max = 36)
    public String noticeNumber;

    @NotNull
    @Size(max = 4)
    public String retCode;

    @NotNull
    @Size(max = 1)
    public String outcomeCode;

    @NotNull
    @Size(max = 210)
    public String payDescription;

    @NotNull
    @Size(max = 140)
    public String billerName;

    @NotNull
    @Size(max = 140)
    public String billerOffice;

    public BigDecimal payOptAmount;

    @Size(max = 3)
    public String payOptType;

    public Instant payOptDuedate;

    @Size(max = 210)
    public String payOptNote;

    @NotNull
    @Size(max = 35)
    public String payToken;

    @NotNull
    public Instant tokenExpDt;

    @NotNull
    @Size(max = 35)
    public String crdReferenceId;

    @NotNull
    @Size(max = 10)
    public String atmCode;

    public PagopaTransactionsDto() {
    }
}
