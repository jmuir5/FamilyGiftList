package com.noxapps.familygiftlist.mygifts

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.GiftWithLists
import com.noxapps.familygiftlist.data.User
import com.noxapps.familygiftlist.data.sampleData
import com.noxapps.familygiftlist.mylists.CreateListDialogue
import com.noxapps.familygiftlist.mylists.ListCard
import com.noxapps.familygiftlist.mylists.MyListsBody
import com.noxapps.familygiftlist.mylists.MyListsViewModel
import com.noxapps.familygiftlist.mylists.SelectableGiftEntry
import com.noxapps.familygiftlist.navigation.loginCheck
import com.noxapps.familygiftlist.ui.theme.FamilyGiftListTheme
import kotlinx.coroutines.launch

@Composable
fun MyGiftsPage(
    db:AppDatabase,
    auth: FirebaseAuth,
    user: User,
    navController: NavHostController,
    viewModel: MyGiftsViewModel = MyGiftsViewModel(db,auth)
) {
    loginCheck(navController, auth)
    val createDrawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
    val showCreateDrawer by remember{ derivedStateOf { createDrawerState.isOpen }}
    val coroutineScope = rememberCoroutineScope()

    var listOfGifts by remember { mutableStateOf<List<GiftWithLists>>(emptyList()) }

    val expandedListCard = remember{mutableStateOf(-1)}

    val headerSize = remember{ mutableStateOf(IntSize.Zero) }


    LaunchedEffect(coroutineScope) {
        listOfGifts = db.userDao().getOneWithGiftsAndListsById(user.userId).giftsWithLists//.giftDao().getGiftsWithLists()
    }

    BackHandler(
        enabled = showCreateDrawer || expandedListCard.value!=-1
    ) {
        if(showCreateDrawer){
            coroutineScope.launch{
                createDrawerState.close()
            }
        } else if(expandedListCard.value != -1){
            expandedListCard.value = -1
        }
    }

    BottomDrawer(
        drawerState = createDrawerState,
        //drawerShape = RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp),
        drawerBackgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        gesturesEnabled = false,
        drawerContent = {
            if(showCreateDrawer){
                CreateGiftDialogue(
                    state = createDrawerState,
                    headerSize = headerSize,
                    scope = coroutineScope,
                    user = user,
                    navController = navController,
                    db = db,
                    viewModel = viewModel
                )
            }
        }
    ){
        MyGiftsBody(
            coroutineScope = coroutineScope,
            drawerState = createDrawerState,
            headerSize = headerSize,
            listOfGifts = listOfGifts,
            expandedCardIndex = expandedListCard,
            navController = navController
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun CreateGiftPreview() {
    FamilyGiftListTheme {
        Column(
            modifier = Modifier
                .background(Color.Blue)
                .fillMaxSize()
        ){
            var state = rememberBottomDrawerState(initialValue = BottomDrawerValue.Expanded)
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val user = sampleData.sampleUser
            val db = Room.databaseBuilder(
                context,
                AppDatabase::class.java, "gift-app-database"
            ).fallbackToDestructiveMigration().build()
            val navController = rememberNavController()
            val auth = Firebase.auth
            val viewModel = MyGiftsViewModel(db, auth)
            val headerSize = remember{mutableStateOf(IntSize.Zero)}

            CreateGiftDialogue(
                state = state,
                headerSize = headerSize,
                scope = coroutineScope,
                user = user,
                db = db,
                navController=navController,
                viewModel=viewModel
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GiftCardExpandedPreview() {
    FamilyGiftListTheme {
        Column(
            modifier = Modifier
        ){
            val list = sampleData.sampleListWithGifts
            val index = remember{ mutableIntStateOf(1) }
            val navHostController = rememberNavController()


            //GiftCard(list = list, index = index.value, indexOfExpanded = index, navHostController = navHostController)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GiftCardClosedPreview() {
    FamilyGiftListTheme {
        Column(
            modifier = Modifier
        ){
            val list = sampleData.sampleListWithGifts
            val index = remember{ mutableIntStateOf(1) }
            val navHostController = rememberNavController()


            //GiftCard(list = list, index = index.intValue+1, indexOfExpanded = index, navHostController = navHostController)
        }

    }
}


@Preview(showBackground = true)
@Composable
fun SelectableListEntryPreview() {
    FamilyGiftListTheme {
        Column(
            modifier = Modifier
        ){
            val gift = sampleData.sampleGift
            val state = remember{mutableStateOf(false)}

            //SelectableListEntry(gift = gift, state = state)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyGiftsBodyPreview() {
    FamilyGiftListTheme {
        Column(
            modifier = Modifier
        ){
            val coroutineScope = rememberCoroutineScope()
            val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
            val listOfLists = sampleData.sampleListOfListsWithGifts
            val expandedListCard = remember{mutableIntStateOf(-1)}
            val navController = rememberNavController()
            val headerSize = remember{mutableStateOf(IntSize.Zero)}

            /*MyGiftsBody(
                coroutineScope = coroutineScope,
                drawerState = drawerState,
                headerSize = headerSize,
                listOfLists = listOfLists,
                expandedListCard = expandedListCard,
                navController = navController
            )*/
        }
    }
}

