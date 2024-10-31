package com.noxapps.familygiftlist.mygifts.singlegift

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.data.User
import com.noxapps.familygiftlist.data.sampleData
import com.noxapps.familygiftlist.metafetcher.MetaFetcher
import com.noxapps.familygiftlist.mygifts.EditGiftDialogue
import kotlinx.coroutines.launch

/*
import com.fresh.materiallinkpreview.models.OpenGraphMetaData
import com.fresh.materiallinkpreview.parsing.OpenGraphMetaDataProvider
import com.fresh.materiallinkpreview.ui.CardLinkPreview
import com.fresh.materiallinkpreview.ui.CardLinkPreviewProperties
import io.ktor.client.request.get*/

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

    val headerSize = remember{ mutableIntStateOf(0)}
    val pageSize = remember{mutableIntStateOf(0)}

    val reloader = remember{mutableStateOf(true)}

    val deleteDialogueState = remember{mutableStateOf(false)}




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
    ){
        SingleGiftBody(
            pageSize = pageSize,
            headerSize = headerSize,
            thisGift = thisGift,
            metadata = metadata,
            deleteDialogueState = deleteDialogueState,
            editDrawerState = editDrawerState,
            navController = navController,
            coroutineScope = coroutineScope,
            viewModel = viewModel
        )

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