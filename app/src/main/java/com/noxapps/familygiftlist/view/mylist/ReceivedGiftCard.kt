package com.noxapps.familygiftlist.view.mylist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.data.Gift
import com.noxapps.familygiftlist.ui.theme.FamilyGiftListTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun ReceivedGiftCard(
    gift: Gift,
    index: Int? = null,
    lazyListState: LazyListState? = null,
    coroutineScope: CoroutineScope? = null
) {
    var expanded by remember{mutableStateOf(false)}
    val uriHandler = LocalUriHandler.current

    val formatter = DateTimeFormatter.ofPattern("dd/MM")
    val reservedText = "Reserved by ${gift.reservedBy} on ${formatter.format(gift.reservedDate)},"
    val purchasedText = "Purchased by ${gift.purchasedBy} on ${formatter.format(gift.purchaseDate)},"

    var cardHeightPx by remember{ mutableFloatStateOf(0f) }


    Column(modifier = Modifier
        .padding(4.dp)
        .clip(RoundedCornerShape(15.dp))
        .background(MaterialTheme.colorScheme.secondaryContainer)
        .onGloballyPositioned { coordinates ->
            // Set column height using the LayoutCoordinates
            cardHeightPx = coordinates.size.height.toFloat()
        }

    ) {
        Row(modifier = Modifier
            .clickable {
                expanded = !expanded
                if(lazyListState!=null && coroutineScope!=null && index != null){
                    //if (expanded) {
                        coroutineScope.launch{
                            Thread.sleep(100)
                            /*lazyListState.animateScrollBy(
                                value = cardHeightPx * index,
                                animationSpec = tween(durationMillis = 5000)
                            )*/
                            lazyListState.animateScrollToItem(index)
                        }
                    //}
                }
            }
            .fillMaxWidth()
            .animateContentSize()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = gift.name,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis

                )
                if (!expanded) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = "\$%.2f".format(gift.price.toFloat().div(100)),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleSmall
                        //textAlign = TextAlign.End
                    )
                }

            }
            if (!expanded)StatusIndicator(gift)


        }
        AnimatedVisibility(expanded){
            Column(
                modifier = Modifier
                    .padding(4.dp)
            ){
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(15.dp)),
                    painter = painterResource(id = if(gift.link == "https://gigiandtom.com.au/products/mirrored-mushroom-sculpture-silver")R.drawable.mirror_mushroom else R.drawable.placeholder),
                    contentDescription = "available"
                )
                Text(
                    text = gift.description,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "\$%.2f".format(gift.price.toFloat().div(100)),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.End
                )
                if(gift.reserved&&!gift.purchased){
                   Text(
                       modifier = Modifier
                           .fillMaxWidth(),
                       text = reservedText,
                       color = MaterialTheme.colorScheme.onSecondaryContainer,
                       style = MaterialTheme.typography.bodySmall,
                       textAlign = TextAlign.End

                   )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = "${gift.reservedDate.until(LocalDate.now(), ChronoUnit.DAYS)} Days ago",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End


                    )
                }
                if(gift.purchased){
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = purchasedText,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.End

                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = "${gift.purchaseDate.until(LocalDate.now(), ChronoUnit.DAYS)} Days ago",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.End

                    )

                    // TODO: Proof of purchase
                }
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Button(
                        modifier = Modifier
                            .height(IntrinsicSize.Min),
                        onClick = { uriHandler.openUri(gift.link) }

                    ) {
                        Text("Go To Product")
                        Image(
                            modifier = Modifier
                                .fillMaxHeight(),
                            painter = painterResource(id = R.drawable.open_in_new_24px),
                            contentDescription = "Open in New"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.StatusIndicator(gift: Gift) {
    Box(modifier = Modifier
        .aspectRatio(1f)
        .fillMaxHeight()
    ){
        if(gift.reserved&&!gift.purchased){

        }
        else if(gift.purchased) {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                painter = painterResource(id = R.drawable.available_tick),
                contentDescription = "available"
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun giftCardPreview() {
    FamilyGiftListTheme {
        val previewGift = Gift(
            0,
            0,
            "Example Gift",
            "This is an example of a gift",
            "www.google.com",
            5000,
            true,
            LocalDate.of(2024, 9, 10),
            "Joey Bonzo",
            false,
            LocalDate.now(),
            "Joey Bonzo",
            "",

        )
        ReceivedGiftCard(previewGift)
    }
}