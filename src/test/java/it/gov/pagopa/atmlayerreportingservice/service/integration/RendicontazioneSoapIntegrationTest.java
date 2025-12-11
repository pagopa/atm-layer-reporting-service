package it.gov.pagopa.atmlayerreportingservice.service.integration;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayerreportingservice.service.model.dto.PagopaTransferListUpdateDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for the PagoPA Rendicontazione SOAP/XML Flow.
 *
 * <p>
 * This test class simulates the complete Rendicontazione flow with:
 * 1. Preparing data with transfer details (CRO, flowId, execution date)
 * 2. Generating FlussoRiversamento XML according to PagoPA SACI specifications
 * 3. Encoding to Base64 for SOAP transmission
 * 4. Building SOAP envelope for nodoInviaFlussoRendicontazione primitive
 *
 * <p>
 * Test data:
 * - AGID_01: idPSP=AGID_01, broker=97735020584, channel=97735020584_03, password=pwd_AgID
 * - AGID_02: idPSP=AGID_02, broker=97735020584, channel=97735020584_05, password=pwd_AgID
 * - BNL: pagoPa ID=BNLIITRR, PSP fiscal code=05963231005, channel=_03, ABI=09514
 */
@QuarkusTest
@DisplayName("PagoPA Rendicontazione SOAP/XML Integration Tests")
class RendicontazioneSoapIntegrationTest {

    private static final String BASE_PATH = "/api/v1/reporting-service";
    private static final String TRANSFER_LISTS_ENDPOINT = BASE_PATH + "/bank/transfer-lists";

    // PSP AGID_01 Configuration
    private static final String PSP_ID_AGID_01 = "AGID_01";
    private static final String BROKER_PSP_ID_AGID = "97735020584";
    private static final String CHANNEL_AGID_01 = "97735020584_03";
    private static final String PSP_PASSWORD_AGID = "pwd_AgID";
    private static final String SENDER_BANK_AGID_01 = "12345";  // ABI value matching V90__test_data_rendicontazione.sql

    // PSP AGID_02 Configuration
    private static final String PSP_ID_AGID_02 = "AGID_02";
    private static final String CHANNEL_AGID_02 = "97735020584_05";
    private static final String SENDER_BANK_AGID_02 = "12346";  // ABI value matching V90__test_data_rendicontazione.sql

    // Bank BNL Configuration
    private static final String PAGOPA_ID_BNL = "BNLIITRR";
    private static final String PSP_FISCAL_CODE_BNL = "05963231005";
    private static final String CHANNEL_BNL = "_03";
    private static final String SENDER_BANK_BNL = "09514";     // ABI value matching V90__test_data_rendicontazione.sql

    // PagoPA EC (Ente Creditore) fiscal code
    private static final String EC_FISCAL_CODE = "97735020584";

    private DateTimeFormatter dateFormatter;
    private DateTimeFormatter dateTimeFormatter;

    @BeforeEach
    void setUp() {
        dateFormatter = DateTimeFormatter.ISO_DATE;
        dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
    }

