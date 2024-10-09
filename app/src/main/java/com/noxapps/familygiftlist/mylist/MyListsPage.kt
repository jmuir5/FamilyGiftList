package com.noxapps.familygiftlist.mylist

import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerState
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.Checkbox
import androidx.compose.material.IconButton
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.familygiftlist.Paths
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.Gift
import com.noxapps.familygiftlist.data.GiftList
import com.noxapps.familygiftlist.data.ListWithGifts
import com.noxapps.familygiftlist.data.User
import com.noxapps.familygiftlist.loginCheck
import com.noxapps.familygiftlist.ui.theme.FamilyGiftListTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun MyListsPage(
    context: Context,
    db:AppDatabase,
    auth: FirebaseAuth,
    user: User,
    navController: NavHostController,
    viewModel: MyListsViewModel = MyListsViewModel(context,db,auth)
) {
    loginCheck(navController, auth)
    val createDrawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
    val showCreateDrawer by remember{ derivedStateOf { createDrawerState.isOpen }}
    val coroutineScope = rememberCoroutineScope()

    var listOfLists by remember { mutableStateOf<List<ListWithGifts>>(emptyList()) }

    val expandedListCard = remember{mutableStateOf(-1)}

    LaunchedEffect(coroutineScope) {
        listOfLists = db.giftListDao().getGiftsWithLists()
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
                CreateListDialogue(
                    state = createDrawerState,
                    scope = coroutineScope,
                    user = user,
                    navController = navController,
                    db = db,
                    viewModel = viewModel
                )
            }
        }
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(10.dp)
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.CenterVertically),
                    text = "My Lists",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            createDrawerState.open()
                        }
                    }
                ){
                    Image(
                        painter = painterResource(id = R.drawable.add_24px),
                        contentDescription = "create new list",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                    )
                }
            }
            LazyColumn() {
                if(listOfLists.isEmpty()){
                    item{
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    createDrawerState.open()
                                }

                            }
                        ) {
                            Text(text = "Create New List +")
                        }
                    }
                }else{
                    itemsIndexed(listOfLists) { index, list ->
                        ListCard(
                            list,
                            index,
                            expandedListCard,
                            navController
                        )
                    }
                }


            }
        /*
        my lists header
         - row
            My Lists
            add new list button?
        (alternate) add new list button

         */
        }
    }
}


@Composable
fun ListCard(list:ListWithGifts,index:Int, indexOfExpanded:MutableState<Int>, navHostController: NavHostController) {
    val thisExpanded by remember{ derivedStateOf { indexOfExpanded.value==index }}
    val toggleBackgroundColor =
        if(thisExpanded){
            MaterialTheme.colorScheme.secondary
        }else{
            MaterialTheme.colorScheme.secondaryContainer
        }

    val toggleTextColor =
        if(thisExpanded){
            MaterialTheme.colorScheme.onSecondary
        }else{
            MaterialTheme.colorScheme.onSecondaryContainer
        }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, 2.dp)
            .background(toggleBackgroundColor)
            .height(IntrinsicSize.Min)
            .clickable {
                if (thisExpanded) {
                    navHostController.navigate("${Paths.SingleList.Path}/${list.giftList.listId}")//todo navigate to gift list
                } else {
                    indexOfExpanded.value = index
                }
            }
            .padding(4.dp),
    ){
        Column(
            modifier = Modifier
                .weight(1f),
            ) {
            Text(
                text = list.giftList.listName,
                color = toggleTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "${list.gifts.size} items",
                color = toggleTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyMedium

            )
            AnimatedVisibility(thisExpanded){
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = list.giftList.description,
                    color = toggleTextColor,
                    textAlign = TextAlign.Center,
                    //style = MaterialTheme.typography.
                )
            }

        }
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            onClick = {
                if (thisExpanded) {
                    navHostController.navigate("${Paths.SingleList.Path}/${list.giftList.listId}")
                } else {
                    indexOfExpanded.value = index
                } }) {
            Crossfade(targetState = thisExpanded, label = "expand/go to list") { thisExpanded ->
                if(thisExpanded){
                    Image(
                        painter = painterResource(id = R.drawable.chevron_right_24px),
                        contentDescription = "Open Gift",
                        colorFilter = ColorFilter.tint(toggleTextColor)
                    )
                }
                else{
                    Image(
                        painter = painterResource(id = R.drawable.keyboard_arrow_down_24px),
                        contentDescription = "Open Gift",
                        colorFilter = ColorFilter.tint(toggleTextColor)
                    )
                }
            }
        }

    }

}

