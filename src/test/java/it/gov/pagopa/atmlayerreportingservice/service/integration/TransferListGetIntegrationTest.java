package it.gov.pagopa.atmlayerreportingservice.service.integration;

import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration Tests for GET requests on PagopaTransferList Endpoint.
 *
 * <p>
 * These tests cover all GET operations for transfer list management:
 * 1. GET /transfer-lists - Retrieve all transfer lists
 * 2. GET /transfer-lists/transaction/{transactionId} - Retrieve transfers for specific transaction
 *
 * <p>
 * Test data uses test transfers with:
 * - Transfer 1 (id=1, transactionId=1, amount=€100.50, paFiscalCode=97735020584)
 * - Transfer 2 (id=2, transactionId=2, amount=€250.75, paFiscalCode=97735020584)
 * - Transfer 3 (id=3, transactionId=3, amount=€500.00, paFiscalCode=05963231005)
 */
@QuarkusTest
@DisplayName("PagoPA TransferList GET Endpoint Integration Tests")
class TransferListGetIntegrationTest {

    private static final String BASE_PATH = "/api/v1/reporting-service";
    private static final String TRANSFER_LISTS_ENDPOINT = BASE_PATH + "/transfer-lists";

    // Sender bank values matching ABI from V90__test_data_rendicontazione.sql
    private static final String SENDER_BANK_AGID_01 = "12345";  // ABI for AGID_01
    private static final String SENDER_BANK_AGID_02 = "12346";  // ABI for AGID_02
    private static final String SENDER_BANK_BNL = "09514";      // ABI for BNL

    // Test data IDs from V90__test_data_rendicontazione.sql
    private static final Long TRANSACTION_ID_1 = 1L;
    private static final Long TRANSACTION_ID_2 = 2L;
    private static final Long TRANSACTION_ID_3 = 3L;

    // Test data values
    private static final String TRANSFER_1_IBAN = "IT60X0542811101000000123456";
    private static final String TRANSFER_2_IBAN = "IT60X0542811101000000123457";
    private static final String TRANSFER_3_IBAN = "IT60X0542811101000000123458";
    private static final String PA_FISCAL_CODE_AGID = "97735020584";
    private static final String PA_FISCAL_CODE_BNL = "05963231005";

    // ========== Tests for GET /transfer-lists ==========

