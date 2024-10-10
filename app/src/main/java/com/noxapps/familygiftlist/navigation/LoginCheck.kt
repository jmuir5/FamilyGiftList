package com.noxapps.familygiftlist.navigation

import android.util.Log
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

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