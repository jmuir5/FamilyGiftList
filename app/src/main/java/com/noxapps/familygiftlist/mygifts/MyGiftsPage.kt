package com.noxapps.familygiftlist.mygifts

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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.Gift
import com.noxapps.familygiftlist.data.GiftList
import com.noxapps.familygiftlist.data.GiftWithLists
import com.noxapps.familygiftlist.data.ListWithGifts
import com.noxapps.familygiftlist.data.User
import com.noxapps.familygiftlist.mylists.ListCard
import com.noxapps.familygiftlist.navigation.Paths
import com.noxapps.familygiftlist.navigation.loginCheck
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

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
        listOfGifts = db.giftDao().getGiftsWithLists()
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

@Composable
fun ColumnScope.CreateGiftDialogue(
    state: BottomDrawerState,
    scope: CoroutineScope,
    headerSize: MutableState<IntSize>,
    user: User,
    db: AppDatabase,
    navController: NavHostController,
    viewModel: MyGiftsViewModel
){
    var giftName by remember { mutableStateOf("") }
    var giftDesc by remember { mutableStateOf("") }
    var giftLink by remember { mutableStateOf("") }
    var giftPrice by remember { mutableIntStateOf(0) }

    val enabled = remember { mutableStateOf(true) }
    val textIconColors = MaterialTheme.colorScheme.onPrimaryContainer
    val textFieldColors = TextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
    )

    var emptyCheck by remember { mutableStateOf(false) }
    val giftNameError by remember {
        derivedStateOf { giftName.isEmpty() && emptyCheck }
    }
    var listOfLists by remember { mutableStateOf<List<GiftList>>(emptyList()) }
    val selectedLists = remember { mutableStateListOf<MutableState<Boolean>>() }
    val coroutineScope = rememberCoroutineScope()
    val screenHeight = LocalConfiguration.current.screenHeightDp

    LaunchedEffect(coroutineScope) {
        listOfLists = db.giftListDao().getAll()
        (0..listOfLists.size).map {selectedLists.add(mutableStateOf(false)) }
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
    ) {
        Text(
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.CenterVertically),
            text = "Create new gift",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
        IconButton(
            onClick = {
                scope.launch {
                    state.close()
                }
            }
        ) {
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
            .height((screenHeight-(headerSize.value.height-8)).dp)//(screenHeight-((headerSize.value.height+drawerHeaderSize.height)/2)).dp
            .padding(4.dp)

    ) {

        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = giftName,
            onValueChange = {
                if (it.length <= 32) {
                    giftName = it
                }
            },
            colors = textFieldColors,
            enabled = enabled.value,
            shape = RectangleShape,
            label = {
                if (giftNameError)
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
            value = giftDesc,
            onValueChange = {
                giftDesc = it

            },
            colors = textFieldColors,
            enabled = enabled.value,
            shape = RectangleShape,
            label = { Text("Description") },

            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.add_24px),
                    contentDescription = "Gift Description",
                    colorFilter = textIconColors.let { ColorFilter.tint(it) }
                )
            }
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = giftLink,
            onValueChange = {
                giftLink = it
            },
            colors = textFieldColors,
            enabled = enabled.value,
            shape = RectangleShape,
            label = { Text("Link") },

            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.link_24px),
                    contentDescription = "Gift Link",
                    colorFilter = textIconColors.let { ColorFilter.tint(it) }
                )
            }
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = giftPrice.toString(),
            onValueChange = {
                if(it.isEmpty()||it.isDigitsOnly()) {
                    giftPrice = it.toInt()
                }
            },
            colors = textFieldColors,
            enabled = enabled.value,
            shape = RectangleShape,
            label = { Text("Link") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.attach_money_24px),
                contentDescription = "Gift Link",
                colorFilter = textIconColors.let { ColorFilter.tint(it) }
            )
        }
        )
        Text("Add this gift to lists:")
        LazyColumn(
            modifier = Modifier
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .weight(1f)
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            itemsIndexed(listOfLists) { index, gift ->
                SelectableListEntry(
                    giftList = gift,
                    state = selectedLists[index],
                    navController = navController
                )
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RectangleShape,
            onClick = {
                emptyCheck = true
                if (!giftNameError) {
                    viewModel.saveGift(
                        enabledState = enabled,
                        drawerState = state,
                        giftObject = Gift(
                            0,
                            giftName,
                            giftDesc,
                            giftLink,
                            giftPrice,
                            false,
                            LocalDate.now(),
                            "",
                            false,
                            LocalDate.now(),
                            "",
                            ""
                        ),
                        initialLists = listOf<Int>(),//inital gifts
                        navController = navController,
                        coroutineScope = coroutineScope
                    )
                }

            }
        ) {
            Text("Create Gift")
            Image(
                painter = painterResource(id = R.drawable.add_24px),
                contentDescription = "Gift Name",
                //colorFilter = textIconColors.let { ColorFilter.tint(it) }
            )
        }
    }
}

