package com.noxapps.familygiftlist.mygifts

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.noxapps.familygiftlist.data.AppDatabase

class SingleGiftViewModel(
    id:Int,
    db: AppDatabase,
    navController: NavHostController
): ViewModel() {
}