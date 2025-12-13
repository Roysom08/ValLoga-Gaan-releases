package app.vitune.android.ui.components.themed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.vitune.android.R
import app.vitune.android.utils.UpdateInfo
import app.vitune.android.utils.medium
import app.vitune.android.utils.semiBold
import app.vitune.core.ui.LocalAppearance

@Composable
fun UpdateAvailableDialog(
    updateInfo: UpdateInfo,
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
    onLater: () -> Unit
) {
    val (colorPalette, typography) = LocalAppearance.current
    
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colorPalette.background1)
                .padding(24.dp)
        ) {
            BasicText(
                text = "Update Available",
                style = typography.m.semiBold.copy(color = colorPalette.text),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BasicText(
                text = "Version ${updateInfo.version} is now available!",
                style = typography.s.medium.copy(color = colorPalette.text),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (updateInfo.changelog.isNotBlank()) {
                BasicText(
                    text = "What's New:",
                    style = typography.xs.semiBold.copy(color = colorPalette.text),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                BasicText(
                    text = updateInfo.changelog,
                    style = typography.xxs.medium.copy(color = colorPalette.textSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .verticalScroll(rememberScrollState())
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            val sizeInMB = updateInfo.size / (1024 * 1024)
            BasicText(
                text = "Size: ${sizeInMB}MB",
                style = typography.xxs.medium.copy(color = colorPalette.textSecondary),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                SecondaryTextButton(
                    text = "Later",
                    onClick = onLater
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                SecondaryTextButton(
                    text = "Download",
                    onClick = onDownload
                )
            }
        }
    }
}

@Composable
fun UpdateDownloadingDialog(
    updateInfo: UpdateInfo,
    progress: Int,
    onCancel: () -> Unit
) {
    val (colorPalette, typography) = LocalAppearance.current
    
    Dialog(onDismissRequest = { }) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colorPalette.background1)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicText(
                text = "Downloading Update",
                style = typography.m.semiBold.copy(color = colorPalette.text),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BasicText(
                text = "Version ${updateInfo.version}",
                style = typography.s.medium.copy(color = colorPalette.text),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LinearProgressIndicator(
                progress = progress / 100f,
                modifier = Modifier.fillMaxWidth(),
                color = colorPalette.accent
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            BasicText(
                text = "$progress%",
                style = typography.xs.medium.copy(color = colorPalette.text),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SecondaryTextButton(
                text = "Cancel",
                onClick = onCancel
            )
        }
    }
}

@Composable
fun UpdateReadyDialog(
    updateInfo: UpdateInfo,
    onInstall: () -> Unit,
    onLater: () -> Unit
) {
    val (colorPalette, typography) = LocalAppearance.current
    
    Dialog(onDismissRequest = { }) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colorPalette.background1)
                .padding(24.dp)
        ) {
            BasicText(
                text = "Update Ready",
                style = typography.m.semiBold.copy(color = colorPalette.text),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BasicText(
                text = "Version ${updateInfo.version} has been downloaded and is ready to install.",
                style = typography.s.medium.copy(color = colorPalette.text),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                SecondaryTextButton(
                    text = "Later",
                    onClick = onLater
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                SecondaryTextButton(
                    text = "Install Now",
                    onClick = onInstall
                )
            }
        }
    }
}