package it.gov.pagopa.atmlayerreportingservice.service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PAGOPA_TRANSFER_LIST")
public class PagopaTransferList extends PanacheEntityBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    public Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "TRANSACTION_ID", nullable = false)
    public PagopaTransactions pagopaTransaction;

    @Column(name = "TRANSFER_ID", nullable = false)
    public Integer transferId;

    @Column(name = "TRANSFER_AMOUNT", nullable = false)
    public BigDecimal transferAmount;

    @Column(name = "TRANSFER_CRO", length = 35)
    public String transferCro;

    @Column(name = "FLOW_ID", length = 35)
    public String flowId;

    @Column(name = "PAGOPA_REPORTED", nullable = false)
    public Boolean pagopaReported;

    @Column(name = "TRANSFER_EXECUTION_DT")
    public LocalDate transferExecutionDt;

    @Column(name = "PA_FISCAL_CODE", length = 11, nullable = false)
    public String paFiscalCode;

    @Column(name = "PA_NAME", length = 140)
    public String paName;

    @Column(name = "PA_IBAN", length = 34, nullable = false)
    public String paIban;

    @Column(name = "RMT_INFO", length = 140, nullable = false)
    public String rmtInfo;

    public PagopaTransferList() {
    }
}
