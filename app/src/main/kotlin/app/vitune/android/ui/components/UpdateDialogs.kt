package app.vitune.android.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.vitune.android.service.UpdateState
import app.vitune.android.service.UpdateViewModel
import app.vitune.android.ui.components.themed.UpdateAvailableDialog
import app.vitune.android.ui.components.themed.UpdateDownloadingDialog
import app.vitune.android.ui.components.themed.UpdateReadyDialog

@Composable
fun UpdateDialogs(updateViewModel: UpdateViewModel) {
    val updateState by updateViewModel.updateState.collectAsState()
    
    when (val state = updateState) {
        is UpdateState.Available -> {
            UpdateAvailableDialog(
                updateInfo = state.updateInfo,
                onDismiss = { updateViewModel.dismissUpdate() },
                onDownload = { updateViewModel.downloadUpdate(state.updateInfo) },
                onLater = { updateViewModel.dismissUpdate() }
            )
        }
        
        is UpdateState.Downloading -> {
            UpdateDownloadingDialog(
                updateInfo = state.updateInfo,
                progress = state.progress,
                onCancel = { updateViewModel.cancelDownload() }
            )
        }
        
        is UpdateState.ReadyToInstall -> {
            UpdateReadyDialog(
                updateInfo = state.updateInfo,
                onInstall = { updateViewModel.installUpdate() },
                onLater = { updateViewModel.dismissUpdate() }
            )
        }
        
        else -> {
            // No dialog needed for other states
        }
    }
}