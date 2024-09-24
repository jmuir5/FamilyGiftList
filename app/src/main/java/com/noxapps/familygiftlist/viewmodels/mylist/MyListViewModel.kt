package com.noxapps.familygiftlist.viewmodels.mylist

import android.content.Context
import androidx.lifecycle.ViewModel
import com.noxapps.familygiftlist.data.AppDatabase

class MyListViewModel(context: Context, db:AppDatabase): ViewModel() {
    private val giftListDao = db.giftListDao()
    private val giftDao = db.giftDao()
    private val allLists = giftListDao.getAllWithGifts()
    var currentListIndex = 1
    var currentList = allLists.keys.toList()[currentListIndex]


}