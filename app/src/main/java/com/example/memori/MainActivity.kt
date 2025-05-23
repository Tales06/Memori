package com.example.memori

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
import com.example.memori.composable.ButtonNote
import com.example.memori.database.theme_data.ThemeViewModel
import com.example.memori.database.theme_data.ThemeViewModelFactory
import com.example.memori.preference.hasSeenSetup
import com.example.memori.preference.setHasSeenSetup
import com.example.memori.routes.MainNavigation
import com.example.memori.theme.MyMemoriTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

}

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

