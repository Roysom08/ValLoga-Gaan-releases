package app.vitune.android.preferences

import app.vitune.android.GlobalPreferencesHolder

object UpdatePreferences : GlobalPreferencesHolder() {
    var autoCheckUpdates by boolean(true)
    var autoDownloadUpdates by boolean(false)
    var lastUpdateCheck by long(0L)
    var skipVersion by string("")
    var updateCheckInterval by long(24 * 60 * 60 * 1000L) // 24 hours in milliseconds
}