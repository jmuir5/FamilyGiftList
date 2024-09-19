package com.noxapps.familygiftlist.myList

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.noxapps.familygiftlist.data.AppDatabase

@Composable
fun MyListPage(
    context: Context,
    db:AppDatabase,
    navController: NavController,
    viewModel: MyListViewModel = MyListViewModel(context,db)) {

}