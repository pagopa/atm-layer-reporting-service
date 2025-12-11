package it.gov.pagopa.atmlayerreportingservice.service.model.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;

import java.util.Base64;

@Provider
@PreMatching
public class AuthorizationFilter implements ContainerRequestFilter {

    private final String HEADER_AUTHORIZATION = "Authorization";
    private final String CLAIM_CLIENT_ID = "client_id";

    String extractTokenMiddlePart(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid token format");
        }
        return parts[1];
    }

    JsonNode getPayload(String base64String) {
        String payload = new String(Base64.getUrlDecoder().decode(base64String));
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return rootNode;
    }

    String getClientId(ContainerRequestContext containerRequestContext) {
        String authorization = containerRequestContext.getHeaderString(HEADER_AUTHORIZATION);
        if ( authorization != null && !authorization.isEmpty()) {
            String middlePart = extractTokenMiddlePart(authorization);
            return getPayload(middlePart).get(CLAIM_CLIENT_ID).asText();
        }
        return null;
    }

    @Override
    public void filter(ContainerRequestContext requestContext){
        String clientId = getClientId(requestContext);
        String apiKey = requestContext.getHeaders().get("x-api-key") != null ? String.valueOf(requestContext.getHeaders().get("x-api-key").getFirst()) : null;

        if (clientId !=null && !clientId.equals(apiKey)) {
            throw new RuntimeException("Client ID does not match API Key");
        }
    }
}
