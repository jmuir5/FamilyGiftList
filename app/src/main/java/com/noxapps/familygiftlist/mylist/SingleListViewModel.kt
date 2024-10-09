package com.noxapps.familygiftlist.mylist

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.GiftList
import kotlinx.coroutines.CoroutineScope

class SingleListViewModel(
    id:Int,
    db: AppDatabase,
    navController: NavHostController
): ViewModel() {

    //val thisList = db.giftListDao().getOneById(id)

    fun populateList(target: MutableState<GiftList>, coroutineScope: CoroutineScope){


    }
}