@Composable
fun ColumnScope.CreateListDialogue(state: BottomDrawerState, scope:CoroutineScope, user: User, db:AppDatabase, navController: NavHostController, viewModel: MyListsViewModel){
    var listName by remember{ mutableStateOf("")}
    var listDesc by remember{ mutableStateOf("")}

    val enabled = remember{mutableStateOf(true)}
    val textIconColors = MaterialTheme.colorScheme.onPrimaryContainer
    val textFieldColors = TextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
    )

    var emptyCheck by remember{ mutableStateOf(false) }
    val listNameError by remember{
        derivedStateOf { listName.isEmpty()&&emptyCheck }
    }

    var listOfGifts by remember{ mutableStateOf<List<Gift>>(emptyList()) }

    val selectedGifts = remember{mutableStateListOf<MutableState<Boolean>>()}

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(coroutineScope) {
        listOfGifts = db.giftDao().getAll()
    }
    LaunchedEffect(state.isOpen) {
        state.expand()
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(12.dp, 2.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.CenterVertically),
            text = "Create new list",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
        IconButton(
            onClick = {
                scope.launch {
                    state.close()
                }
            }
        ){
            Image(
                painter = painterResource(id = R.drawable.close_24px),
                contentDescription = "close",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )
        }
    }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxHeight(0.7f)
            .padding(4.dp)

    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = listName,
            onValueChange = {
                if (it.length <= 32) {
                    listName = it
                }
            },
            colors = textFieldColors,
            enabled = enabled.value,
            shape = RectangleShape,
            label = {
                if(listNameError)
                    Text("Name can not be empty")
                else
                    Text("List Name")
            },

            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.add_24px),
                    contentDescription = "List Name",
                    colorFilter = textIconColors.let { ColorFilter.tint(it) }
                )
            }
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = listDesc,
            onValueChange = {
                listDesc = it

            },
            colors = textFieldColors,
            enabled = enabled.value,
            shape = RectangleShape,
            label = {Text("Description")},

            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.add_24px),
                    contentDescription = "List Name",
                    colorFilter = textIconColors.let { ColorFilter.tint(it) }
                )
            }
        )
        Text("Add gifts to this list:")
        LazyColumn(
            modifier = Modifier
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .weight(1f)
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            itemsIndexed(listOfGifts){index, gift ->
                SelectableGiftEntry(gift = gift, state = selectedGifts[index])
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RectangleShape,
            onClick = {
                Log.d("MyList", "Button Clicked")
                emptyCheck = true
                if(!listNameError){
                    Log.d("MyList", "error check passed")
                    viewModel.saveList(
                        enabledState = enabled,
                        drawerState = state,
                        listObject = GiftList(
                            0,
                            user.id,
                            "${user.firstName} ${user.lastName}",
                            listName,
                            listDesc
                        ),
                        initialGifts = listOf<Int>(),//inital gifts
                        navController = navController,
                        coroutineScope = coroutineScope
                    )
                }

            }
        ) {
            Text("Create List")
            Image(
                painter = painterResource(id = R.drawable.add_24px),
                contentDescription = "List Name",
                //colorFilter = textIconColors.let { ColorFilter.tint(it) }
            )
        }
    }
}

@Composable
fun SelectableGiftEntry(gift: Gift, state:MutableState<Boolean>){
    val toggleBackgroundColor =
        if(state.value){
            MaterialTheme.colorScheme.primary
        }else{
            MaterialTheme.colorScheme.secondaryContainer
        }

    val toggleTextColor =
        if(state.value){
            MaterialTheme.colorScheme.onPrimary
        }else{
            MaterialTheme.colorScheme.onPrimaryContainer
        }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, 2.dp)
            .background(toggleBackgroundColor)
            .height(IntrinsicSize.Min)
            .clickable { state.value = !state.value }
            .padding(4.dp),
    ){
        Checkbox(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            checked = state.value,
            onCheckedChange = {
                state.value = it
            }
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = gift.name,
            color = toggleTextColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            onClick = { /*TODO open gift page*/ }) {
            Image(
                painter = painterResource(id = R.drawable.open_in_new_24px),
                contentDescription = "Open Gift",
                colorFilter = ColorFilter.tint(toggleTextColor)
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun createListPreview() {
    FamilyGiftListTheme {
        Box(
            modifier = Modifier
                .background(Color.Blue)
                .fillMaxSize()
        ){
            var state = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
            //CreateListDialogue(state = state)
        }

    }
}
