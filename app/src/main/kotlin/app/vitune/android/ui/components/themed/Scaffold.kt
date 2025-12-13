package app.vitune.android.ui.components.themed

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Right
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import app.vitune.android.R
import app.vitune.android.preferences.UIStatePreferences
import app.vitune.core.ui.LocalAppearance
import kotlinx.collections.immutable.toImmutableList

@Composable
fun Scaffold(
    key: String,
    topIconButtonId: Int,
    onTopIconButtonClick: () -> Unit,
    tabIndex: Int,
    onTabChange: (Int) -> Unit,
    tabColumnContent: TabsBuilder.() -> Unit,
    modifier: Modifier = Modifier,
    onSearchClick: (() -> Unit)? = null,
    searchInputContent: (@Composable () -> Unit)? = null,
    tabsEditingTitle: String = stringResource(R.string.tabs),
    content: @Composable AnimatedVisibilityScope.(Int) -> Unit
) {
    val (colorPalette) = LocalAppearance.current
    var hiddenTabs by UIStatePreferences.mutableTabStateOf(key)
    
    // Get current tab title
    val tabs = TabsBuilder.rememberTabs(tabColumnContent)
    val currentTabTitle = if (tabIndex < tabs.size) tabs[tabIndex].title() else ""

    Column(
        modifier = modifier
            .background(colorPalette.background0)
            .fillMaxSize()
    ) {
        NavigationBar(
            topIconButtonId = topIconButtonId,
            onTopIconButtonClick = onTopIconButtonClick,
            tabIndex = tabIndex,
            onTabIndexChange = onTabChange,
            hiddenTabs = hiddenTabs,
            setHiddenTabs = { hiddenTabs = it.toImmutableList() },
            currentTabTitle = currentTabTitle,
            onSearchClick = onSearchClick,
            searchInputContent = searchInputContent,
            tabsEditingTitle = tabsEditingTitle,
            content = tabColumnContent
        )

        AnimatedContent(
            targetState = tabIndex,
            transitionSpec = {
                val slideDirection = if (targetState > initialState) Left else Right
                val animationSpec = spring(
                    dampingRatio = 0.9f,
                    stiffness = Spring.StiffnessLow,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                )

                ContentTransform(
                    targetContentEnter = slideIntoContainer(slideDirection, animationSpec),
                    initialContentExit = slideOutOfContainer(slideDirection, animationSpec),
                    sizeTransform = null
                )
            },
            content = content,
            label = ""
        )
    }
}
