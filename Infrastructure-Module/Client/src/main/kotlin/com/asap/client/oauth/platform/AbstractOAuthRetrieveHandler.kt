package com.asap.client.oauth.platform

import com.asap.client.oauth.OAuthRetrieveHandler
import com.asap.client.oauth.exception.OAuthException
import org.springframework.web.reactive.function.client.WebClient

abstract class AbstractOAuthRetrieveHandler<T>(
    private val webClient: WebClient,
) : OAuthRetrieveHandler {
    
    override fun getOAuthInfo(request: OAuthRetrieveHandler.OAuthRequest): OAuthRetrieveHandler.OAuthResponse {
        val response = webClient
            .get()
            .uri(getApiEndpoint())
            .header("Authorization", "Bearer ${request.accessToken}")
            .retrieve()
            .onStatus({ it.isError }, {
                throw OAuthException.OAuthRetrieveFailedException(getErrorMessage())
            })
            .bodyToMono(getResponseType())
            .block()

        if (response == null) {
            throw OAuthException.OAuthRetrieveFailedException(getErrorMessage())
        }

        return mapToOAuthResponse(response)
    }

    /**
     * Returns the API endpoint URI for the specific OAuth provider
     */
    protected abstract fun getApiEndpoint(): String

    /**
     * Returns the error message for the specific OAuth provider
     */
    protected abstract fun getErrorMessage(): String

    /**
     * Returns the response type class for the specific OAuth provider
     */
    protected abstract fun getResponseType(): Class<T>

    /**
     * Maps the provider-specific response to a common OAuthResponse
     */
    protected abstract fun mapToOAuthResponse(response: T): OAuthRetrieveHandler.OAuthResponse
}