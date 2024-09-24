package com.noxapps.familygiftlist.view.mylist

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.loginCheck
import com.noxapps.familygiftlist.viewmodels.mylist.MyListViewModel

@Composable
fun MyListPage(
    context: Context,
    db:AppDatabase,
    auth: FirebaseAuth,
    navController: NavHostController,
    viewModel: MyListViewModel = MyListViewModel(context,db)
) {
    loginCheck(navController, auth)

}