@Composable
fun SelectableListEntry(giftList: GiftList, state: MutableState<Boolean>, navController: NavHostController){
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, 2.dp)
            .background(toggleBackgroundColor)
            .height(IntrinsicSize.Min)
            .clickable { state.value = !state.value }
            .padding(4.dp),
    ) {
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
            text = giftList.listName,
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
            onClick = {
                navController.navigate("${Paths.SingleList.Path}/${giftList.listId}")
            }) {
            Image(
                painter = painterResource(id = R.drawable.open_in_new_24px),
                contentDescription = "Open Gift",
                colorFilter = ColorFilter.tint(toggleTextColor)
            )
        }

    }
}

@Composable
fun MyGiftsBody(
    listOfGifts: List<GiftWithLists>,
    expandedCardIndex: MutableState<Int>,
    headerSize: MutableState<IntSize>,
    coroutineScope: CoroutineScope,
    drawerState: BottomDrawerState,
    navController: NavHostController
){
    val density = LocalDensity.current
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(10.dp)
                .height(IntrinsicSize.Min)
                .onGloballyPositioned { coordinates ->
                    headerSize.value = coordinates.size
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterVertically),
                text = "My Gifts",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add_24px),
                    contentDescription = "create new gift",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                )
            }
        }
        LazyColumn() {
            if (listOfGifts.isEmpty()) {
                item { //todo no gifts dialogue
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }
                    ) {
                        Text(text = "Create New Gift +")
                    }
                }
            } else {
                itemsIndexed(listOfGifts) { index, gift ->
                    GiftCard(
                        gift,
                        index,
                        expandedCardIndex,
                        navController
                    )
                }
            }


        }
    }
}


@Composable
fun GiftCard(
    gift: GiftWithLists,
    index:Int,
    indexOfExpanded: MutableState<Int>,
    navHostController: NavHostController
) {
    val thisExpanded by remember { derivedStateOf { indexOfExpanded.value == index } }
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, 2.dp)
            .background(toggleBackgroundColor)
            .height(IntrinsicSize.Min)
            .clickable {
                if (thisExpanded) {
                    navHostController.navigate("${Paths.SingleGift.Path}/${gift.gift.giftId}")
                } else {
                    indexOfExpanded.value = index
                }
            }
            .padding(4.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                text = gift.gift.name,
                color = toggleTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "On ${gift.lists.size} lists",
                color = toggleTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyMedium

            )
            AnimatedVisibility(thisExpanded) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = gift.gift.description,
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
                    navHostController.navigate("${Paths.SingleList.Path}/${gift.gift.giftId}")
                } else {
                    indexOfExpanded.value = index
                }
            }) {
            Crossfade(targetState = thisExpanded, label = "expand/go to list") { thisExpanded ->
                if (thisExpanded) {
                    Image(
                        painter = painterResource(id = R.drawable.chevron_right_24px),
                        contentDescription = "Open Gift",
                        colorFilter = ColorFilter.tint(toggleTextColor)
                    )
                } else {
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

