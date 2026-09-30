package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressTrackerScreen
import com.example.ui.screens.WorkoutLoggerScreen
import com.example.ui.screens.WorkoutPlannerScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.viewmodel.FitPulseViewModel

enum class FitnessNavTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    DASHBOARD("Activity", Icons.Default.DirectionsRun, Icons.Outlined.DirectionsRun, "nav_tab_dashboard"),
    PLANNER("Planner", Icons.Default.EventNote, Icons.Outlined.EventNote, "nav_tab_planner"),
    LOGGER("Workout", Icons.Default.FitnessCenter, Icons.Outlined.FitnessCenter, "nav_tab_logger"),
    PROGRESS("Progress", Icons.Default.ShowChart, Icons.Outlined.ShowChart, "nav_tab_progress"),
    PROFILE("Profile", Icons.Default.Person, Icons.Outlined.Person, "nav_tab_profile")
}

class MainActivity : ComponentActivity() {

    private val viewModel: FitPulseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                FitPulseApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FitPulseApp(
    viewModel: FitPulseViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by rememberSaveable { mutableStateOf(FitnessNavTab.DASHBOARD) }
    val activeSession by viewModel.activeWorkoutSession.collectAsStateWithLifecycle()

    BackHandler(enabled = currentTab != FitnessNavTab.DASHBOARD) {
        currentTab = FitnessNavTab.DASHBOARD
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        containerColor = DarkBackground,
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier
                    .testTag("main_navigation_bar")
            ) {
                FitnessNavTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    val hasActiveSessionBadge = tab == FitnessNavTab.LOGGER && activeSession != null

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            if (hasActiveSessionBadge) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = NeonGreen,
                                            contentColor = Color.Black,
                                            modifier = Modifier.size(8.dp)
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label
                                )
                            }
                        },
                        label = {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = NeonGreen,
                            indicatorColor = NeonGreen,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextTertiary
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Crossfade(
                targetState = currentTab,
                label = "ScreenTransition"
            ) { tab ->
                when (tab) {
                    FitnessNavTab.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToLogger = { currentTab = FitnessNavTab.LOGGER },
                        onNavigateToPlanner = { currentTab = FitnessNavTab.PLANNER }
                    )
                    FitnessNavTab.PLANNER -> WorkoutPlannerScreen(
                        viewModel = viewModel,
                        onNavigateToLogger = { currentTab = FitnessNavTab.LOGGER }
                    )
                    FitnessNavTab.LOGGER -> WorkoutLoggerScreen(
                        viewModel = viewModel
                    )
                    FitnessNavTab.PROGRESS -> ProgressTrackerScreen(
                        viewModel = viewModel
                    )
                    FitnessNavTab.PROFILE -> ProfileScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
