package it.gov.pagopa.atmlayerreportingservice.service.model.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "CBILL_ABI_FEDERAZIONE")
public class CbillAbiFederazione extends PanacheEntityBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ABI", length = 5)
    public String abi;

    @Column(name = "PAGOPA_ID", length = 35, nullable = false)
    public String pagopaId;

    @Column(name = "PSP_FISCAL_CODE", length = 11, nullable = false)
    public String pspFiscalCode;

    @Column(name = "PSP_CHANNEL", length = 5, nullable = false)
    public String pspChannel;

    @Column(name = "PAGOPA_DIRECT")
    public Boolean pagopaDirect;

    public CbillAbiFederazione() {
    }
}
