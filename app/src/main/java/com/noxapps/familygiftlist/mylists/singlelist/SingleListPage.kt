package com.noxapps.familygiftlist.mylists.singlelist

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerState
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.GiftWithLists
import com.noxapps.familygiftlist.data.ListWithGifts
import com.noxapps.familygiftlist.data.User
import com.noxapps.familygiftlist.data.sampleData
import com.noxapps.familygiftlist.metafetcher.MetaFetcher
import com.noxapps.familygiftlist.mygifts.EditGiftDialogue
import com.noxapps.familygiftlist.mygifts.UnselectableListEntry
import com.noxapps.familygiftlist.mygifts.singlegift.DeleteDialogue
import com.noxapps.familygiftlist.mygifts.singlegift.MetadataCard
import com.noxapps.familygiftlist.mygifts.singlegift.MetadataPlaceholderCard
import com.noxapps.familygiftlist.mygifts.singlegift.SingleGiftViewModel
import com.noxapps.familygiftlist.mylists.EditListDialogue
import com.noxapps.familygiftlist.mylists.UnselectableGiftEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SingleListPage(
    id: Int,
    db: AppDatabase,
    user: User,
    auth: FirebaseAuth,
    navController: NavHostController,
    viewModel: SingleListViewModel = SingleListViewModel(
        id = id,
        db = db,
        auth = auth,
        navController = navController
    )
) {
    var thisList by remember{ mutableStateOf(sampleData.nullListWithGifts) }
    //var metadata by remember{ mutableStateOf<MetaFetcher?>(null)}

    val editDrawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
    val showEditDrawer by remember{ derivedStateOf { editDrawerState.isOpen } }

    val coroutineScope = rememberCoroutineScope()

    val headerSize = remember{ mutableIntStateOf(0) }
    val pageSize = remember{ mutableIntStateOf(0) }

    val reloader = remember{mutableStateOf(true)}

    val deleteDialogueState = remember{mutableStateOf(false)}

    LaunchedEffect(reloader.value) {
        Log.d("Launched effect", "reloader Triggered")
        thisList = db.giftListDao().getOneWithListsById(id)
        Log.d("pulled list", thisList.toString())


    }

    BackHandler(
        enabled = showEditDrawer
    ) {
        if(showEditDrawer){
            coroutineScope.launch{
                editDrawerState.close()
            }
        }
    }

    BottomDrawer(
        drawerState = editDrawerState,
        //drawerShape = RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp),
        drawerBackgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        gesturesEnabled = false,
        drawerContent = {
            if(showEditDrawer) {
                EditListDialogue(
                    state = editDrawerState,
                    reloader = reloader,
                    drawerSize = pageSize.intValue-headerSize.intValue,
                    scope = coroutineScope,
                    user = user,
                    navController = navController,
                    db = db,
                    initialList = thisList,
                    viewModel = viewModel
                )
            }
        }
    ){
        SingleListBody(
            pageSize = pageSize,
            headerSize = headerSize,
            thisList = thisList,
            deleteDialogueState = deleteDialogueState,
            editDrawerState = editDrawerState,
            coroutineScope = coroutineScope,
            navController = navController,
            viewModel = viewModel
        )
    }
}

@Composable
fun SingleListBody(
    pageSize: MutableState<Int>,
    headerSize: MutableState<Int>,
    thisList: ListWithGifts,
    deleteDialogueState: MutableState<Boolean>,
    editDrawerState: BottomDrawerState,
    coroutineScope: CoroutineScope,
    navController: NavHostController,
    viewModel: SingleListViewModel
){
    Column(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                pageSize.value = coordinates.size.height
            }
    ) {
        Row(
            modifier = Modifier.Companion
                .onGloballyPositioned { coordinates ->
                    headerSize.value = coordinates.size.height
                }
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(10.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.Companion
                    .padding(4.dp)
                    .align(Alignment.CenterVertically),
                text = thisList.giftList.listName,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Column(
            modifier = Modifier
                .padding(4.dp)
                .weight(1f)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = thisList.giftList.description,
                style = MaterialTheme.typography.titleLarge
            )
            /*Text(
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.End,
                text = "\$%.2f".format(thisList.gift.price.toFloat().div(100)),
                style = MaterialTheme.typography.bodyLarge
            )

             */

            Text("Gifts on this list:")
            LazyColumn(
                modifier = Modifier.Companion
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                itemsIndexed(thisList.gifts) { index, gift ->
                    UnselectableGiftEntry(
                        gift = gift,
                        navController = navController
                    )
                }
            }

        }
        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.error)
                    .clickable {
                        deleteDialogueState.value = true
                    }
                    .padding(10.dp)
            ) {
                Text(
                    modifier = Modifier.Companion
                        .padding(4.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically),
                    text = "Delete",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onError,
                    textAlign = TextAlign.Center

                )
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        coroutineScope.launch {
                            editDrawerState.open()
                        }
                    }
                    .padding(10.dp)
            ) {
                Text(
                    modifier = Modifier.Companion
                        .padding(4.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically),
                    text = "Edit",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
    if(deleteDialogueState.value){
        DeleteDialogue(deleteDialogueState) {
            viewModel.deleteList(
                listObject = thisList,
                navController = navController,
                coroutineScope = coroutineScope
            )
        }
    }
}