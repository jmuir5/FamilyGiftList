package com.noxapps.familygiftlist.navigation

import android.util.Log
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.familygiftlist.data.User
import com.noxapps.familygiftlist.data.sampleData

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

fun loggedCheck(navController: NavHostController, auth: FirebaseAuth, loggedUser:User?){
    val currentUser = auth.currentUser
    currentUser?.uid?.let { Log.e("Current user", it) }
    if (currentUser != null && loggedUser!=null &&loggedUser!= sampleData.nullUser) {
        navController.navigate(Paths.Home.Path){
            popUpTo(Paths.Login.Path){
                inclusive = true
            }
        }
    }
}