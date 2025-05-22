package com.example.memori.composable

import android.net.Uri
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.memori.MainScreen
import com.example.memori.data.icons
import com.example.memori.setup.SetupPage
import com.example.memori.setup.ThemeSetupPage
import com.example.memori.setup.ThemeType

@ExperimentalMaterial3Api
@Composable
fun BottomBar(
    navController: NavHostController,
    showBottomBar: Boolean
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination?.route

    val bottomBarRoutes = listOf("home", "favorites", "settings")



    if (bottomBarRoutes.any { currentDestination?.startsWith(it) == true } && showBottomBar) {

        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {

            icons.forEach { bottomNavItem ->
                val selected = currentDestination == bottomNavItem.route
                NavigationBarItem(

                    selected = selected,
                    onClick = {
                        bottomNavItem.route?.let {
                            navController.navigate(it) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (selected) bottomNavItem.selectedIcon else bottomNavItem.unselectedIcon,
                            contentDescription = null,
                            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.6f
                            )
                        )


                    },
                    label = {
                        Text(text = bottomNavItem.title)
                    }

                )

            }
        }
    }


}


