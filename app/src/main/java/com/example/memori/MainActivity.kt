package com.example.memori

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Modifier
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.memori.ui.theme.MemoriTheme
import kotlinx.serialization.Serializable



class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MemoriTheme {
                // A surface container using the 'background' color from the theme


                Surface (
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxSize(),
                    contentColor = MaterialTheme.colorScheme.onSecondary,




                ) {
                    MainScreen(navController = navController)

                }

            }

        }

    }
    private fun onClick(){
        println("Ciao")
    }
}

@ExperimentalMaterial3Api
@Composable
fun MainScreen(navController: NavHostController){

    Scaffold(
        modifier = Modifier.fillMaxSize().padding(WindowInsets.navigationBars.asPaddingValues()),
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntry?.destination?.route //obtain the current route

            if(currentDestination !in listOf("pageNotes")){
                BottomBar(navController)
            }


        }
    ) {

        innerPadding -> Navigation(navController, modifier = Modifier.padding(innerPadding))

    }
}