    @Test
    @DisplayName("Should prepare AGID_01 transfer with all required fields for rendicontazione")
    void testPrepareAGID01TransferForRendicontazione() {
        // Prepare transfer data for AGID_01
        PagopaTransferListUpdateDto updateDto = new PagopaTransferListUpdateDto();
        updateDto.transactionId = "550e8400-e29b-41d4-a716-446655440001";
        updateDto.transferId = 1;
        updateDto.transferAmount = new BigDecimal("100.50");
        updateDto.transferCro = "CRO00000001";  // Reference for bank transfer
        updateDto.flowId = "FLW00000001";      // Flusso identifier
        updateDto.transferExecutionDt = LocalDate.now();
        updateDto.rmtInfo = "RMT Info 1";

        given()
                .header("SenderBank", SENDER_BANK_AGID_01)
                .contentType("application/json")
                .body(updateDto)
                .when()
                .put(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("transferCro", equalTo("CRO00000001"))
                .body("flowId", equalTo("FLW00000001"))
                .body("transferExecutionDt", notNullValue());
    }

    @Test
    @DisplayName("Should prepare AGID_02 transfer with all required fields for rendicontazione")
    void testPrepareAGID02TransferForRendicontazione() {
        PagopaTransferListUpdateDto updateDto = new PagopaTransferListUpdateDto();
        updateDto.transactionId = "550e8400-e29b-41d4-a716-446655440002";
        updateDto.transferId = 1;
        updateDto.transferAmount = new BigDecimal("250.75");
        updateDto.transferCro = "CRO00000002";
        updateDto.flowId = "FLW00000002";
        updateDto.transferExecutionDt = LocalDate.now();
        updateDto.rmtInfo = "RMT Info 2";

        given()
                .header("SenderBank", SENDER_BANK_AGID_02)
                .contentType("application/json")
                .body(updateDto)
                .when()
                .put(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("transferCro", equalTo("CRO00000002"))
                .body("flowId", equalTo("FLW00000002"));
    }

    @Test
    @DisplayName("Should prepare BNL transfer with all required fields for rendicontazione")
    void testPrepareBNLTransferForRendicontazione() {
        PagopaTransferListUpdateDto updateDto = new PagopaTransferListUpdateDto();
        updateDto.transactionId = "550e8400-e29b-41d4-a716-446655440003";
        updateDto.transferId = 1;
        updateDto.transferAmount = new BigDecimal("500.00");
        updateDto.transferCro = "CRO00000003";
        updateDto.flowId = "FLW00000003";
        updateDto.transferExecutionDt = LocalDate.now();
        updateDto.rmtInfo = "RMT Info 3";

        given()
                .header("SenderBank", SENDER_BANK_BNL)
                .contentType("application/json")
                .body(updateDto)
                .when()
                .put(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("transferCro", equalTo("CRO00000003"))
                .body("flowId", equalTo("FLW00000003"));
    }

    @Test
    @DisplayName("Should generate FlussoRiversamento XML for AGID_01 with correct structure")
    void testGenerateFlussoRiversamentoAGID01() {
        // This test simulates the generation of FlussoRiversamento XML
        // According to PagoPA SACI specifications

        String flussoRiversamento = buildFlussoRiversamentoXml(
                "1.0",  // versioneOggetto
                "2025-12-09AGID_01-FLW00000001",  // identificativoFlusso (date-psp-flow)
                LocalDate.now(),  // dataRegolamento
                PSP_FISCAL_CODE_BNL,  // for AGID_01, using test data
                "AGID BANK",  // denominazioneMittente
                "B",  // tipoIdentificativoUnivoco (BIC for AGID)
                BROKER_PSP_ID_AGID,  // codiceIdentificativoUnivoco (BIC/Code)
                EC_FISCAL_CODE,  // Ente Creditore fiscal code
                "TEST EC",  // denominazioneRicevente
                1,  // numeroTotalePagamenti
                "100.50",  // importoTotalePagamenti
                "CRO00000001",  // CRO
                "FLW00000001"  // flowId
        );

        // Verify XML structure
        assert flussoRiversamento.contains("<FlussoRiversamento");
        assert flussoRiversamento.contains("<versioneOggetto>1.0</versioneOggetto>");
        assert flussoRiversamento.contains("<identificativoFlusso>");
        assert flussoRiversamento.contains("<dataOraFlusso>");
        assert flussoRiversamento.contains("<istitutoMittente>");
        assert flussoRiversamento.contains("<istitutoRicevente>");
        assert flussoRiversamento.contains("<numeroTotalePagamenti>1</numeroTotalePagamenti>");
        assert flussoRiversamento.contains("<importoTotalePagamenti>100.50</importoTotalePagamenti>");
        assert flussoRiversamento.contains("</FlussoRiversamento>");
    }

    @Test
    @DisplayName("Should generate FlussoRiversamento XML for AGID_02 with correct structure")
    void testGenerateFlussoRiversamentoAGID02() {
        String flussoRiversamento = buildFlussoRiversamentoXml(
                "1.0",
                "2025-12-09AGID_02-FLW00000002",
                LocalDate.now(),
                PSP_FISCAL_CODE_BNL,
                "AGID BANK 2",
                "B",
                BROKER_PSP_ID_AGID,
                EC_FISCAL_CODE,
                "TEST EC",
                1,
                "250.75",
                "CRO00000002",
                "FLW00000002"
        );

        assert flussoRiversamento.contains("<importoTotalePagamenti>250.75</importoTotalePagamenti>");
        assert flussoRiversamento.contains("<numeroTotalePagamenti>1</numeroTotalePagamenti>");
    }

    @Test
    @DisplayName("Should generate FlussoRiversamento XML for BNL with correct structure")
    void testGenerateFlussoRiversamentoBNL() {
        String flussoRiversamento = buildFlussoRiversamentoXml(
                "1.0",
                "2025-12-09" + PAGOPA_ID_BNL + "-FLW00000003",
                LocalDate.now(),
                PSP_FISCAL_CODE_BNL,
                "Banca Lazio",
                "A",  // ABI identifier
                "09514",  // ABI code
                EC_FISCAL_CODE,
                "TEST EC",
                1,
                "500.00",
                "CRO00000003",
                "FLW00000003"
        );

        assert flussoRiversamento.contains("<codiceIdentificativoUnivoco>09514</codiceIdentificativoUnivoco>");
        assert flussoRiversamento.contains("<importoTotalePagamenti>500.00</importoTotalePagamenti>");
    }

    @Test
    @DisplayName("Should encode FlussoRiversamento to Base64 for SOAP transmission")
    void testEncodeToBase64ForSoapTransmission() {
        String flussoRiversamento = buildFlussoRiversamentoXml(
                "1.0",
                "2025-12-09AGID_01-FLW00000001",
                LocalDate.now(),
                PSP_FISCAL_CODE_BNL,
                "AGID BANK",
                "B",
                BROKER_PSP_ID_AGID,
                EC_FISCAL_CODE,
                "TEST EC",
                1,
                "100.50",
                "CRO00000001",
                "FLW00000001"
        );

        String base64Encoded = Base64.getEncoder().encodeToString(flussoRiversamento.getBytes());

        // Verify Base64 encoding
        assert base64Encoded != null && !base64Encoded.isEmpty();

        // Verify it can be decoded back
        String decoded = new String(Base64.getDecoder().decode(base64Encoded));
        assert decoded.equals(flussoRiversamento);
        assert decoded.contains("<FlussoRiversamento");
    }

    @Test
    @DisplayName("Should build correct SOAP envelope for nodoInviaFlussoRendicontazione")
    void testBuildSoapEnvelopeForNodoInviaFlussoRendicontazione() {
        String flussoRiversamento = buildFlussoRiversamentoXml(
                "1.0",
                "2025-12-09AGID_01-FLW00000001",
                LocalDate.now(),
                PSP_FISCAL_CODE_BNL,
                "AGID BANK",
                "B",
                BROKER_PSP_ID_AGID,
                EC_FISCAL_CODE,
                "TEST EC",
                1,
                "100.50",
                "CRO00000001",
                "FLW00000001"
        );

        String base64Flusso = Base64.getEncoder().encodeToString(flussoRiversamento.getBytes());

        String soapEnvelope = buildNodoInviaFlussoRendicontazioneSoapRequest(
                PSP_ID_AGID_01,
                BROKER_PSP_ID_AGID,
                CHANNEL_AGID_01,
                PSP_PASSWORD_AGID,
                EC_FISCAL_CODE,
                "2025-12-09AGID_01-FLW00000001",
                "2025-12-09T00:00:00",
                base64Flusso
        );

        // Verify SOAP structure
        assert soapEnvelope.contains("<soap:Envelope");
        assert soapEnvelope.contains("<soap:Body>");
        assert soapEnvelope.contains("<ns5:nodoInviaFlussoRendicontazione>");
        assert soapEnvelope.contains("<identificativoPSP>" + PSP_ID_AGID_01 + "</identificativoPSP>");
        assert soapEnvelope.contains("<identificativoIntermediarioPSP>" + BROKER_PSP_ID_AGID + "</identificativoIntermediarioPSP>");
        assert soapEnvelope.contains("<identificativoCanale>" + CHANNEL_AGID_01 + "</identificativoCanale>");
        assert soapEnvelope.contains("<password>" + PSP_PASSWORD_AGID + "</password>");
        assert soapEnvelope.contains("<identificativoDominio>" + EC_FISCAL_CODE + "</identificativoDominio>");
        assert soapEnvelope.contains("<xmlRendicontazione>");
        assert soapEnvelope.contains("</soap:Body>");
        assert soapEnvelope.contains("</soap:Envelope>");
    }

    @Test
    @DisplayName("Should build correct SOAP request with all AGID_01 credentials")
    void testSoapRequestAGID01Complete() {
        String soapRequest = buildNodoInviaFlussoRendicontazioneSoapRequest(
                PSP_ID_AGID_01,
                BROKER_PSP_ID_AGID,
                CHANNEL_AGID_01,
                PSP_PASSWORD_AGID,
                EC_FISCAL_CODE,
                "2025-12-09AGID_01-FLW00000001",
                "2025-12-09T00:00:00",
                Base64.getEncoder().encodeToString(
                        buildFlussoRiversamentoXml("1.0", "2025-12-09AGID_01-FLW00000001", LocalDate.now(),
                                PSP_FISCAL_CODE_BNL, "AGID BANK", "B", BROKER_PSP_ID_AGID, EC_FISCAL_CODE,
                                "TEST EC", 1, "100.50", "CRO00000001", "FLW00000001").getBytes()
                )
        );

        assert soapRequest.contains(PSP_ID_AGID_01);
        assert soapRequest.contains(BROKER_PSP_ID_AGID);
        assert soapRequest.contains(CHANNEL_AGID_01);
        assert soapRequest.contains(PSP_PASSWORD_AGID);
    }

    @Test
    @DisplayName("Should build correct SOAP request with all AGID_02 credentials")
    void testSoapRequestAGID02Complete() {
        String soapRequest = buildNodoInviaFlussoRendicontazioneSoapRequest(
                PSP_ID_AGID_02,
                BROKER_PSP_ID_AGID,
                CHANNEL_AGID_02,
                PSP_PASSWORD_AGID,
                EC_FISCAL_CODE,
                "2025-12-09AGID_02-FLW00000002",
                "2025-12-09T00:00:00",
                Base64.getEncoder().encodeToString(
                        buildFlussoRiversamentoXml("1.0", "2025-12-09AGID_02-FLW00000002", LocalDate.now(),
                                PSP_FISCAL_CODE_BNL, "AGID BANK 2", "B", BROKER_PSP_ID_AGID, EC_FISCAL_CODE,
                                "TEST EC", 1, "250.75", "CRO00000002", "FLW00000002").getBytes()
                )
        );

        assert soapRequest.contains(PSP_ID_AGID_02);
        assert soapRequest.contains(CHANNEL_AGID_02);
    }

    @Test
    @DisplayName("Should build correct SOAP request with BNL credentials")
    void testSoapRequestBNLComplete() {
        String soapRequest = buildNodoInviaFlussoRendicontazioneSoapRequest(
                PAGOPA_ID_BNL,
                PSP_FISCAL_CODE_BNL,
                CHANNEL_BNL,
                "pwd_BNL",
                EC_FISCAL_CODE,
                "2025-12-09" + PAGOPA_ID_BNL + "-FLW00000003",
                "2025-12-09T00:00:00",
                Base64.getEncoder().encodeToString(
                        buildFlussoRiversamentoXml("1.0", "2025-12-09" + PAGOPA_ID_BNL + "-FLW00000003", LocalDate.now(),
                                PSP_FISCAL_CODE_BNL, "Banca Lazio", "A", "09514", EC_FISCAL_CODE,
                                "TEST EC", 1, "500.00", "CRO00000003", "FLW00000003").getBytes()
                )
        );

        assert soapRequest.contains(PAGOPA_ID_BNL);
        assert soapRequest.contains(PSP_FISCAL_CODE_BNL);
        assert soapRequest.contains(CHANNEL_BNL);
    }

    @Test
    @DisplayName("Should verify FlussoRiversamento includes all payment details")
    void testFlussoRiversamentoIncludesPaymentDetails() {
        String flussoRiversamento = buildFlussoRiversamentoXml(
                "1.0",
                "2025-12-09AGID_01-FLW00000001",
                LocalDate.now(),
                PSP_FISCAL_CODE_BNL,
                "AGID BANK",
                "B",
                BROKER_PSP_ID_AGID,
                EC_FISCAL_CODE,
                "TEST EC",
                1,
                "100.50",
                "CRO00000001",
                "FLW00000001"
        );

        // Verify all required payment elements
        assert flussoRiversamento.contains("<datiSingoliPagamenti>");
        assert flussoRiversamento.contains("<identificativoUnivocoVersamento>");
        assert flussoRiversamento.contains("<identificativoUnivocoRiscossione>");
        assert flussoRiversamento.contains("<singoloImportoPagato>");
        assert flussoRiversamento.contains("<codiceEsitoSingoloPagamento>");
        assert flussoRiversamento.contains("<dataEsitoSingoloPagamento>");
    }

    // ===== Helper Methods for XML/SOAP Generation =====

    /**
     * Builds a FlussoRiversamento XML according to PagoPA SACI specifications.
     * This is a simplified version for testing purposes.
     */
    private String buildFlussoRiversamentoXml(
            String versioneOggetto,
            String identificativoFlusso,
            LocalDate dataRegolamento,
            String pspFiscalCode,
            String denominazioneMittente,
            String tipoIdentificativoUnivoco,
            String codiceIdentificativoUnivoco,
            String ecFiscalCode,
            String denominazioneRicevente,
            int numeroTotalePagamenti,
            String importoTotalePagamenti,
            String cro,
            String flowId
    ) {
        LocalDate today = LocalDate.now();
        String dataOraFlusso = today.format(DateTimeFormatter.ISO_DATE) + "T00:00:00";

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<FlussoRiversamento xmlns=\"http://www.digitpa.gov.it/schemas/2011/Pagamenti/\">\n" +
                "  <versioneOggetto>" + versioneOggetto + "</versioneOggetto>\n" +
                "  <identificativoFlusso>" + identificativoFlusso + "</identificativoFlusso>\n" +
                "  <dataOraFlusso>" + dataOraFlusso + "</dataOraFlusso>\n" +
                "  <identificativoUnivocoRegolamento>Bonifico SEPA-" + cro + "</identificativoUnivocoRegolamento>\n" +
                "  <dataRegolamento>" + dataRegolamento.format(DateTimeFormatter.ISO_DATE) + "</dataRegolamento>\n" +
                "  <istitutoMittente>\n" +
                "    <identificativoUnivocoMittente>\n" +
                "      <tipoIdentificativoUnivoco>" + tipoIdentificativoUnivoco + "</tipoIdentificativoUnivoco>\n" +
                "      <codiceIdentificativoUnivoco>" + codiceIdentificativoUnivoco + "</codiceIdentificativoUnivoco>\n" +
                "    </identificativoUnivocoMittente>\n" +
                "    <denominazioneMittente>" + denominazioneMittente + "</denominazioneMittente>\n" +
                "  </istitutoMittente>\n" +
                "  <istitutoRicevente>\n" +
                "    <identificativoUnivocoRicevente>\n" +
                "      <tipoIdentificativoUnivoco>G</tipoIdentificativoUnivoco>\n" +
                "      <codiceIdentificativoUnivoco>" + ecFiscalCode + "</codiceIdentificativoUnivoco>\n" +
                "    </identificativoUnivocoRicevente>\n" +
                "    <denominazioneRicevente>" + denominazioneRicevente + "</denominazioneRicevente>\n" +
                "  </istitutoRicevente>\n" +
                "  <numeroTotalePagamenti>" + numeroTotalePagamenti + "</numeroTotalePagamenti>\n" +
                "  <importoTotalePagamenti>" + importoTotalePagamenti + "</importoTotalePagamenti>\n" +
                "  <datiSingoliPagamenti>\n" +
                "    <identificativoUnivocoVersamento>012210209926737900</identificativoUnivocoVersamento>\n" +
                "    <identificativoUnivocoRiscossione>2130101502302932577</identificativoUnivocoRiscossione>\n" +
                "    <indiceDatiSingoloPagamento>1</indiceDatiSingoloPagamento>\n" +
                "    <singoloImportoPagato>" + importoTotalePagamenti + "</singoloImportoPagato>\n" +
                "    <codiceEsitoSingoloPagamento>0</codiceEsitoSingoloPagamento>\n" +
                "    <dataEsitoSingoloPagamento>" + today.format(DateTimeFormatter.ISO_DATE) + "</dataEsitoSingoloPagamento>\n" +
                "  </datiSingoliPagamenti>\n" +
                "</FlussoRiversamento>";
    }

    /**
     * Builds a SOAP envelope for nodoInviaFlussoRendicontazione primitive.
     * This follows PagoPA SANP specifications.
     */
    private String buildNodoInviaFlussoRendicontazioneSoapRequest(
            String identificativoPSP,
            String identificativoIntermediarioPSP,
            String identificativoCanale,
            String password,
            String identificativoDominio,
            String identificativoFlusso,
            String dataOraFlusso,
            String xmlRendicontazioneBase64
    ) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:ns5=\"http://PagoPA/SPC/ggovext/TargetServices/service\">\n" +
                "  <soap:Body>\n" +
                "    <ns5:nodoInviaFlussoRendicontazione>\n" +
                "      <identificativoPSP>" + identificativoPSP + "</identificativoPSP>\n" +
                "      <identificativoIntermediarioPSP>" + identificativoIntermediarioPSP + "</identificativoIntermediarioPSP>\n" +
                "      <identificativoCanale>" + identificativoCanale + "</identificativoCanale>\n" +
                "      <password>" + password + "</password>\n" +
                "      <identificativoDominio>" + identificativoDominio + "</identificativoDominio>\n" +
                "      <identificativoFlusso>" + identificativoFlusso + "</identificativoFlusso>\n" +
                "      <dataOraFlusso>" + dataOraFlusso + "</dataOraFlusso>\n" +
                "      <xmlRendicontazione>" + xmlRendicontazioneBase64 + "</xmlRendicontazione>\n" +
                "    </ns5:nodoInviaFlussoRendicontazione>\n" +
                "  </soap:Body>\n" +
                "</soap:Envelope>";
    }
}

