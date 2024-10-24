package com.noxapps.familygiftlist.mygifts

import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.familygiftlist.Indicator
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.GiftWithLists
import com.noxapps.familygiftlist.data.User
import com.noxapps.familygiftlist.data.sampleData
import com.noxapps.familygiftlist.metafetcher.MetaFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
import com.fresh.materiallinkpreview.models.OpenGraphMetaData
import com.fresh.materiallinkpreview.parsing.OpenGraphMetaDataProvider
import com.fresh.materiallinkpreview.ui.CardLinkPreview
import com.fresh.materiallinkpreview.ui.CardLinkPreviewProperties
import io.ktor.client.request.get*/
import java.net.URL

@Composable
fun SingleGiftPage(
    id: Int,
    db: AppDatabase,
    user:User,
    auth: FirebaseAuth,
    navController: NavHostController,
    viewModel: SingleGiftViewModel = SingleGiftViewModel(
        id = id,
        db = db,
        auth = auth,
        navController = navController
    )
) {
    var thisGift by remember{ mutableStateOf(sampleData.nullGiftWithLists) }
    var metadata by remember{ mutableStateOf<MetaFetcher?>(null)}

    val editDrawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
    val showEditDrawer by remember{ derivedStateOf { editDrawerState.isOpen }}


    val coroutineScope = rememberCoroutineScope()

    var headerSize by remember{ mutableIntStateOf(0)}
    var pageSize by remember{mutableIntStateOf(0)}

    val reloader = remember{mutableStateOf(true)}




    LaunchedEffect(reloader.value) {
        Log.d("Launched effect", "reloader Triggered")
        thisGift = db.giftDao().getOneWithListsById(id)
        metadata = null
        metadata = MetaFetcher(thisGift.gift.link)
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
                EditGiftDialogue(
                    state = editDrawerState,
                    reloader = reloader,
                    drawerSize = pageSize-headerSize,
                    scope = coroutineScope,
                    user = user,
                    navController = navController,
                    db = db,
                    initialGift = thisGift,
                    viewModel = viewModel
                )
            }
        }
    ){
        Column(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    pageSize = coordinates.size.height
                }
        ) {
            Row(
                modifier = Modifier.Companion
                    .onGloballyPositioned { coordinates ->
                        headerSize = coordinates.size.height
                    }
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(10.dp)
                    .height(IntrinsicSize.Min)
                    ,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.Companion
                        .padding(4.dp)
                        .align(Alignment.Companion.CenterVertically),
                    text = thisGift.gift.name,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            //MetadataPlaceholderCard()

            if (metadata != null) {
                MetadataCard(metadata!!)

            } else {
                MetadataPlaceholderCard()
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
                    text = thisGift.gift.description,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.End,
                    text = "\$%.2f".format(thisGift.gift.price.toFloat().div(100)),
                    style = MaterialTheme.typography.bodyLarge
                )

                Text("this gift is on the following lists:")
                LazyColumn(
                    modifier = Modifier.Companion
                        .border(2.dp, MaterialTheme.colorScheme.primary)
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    itemsIndexed(thisGift.lists) { index, gift ->
                        UnselectableListEntry(
                            giftList = gift,
                            navController = navController
                        )
                    }
                }

            }
            Row(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                ,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.error)
                        .clickable{
                            //todo bring up delete dialogue
                        }
                        .padding(10.dp)
                ){
                    Text(
                        modifier = Modifier.Companion
                            .padding(4.dp)
                            .fillMaxWidth()
                            .align(Alignment.Companion.CenterVertically),
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
                        .clickable{
                            coroutineScope.launch{
                                editDrawerState.open()
                            }
                        }
                        .padding(10.dp)
                ){
                    Text(
                        modifier = Modifier.Companion
                            .padding(4.dp)
                            .fillMaxWidth()
                            .align(Alignment.Companion.CenterVertically),
                        text = "Edit",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
    }
}

@Composable
fun DeleteDialogue(dialogueState:MutableState<Boolean>, onConfirm: ()-> Unit){
    Dialog(
        onDismissRequest = {dialogueState.value = !dialogueState.value},
    ){
        Box(){

        }
    }
}

@Composable
fun MetadataCard(metadata: MetaFetcher) {
    val title = metadata.getTitle()
    val description = metadata.getDescription()
    val imageString = metadata.getImageString()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .heightIn(200.dp)
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(15.dp))
                .align(Alignment.Center)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(15.dp))
            //verticalAlignment = Alignment.CenterVertically
        ) {
            Column(){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    ){
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)

                    ){
                        RemoteGiftImage(
                            link = imageString,
                            desc = description,
                            readyState = (true),
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .border(Dp.Hairline, MaterialTheme.colorScheme.outlineVariant)
                        .background(Color.White)
                        .padding(4.dp)

                ){
                    Column(
                        modifier = Modifier
                            .weight(9f)
                    ){
                        Text(
                            text = title,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMedium,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = description,
                            maxLines = 3,
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        modifier = Modifier
                            .weight(1f),
                        onClick = {
                            //todo go to metadata.target on click
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.open_in_new_24px),
                            contentDescription = "open in new"
                        )
                    }
                }
            }

        }

    }
}

@Composable
fun MetadataPlaceholderCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 10.dp)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(200.dp)
                .clip(RoundedCornerShape(15.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(15.dp))
                .align(Alignment.Center)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            //verticalAlignment = Alignment.CenterVertically
        ){
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
            ){
                Indicator()
            }
        }
    }
}

@Composable
fun RemoteGiftImage(link:String?, desc:String?, readyState:Boolean,  width:Float =1f, passedHeight:Int = 0){

    var painter by remember{mutableStateOf<BitmapPainter?>(null)}
    var loaded by remember{mutableStateOf(false)}
    LaunchedEffect(link){
        try {
            val image = withContext(Dispatchers.IO) {
                BitmapFactory.decodeStream(
                    URL(link?.normaliseLink()).openConnection().getInputStream()
                )
            }
            val p = BitmapPainter(image = image.asImageBitmap())
            MainScope().launch {
                painter = p
                if(readyState)loaded = true
            }
        }
        catch(e:Exception){
            if(readyState)loaded = true
        }
    }

    if(readyState && loaded) {
        if (painter != null) {
            Image(
                painter = painter!!,
                contentDescription = desc,
                modifier = Modifier
                    //.aspectRatio(painter!!.intrinsicSize.width / painter!!.intrinsicSize.height)
                    .fillMaxWidth(width)
                    .conditional(passedHeight>0, {
                        height(with(LocalDensity.current) { passedHeight.toDp() })
                    }),
                contentScale = ContentScale.Fit
            )
        } else {
            Image(
                painter = painterResource(R.drawable.placeholder),
                contentDescription = desc,
                modifier = Modifier
//                    .aspectRatio(painter!!.intrinsicSize.width / painter!!.intrinsicSize.height)
                    .fillMaxWidth(width)
                    .conditional(passedHeight>0, {
                        height(with(LocalDensity.current) { passedHeight.toDp() })
                    }),
                contentScale = ContentScale.Fit
            )
        }
    }
    else{
        Indicator()
    }
}

inline fun Modifier.conditional(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this },
): Modifier = if (condition) {
    then(ifTrue(Modifier))
} else {
    then(ifFalse(Modifier))
}