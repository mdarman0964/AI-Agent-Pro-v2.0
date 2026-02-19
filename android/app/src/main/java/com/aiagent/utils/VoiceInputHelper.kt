package com.aiagent.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class VoiceInputHelper(private val context: Context) {
    
    private var speechRecognizer: SpeechRecognizer? = null
    
    private val _state = MutableStateFlow<VoiceState>(VoiceState.Idle)
    val state: StateFlow<VoiceState> = _state
    
    private var onResult: ((String) -> Unit)? = null
    private var onError: ((String) -> Unit)? = null
    
    sealed class VoiceState {
        object Idle : VoiceState()
        object Listening : VoiceState()
        data class Result(val text: String) : VoiceState()
        data class Error(val message: String) : VoiceState()
    }
    
    fun startListening(
        language: String = Locale.getDefault().language,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        this.onResult = onResult
        this.onError = onError
        
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _state.value = VoiceState.Listening
                    }
                    
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    
                    override fun onError(error: Int) {
                        val message = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client error"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission denied"
                            SpeechRecognizer.ERROR_NETWORK -> "Network error"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                            SpeechRecognizer.ERROR_SERVER -> "Server error"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                            else -> "Unknown error"
                        }
                        _state.value = VoiceState.Error(message)
                        onError(message)
                    }
                    
                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        _state.value = VoiceState.Result(text)
                        onResult(text)
                    }
                    
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
                
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                }
                
                startListening(intent)
            }
        } else {
            onError("Speech recognition not available")
        }
    }
    
    fun stopListening() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
        _state.value = VoiceState.Idle
    }
    
    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
