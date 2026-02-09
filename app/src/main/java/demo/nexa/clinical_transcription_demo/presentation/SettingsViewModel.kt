package demo.nexa.clinical_transcription_demo.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import demo.nexa.clinical_transcription_demo.data.repository.NotesRepository
import demo.nexa.clinical_transcription_demo.model.ModelCatalog
import demo.nexa.clinical_transcription_demo.model.ModelDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NotesRepository.getInstance(application)
    private val downloader = ModelDownloader(application.applicationContext)

    fun clearAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllNotes()
        }
    }

    fun clearModels() {
        viewModelScope.launch(Dispatchers.IO) {
            // delete known model install roots + clean up any lingering filesDir/nexa_models
            ModelCatalog.all.forEach { spec ->
                downloader.delete(spec)
            }
            val root = File(getApplication<Application>().filesDir, "nexa_models")
            if (root.exists()) root.deleteRecursively()
        }
    }
}

