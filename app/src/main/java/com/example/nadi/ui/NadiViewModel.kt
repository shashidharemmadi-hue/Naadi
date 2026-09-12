package com.example.nadi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nadi.audio.AudioEngine
import com.example.nadi.audio.MusicEngine
import com.example.nadi.data.SettingsManager
import com.example.nadi.model.Raga
import com.example.nadi.model.Swara
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NadiUiState(
    val shrutiFrequency: Double = 240.0,
    val selectedRaga: Raga = Raga.Hamsadhwani,
    val activeSwara: Swara? = null,
    val isTanpuraPlaying: Boolean = false,
    val isPlaybackActive: Boolean = false,
    val isDarkMode: Boolean = true,
    val volume: Float = 0.7f
)

class NadiViewModel(private val settingsManager: SettingsManager) : ViewModel() {

    private val _uiState = MutableStateFlow(NadiUiState())
    val uiState: StateFlow<NadiUiState> = _uiState.asStateFlow()

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents = _errorEvents.asSharedFlow()

    val allRagas = Raga.allRagas

    private val audioEngine = AudioEngine()
    private val musicEngine = MusicEngine(_uiState.value.shrutiFrequency)

    private var playbackJob: Job? = null

    init {
        audioEngine.onAudioError = { error ->
            viewModelScope.launch {
                _errorEvents.emit(error)
            }
        }

        viewModelScope.launch {
            settingsManager.saFrequencyFlow.collect { freq ->
                _uiState.update { it.copy(shrutiFrequency = freq) }
                musicEngine.baseSaFrequency = freq
            }
        }
        viewModelScope.launch {
            settingsManager.selectedRagaNameFlow.collect { name ->
                val raga = Raga.allRagas.find { it.name == name } ?: Raga.Hamsadhwani
                _uiState.update { it.copy(selectedRaga = raga) }
            }
        }
        viewModelScope.launch {
            settingsManager.isDarkModeFlow.collect { isDark ->
                _uiState.update { it.copy(isDarkMode = isDark) }
            }
        }
        viewModelScope.launch {
            settingsManager.volumeFlow.collect { vol ->
                val volFloat = vol.toFloat()
                _uiState.update { it.copy(volume = volFloat) }
                audioEngine.setVolume(volFloat)
            }
        }
    }

    fun increaseFrequency() {
        _uiState.update { currentState ->
            val newFreq = (currentState.shrutiFrequency + 0.1).coerceAtMost(500.0)
            musicEngine.baseSaFrequency = newFreq
            if (currentState.isTanpuraPlaying) {
                audioEngine.startDrone(listOf(newFreq, newFreq * 1.5, newFreq * 2.0))
            }
            viewModelScope.launch { settingsManager.saveSaFrequency(newFreq) }
            currentState.copy(shrutiFrequency = newFreq)
        }
    }

    fun decreaseFrequency() {
        _uiState.update { currentState ->
            val newFreq = (currentState.shrutiFrequency - 0.1).coerceAtLeast(100.0)
            musicEngine.baseSaFrequency = newFreq
            if (currentState.isTanpuraPlaying) {
                audioEngine.startDrone(listOf(newFreq, newFreq * 1.5, newFreq * 2.0))
            }
            viewModelScope.launch { settingsManager.saveSaFrequency(newFreq) }
            currentState.copy(shrutiFrequency = newFreq)
        }
    }

    fun setVolume(volume: Float) {
        val vol = volume.coerceIn(0f, 1f)
        _uiState.update { it.copy(volume = vol) }
        audioEngine.setVolume(vol)
        viewModelScope.launch { settingsManager.saveVolume(vol) }
    }

    fun toggleTanpura() {
        stopPlayback()
        _uiState.update { currentState ->
            val nextPlaying = !currentState.isTanpuraPlaying
            if (nextPlaying) {
                audioEngine.startDrone(listOf(currentState.shrutiFrequency, currentState.shrutiFrequency * 1.5, currentState.shrutiFrequency * 2.0))
            } else {
                audioEngine.stopDrone()
            }
            currentState.copy(isTanpuraPlaying = nextPlaying)
        }
    }

    fun selectRaga(raga: Raga) {
        stopPlayback()
        audioEngine.stopNote()
        _uiState.update { it.copy(selectedRaga = raga, activeSwara = null) }
        viewModelScope.launch { settingsManager.saveSelectedRagaName(raga.name) }
    }

    fun toggleDarkMode() {
        val newDarkMode = !_uiState.value.isDarkMode
        _uiState.update { it.copy(isDarkMode = newDarkMode) }
        viewModelScope.launch { settingsManager.saveDarkMode(newDarkMode) }
    }

    fun playSwara(swara: Swara) {
        stopPlayback()
        if (_uiState.value.activeSwara == swara) {
            audioEngine.stopNote()
            _uiState.update { it.copy(activeSwara = null) }
        } else {
            val freq = musicEngine.calculateFrequency(swara)
            audioEngine.startNote(freq)
            _uiState.update { it.copy(activeSwara = swara) }
        }
    }

    fun playArohanam() {
        playSequence(_uiState.value.selectedRaga.arohanam)
    }

    fun playAvarohanam() {
        playSequence(_uiState.value.selectedRaga.avarohanam)
    }

    private fun playSequence(swaras: List<Swara>) {
        stopPlayback()
        playbackJob = viewModelScope.launch {
            _uiState.update { it.copy(isPlaybackActive = true) }
            try {
                for (swara in swaras) {
                    val freq = musicEngine.calculateFrequency(swara)
                    audioEngine.startNote(freq)
                    _uiState.update { it.copy(activeSwara = swara) }
                    delay(600) // Note duration
                }
            } finally {
                audioEngine.stopNote()
                _uiState.update { it.copy(activeSwara = null, isPlaybackActive = false) }
            }
        }
    }

    fun stopPlayback() {
        playbackJob?.cancel()
        playbackJob = null
        _uiState.update { it.copy(isPlaybackActive = false) }
    }

    override fun onCleared() {
        audioEngine.release()
    }
}
