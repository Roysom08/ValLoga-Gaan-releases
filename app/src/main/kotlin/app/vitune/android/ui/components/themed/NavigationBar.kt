package app.vitune.android.ui.components.themed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import app.vitune.android.LocalPlayerAwareWindowInsets
import app.vitune.android.R
import app.vitune.android.ui.screens.settings.SwitchSettingsEntry
import app.vitune.android.utils.center
import app.vitune.android.utils.color
import app.vitune.android.utils.semiBold
import app.vitune.core.ui.Dimensions
import app.vitune.core.ui.LocalAppearance
import app.vitune.core.ui.utils.roundedShape
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalFoundationApi::class)
@Composable
inline fun NavigationBar(
    topIconButtonId: Int,
    noinline onTopIconButtonClick: () -> Unit,
    tabIndex: Int,
    crossinline onTabIndexChange: (Int) -> Unit,
    hiddenTabs: ImmutableList<String>,
    crossinline setHiddenTabs: (List<String>) -> Unit,
    currentTabTitle: String = "",
    noinline onSearchClick: (() -> Unit)? = null,
    noinline searchInputContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    tabsEditingTitle: String = stringResource(R.string.tabs),
    crossinline content: TabsBuilder.() -> Unit
) {
    val (colorPalette, typography) = LocalAppearance.current

    val tabs = TabsBuilder.rememberTabs(content)

    val paddingValues = LocalPlayerAwareWindowInsets.current
        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
        .asPaddingValues()

    var editing by remember { mutableStateOf(false) }

    if (editing) DefaultDialog(
        onDismiss = { editing = false },
        horizontalPadding = 0.dp
    ) {
        BasicText(
            text = tabsEditingTitle,
            style = typography.s.center.semiBold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn {
            items(
                items = tabs,
                key = { it.key }
            ) { tab ->
                SwitchSettingsEntry(
                    title = tab.title(),
                    text = null,
                    isChecked = tab.key !in hiddenTabs,
                    onCheckedChange = {
                        if (!it && hiddenTabs.size == tabs.size - 1) return@SwitchSettingsEntry

                        setHiddenTabs(if (it) hiddenTabs - tab.key else hiddenTabs + tab.key)
                    },
                    isEnabled = tab.canHide && (tab.key in hiddenTabs || hiddenTabs.size < tabs.size - 1)
                )
            }
        }
    }

    Column(
        modifier = modifier
            .background(colorPalette.background0)
            .fillMaxWidth()
            .padding(paddingValues)
    ) {
        // Top bar with title on left, search and settings/back button on right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Page title on the left
            BasicText(
                text = currentTabTitle,
                style = typography.l.semiBold.color(colorPalette.text),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Search input field or search icon
            if (searchInputContent != null) {
                Box {
                    searchInputContent()
                }
            } else {
                onSearchClick?.let { searchClick ->
                    Image(
                        painter = painterResource(R.drawable.search),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorPalette.textSecondary),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(onClick = searchClick)
                            .padding(all = 12.dp)
                            .size(22.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Back/Settings button on the right
            Image(
                painter = painterResource(topIconButtonId),
                contentDescription = null,
                colorFilter = ColorFilter.tint(colorPalette.textSecondary),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onTopIconButtonClick)
                    .padding(all = 12.dp)
                    .size(22.dp)
            )
        }

        // Scrollable horizontal tabs
        val transition = updateTransition(targetState = tabIndex, label = null)
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            itemsIndexed(tabs) { index, tab ->
                AnimatedVisibility(
                    visible = tabIndex == index || tab.key !in hiddenTabs,
                    label = ""
                ) {
                    val backgroundColor by transition.animateColor(label = "") {
                        if (it == index) colorPalette.accent else colorPalette.background2
                    }

                    val textColor by transition.animateColor(label = "") {
                        if (it == index) colorPalette.onAccent else colorPalette.text
                    }

                    val iconColor by transition.animateColor(label = "") {
                        if (it == index) colorPalette.onAccent else colorPalette.textSecondary
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(20.dp.roundedShape)
                            .background(backgroundColor)
                            .combinedClickable(
                                onClick = { onTabIndexChange(index) },
                                onLongClick = { editing = true }
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(tab.icon),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(iconColor),
                            modifier = Modifier.size(18.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(6.dp))
                        
                        BasicText(
                            text = tab.title(),
                            style = typography.xs.semiBold.color(textColor),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}