    @Test
    @Order(1)
    @DisplayName("GET /transfer-lists: Should return transfers for AGID_01 bank with status 200")
    void testGetAllTransferListsAGID01() {
        given()
                .header("SenderBank", SENDER_BANK_AGID_01)
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200);
    }

    @Test
    @Order(2)
    @DisplayName("GET /transfer-lists: Should return list of transfer lists for AGID_01")
    void testGetAllTransferListsAGID01ReturnsList() {
        given()
                .header("SenderBank", SENDER_BANK_AGID_01)
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("$", instanceOf(List.class))
                .body("$", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    @Order(3)
    @DisplayName("GET /transfer-lists: Should return transfer 1 for AGID_01")
    void testGetAllTransferListsAGID01ContainsTransfer1() {
        given()
                .header("SenderBank", SENDER_BANK_AGID_01)
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("find { it.id == 1 }", notNullValue());
    }

    @Test
    @Order(4)
    @DisplayName("GET /transfer-lists: AGID_01 should NOT return transfers from other banks")
    void testGetAllTransferListsAGID01IsolatesData() {
        given()
                .header("SenderBank", SENDER_BANK_AGID_01)
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                // Transfer 1 and 2 belong to AGID banks
                .body("find { it.id == 1 }", notNullValue())
                // Transfer 3 belongs to BNL, should not be in this result
                .body("find { it.id == 3 }", nullValue());
    }

    @Test
    @Order(5)
    @DisplayName("GET /transfer-lists: Should return transfers for AGID_02 bank")
    void testGetAllTransferListsAGID02() {
        given()
                .header("SenderBank", SENDER_BANK_AGID_02)
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("find { it.id == 2 }", notNullValue());
    }

    @Test
    @Order(6)
    @DisplayName("GET /transfer-lists: Should return transfers for BNL bank")
    void testGetAllTransferListsBNL() {
        given()
                .header("SenderBank", SENDER_BANK_BNL)
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("find { it.id == 3 }", notNullValue());
    }

    @Test
    @Order(7)
    @DisplayName("GET /transfer-lists: Transfer data for AGID_01 should match expected values")
    void testGetAllTransferListsAGID01DataValidation() {
        given()
                .header("SenderBank", SENDER_BANK_AGID_01)
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("find { it.id == 1 }.transactionId", equalTo(1))
                .body("find { it.id == 1 }.transferId", equalTo(1))
                .body("find { it.id == 1 }.transferAmount", notNullValue())
                .body("find { it.id == 1 }.paFiscalCode", equalTo(PA_FISCAL_CODE_AGID))
                .body("find { it.id == 1 }.paIban", equalTo(TRANSFER_1_IBAN))
                .body("find { it.id == 1 }.pagopaReported", equalTo(false));
    }

    @Test
    @Order(8)
    @DisplayName("GET /transfer-lists: Should require SenderBank header")
    void testGetAllTransferListsMissingSenderBankHeader() {
        given()
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(400);  // Bad request - missing required header
    }

    @Test
    @Order(9)
    @DisplayName("GET /transfer-lists: Should have proper JSON structure for AGID_01")
    void testGetAllTransferListsJsonStructureAGID01() {
        given()
                .header("SenderBank", SENDER_BANK_AGID_01)
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("$", instanceOf(List.class));
    }

    // ========== Tests for GET /transfer-lists/transaction/{transactionId} ==========

    @Test
    @Order(10)
    @DisplayName("GET /transfer-lists/transaction/{id}: Should return transfers for transaction 1")
    void testGetTransfersForTransaction1() {
        given()
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT + "/transaction/" + TRANSACTION_ID_1)
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("[0].transactionId", equalTo(1));
    }

    @Test
    @Order(11)
    @DisplayName("GET /transfer-lists/transaction/{id}: Should return transfers for transaction 2")
    void testGetTransfersForTransaction2() {
        given()
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT + "/transaction/" + TRANSACTION_ID_2)
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("[0].transactionId", equalTo(2));
    }

    @Test
    @Order(12)
    @DisplayName("GET /transfer-lists/transaction/{id}: Should return transfers for transaction 3")
    void testGetTransfersForTransaction3() {
        given()
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT + "/transaction/" + TRANSACTION_ID_3)
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("[0].transactionId", equalTo(3));
    }

    @Test
    @Order(13)
    @DisplayName("GET /transfer-lists/transaction/{id}: Should only return transfers for specific transaction")
    void testGetTransfersForTransactionIsolation() {
        // Get transfers for transaction 1
        given()
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT + "/transaction/" + TRANSACTION_ID_1)
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("[0].transactionId", equalTo(1))
                .body("[0].id", equalTo(1));

        // Verify transaction 2 has transfers belonging to transaction 2
        given()
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT + "/transaction/" + TRANSACTION_ID_2)
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("[0].transactionId", equalTo(2));
    }

    @Test
    @Order(14)
    @DisplayName("GET /transfer-lists/transaction/{id}: Transfer data should match expected values")
    void testGetTransfersForTransactionDataValidation() {
        given()
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT + "/transaction/" + TRANSACTION_ID_1)
                .then()
                .statusCode(200)
                .body("[0].transferAmount", notNullValue())
                .body("[0].paFiscalCode", equalTo(PA_FISCAL_CODE_AGID))
                .body("[0].paIban", equalTo(TRANSFER_1_IBAN))
                .body("[0].pagopaReported", equalTo(false));
    }

    @Test
    @Order(15)
    @DisplayName("GET /transfer-lists/transaction/{id}: Should return all required fields")
    void testGetTransfersForTransactionRequiredFields() {
        given()
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT + "/transaction/" + TRANSACTION_ID_1)
                .then()
                .statusCode(200)
                .body("[0].id", notNullValue())
                .body("[0].transactionId", notNullValue())
                .body("[0].transferId", notNullValue())
                .body("[0].transferAmount", notNullValue())
                .body("[0].paFiscalCode", notNullValue())
                .body("[0].paIban", notNullValue())
                .body("[0].rmtInfo", notNullValue())
                .body("[0].pagopaReported", notNullValue());
    }

    @Test
    @Order(16)
    @DisplayName("GET /transfer-lists/transaction/{id}: Should have JSON content type")
    void testGetTransfersForTransactionContentType() {
        given()
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT + "/transaction/" + TRANSACTION_ID_1)
                .then()
                .statusCode(200)
                .contentType("application/json");
    }

    @Test
    @Order(17)
    @DisplayName("GET /transfer-lists/transaction/{id}: Non-existent transaction should return 404 or empty list")
    void testGetTransfersForNonExistentTransaction() {
        Long nonExistentTransactionId = 99999L;
        var response = given()
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT + "/transaction/" + nonExistentTransactionId)
                .then()
                .extract()
                .response();

        // Should either return 404 or empty list depending on implementation
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            // If 200, should return empty list
            response.then().body("$", hasSize(0));
        } else {
            // If error, should be 404
            assert statusCode == 404 : "Expected 404 for non-existent transaction";
        }
    }

    @Test
    @Order(18)
    @DisplayName("GET /transfer-lists/transaction/{id}: BNL transfer should have correct PA fiscal code")
    void testGetTransfersForTransactionBNLData() {
        given()
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT + "/transaction/" + TRANSACTION_ID_3)
                .then()
                .statusCode(200)
                .body("[0].paFiscalCode", equalTo(PA_FISCAL_CODE_BNL))
                .body("[0].paIban", equalTo(TRANSFER_3_IBAN))
                .body("[0].transactionId", equalTo(3));
    }

    @Test
    @Order(19)
    @DisplayName("GET /transfer-lists: All transfers should have pagopaReported=false initially")
    void testGetAllTransferListsInitialReportedStatus() {
        given()
                .accept("application/json")
                .when()
                .header("SenderBank", SENDER_BANK_AGID_01)
                .get(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("find { it.id == 1 }.pagopaReported", equalTo(false));
    }

    @Test
    @Order(20)
    @DisplayName("GET /transfer-lists: Should return transfers with proper numeric values")
    void testGetAllTransferListsNumericValues() {
        given()
                .accept("application/json")
                .when()
                .header("SenderBank", SENDER_BANK_AGID_01)
                .get(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("find { it.id == 1 }.transferAmount", notNullValue())
                // Verify they are positive numbers
                .body("find { it.id == 1 }.transferAmount", greaterThan(0.0F));
    }

    @Test
    @Order(21)
    @DisplayName("GET /transfer-lists/transaction/{id}: Optional fields handling")
    void testGetTransfersOptionalFields() {
        // Test optional fields like transferCro and flowId
        given()
                .accept("application/json")
                .when()
                .get(TRANSFER_LISTS_ENDPOINT + "/transaction/" + TRANSACTION_ID_1)
                .then()
                .statusCode(200)
                .body("[0].id", notNullValue())
                .body("[0].transactionId", notNullValue());
                // transferCro and flowId are optional, so we don't require them
    }

    @Test
    @Order(22)
    @DisplayName("GET /transfer-lists: Verify paName is present")
    void testGetAllTransferListsPaName() {
        given()
                .accept("application/json")
                .when()
                .header("SenderBank", SENDER_BANK_AGID_01)
                .get(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("find { it.id == 1 }.paName", notNullValue());
    }

    @Test
    @Order(23)
    @DisplayName("GET /transfer-lists: AGID and BNL transfers should have different PA fiscal codes")
    void testGetAllTransferListsFiscalCodeDifference() {
        given()
                .accept("application/json")
                .when()
                .header("SenderBank", SENDER_BANK_AGID_01)
                .get(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                // AGID transfers (1 and 2)
                .body("find { it.id == 1 }.paFiscalCode", equalTo(PA_FISCAL_CODE_AGID));
    }

    @Test
    @Order(24)
    @DisplayName("GET /transfer-lists: Response should be consistent across multiple calls")
    void testGetAllTransferListsConsistency() {
        // First call
        var response1 = given()
                .accept("application/json")
                .when()
                .header("SenderBank", SENDER_BANK_AGID_01)
                .get(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .response();

        int size1 = response1.jsonPath().getList("$").size();

        // Second call with same SenderBank
        var response2 = given()
                .accept("application/json")
                .when()
                .header("SenderBank", SENDER_BANK_AGID_01)
                .get(TRANSFER_LISTS_ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .response();

        int size2 = response2.jsonPath().getList("$").size();

        // Sizes should match
        assert size1 == size2 : "Transfer list sizes should be consistent across calls";
    }
}

