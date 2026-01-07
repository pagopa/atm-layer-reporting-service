package it.gov.pagopa.atmlayerreportingservice.service.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CbillAbiFederazioneDto {
    @NotNull
    @Size(max = 5)
    public String abi;

    @NotNull
    @Size(max = 35)
    public String pagopaId;

    @NotNull
    @Size(max = 11)
    public String pspFiscalCode;

    @NotNull
    @Size(max = 5)
    public String pspChannel;

    @NotNull
    @Size(max = 255)
    public String password;

    public Boolean pagopaDirect;

    public CbillAbiFederazioneDto() {
    }
}
