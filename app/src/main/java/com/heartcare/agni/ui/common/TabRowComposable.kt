package com.heartcare.agni.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabRowComposable(
    tabs: List<String>,
    pagerState: PagerState,
    onClick: (Int) -> Unit
) {
    InternalTabRowComposable(
        tabs = tabs,
        pagerState = pagerState,
        onClick = onClick,
        scrollable = false
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollableTabRowComposable(
    tabs: List<String>,
    pagerState: PagerState,
    onClick: (Int) -> Unit
) {
    InternalTabRowComposable(
        tabs = tabs,
        pagerState = pagerState,
        onClick = onClick,
        scrollable = true
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InternalTabRowComposable(
    tabs: List<String>,
    pagerState: PagerState,
    onClick: (Int) -> Unit,
    scrollable: Boolean
) {
    val density = LocalDensity.current
    val tabWidths = remember(tabs.size) {
        mutableStateListOf<Dp>().apply {
            repeat(tabs.size) { add(0.dp) }
        }
    }

    val indicator: @Composable (List<TabPosition>) -> Unit = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.customTabIndicatorOffset(
                currentTabPosition = tabPositions[pagerState.currentPage],
                tabWidth = tabWidths.getOrElse(pagerState.currentPage) { 0.dp }
            )
        )
    }

    val tabContent: @Composable () -> Unit = {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = { onClick(index) },
                modifier = Modifier.testTag(title.uppercase()),
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                text = {
                    Text(
                        text = title,
                        onTextLayout = { result ->
                            tabWidths[index] = with(density) { result.size.width.toDp() }
                        }
                    )
                }
            )
        }
    }

    if (scrollable) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
            indicator = indicator
        ) {
            tabContent()
        }
    } else {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.testTag("TABS"),
            indicator = indicator
        ) {
            tabContent()
        }
    }
}
