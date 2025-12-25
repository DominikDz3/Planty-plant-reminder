package pl.edu.ur.dd131428.planty

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pl.edu.ur.dd131428.planty.notifications.NotificationService
import pl.edu.ur.dd131428.planty.ui.screens.add_edit.AddEditPlantScreen
import pl.edu.ur.dd131428.planty.ui.screens.details.PlantDetailsScreen
import pl.edu.ur.dd131428.planty.ui.screens.home.HomeScreen
import pl.edu.ur.dd131428.planty.ui.screens.settings.SettingsScreen
import pl.edu.ur.dd131428.planty.ui.screens.splash.SplashScreen
import pl.edu.ur.dd131428.planty.ui.settings.SettingsViewModel
import pl.edu.ur.dd131428.planty.ui.PlantyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationService = NotificationService(this)
        notificationService.createNotificationChannel()

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Companion.Factory)
            val themeMode by settingsViewModel.themeMode.collectAsState()

            val darkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            PlantyTheme(
                darkTheme = darkTheme,
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val navController = rememberNavController()

                    LaunchedEffect(Unit) {
                        val plantIdFromNotif = intent.getIntExtra("plantId_from_notification", -1)
                        if (plantIdFromNotif != -1) {
                            navController.navigate("details/$plantIdFromNotif")
                        }
                    }

                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted ->
                            if (!isGranted) {
                                Toast.makeText(
                                    context,
                                    "Bez zgody nie otrzymasz przypomnień",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )

                    LaunchedEffect(Unit) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) !=
                                PackageManager.PERMISSION_GRANTED
                            ) {
                                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }

                    NavHost(navController = navController, startDestination = "splash") {

                        composable("splash") {
                            SplashScreen(
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                onNavigateToAddPlant = {
                                    navController.navigate("add_plant")
                                },
                                onNavigateToSettings = {
                                    navController.navigate("settings")
                                },
                                onNavigateToDetails = { plantId ->
                                    navController.navigate("details/$plantId")
                                }
                            )
                        }

                        composable("add_plant") {
                            AddEditPlantScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("settings") {
                            SettingsScreen(
                                viewModel = settingsViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(
                            route = "details/{plantId}",
                            arguments = listOf(navArgument("plantId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val plantId = backStackEntry.arguments?.getInt("plantId") ?: 0
                            PlantDetailsScreen(
                                plantId = plantId,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}