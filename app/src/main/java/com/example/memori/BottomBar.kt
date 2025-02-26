package com.example.memori

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@ExperimentalMaterial3Api
@Composable
fun BottomBar(
    navController: NavHostController
){
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination?.route

    val bottomBarRoutes = listOf("home", "favorites", "settings")
    Scaffold (

        bottomBar = {
            if(bottomBarRoutes.any {currentDestination?.startsWith(it) == true}){

                NavigationBar {
                    icons.forEach{bottomNavItem ->
                        val selected = currentDestination == bottomNavItem.route
                        NavigationBarItem(

                            selected = selected,
                            onClick = {
                                bottomNavItem.route?.let {
                                    navController.navigate(it){
                                        popUpTo(navController.graph.findStartDestination().id) {saveState = true}
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) bottomNavItem.selectedIcon else bottomNavItem.unselectedIcon,
                                    contentDescription = null
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
    ){
        innerPadding ->
        Navigation(navController = navController, modifier = Modifier.padding(innerPadding))


    }


}


@ExperimentalMaterial3Api
@Composable
fun Navigation(navController: NavHostController, modifier: Modifier){
    NavHost(navController = navController, startDestination = "home", modifier = modifier) {
        composable("home") {

            HomeScreen(navController)
        }
        composable("favorites") {
            FavoritesScreen(navController)
        }
        composable("settings") {
            SettingsScreen()
        }
        composable("pageNotes") {
            ScreenNotes(navController)

        }
        composable(
            route = "modifiedNotes/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType})
        ) {
            backStackEntry -> val id = backStackEntry.arguments?.getInt("id") ?: -1
            if(id != -1){
                ScreenModifiedNotes(id = id, navController)
            } else {
                Log.e("Nav","Id è -1")
            }
        }

    }
}