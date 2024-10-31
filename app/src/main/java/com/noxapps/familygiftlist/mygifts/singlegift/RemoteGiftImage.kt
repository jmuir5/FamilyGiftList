package com.noxapps.familygiftlist.mygifts.singlegift

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import com.noxapps.familygiftlist.Indicator
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.mygifts.normaliseLink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun RemoteGiftImage(link:String?, desc:String?, readyState:Boolean,  width:Float =1f, passedHeight:Int = 0){

    var painter by remember { mutableStateOf<BitmapPainter?>(null) }
    var loaded by remember { mutableStateOf(false) }
    LaunchedEffect(link) {
        try {
            val image = withContext(Dispatchers.IO) {
                BitmapFactory.decodeStream(
                    URL(link?.normaliseLink()).openConnection().getInputStream()
                )
            }
            val p = BitmapPainter(image = image.asImageBitmap())
            MainScope().launch {
                painter = p
                if (readyState) loaded = true
            }
        } catch (e: Exception) {
            if (readyState) loaded = true
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
                    .conditional(passedHeight > 0, {
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
                    .conditional(passedHeight > 0, {
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