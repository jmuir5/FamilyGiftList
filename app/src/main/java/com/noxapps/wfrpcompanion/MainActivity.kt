package com.noxapps.wfrpcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noxapps.wfrpcompanion.Home.HomePage
import com.noxapps.wfrpcompanion.ui.theme.WFRPCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WFRPCompanionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    Box(
                        Modifier
                            .padding(innerPadding)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        NavMain(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun NavMain(navController: NavHostController){
    NavHost(navController = navController, startDestination = Paths.Home.Path){
        composable(Paths.Home.Path) { HomePage(navController = navController) }

    }
}

