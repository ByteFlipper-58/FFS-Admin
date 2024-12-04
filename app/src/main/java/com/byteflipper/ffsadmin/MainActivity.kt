package com.byteflipper.ffsadmin

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.byteflipper.ffsadmin.ui.screens.FabLocaleAction
import com.byteflipper.ffsadmin.ui.screens.LocaleScreen
import com.byteflipper.ffsadmin.ui.screens.RemoteScreen
import com.byteflipper.ffsadmin.ui.screens.SetupScreen
import com.byteflipper.ffsadmin.ui.theme.FFSAdminTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FFSAdminTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AppContent() {
    val context = LocalContext.current

    var isFirstLaunch by remember {
        mutableStateOf(isFirstLaunch(context))
    }

    if (isFirstLaunch) {
        SetupScreen(
            onSetupComplete = {
                markFirstLaunchComplete(context)
                isFirstLaunch = false
            }
        )
    } else {
        MainScreenWithFloatingTabs()
    }
}

private fun isFirstLaunch(context: Context): Boolean {
    val prefs = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    return prefs.getBoolean("first_launch", true)
}

// Mark first launch as completed
private fun markFirstLaunchComplete(context: Context) {
    val prefs = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("first_launch", false).apply()
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreenWithFloatingTabs() {
    val tabs = listOf(
        TabItem(
            title = "Remote",
            icon = painterResource(id = R.drawable.database_24px),
            content = { RemoteScreen() },
            fab = {  }
        ),
        TabItem(
            title = "Locale",
            icon = painterResource(id = R.drawable.storage_24px),
            content = { LocaleScreen() },
            fab = { FabLocaleAction() }
        )
    )

    val pagerState = rememberPagerState { tabs.size }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            tabs[pagerState.currentPage].fab()
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(top = 80.dp)
            ) { page ->
                tabs[page].content()
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = 36.dp)
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50.dp)
                    )
                    .clip(RoundedCornerShape(50.dp)),
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.clip(RoundedCornerShape(50.dp)),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = tab.title,
                                    color = if (pagerState.currentPage == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            icon = {
                                Icon(
                                    painter = tab.icon,
                                    contentDescription = tab.title,
                                    tint = if (pagerState.currentPage == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

data class TabItem(
    val title: String,
    val icon: Painter,
    val content: @Composable () -> Unit,
    val fab: @Composable () -> Unit
)

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    FFSAdminTheme {
        MainScreenWithFloatingTabs()
    }
}