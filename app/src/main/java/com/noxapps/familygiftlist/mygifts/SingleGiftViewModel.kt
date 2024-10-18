package com.noxapps.familygiftlist.mygifts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.Gift
import com.noxapps.familygiftlist.data.GiftWithLists
import com.noxapps.familygiftlist.data.sampleData
import com.noxapps.familygiftlist.metafetcher.MetaFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class SingleGiftViewModel(
     id:Int,
     db: AppDatabase,
     navController: NavHostController
): ViewModel() {


    //val metadata = MetaFetcher()
    /*companion object {
        suspend operator fun invoke(id:Int,
                                    db: AppDatabase,
                                    navController: NavHostController
        ): SingleGiftViewModel {
            val thisGift = db.giftDao().getOneWithListsById(id)

            return SingleGiftViewModel(thisGift, db, navController)
        }
    }*/
}