package it.gov.pagopa.atmlayerreportingservice.service.model.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
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
    void testExtractTokenMiddlePartValid() {
        String token = "header.payload.signature";
        String result = authorizationFilter.extractTokenMiddlePart(token);
        assertEquals("payload", result);
    }

    @Test
    void testExtractTokenMiddlePartInvalid() {
        String token = "header.payload";
        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            authorizationFilter.extractTokenMiddlePart(token);
        });
        assertEquals(400, ex.getResponse().getStatus());
        Map entity = (Map) ex.getResponse().getEntity();
        assertInstanceOf(String.class, entity.get("message"));
        assertEquals("Invalid token format", entity.get("message"));
    }

    @Test
    void testExtractTokenMiddlePartTooManyParts() {
        String token = "header.payload.signature.extra";
        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            authorizationFilter.extractTokenMiddlePart(token);
        });
        assertEquals(400, ex.getResponse().getStatus());
        Map entity = (Map) ex.getResponse().getEntity();
        assertInstanceOf(String.class, entity.get("message"));
        assertEquals("Invalid token format", entity.get("message"));
    }

    @Test
    void testExtractTokenMiddlePartOnePart() {
        String token = "onlyheader";
        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            authorizationFilter.extractTokenMiddlePart(token);
        });
        assertEquals(400, ex.getResponse().getStatus());
        Map entity = (Map) ex.getResponse().getEntity();
        assertInstanceOf(String.class, entity.get("message"));
        assertEquals("Invalid token format", entity.get("message"));
    }

    @Test
    void testExtractTokenMiddlePartEmptyParts() {
        String token = "..";
        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            authorizationFilter.extractTokenMiddlePart(token);
        });
        assertEquals(400, ex.getResponse().getStatus());
        Map entity = (Map) ex.getResponse().getEntity();
        assertInstanceOf(String.class, entity.get("message"));
        assertEquals("Invalid token format", entity.get("message"));
    }

    @Test
    void testExtractTokenMiddlePartValidJWT() {
        String validJWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U";
        String result = authorizationFilter.extractTokenMiddlePart(validJWT);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("eyJzdWIiOiIxMjM0NTY3ODkwIn0", result);
    }

    @Test
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
    void testExtractTokenMiddlePartZeroParts() {
        String token = "";
        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            authorizationFilter.extractTokenMiddlePart(token);
        });
        assertEquals(400, ex.getResponse().getStatus());
        Map entity = (Map) ex.getResponse().getEntity();
        assertInstanceOf(String.class, entity.get("message"));
        assertEquals("Invalid token format", entity.get("message"));
    }

    @Test
    void testExtractTokenMiddlePartEmptyString() {
        String token = ".";
        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            authorizationFilter.extractTokenMiddlePart(token);
        });
        assertEquals(400, ex.getResponse().getStatus());
        Map entity = (Map) ex.getResponse().getEntity();
        assertInstanceOf(String.class, entity.get("message"));
        assertEquals("Invalid token format", entity.get("message"));
    }

    @Test
    void testExtractTokenMiddlePartEmptyPayload() {
        String token = "a..c";
        String result = authorizationFilter.extractTokenMiddlePart(token);
        assertEquals("", result);
    }

    @Test
    void testGetPayloadValid() {
        String jsonString = "{\"client_id\": \"test-client\", \"sub\": \"user123\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        JsonNode result = authorizationFilter.getPayload(encodedPayload);

        assertNotNull(result);
        assertEquals("test-client", result.get("client_id").asText());
        assertEquals("user123", result.get("sub").asText());
    }

    @Test
    void testGetPayloadInvalidBase64() {
        String invalidBase64 = "!!!invalid!!!";
        assertThrows(IllegalArgumentException.class, () -> {
            authorizationFilter.getPayload(invalidBase64);
        });
    }

    @Test
    void testGetPayloadInvalidJson() {
        String invalidJson = "not a json at all";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(invalidJson.getBytes());

        assertThrows(RuntimeException.class, () -> {
            authorizationFilter.getPayload(encodedPayload);
        });
    }

    @Test
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
    void testGetPayloadMissingClientId() {
        String jsonString = "{\"sub\": \"user123\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        JsonNode result = authorizationFilter.getPayload(encodedPayload);

        assertNotNull(result);
        assertNull(result.get("client_id"));
    }

    @Test
    void testGetPayloadWithSpecialCharacters() {
        String jsonString = "{\"client_id\": \"test-123_abc\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        JsonNode result = authorizationFilter.getPayload(encodedPayload);

        assertNotNull(result);
        assertEquals("test-123_abc", result.get("client_id").asText());
    }

    @Test
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
    void testGetPayloadEmptyJson() {
        String jsonString = "{}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        JsonNode result = authorizationFilter.getPayload(encodedPayload);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetPayloadNullClientId() {
        String jsonString = "{\"client_id\": null}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        JsonNode result = authorizationFilter.getPayload(encodedPayload);

        assertNotNull(result);
        assertTrue(result.get("client_id").isNull());
    }

    @Test
    void testGetPayloadArray() {
        String jsonString = "[1, 2, 3]";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        JsonNode result = authorizationFilter.getPayload(encodedPayload);

        assertNotNull(result);
        assertTrue(result.isArray());
    }

    @Test
    void testGetPayloadString() {
        String jsonString = "\"just a string\"";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        JsonNode result = authorizationFilter.getPayload(encodedPayload);

        assertNotNull(result);
        assertTrue(result.isTextual());
    }

    @Test
    void testGetPayloadNumber() {
        String jsonString = "123";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        JsonNode result = authorizationFilter.getPayload(encodedPayload);

        assertNotNull(result);
        assertTrue(result.isNumber());
    }

    @Test
    void testGetPayloadBoolean() {
        String jsonString = "true";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        JsonNode result = authorizationFilter.getPayload(encodedPayload);

        assertNotNull(result);
        assertTrue(result.isBoolean());
    }

    @Test
    void testGetClientIdValid() {
        String jsonString = "{\"client_id\": \"my-client-id\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);

        String result = authorizationFilter.getClientId(requestContext);

        assertEquals("my-client-id", result);
    }

    @Test
    void testGetClientIdNullHeader() {
        when(requestContext.getHeaderString("Authorization")).thenReturn(null);

        String result = authorizationFilter.getClientId(requestContext);

        assertNull(result);
    }

    @Test
    void testGetClientIdEmptyHeader() {
        when(requestContext.getHeaderString("Authorization")).thenReturn("");

        String result = authorizationFilter.getClientId(requestContext);

        assertNull(result);
    }

    @Test
    void testGetClientIdWithWhitespace() {
        String jsonString = "{\"client_id\": \"whitespace-client\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);

        String result = authorizationFilter.getClientId(requestContext);

        assertEquals("whitespace-client", result);
    }

    @Test
    void testGetClientIdWithSpacesInPayload() {
        String jsonString = "{\"client_id\": \"test client\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);

        String result = authorizationFilter.getClientId(requestContext);

        assertEquals("test client", result);
    }

    @Test
    void testGetClientIdEmptyClientId() {
        String jsonString = "{\"client_id\": \"\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);

        String result = authorizationFilter.getClientId(requestContext);

        assertEquals("", result);
    }

    @Test
    void testGetClientIdNullClientId() {
        String jsonString = "{\"client_id\": null}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);

        String result = authorizationFilter.getClientId(requestContext);

        assertEquals("null", result);
    }

    @Test
    void testGetClientIdWithNumericId() {
        String jsonString = "{\"client_id\": \"12345\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);

        String result = authorizationFilter.getClientId(requestContext);

        assertEquals("12345", result);
    }

    @Test
    void testGetClientIdWithSpecialCharacters() {
        String jsonString = "{\"client_id\": \"client@domain.com\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);

        String result = authorizationFilter.getClientId(requestContext);

        assertEquals("client@domain.com", result);
    }

    @Test
    void testFilterBothNull() {
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        when(requestContext.getHeaderString("Authorization")).thenReturn(null);
        when(requestContext.getHeaders()).thenReturn(headers);

        assertDoesNotThrow(() -> {
            authorizationFilter.filter(requestContext);
        });
    }

    @Test
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

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            authorizationFilter.filter(requestContext);
        });

        Map entity = (Map) exception.getResponse().getEntity();
        assertInstanceOf(String.class, entity.get("message"));
        assertEquals(401, exception.getResponse().getStatus());
        assertTrue("Client ID does not match API key".equalsIgnoreCase((String) entity.get("message")));
    }

    @Test
    void testFilterClientIdPresentApiKeyNull() {
        String jsonString = "{\"client_id\": \"client-id\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            authorizationFilter.filter(requestContext);
        });

        Map entity = (Map) exception.getResponse().getEntity();
        assertInstanceOf(String.class, entity.get("message"));
        assertEquals(401, exception.getResponse().getStatus());
        assertTrue("Client ID does not match API key".equalsIgnoreCase((String) entity.get("message")));
    }

    @Test
    void testFilterEmptyClientIdWithNull() {
        String jsonString = "{\"client_id\": \"\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            authorizationFilter.filter(requestContext);
        });

        Map entity = (Map) exception.getResponse().getEntity();
        assertInstanceOf(String.class, entity.get("message"));
        assertEquals(401, exception.getResponse().getStatus());
        assertTrue("Client ID does not match API key".equalsIgnoreCase((String) entity.get("message")));
    }

    @Test
    void testFilterMultipleApiKeysMatch() {
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
    void testFilterMultipleApiKeysMismatch() {
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

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            authorizationFilter.filter(requestContext);
        });

        Map entity = (Map) exception.getResponse().getEntity();
        assertInstanceOf(String.class, entity.get("message"));
        assertEquals(401, exception.getResponse().getStatus());
        assertTrue("Client ID does not match API key".equalsIgnoreCase((String) entity.get("message")));
    }

    @Test
    void testFilterEmptyApiKeyList() {
        String jsonString = "{\"client_id\": \"client-id\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        List<String> emptyList = new ArrayList<>();
        headers.put("x-api-key", emptyList);

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        assertThrows(Exception.class, () -> {
            authorizationFilter.filter(requestContext);
        });
    }

    @Test
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

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            authorizationFilter.filter(requestContext);
        });

        Map entity = (Map) exception.getResponse().getEntity();
        assertInstanceOf(String.class, entity.get("message"));
        assertEquals(401, exception.getResponse().getStatus());
        assertTrue("Client ID does not match API key".equalsIgnoreCase((String) entity.get("message")));
    }

    @Test
    void testFilterNullClientIdNullApiKey() {
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        when(requestContext.getHeaderString("Authorization")).thenReturn(null);
        when(requestContext.getHeaders()).thenReturn(headers);

        assertDoesNotThrow(() -> {
            authorizationFilter.filter(requestContext);
        });

        verify(requestContext, times(1)).getHeaderString("Authorization");
        verify(requestContext, times(1)).getHeaders();
    }

    @Test
    void testFilterHeadersGetReturnsNull() {
        when(requestContext.getHeaderString("Authorization")).thenReturn(null);
        when(requestContext.getHeaders()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> {
            authorizationFilter.filter(requestContext);
        });
    }

    @Test
    void testFilterXApiKeyHeaderGetReturnsNull() {
        String jsonString = "{\"client_id\": \"test-id\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            authorizationFilter.filter(requestContext);
        });

        Map entity = (Map) exception.getResponse().getEntity();
        assertInstanceOf(String.class, entity.get("message"));
        assertEquals(401, exception.getResponse().getStatus());
        assertTrue("Client ID does not match API key".equalsIgnoreCase((String) entity.get("message")));
    }

    @Test
    void testFilterClientIdEqualsApiKey() {
        String jsonString = "{\"client_id\": \"test-key\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "header." + encodedPayload + ".signature";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        List<String> apiKeyList = new ArrayList<>();
        apiKeyList.add("test-key");
        headers.put("x-api-key", apiKeyList);

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        assertDoesNotThrow(() -> {
            authorizationFilter.filter(requestContext);
        });
    }

    @Test
    void testFilterAllConditions() {
        String jsonString = "{\"client_id\": \"valid\"}";
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        String token = "h." + encodedPayload + ".s";

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        List<String> apiKeyList = new ArrayList<>();
        apiKeyList.add("valid");
        headers.put("x-api-key", apiKeyList);

        when(requestContext.getHeaderString("Authorization")).thenReturn(token);
        when(requestContext.getHeaders()).thenReturn(headers);

        assertDoesNotThrow(() -> {
            authorizationFilter.filter(requestContext);
        });
    }
}

