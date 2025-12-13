package app.vitune.android.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.vitune.android.preferences.UpdatePreferences
import app.vitune.android.utils.UpdateInfo
import app.vitune.android.utils.UpdateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UpdateViewModel(private val context: Context) : ViewModel() {
    
    private val updateManager = UpdateManager(context)
    private var downloadReceiver: BroadcastReceiver? = null
    
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()
    
    private val _currentDownloadId = MutableStateFlow<Long?>(null)
    
    init {
        checkForUpdatesIfNeeded()
    }
    
    private fun checkForUpdatesIfNeeded() {
        if (!UpdatePreferences.autoCheckUpdates) return
        
        val now = System.currentTimeMillis()
        val lastCheck = UpdatePreferences.lastUpdateCheck
        val interval = UpdatePreferences.updateCheckInterval
        
        if (now - lastCheck > interval) {
            checkForUpdates()
        }
    }
    
    fun checkForUpdates() {
        if (_updateState.value is UpdateState.Checking) return
        
        viewModelScope.launch {
            _updateState.value = UpdateState.Checking
            
            try {
                val updateInfo = updateManager.checkForUpdates()
                UpdatePreferences.lastUpdateCheck = System.currentTimeMillis()
                
                if (updateInfo != null && updateInfo.version != UpdatePreferences.skipVersion) {
                    _updateState.value = UpdateState.Available(updateInfo)
                    
                    if (UpdatePreferences.autoDownloadUpdates) {
                        downloadUpdate(updateInfo)
                    }
                } else {
                    _updateState.value = UpdateState.UpToDate
                }
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun downloadUpdate(updateInfo: UpdateInfo) {
        if (_updateState.value is UpdateState.Downloading) return
        
        _updateState.value = UpdateState.Downloading(updateInfo, 0)
        
        val downloadId = updateManager.downloadUpdate(updateInfo) { progress ->
            _updateState.value = UpdateState.Downloading(updateInfo, progress)
        }
        
        _currentDownloadId.value = downloadId
        
        downloadReceiver = updateManager.registerDownloadReceiver { completedDownloadId ->
            if (completedDownloadId == downloadId) {
                _updateState.value = UpdateState.ReadyToInstall(updateInfo, downloadId)
            }
        }
    }
    
    fun installUpdate() {
        val state = _updateState.value
        if (state is UpdateState.ReadyToInstall) {
            updateManager.installUpdate(state.downloadId)
        }
    }
    
    fun skipVersion(version: String) {
        UpdatePreferences.skipVersion = version
        _updateState.value = UpdateState.Idle
    }
    
    fun dismissUpdate() {
        _updateState.value = UpdateState.Idle
    }
    
    fun cancelDownload() {
        // Cancel download logic here if needed
        _updateState.value = UpdateState.Idle
    }
    
    override fun onCleared() {
        super.onCleared()
        downloadReceiver?.let { receiver ->
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {
                // Receiver might not be registered
            }
        }
    }
}

sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    object UpToDate : UpdateState()
    data class Available(val updateInfo: UpdateInfo) : UpdateState()
    data class Downloading(val updateInfo: UpdateInfo, val progress: Int) : UpdateState()
    data class ReadyToInstall(val updateInfo: UpdateInfo, val downloadId: Long) : UpdateState()
    data class Error(val message: String) : UpdateState()
}