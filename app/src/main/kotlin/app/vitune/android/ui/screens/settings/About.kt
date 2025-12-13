package app.vitune.android.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import app.vitune.android.BuildConfig
import app.vitune.android.R
import app.vitune.android.service.UpdateState
import app.vitune.android.service.UpdateViewModel
import app.vitune.android.ui.screens.Route

private val VERSION_NAME = BuildConfig.VERSION_NAME.substringBeforeLast("-")

@Route
@Composable
fun About() {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val updateViewModel = UpdateViewModel(context)
    val updateState by updateViewModel.updateState.collectAsState()
    
    SettingsCategoryScreen(
        title = stringResource(R.string.about),
        description = "v$VERSION_NAME modified by Roysom Duwarah"
    ) {
        SettingsGroup(title = "Updates") {
            SettingsEntry(
                title = "Check for updates",
                text = when (val state = updateState) {
                    is UpdateState.Checking -> "Checking for updates..."
                    is UpdateState.UpToDate -> "You're up to date!"
                    is UpdateState.Available -> "Update available: v${state.updateInfo.version}"
                    is UpdateState.Error -> "Error: ${state.message}"
                    else -> "Check for the latest version"
                },
                onClick = {
                    updateViewModel.checkForUpdates()
                }
            )
        }
        
        SettingsGroup(title = "Social") {
            SettingsEntry(
                title = "View source code",
                text = "Check out the source code on GitHub",
                onClick = {
                    uriHandler.openUri("https://github.com/Roysom08/ValLoga-Gaan-releases")
                }
            )
            
            SettingsEntry(
                title = "Original ViTune",
                text = "Visit the original ViTune project",
                onClick = {
                    uriHandler.openUri("https://github.com/25huizengek1/ViTune")
                }
            )
        }
        
        SettingsGroup(title = "License") {
            SettingsEntry(
                title = "View license",
                text = "View the app license and terms",
                onClick = {
                    uriHandler.openUri("https://github.com/Roysom08/ValLoga-Gaan-releases/blob/main/LICENSE")
                }
            )
        }
    }
}
