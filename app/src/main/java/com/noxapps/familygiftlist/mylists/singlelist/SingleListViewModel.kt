package com.noxapps.familygiftlist.mylists.singlelist

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.familygiftlist.data.AppDatabase

class SingleListViewModel(
    id:Int,
    db: AppDatabase,
    auth: FirebaseAuth,
    navController: NavHostController
): ViewModel() {


}