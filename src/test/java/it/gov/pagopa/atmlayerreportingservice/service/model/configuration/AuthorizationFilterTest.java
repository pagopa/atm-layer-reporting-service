package it.gov.pagopa.atmlayerreportingservice.service.model.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AuthorizationFilter Tests")
class AuthorizationFilterTest {

    private AuthorizationFilter authorizationFilter;

    @Mock
    private ContainerRequestContext requestContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authorizationFilter = new AuthorizationFilter();
    }

    @Test
    @DisplayName("extractTokenMiddlePart should extract middle part of valid token")
    void testExtractTokenMiddlePartValid() {
        String token = "header.payload.signature";
        String result = authorizationFilter.extractTokenMiddlePart(token);
        assertEquals("payload", result);
    }

    @Test
    @DisplayName("extractTokenMiddlePart should throw exception for invalid token format")
    void testExtractTokenMiddlePartInvalid() {
        String token = "header.payload";
        assertThrows(IllegalArgumentException.class, () -> {
            authorizationFilter.extractTokenMiddlePart(token);
        });
    }

    @Test
    @DisplayName("extractTokenMiddlePart should throw exception for token with more than 3 parts")
    void testExtractTokenMiddlePartTooManyParts() {
        String token = "header.payload.signature.extra";
        assertThrows(IllegalArgumentException.class, () -> {
            authorizationFilter.extractTokenMiddlePart(token);
        });
    }

    @Test
    @DisplayName("extractTokenMiddlePart should throw exception for token with only 1 part")
    void testExtractTokenMiddlePartOnePart() {
        String token = "onlyheader";
        assertThrows(IllegalArgumentException.class, () -> {
            authorizationFilter.extractTokenMiddlePart(token);
        });
    }

    @Test
    @DisplayName("extractTokenMiddlePart should throw exception for token with only dots")
    void testExtractTokenMiddlePartEmptyParts() {
        String token = "..";
        assertThrows(IllegalArgumentException.class, () -> {
            authorizationFilter.extractTokenMiddlePart(token);
        });
    }

    @Test
    @DisplayName("extractTokenMiddlePart should successfully parse valid JWT")
    void testExtractTokenMiddlePartValidJWT() {
        String validJWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U";
        String result = authorizationFilter.extractTokenMiddlePart(validJWT);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("extractTokenMiddlePart should handle large token")
    void testExtractTokenMiddlePartLargeToken() {
        StringBuilder largePayload = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largePayload.append("a");
        }
        String token = "header." + largePayload.toString() + ".signature";
        String result = authorizationFilter.extractTokenMiddlePart(token);
        assertEquals(1000, result.length());
    }

    @Test
    @DisplayName("getPayload should parse valid base64 encoded JSON")
    void testGetPayloadValid() {
        String jsonString = "{\"client_id\": \"test-client\", \"sub\": \"user123\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        JsonNode result = authorizationFilter.getPayload(encodedPayload);

        assertNotNull(result);
        assertEquals("test-client", result.get("client_id").asText());
        assertEquals("user123", result.get("sub").asText());
    }

    @Test
    @DisplayName("getPayload should throw exception for invalid base64")
    void testGetPayloadInvalidBase64() {
        String invalidBase64 = "!!!invalid!!!";
        assertThrows(IllegalArgumentException.class, () -> {
            authorizationFilter.getPayload(invalidBase64);
        });
    }

    @Test
    @DisplayName("getPayload should throw exception for invalid JSON")
    void testGetPayloadInvalidJson() {
        String invalidJson = "not a json at all";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(invalidJson.getBytes());

        assertThrows(RuntimeException.class, () -> {
            authorizationFilter.getPayload(encodedPayload);
        });
    }

    @Test
    @DisplayName("getPayload should handle complex JSON structures")
    void testGetPayloadComplexJson() {
        String jsonString = "{\"client_id\": \"test\", \"nested\": {\"key\": \"value\"}, \"array\": [1, 2, 3]}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        JsonNode result = authorizationFilter.getPayload(encodedPayload);

        assertNotNull(result);
        assertEquals("test", result.get("client_id").asText());
        assertEquals("value", result.get("nested").get("key").asText());
        assertEquals(1, result.get("array").get(0).asInt());
    }

    @Test
    @DisplayName("getPayload should return JsonNode with null client_id if not present")
    void testGetPayloadMissingClientId() {
        String jsonString = "{\"sub\": \"user123\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        JsonNode result = authorizationFilter.getPayload(encodedPayload);

        assertNotNull(result);
        assertNull(result.get("client_id"));
    }

    @Test
    @DisplayName("getPayload should handle special characters in base64")
    void testGetPayloadWithSpecialCharacters() {
        String jsonString = "{\"client_id\": \"test-123_abc\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        JsonNode result = authorizationFilter.getPayload(encodedPayload);

        assertNotNull(result);
        assertEquals("test-123_abc", result.get("client_id").asText());
    }

    @Test
    @DisplayName("getPayload should handle numeric values in JSON")
    void testGetPayloadWithNumericValues() {
        String jsonString = "{\"client_id\": \"test\", \"code\": 200, \"timeout\": 3.5}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        JsonNode result = authorizationFilter.getPayload(encodedPayload);

        assertNotNull(result);
        assertEquals("test", result.get("client_id").asText());
        assertEquals(200, result.get("code").asInt());
        assertEquals(3.5, result.get("timeout").asDouble());
    }

    @Test
    @DisplayName("getPayload should decode multiple properties correctly")
    void testGetPayloadMultipleProperties() {
        String jsonString = "{\"client_id\": \"test\", \"scope\": \"read write\", \"exp\": 1234567890}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        JsonNode result = authorizationFilter.getPayload(encodedPayload);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.has("client_id"));
        assertTrue(result.has("scope"));
        assertTrue(result.has("exp"));
    }

    @Test
    @DisplayName("getClientId should extract client_id from valid authorization header")
    void testGetClientIdValid() {
        String jsonString = "{\"client_id\": \"my-client-id\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);

        String result = authorizationFilter.getClientId(requestContext);

        assertEquals("my-client-id", result);
    }

    @Test
    @DisplayName("getClientId should return null when Authorization header is null")
    void testGetClientIdNullHeader() {
        when(requestContext.getHeaderString("Authorization")).thenReturn(null);

        String result = authorizationFilter.getClientId(requestContext);

        assertNull(result);
    }

    @Test
    @DisplayName("getClientId should return null when Authorization header is empty")
    void testGetClientIdEmptyHeader() {
        when(requestContext.getHeaderString("Authorization")).thenReturn("");

        String result = authorizationFilter.getClientId(requestContext);

        assertNull(result);
    }

    @Test
    @DisplayName("getClientId should extract client_id even with whitespace in header")
    void testGetClientIdWithWhitespace() {
        String jsonString = "{\"client_id\": \"whitespace-client\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);

        String result = authorizationFilter.getClientId(requestContext);

        assertEquals("whitespace-client", result);
    }

    @Test
    @DisplayName("getClientId should handle token with spaces in middle part")
    void testGetClientIdWithSpacesInPayload() {
        String jsonString = "{\"client_id\": \"test client\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);

        String result = authorizationFilter.getClientId(requestContext);

        assertEquals("test client", result);
    }

    @Test
    @DisplayName("filter should pass when clientId is null and apiKey is null")
    void testFilterBothNull() {
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        when(requestContext.getHeaderString("Authorization")).thenReturn(null);
        when(requestContext.getHeaders()).thenReturn(headers);

        assertDoesNotThrow(() -> {
            authorizationFilter.filter(requestContext);
        });
    }

    @Test
    @DisplayName("filter should pass when clientId is null and apiKey is present")
    void testFilterClientIdNullApiKeyPresent() {
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        List<String> apiKeyList = new ArrayList<>();
        apiKeyList.add("some-api-key");
        headers.put("x-api-key", apiKeyList);

        when(requestContext.getHeaderString("Authorization")).thenReturn(null);
        when(requestContext.getHeaders()).thenReturn(headers);

        assertDoesNotThrow(() -> {
            authorizationFilter.filter(requestContext);
        });
    }

    @Test
    @DisplayName("filter should pass when clientId matches apiKey")
    void testFilterMatch() {
        String jsonString = "{\"client_id\": \"matching-id\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        List<String> apiKeyList = new ArrayList<>();
        apiKeyList.add("matching-id");
        headers.put("x-api-key", apiKeyList);

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        assertDoesNotThrow(() -> {
            authorizationFilter.filter(requestContext);
        });
    }

    @Test
    @DisplayName("filter should pass when clientId matches apiKey with numeric characters")
    void testFilterClientIdNumeric() {
        String jsonString = "{\"client_id\": \"client123\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        List<String> apiKeyList = new ArrayList<>();
        apiKeyList.add("client123");
        headers.put("x-api-key", apiKeyList);

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        assertDoesNotThrow(() -> {
            authorizationFilter.filter(requestContext);
        });
    }

    @Test
    @DisplayName("filter should throw exception when clientId does not match apiKey")
    void testFilterMismatch() {
        String jsonString = "{\"client_id\": \"client-123\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        List<String> apiKeyList = new ArrayList<>();
        apiKeyList.add("different-key");
        headers.put("x-api-key", apiKeyList);

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authorizationFilter.filter(requestContext);
        });

        assertEquals("Client ID does not match API Key", exception.getMessage());
    }

    @Test
    @DisplayName("filter should throw exception when clientId present but apiKey is null")
    void testFilterClientIdPresentApiKeyNull() {
        String jsonString = "{\"client_id\": \"client-id\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authorizationFilter.filter(requestContext);
        });

        assertEquals("Client ID does not match API Key", exception.getMessage());
    }

    @Test
    @DisplayName("filter should throw exception when clientId is empty string and apiKey is null")
    void testFilterClientIdEmptyString() {
        String jsonString = "{\"client_id\": \"\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authorizationFilter.filter(requestContext);
        });

        assertEquals("Client ID does not match API Key", exception.getMessage());
    }

    @Test
    @DisplayName("filter should handle multiple API keys and use first")
    void testFilterMultipleApiKeys() {
        String jsonString = "{\"client_id\": \"matching-id\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        List<String> apiKeyList = new ArrayList<>();
        apiKeyList.add("matching-id");
        apiKeyList.add("other-key");
        headers.put("x-api-key", apiKeyList);

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        assertDoesNotThrow(() -> {
            authorizationFilter.filter(requestContext);
        });
    }

    @Test
    @DisplayName("filter should throw exception when clientId does not match first apiKey")
    void testFilterMismatchFirstApiKey() {
        String jsonString = "{\"client_id\": \"client-id\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        List<String> apiKeyList = new ArrayList<>();
        apiKeyList.add("different-key");
        apiKeyList.add("client-id");
        headers.put("x-api-key", apiKeyList);

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authorizationFilter.filter(requestContext);
        });

        assertEquals("Client ID does not match API Key", exception.getMessage());
    }

    @Test
    @DisplayName("filter should pass when token has Bearer prefix and clientId matches")
    void testFilterWithBearerPrefix() {
        String jsonString = "{\"client_id\": \"bearer-client\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "Bearer header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        List<String> apiKeyList = new ArrayList<>();
        apiKeyList.add("bearer-client");
        headers.put("x-api-key", apiKeyList);

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        assertDoesNotThrow(() -> {
            authorizationFilter.filter(requestContext);
        });
    }

    @Test
    @DisplayName("filter should pass with empty apiKey list")
    void testFilterEmptyApiKeyList() {
        String jsonString = "{\"client_id\": \"client-id\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        List<String> emptyList = new ArrayList<>();
        headers.put("x-api-key", emptyList);

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        assertThrows(RuntimeException.class, () -> {
            authorizationFilter.filter(requestContext);
        });
    }

    @Test
    @DisplayName("filter should throw exception when clientId present and apiKey header missing")
    void testFilterClientIdPresentApiKeyMissing() {
        String jsonString = "{\"client_id\": \"client-id\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authorizationFilter.filter(requestContext);
        });

        assertEquals("Client ID does not match API Key", exception.getMessage());
    }

    @Test
    @DisplayName("filter should throw exception when clientId does not match with special characters")
    void testFilterMismatchWithSpecialChars() {
        String jsonString = "{\"client_id\": \"client@domain.com\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        List<String> apiKeyList = new ArrayList<>();
        apiKeyList.add("different@domain.com");
        headers.put("x-api-key", apiKeyList);

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authorizationFilter.filter(requestContext);
        });

        assertEquals("Client ID does not match API Key", exception.getMessage());
    }
}

