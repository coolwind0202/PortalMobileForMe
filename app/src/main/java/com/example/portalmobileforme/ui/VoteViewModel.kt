package com.example.portalmobileforme.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

sealed class VoteRequestError {
    data object TimeoutError : VoteRequestError()
    data class ServerError(val reason: String) : VoteRequestError()
}

@Serializable
data class VoteSuccessResponse(val count: Int)

@Serializable
data class VoteFailedResponse(val reason: String)

const val url = "http://192.168.1.100:3000"

data class VoteUiState(
    val isLoading: Boolean = false,
    val count: Result<Int, VoteRequestError> = Ok(0)
)

class VoteViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(VoteUiState())
    val uiState = _uiState.asStateFlow()

    init {

    }

    fun vote() {
        viewModelScope.launch {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 1000
                }
            }
            viewModelScope.launch {
                val fetchDeferred: Deferred<Result<HttpResponse, VoteRequestError>> =
                    viewModelScope.async {
                        try {
                            val response: HttpResponse = client.get(url)
                            Ok(response)
                        } catch (e: HttpRequestTimeoutException) {
                            Err(VoteRequestError.TimeoutError)
                        } catch (e: Exception) {
                            throw e
                        }
                    }
                val httpRequestResult = fetchDeferred.await()
                client.close()

                val count = httpRequestResult.flatMap {
                    if (it.status.value != 200) {
                        val failed: VoteFailedResponse = it.body()
                        Err(VoteRequestError.ServerError(failed.reason))
                    } else {
                        val success: VoteSuccessResponse = it.body()
                        Ok(success.count)
                    }
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        count = count
                    )
                }
            }

            _uiState.update { currentState -> currentState.copy(isLoading = true) }
        }
    }
}