package com.noxapps.familygiftlist.mygifts.singlegift

import android.util.Log
import androidx.compose.material.BottomDrawerState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.FirebaseDBInteractor
import com.noxapps.familygiftlist.data.Gift
import com.noxapps.familygiftlist.data.GiftListGiftCrossReference
import com.noxapps.familygiftlist.data.GiftWithLists
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SingleGiftViewModel(
     val id:Int,
     val db: AppDatabase,
     val auth: FirebaseAuth,
     val navController: NavHostController

): ViewModel() {

     fun saveGift(
          enabledState: MutableState<Boolean>,
          reloader: MutableState<Boolean>,
          drawerState: BottomDrawerState,
          giftObject: Gift,
          initialRelationships:List<Int>,
          coroutineScope: CoroutineScope
     ){
          enabledState.value = false
          try {
               //local
               coroutineScope.launch {
                    db.giftDao().update(giftObject)
                    var oldRelationships = db.referenceDao().getByGiftId(giftObject.giftId)
                    oldRelationships.forEach { relationship ->
                         db.referenceDao().delete(
                              GiftListGiftCrossReference(
                                   listId = relationship.listId,
                                   giftId = giftObject.giftId
                              )
                         )
                    }
                    initialRelationships.forEach { listId ->
                         db.referenceDao().insert(
                              GiftListGiftCrossReference(
                                   listId = listId,
                                   giftId = giftObject.giftId
                              )
                         )

                    }

                    //to server
                    //val newGift = Gift(giftObject, giftObject.giftId)
                    FirebaseDBInteractor.upsertGift(auth.currentUser?.uid!!, giftObject, initialRelationships)

                    enabledState.value = true
                    //coroutineScope.launch {
                         drawerState.close()
                    //}
                    MainScope().launch {
                         reloader.value = !reloader.value
                         enabledState.value=true
                         //drawerState.close()
                    }
               }

          }
          catch(e:Exception){
               Log.d("MyList", "function failed")
               Log.e("CreateList error", e.toString())
          }

     }

     fun deleteGift(
          //enabledState: MutableState<Boolean>,
          //drawerState: BottomDrawerState,
          giftObject: GiftWithLists,
          navController:NavHostController,
          coroutineScope: CoroutineScope
     ){
          auth.uid?.let {
               FirebaseDBInteractor.deleteGift(it, giftObject)
               coroutineScope.launch {
                    db.giftDao().delete(giftObject.gift)
                    val references = db.referenceDao().getByGiftId(giftObject.gift.giftId)
                    references.forEach{ it ->
                         db.referenceDao().delete(it)
                    }
                    MainScope().launch {
                         navController.popBackStack()
                    }
               }

          }



          //local database
          //gift
          //relationships

          //firebase
          //gift
          //relationships
     }
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