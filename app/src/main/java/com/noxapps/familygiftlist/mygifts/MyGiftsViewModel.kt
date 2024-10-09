package com.noxapps.familygiftlist.mygifts

import android.content.Context
import android.util.Log
import androidx.compose.material.BottomDrawerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.noxapps.familygiftlist.Paths
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.Gift
import com.noxapps.familygiftlist.data.GiftList
import com.noxapps.familygiftlist.data.GiftListGiftCrossReference
import com.noxapps.familygiftlist.data.sampleData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MyGiftsViewModel(val context: Context, val db:AppDatabase, val auth: FirebaseAuth): ViewModel() {
    //private val giftListDao = db.giftListDao()
    //private val giftDao = db.giftDao()
    //private val allLists = giftListDao.getAllWithGifts()
    //var currentListIndex = 1
    //var currentList = allLists.keys.toList()[currentListIndex]
    val firebaseDB = Firebase.database.reference
    val previewListOfGifts = (0..10).map{ sampleData.gift}
    val previewListOfLists = (0..10).map{sampleData.list}
    val emptyListOfGifts = listOf<Gift>()
    val emptyListOfLists = listOf<GiftList>()

    var listOfLists = listOf<GiftList>()
    init{

    }
    /*fun populateListOfLists(target: SnapshotStateList<GiftList>, flag:MutableState<Boolean>){
        target.clear()
        viewModelScope.launch {
            val lists = db.giftListDao().getAll()
            MainScope().launch {
                lists.forEach {
                    target.add(it)
                }
                flag.value = true
            }

        }
    }*/


    fun populateListOfGifts(target: SnapshotStateList<Gift>, boolList: SnapshotStateList<MutableState<Boolean>>, flag:MutableState<Boolean>){
        target.clear()
        boolList.clear()
        viewModelScope.launch {
            val lists = db.giftDao().getAll()
            MainScope().launch {
                lists.forEach {
                    target.add(it)
                    boolList.add(mutableStateOf(false))
                }
                flag.value = true
            }

        }
    }

    fun saveList(
        enabledState: MutableState<Boolean>,
        drawerState: BottomDrawerState,
        listObject: GiftList,
        initialGifts:List<Int>,
        navController:NavHostController,
        coroutineScope: CoroutineScope
    ){
        Log.d("MyList", "function Started")
        enabledState.value = false
        try {
            //local
            Log.d("MyList", "local started")
            coroutineScope.launch {
                db.giftListDao().insertAll(listObject)
                initialGifts.forEach { giftId ->
                    db.referenceDao().insertAll(
                        GiftListGiftCrossReference(
                            listId = listObject.listId,
                            giftId = giftId
                        )
                    )
                }
            }
            Log.d("MyList", "local finished, server started")
            //to server
            firebaseDB
                .child("${auth.currentUser?.uid}")
                .child("Lists")
                .child("${listObject.listId}")
                .setValue(listObject)
            initialGifts.forEach { giftId ->
                firebaseDB
                    .child("${auth.currentUser?.uid}")
                    .child("Relationships")
                    .child("${listObject.listId}")
                    .child("$giftId")
                    .setValue(true)
            }
        }
        catch(e:Exception){
            Log.d("MyList", "function failed")
            Log.e("CreateList error", e.toString())
        }
        enabledState.value = true
        coroutineScope.launch {
            drawerState.close()
        }
        Log.d("MyList", "function finished")
        navController.navigate(Paths.SingleList.Path)

    }



}