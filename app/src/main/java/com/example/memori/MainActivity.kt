package com.example.memori

import com.example.memori.composable.ButtonNote
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.memori.composable.BottomBar
import com.example.memori.database.theme_data.ThemeViewModel
import com.example.memori.database.theme_data.ThemeViewModelFactory
import com.example.memori.preference.hasSeenSetup
import com.example.memori.preference.setHasSeenSetup
import com.example.memori.routes.MainNavigation
import com.example.memori.sync.NetworkStatusTracker
import com.example.memori.theme.MyMemoriTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.core.content.edit


/**
 * MainActivity is the entry point of the Memori application.
 *
 * This activity sets up the Compose UI, initializes navigation, manages theme selection,
 * and determines whether to show the welcome/setup screen based on user preferences.
 *
 * Key Features:
 * - Uses Jetpack Compose for UI rendering.
 * - Observes the selected theme from [ThemeViewModel] and applies it dynamically.
 * - Checks if the user has completed the setup process and conditionally displays the welcome screen.
 * - Sets up the main navigation controller and coroutine scope for use throughout the app.
 *
 * Experimental APIs:
 * - Utilizes [ExperimentalMaterial3Api] and [ExperimentalMaterial3ExpressiveApi] for Material 3 components.
 *
 * @see ThemeViewModel
 * @see MyMemoriTheme
 * @see MainScreen
 */
/**
 * Il punto di ingresso principale dell'applicazione Memori.
 *
 * Questa activity è responsabile dell'inizializzazione dell'interfaccia utente dell'app
 * e della gestione dei principali eventi del ciclo di vita.
 * Estende [ComponentActivity], che fornisce il supporto di compatibilità per i componenti Android moderni.
 */
/**
 * The main entry point of the Memori application.
 *
 * This activity is responsible for initializing the app's UI and handling the main lifecycle events.
 */
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkStatusTracker.registerNetworkCallback(this)
        resetToastShown(this, "offline_warning", "login_successful")
        setContent {
            val context = applicationContext
            val navController = rememberNavController()
            val scope = rememberCoroutineScope()


            var isLoaded by remember { mutableStateOf(false) }
            var showWelcome by remember { mutableStateOf(false) }


            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModelFactory(context.applicationContext as Application)
            )

            val selectedTheme by themeViewModel.selectedTheme.collectAsStateWithLifecycle()


            /**
             * Launches a coroutine when the composable enters the composition.
             * - Sets [showWelcome] to true if the user has not completed the setup, based on [context.hasSeenSetup()].
             * - Sets [isLoaded] to true to indicate that the loading process is complete.
             */
            LaunchedEffect(Unit) {
                showWelcome = !context.hasSeenSetup()
                isLoaded = true
            }

            if (isLoaded) {
                MyMemoriTheme(themeType = selectedTheme) {

                    Surface(
                        color = MaterialTheme.colorScheme.background
                    ) {

                        MainScreen(navController, context, scope, showWelcome)


                    }
                }
                // A surface container using the 'background' color from the theme

            }

        }

    }
    fun resetToastShown(context: Context, key1: String, key2: String) {
        context.getSharedPreferences(key1, Context.MODE_PRIVATE)
            .edit {
                putBoolean(key1, false)
            }
        context.getSharedPreferences(key2, Context.MODE_PRIVATE)
            .edit {
                putBoolean(key2, false)
            }
    }



}

/**
 * MainScreen is the primary composable function for the application's main UI.
 *
 * This function sets up the main scaffold, including the bottom navigation bar and floating action button,
 * and manages navigation between different screens using the provided [NavHostController].
 *
 * @param navController The navigation controller used to manage app navigation.
 * @param context The context used for accessing application resources.
 * @param scope The coroutine scope for launching asynchronous operations.
 * @param showWelcome Boolean flag indicating whether to show the welcome screen.
 *
 * The function also manages the state for search expansion and theme selection,
 * and conditionally displays UI elements based on the current navigation destination.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@ExperimentalMaterial3Api
@Composable
fun MainScreen(
    navController: NavHostController,
    context: Context,
    scope: CoroutineScope,
    showWelcome: Boolean,
) {
    var searchExpanded by rememberSaveable { mutableStateOf(false) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination?.route //obtain the current route
    val themeViewModel = remember { ThemeViewModel(context.applicationContext as Application) }


    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {

            if (currentDestination in listOf("home", "favorites", "settings") && !searchExpanded) {
                BottomBar(navController, showBottomBar = true)
            }


        },
        floatingActionButton = {
            if (currentDestination == "home" && !searchExpanded) {
                ButtonNote(navController)
            }
        }
    ) {

            innerPadding ->
        MainNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            showWelcome = showWelcome,
            onThemeSelected = { selectedTheme ->

                themeViewModel.setTheme(selectedTheme)

            },
            searchExpanded = searchExpanded,
            onSearchExpanded = { searchExpanded = it },
            context = context,
        )


    }
}

