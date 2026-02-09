package demo.nexa.clinical_transcription_demo.nexa

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class NexaInitState {
    data object Uninitialized : NexaInitState()
    data object Ready : NexaInitState()
    data class Failed(val reason: String) : NexaInitState()
}

object NexaRuntime {
    private val _state = MutableStateFlow<NexaInitState>(NexaInitState.Uninitialized)
    val state: StateFlow<NexaInitState> = _state.asStateFlow()

    fun markReady() {
        _state.value = NexaInitState.Ready
    }

    fun markFailed(reason: String) {
        _state.value = NexaInitState.Failed(reason)
    }

    fun isReady(): Boolean = _state.value is NexaInitState.Ready
}

