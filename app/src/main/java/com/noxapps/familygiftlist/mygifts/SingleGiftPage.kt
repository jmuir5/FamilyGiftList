package com.noxapps.familygiftlist.mygifts

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.noxapps.familygiftlist.Indicator
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.data.AppDatabase
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
import kotlin.div

@Composable
fun SingleGiftPage(
    id: Int,
    db: AppDatabase,
    navController: NavHostController,
    viewModel: SingleGiftViewModel = SingleGiftViewModel(
        id = id,
        db = db,
        navController = navController
    )
) {
    var thisGift by remember{ mutableStateOf(sampleData.nullGiftWithLists) }
    var metadata by remember{ mutableStateOf<MetaFetcher?>(null)}

    val coroutineScope = rememberCoroutineScope()

    val headerSize = remember{ mutableStateOf(IntSize.Zero) }




    LaunchedEffect(coroutineScope) {
        thisGift = db.giftDao().getOneWithListsById(id)
        metadata = MetaFetcher(thisGift.gift.link)
    }

    Column(

    ) {
        Row(
            modifier = Modifier.Companion
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
                modifier = Modifier.Companion
                    .padding(4.dp)
                    .align(Alignment.Companion.CenterVertically),
                text = thisGift.gift.name,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        MetadataPlaceholderCard()

        if (metadata == null) {
            MetadataPlaceholderCard()
        } else {
            //MetadataCard(metadata)
        }



    }


    /*LazyColumn {
        item() {
            Text("This is a Gift page")
            Text("Gift id = ${thisGift.gift.giftId}")
            Text("Gift Name = ${thisGift.gift.name}")
            Text("description = ${thisGift.gift.description}")
            /*Text("Link = ${thisGift.gift.link}")
            Text("reserved = ${thisGift.gift.reserved}")
            Text("reserved Date = ${thisGift.gift.reservedDate}")
            Text("reserved by = ${thisGift.gift.reservedBy}")
            Text("purchased = ${thisGift.gift.purchased}")
            Text("purchased Date = ${thisGift.gift.purchaseDate}")
            Text("purchased by = ${thisGift.gift.purchasedBy}")
            Text("purchased proof = ${thisGift.gift.purchaseProof}")*/
            Text("metadata title = ${metadata?.getTitle()}")
            Text("metadata description = ${metadata?.getDescription()}")
            Text("metadata imageUrl = ${metadata?.getImageString()}")
            //Text("metadata siteName = ${targetResult?.siteName}")

        }
        item() {
            Text("begin image card")
            if(metadata!=null){
                RemoteGiftImage(metadata?.getImageString()!!, metadata?.getDescription()!!, (metadata!=null), 0.5f)
            }
            Text("end image card")
            //Text("metadata Body = ${metadata?.document?.body().toString()}")
        }


    }*/

}

@Composable
fun MetadataCard(x0: MetaFetcher?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(200.dp)
                .clip(RoundedCornerShape(15.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(15.dp))
                .align(Alignment.Center)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            //verticalAlignment = Alignment.CenterVertically
        ) {

        }
    }
}

@Composable
fun MetadataPlaceholderCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
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