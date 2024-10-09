package com.noxapps.familygiftlist

import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.ui.theme.FamilyGiftListTheme
import com.noxapps.familygiftlist.home.HomePage
import com.noxapps.familygiftlist.auth.LoginPage
import com.noxapps.familygiftlist.data.User
import com.noxapps.familygiftlist.mylist.MyListsPage
import com.noxapps.familygiftlist.mylist.SingleListPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDate

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
    val coroutineScope = rememberCoroutineScope()
    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "gift-app-database"
    ).fallbackToDestructiveMigration().build()
    val currentUser = remember {
        mutableStateOf(
            User(
                "0",
                "Empty user",
                "Empty",
                "User",
                LocalDate.now()
            )
        )
    }
    auth.currentUser?.uid?.let { setUser(currentUser, db, it, coroutineScope) }
    NavHost(navController = navController, startDestination = Paths.Home.Path){
        composable(Paths.Home.Path) {
            HomePage(
                auth = auth,
                currentUser= currentUser.value,
                navController = navController
            )
        }
        composable(Paths.MyList.Path) {
            MyListsPage(
                context = context,
                db = db,
                auth = auth,
                user = currentUser.value,
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
        composable(
            route = "${Paths.SingleList.Path}/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.IntType }),

            ){
            val listId = it.arguments?.getInt("listId")
            if (listId != null) {
                SingleListPage(
                    listId,
                    db,
                    navController
                )
            }
            else{
                //todo: error code
            }

        }


    }
}


fun loginCheck(navController: NavHostController, auth: FirebaseAuth){
    val currentUser = auth.currentUser
    currentUser?.uid?.let { Log.e("Current user", it) }
    if (currentUser == null) {
        navController.navigate(Paths.Login.Path){
            popUpTo(Paths.Login.Path){
                inclusive = true
            }
        }
    }
}

fun setUser(currentUser: MutableState<User>, db:AppDatabase, userId:String, coroutineScope: CoroutineScope){
    coroutineScope.launch {
        val user = db.userDao().getOneById(userId)
        MainScope().launch {
            currentUser.value = user
        }
    }
}

/* todo checklist
 - implement gemini nano to analyse web pages

 */