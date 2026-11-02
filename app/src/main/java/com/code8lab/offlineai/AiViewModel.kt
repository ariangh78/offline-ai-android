package com.code8lab.offlineai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.Generation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AiState(
    val status: String = "Checking...",
    val ready: Boolean = false,
    val progress: Float? = null,
    val prompt: String = "",
    val answer: String = "",
    val busy: Boolean = false,
)

class AiViewModel : ViewModel() {

    private val model = Generation.getClient()
    private val _state = MutableStateFlow(AiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch { prepare() }
    }

    private suspend fun prepare() {
        when (model.checkStatus()) {
            FeatureStatus.UNAVAILABLE ->
                _state.update { it.copy(status = "This phone can't run Gemini Nano") }

            FeatureStatus.DOWNLOADABLE -> download()

            FeatureStatus.DOWNLOADING ->
                _state.update { it.copy(status = "Model is downloading, try again soon") }

            FeatureStatus.AVAILABLE ->
                _state.update { it.copy(status = "Ready", ready = true) }
        }
    }

    private suspend fun download() {
        model.download().collect { s ->
            when (s) {
                is DownloadStatus.DownloadStarted ->
                    _state.update { it.copy(status = "Downloading model...") }

                is DownloadStatus.DownloadProgress ->
                    _state.update {
                        it.copy(status = "${s.totalBytesDownloaded / 1_000_000} MB downloaded")
                    }

                DownloadStatus.DownloadCompleted ->
                    _state.update { it.copy(status = "Ready", ready = true, progress = null) }

                is DownloadStatus.DownloadFailed ->
                    _state.update { it.copy(status = "Download failed: ${s.e.message}") }
            }
        }
    }

    fun onPromptChange(text: String) = _state.update { it.copy(prompt = text) }

    fun send() {
        val prompt = _state.value.prompt
        if (prompt.isBlank() || !_state.value.ready) return
        viewModelScope.launch {
            _state.update { it.copy(answer = "", busy = true) }
            try {
                model.generateContentStream(prompt).collect { chunk ->
                    val text = chunk.candidates[0].text
                    _state.update { it.copy(answer = it.answer + text) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(answer = "Error: ${e.message}") }
            } finally {
                _state.update { it.copy(busy = false) }
            }
        }
    }

    override fun onCleared() {
        model.close()
        super.onCleared()
    }
}
