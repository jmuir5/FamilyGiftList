package com.noxapps.familygiftlist.myList

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.loginCheck

@Composable
fun MyListPage(
    context: Context,
    db:AppDatabase,
    auth: FirebaseAuth,
    navController: NavHostController,
    viewModel: MyListViewModel = MyListViewModel(context,db)) {
    loginCheck(navController, auth)

}