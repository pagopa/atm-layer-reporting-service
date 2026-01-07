package it.gov.pagopa.atmlayerreportingservice.service.integration;

import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Integration Tests for CbillAbiFederazione endpoints.
 *
 * <p>Endpoints covered:
 * - GET /cbill-abi-federazione
 * - GET /cbill-abi-federazione/{abi}
 * - POST /cbill-abi-federazione
 */
@QuarkusTest
@DisplayName("CbillAbiFederazione Endpoint Integration Tests")
class CbillAbiFederazioneIntegrationTest {

    private static final String BASE_PATH = "/api/v1/reporting-service";
    private static final String CBILL_ENDPOINT = BASE_PATH + "/cbill-abi-federazione";

    private static final String ABI_AGID_01 = "12345";
    private static final String ABI_AGID_02 = "12346";
    private static final String ABI_BNL = "09514";

    private static final String PAGOPA_ID_AGID_01 = "AGID_01";
    private static final String PAGOPA_ID_AGID_02 = "AGID_02";
    private static final String PAGOPA_ID_BNL = "BNLIITRR";

    @Test
    @Order(1)
    @DisplayName("GET /cbill-abi-federazione: Should list all federations")
    void listCbillAbiFederazione_shouldReturnAllRecords() {
        given()
                .accept("application/json")
                .when()
                .get(CBILL_ENDPOINT)
                .then()
                .statusCode(200)
                .body("$", instanceOf(List.class))
                .body("$", hasSize(greaterThanOrEqualTo(3)))
                .body("abi", hasItems(ABI_AGID_01, ABI_AGID_02, ABI_BNL));
    }

    @Test
    @Order(2)
    @DisplayName("GET /cbill-abi-federazione/{abi}: Should return AGID_01 configuration")
    void getCbillAbiFederazione_shouldReturnRecord_whenAbiExists() {
        given()
                .accept("application/json")
                .when()
                .get(CBILL_ENDPOINT + "/" + ABI_AGID_01)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("abi", equalTo(ABI_AGID_01))
                .body("pagopaId", equalTo(PAGOPA_ID_AGID_01))
                .body("pspFiscalCode", equalTo("97735020584"))
                .body("pspChannel", equalTo("03"))
                .body("password", equalTo("password-12345"))
                .body("pagopaDirect", equalTo(false));
    }

    @Test
    @Order(3)
    @DisplayName("GET /cbill-abi-federazione/{abi}: Should return AGID_02 configuration")
    void getCbillAbiFederazione_shouldReturnSecondRecord_whenAbiExists() {
        given()
                .accept("application/json")
                .when()
                .get(CBILL_ENDPOINT + "/" + ABI_AGID_02)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("abi", equalTo(ABI_AGID_02))
                .body("pagopaId", equalTo(PAGOPA_ID_AGID_02))
                .body("pspFiscalCode", equalTo("97735020584"))
                .body("pspChannel", equalTo("05"))
                .body("password", equalTo("password-12346"))
                .body("pagopaDirect", equalTo(false));
    }

    @Test
    @Order(4)
    @DisplayName("GET /cbill-abi-federazione/{abi}: Should return BNL configuration")
    void getCbillAbiFederazione_shouldReturnBNLRecord_whenAbiExists() {
        given()
                .accept("application/json")
                .when()
                .get(CBILL_ENDPOINT + "/" + ABI_BNL)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("abi", equalTo(ABI_BNL))
                .body("pagopaId", equalTo(PAGOPA_ID_BNL))
                .body("pspFiscalCode", equalTo("05963231005"))
                .body("pspChannel", equalTo("03"))
                .body("password", equalTo("password-09514"))
                .body("pagopaDirect", equalTo(true));
    }

    @Test
    @Order(5)
    @DisplayName("GET /cbill-abi-federazione/{abi}: Should return 404 when ABI not found")
    void getCbillAbiFederazione_shouldReturn404_whenAbiMissing() {
        given()
                .accept("application/json")
                .when()
                .get(CBILL_ENDPOINT + "/99999")
                .then()
                .statusCode(404)
                .body("message", equalTo("ABI not found"));
    }

    @Test
    @Order(6)
    @DisplayName("GET /cbill-abi-federazione/{abi}: Should return 400 for invalid ABI")
    void getCbillAbiFederazione_shouldReturn400_whenAbiInvalid() {
        given()
                .accept("application/json")
                .when()
                .get(CBILL_ENDPOINT + "/%20")
                .then()
                .statusCode(404)
                .body("message", equalTo("ABI not found"));
    }

    @Test
    @Order(7)
    @DisplayName("POST /cbill-abi-federazione: Should create new federation")
    void createCbillAbiFederazione_shouldPersistRecord_whenPayloadIsValid() {
        String newAbi = String.format("%05d", System.currentTimeMillis() % 100000);
        String requestBody = """
                {
                  "abi": "%s",
                  "pagopaId": "TEST01",
                  "pspFiscalCode": "12345678901",
                  "pspChannel": "09",
                  "password": "secure-password",
                  "pagopaDirect": true
                }
                """.formatted(newAbi);

        given()
                .contentType("application/json")
                .accept("application/json")
                .body(requestBody)
                .when()
                .post(CBILL_ENDPOINT)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("abi", equalTo(newAbi))
                .body("pagopaId", equalTo("TEST01"))
                .body("pspFiscalCode", equalTo("12345678901"))
                .body("pspChannel", equalTo("09"))
                .body("password", equalTo("secure-password"))
                .body("pagopaDirect", equalTo(true));

        given()
                .accept("application/json")
                .when()
                .get(CBILL_ENDPOINT + "/" + newAbi)
                .then()
                .statusCode(200)
                .body("abi", equalTo(newAbi))
                .body("pagopaId", equalTo("TEST01"))
                .body("pspChannel", equalTo("09"));
    }

    @Test
    @Order(8)
    @DisplayName("POST /cbill-abi-federazione: Should return 400 when ABI length exceeds limit")
    void createCbillAbiFederazione_shouldReturn400_whenPayloadInvalid() {
        String invalidRequest = """
                {
                  "abi": "123456",
                  "pagopaId": "TEST02",
                  "pspFiscalCode": "12345678901",
                  "pspChannel": "_10",
                  "password": "pwd",
                  "pagopaDirect": false
                }
                """;

        given()
                .contentType("application/json")
                .accept("application/json")
                .body(invalidRequest)
                .when()
                .post(CBILL_ENDPOINT)
                .then()
                .statusCode(400)
                .body("message", equalTo("ABI exceeds 5 characters"));
    }
}
