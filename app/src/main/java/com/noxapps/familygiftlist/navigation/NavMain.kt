package com.noxapps.familygiftlist.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.familygiftlist.auth.LoginPage
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.sampleData
import com.noxapps.familygiftlist.home.HomePage
import com.noxapps.familygiftlist.mygifts.MyGiftsPage
import com.noxapps.familygiftlist.mygifts.SingleGiftPage
import com.noxapps.familygiftlist.mylists.MyListsPage
import com.noxapps.familygiftlist.mylists.SingleListPage

@Composable
fun NavMain(navController: NavHostController, auth: FirebaseAuth){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "gift-app-database"
    ).fallbackToDestructiveMigration().build()
    val currentUser = remember { mutableStateOf( sampleData.nullUser ) }
    LaunchedEffect(auth.currentUser) {
        try{
            auth.currentUser?.uid?.let { currentUser.value = db.userDao().getOneById(it) }
        }
        catch(e: Exception){
            Log.e("login check failed", "login Error")
        }

    }

    //auth.currentUser?.uid?.let { setUser(currentUser, db, it, coroutineScope) }
    NavHost(navController = navController, startDestination = Paths.Home.Path) {
        composable(Paths.Home.Path) {
            HomePage(
                auth = auth,
                currentUser = currentUser.value,
                db = db,
                navController = navController
            )
        }
        composable(Paths.Login.Path) {
            LoginPage(
                auth,
                currentUser.value,
                db,
                navController
            )
        }
        composable(Paths.MyLists.Path) {
            MyListsPage(
                db = db,
                auth = auth,
                user = currentUser.value,
                navController = navController
            )
        }
        composable(Paths.MyGifts.Path) {
            MyGiftsPage(
                db = db,
                auth = auth,
                user = currentUser.value,
                navController = navController
            )
        }
        composable(
            route = "${Paths.SingleList.Path}/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.IntType })
        ) {
            val listId = it.arguments?.getInt("listId")
            if (listId != null) {
                SingleListPage(
                    listId,
                    db,
                    navController
                )
            } else {
                //todo: error code
            }

        }
        composable(
            route = "${Paths.SingleGift.Path}/{giftId}",
            arguments = listOf(navArgument("giftId") { type = NavType.IntType })
        ) {
            val giftId = it.arguments?.getInt("giftId")
            if (giftId != null) {
                SingleGiftPage(
                    giftId,
                    db = db,
                    user = currentUser.value,
                    auth = auth,
                    navController = navController
                )
            } else {
                //todo: error code
            }

        }


    }
}