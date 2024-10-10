package com.noxapps.familygiftlist.navigation

import androidx.compose.runtime.MutableState
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun setUser(currentUser: MutableState<User>, db: AppDatabase, userId:String, coroutineScope: CoroutineScope){
    coroutineScope.launch {
        val user = db.userDao().getOneById(userId)
        MainScope().launch {
            currentUser.value = user
        }
    }
}