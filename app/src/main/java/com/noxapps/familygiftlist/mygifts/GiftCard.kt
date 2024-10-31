package com.noxapps.familygiftlist.mygifts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.data.GiftWithLists
import com.noxapps.familygiftlist.metafetcher.MetaFetcher
import com.noxapps.familygiftlist.mygifts.singlegift.RemoteGiftImage
import com.noxapps.familygiftlist.navigation.Paths


@Composable
fun GiftCard(
    gift: GiftWithLists,
    index:Int,
    indexOfExpanded: MutableState<Int>,
    navHostController: NavHostController
) {
    var metaData by remember{mutableStateOf<MetaFetcher?>(null)}
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

    var maxHeight by remember{mutableIntStateOf(0)}
    LaunchedEffect(Unit) {
        metaData = MetaFetcher(gift.gift.link)
    }
    Row(
        modifier = Modifier.Companion
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
            .onGloballyPositioned { coordinates->
                if(maxHeight==0)maxHeight = coordinates.size.height
            },
    ) {
        Box(
            modifier = Modifier
                .weight(2f)
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
        Column(
            modifier = Modifier
                .weight(8f)
                .padding(4.dp),
        ) {
            Text(
                text = gift.gift.name,
                color = toggleTextColor,
                maxLines = 1,
                overflow = TextOverflow.Companion.Ellipsis,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                modifier = Modifier.Companion
                    .fillMaxWidth(),
                text = "On ${gift.lists.size} lists",
                color = toggleTextColor,
                maxLines = 1,
                overflow = TextOverflow.Companion.Ellipsis,
                textAlign = TextAlign.Companion.End,
                style = MaterialTheme.typography.bodyMedium

            )
            AnimatedVisibility(thisExpanded) {
                Text(
                    modifier = Modifier.Companion.fillMaxWidth(),
                    text = gift.gift.description,
                    color = toggleTextColor,
                    textAlign = TextAlign.Companion.Center,
                    //style = MaterialTheme.typography.
                )
            }

        }
        IconButton(
            modifier = Modifier.Companion
                .align(Alignment.Companion.CenterVertically),
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
                        colorFilter = ColorFilter.Companion.tint(toggleTextColor)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.keyboard_arrow_down_24px),
                        contentDescription = "Open Gift",
                        colorFilter = ColorFilter.Companion.tint(toggleTextColor)
                    )
                }
            }
        }

    }

}