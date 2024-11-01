package com.noxapps.familygiftlist.mylists

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.data.Gift
import com.noxapps.familygiftlist.metafetcher.MetaFetcher
import com.noxapps.familygiftlist.mygifts.singlegift.RemoteGiftImage
import com.noxapps.familygiftlist.navigation.Paths

@Composable
fun SelectableGiftEntry(
    gift: Gift,
    state: MutableState<Boolean>,
    navController: NavHostController
){
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
            onClick = {
                navController.navigate("${Paths.SingleGift.Path}/${gift.giftId}")
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
fun UnselectableGiftEntry(
    gift: Gift,
    navController: NavHostController
){
    var metaData by remember{ mutableStateOf<MetaFetcher?>(null) }
    var maxHeight by remember{ mutableIntStateOf(0) }

    LaunchedEffect(gift) {
        metaData = null
        metaData = MetaFetcher(gift.link)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, 2.dp)
            .background(MaterialTheme.colorScheme.primary)
            .height(IntrinsicSize.Min)
            .padding(4.dp)
            .onGloballyPositioned { coordinates->
                if(maxHeight==0)maxHeight = coordinates.size.height
            },
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                //.align(Alignment.CenterVertically)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            //verticalAlignment = Alignment.CenterVertically
        ){
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
            ){
                RemoteGiftImage(
                    link = metaData?.getImageString(),
                    desc = metaData?.getDescription(),
                    readyState = (metaData!=null),
                    passedHeight = maxHeight
                )
            }
        }
        Row(
            modifier = Modifier
                .weight(4f)
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                text = gift.name,
                color = MaterialTheme.colorScheme.onPrimary,
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
                    navController.navigate("${Paths.SingleGift.Path}/${gift.giftId}")
                }) {
                Image(
                    painter = painterResource(id = R.drawable.open_in_new_24px),
                    contentDescription = "Open Gift",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                )
            }
        }

    }
}