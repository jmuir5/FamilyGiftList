package com.noxapps.familygiftlist.mylists.singlelist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.User
import com.noxapps.familygiftlist.data.sampleData
import com.noxapps.familygiftlist.metafetcher.MetaFetcher
import com.noxapps.familygiftlist.mygifts.EditGiftDialogue
import com.noxapps.familygiftlist.mylists.EditListDialogue
import kotlinx.coroutines.launch

@Composable
fun SingleListPage(
    id: Int,
    db: AppDatabase,
    user: User,
    auth: FirebaseAuth,
    navController: NavHostController,
    singleListViewModel: SingleListViewModel = SingleListViewModel(
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

    LaunchedEffect(coroutineScope) {
        thisList = db.giftListDao().getOneWithListsById(id)
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
                    initialGift = thisGift,
                    viewModel = viewModel
                )
            }
        }
    ){}


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