package com.noxapps.familygiftlist.mylists

import androidx.compose.foundation.layout.Column
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.sampleData

@Composable
fun SingleListPage(
    id: Int,
    db: AppDatabase,
    navController: NavHostController,
    singleListViewModel: SingleListViewModel = SingleListViewModel(
        id = id,
        db = db,
        navController = navController
    )
) {
    val coroutineScope = rememberCoroutineScope()

    var thisList by remember{ mutableStateOf(sampleData.nullListWithGifts) }
    LaunchedEffect(coroutineScope) {
        thisList = db.giftListDao().getOneWithListsById(id)
    }


    TopAppBar(title = { thisList.giftList.listName })

    Column {
        Text("This is a list page")
        Text("List Name = ${thisList.giftList.listName}")
        Text("List id = ${thisList.giftList.listId}")
        Text("Creator id = ${thisList.giftList.creatorId}")
        Text("Creator Name = ${thisList.giftList.creatorName}")
        Text("List description = ${thisList.giftList.description}")

    }
}