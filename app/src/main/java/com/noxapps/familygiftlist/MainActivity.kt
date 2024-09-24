package com.noxapps.familygiftlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.ui.theme.FamilyGiftListTheme
import com.noxapps.familygiftlist.view.home.HomePage
import com.noxapps.familygiftlist.view.login.LoginPage
import com.noxapps.familygiftlist.view.mylist.MyListPage

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FamilyGiftListTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                ) { innerPadding ->
                    val navController = rememberNavController()
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        NavMain(navController = navController, auth = auth)
                    }
                }
            }
        }
    }
}

@Composable
fun NavMain(navController: NavHostController, auth: FirebaseAuth){
    val context = LocalContext.current
    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "gift-app-database"
    ).build()
    NavHost(navController = navController, startDestination = Paths.Home.Path){
        composable(Paths.Home.Path) {
            HomePage(
                auth = auth,
                navController = navController
            )
        }
        composable(Paths.MyList.Path) {
            MyListPage(
                context = context,
                db = db,
                auth = auth,
                navController = navController
            )
        }
        composable(Paths.Login.Path){
            LoginPage(
                auth,
                db,
                navController
            )
        }

    }
}


fun loginCheck(navController: NavHostController, auth: FirebaseAuth){
    val currentUser = auth.currentUser
    if (currentUser == null) {
        navController.navigate(Paths.Login.Path){
            popUpTo(Paths.Login.Path){
                inclusive = true
            }
        }
    }
}

