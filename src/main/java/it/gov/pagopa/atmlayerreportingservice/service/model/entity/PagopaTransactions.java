package it.gov.pagopa.atmlayerreportingservice.service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "PAGOPA_TRANSACTIONS")
public class PagopaTransactions extends PanacheEntityBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    public Long id;

    @Column(name = "TRANSACTION_ID", length = 36, nullable = false, unique = true)
    public String transactionId;

    @Column(name = "STATUS", length = 1, nullable = false)
    public String status;

    @Column(name = "BILL_ACCOUNT_ID", length = 36, nullable = false)
    public String billAccountId;

    @Column(name = "BILL_AMOUNT", nullable = false)
    public BigDecimal billAmount;

    @Column(name = "SENDER_BANK", length = 5, nullable = false)
    public String senderBank;

    @Column(name = "PAY_DATE", nullable = false)
    public Instant payDate;

    @Column(name = "REPORTED", nullable = false)
    public Boolean reported;

    @Column(name = "BILLER_IBAN", length = 27, nullable = false)
    public String billerIban;

    @Column(name = "BILL_ID", length = 18, nullable = false)
    public String billId;

    @Column(name = "BILLER_COMMISSION", nullable = false)
    public BigDecimal billerCommission;

    @Column(name = "BANK_COMMISSION", nullable = false)
    public BigDecimal bankCommission;

    @Column(name = "IDEMPOTENCY_KEY", length = 22, nullable = false)
    public String idempotencyKey;

    @Column(name = "BILLER_FISCAL_CODE", length = 11, nullable = false)
    public String billerFiscalCode;

    @Column(name = "NOTICE_NUMBER", length = 36, nullable = false)
    public String noticeNumber;

    @Column(name = "RET_CODE", length = 4, nullable = false)
    public String retCode;

    @Column(name = "OUTCOME_CODE", length = 1, nullable = false)
    public String outcomeCode;

    @Column(name = "PAY_DESCRIPTION", length = 210, nullable = false)
    public String payDescription;

    @Column(name = "BILLER_NAME", length = 140, nullable = false)
    public String billerName;

    @Column(name = "BILLER_OFFICE", length = 140, nullable = false)
    public String billerOffice;

    @Column(name = "PAY_OPT_AMOUNT")
    public BigDecimal payOptAmount;

    @Column(name = "PAY_OPT_TYPE", length = 3)
    public String payOptType;

    @Column(name = "PAY_OPT_DUEDATE")
    public Instant payOptDuedate;

    @Column(name = "PAY_OPT_NOTE", length = 210)
    public String payOptNote;

    @Column(name = "PAY_TOKEN", length = 35, nullable = false)
    public String payToken;

    @Column(name = "TOKEN_EXP_DT", nullable = false)
    public Instant tokenExpDt;

    @Column(name = "CRD_REFERENCE_ID", length = 35, nullable = false)
    public String crdReferenceId;

    @Column(name = "ATM_CODE", length = 10, nullable = false)
    public String atmCode;

    @JsonIgnore
    @OneToMany(mappedBy = "pagopaTransaction")
    public List<PagopaTransferList> transferLists;

    public PagopaTransactions() {
    }
}
