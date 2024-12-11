package com.example.flashkart.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flashkart.Data.InternetItem
import com.example.flashkart.Data.InternetItemWithQuantity
import com.example.flashkart.R

@Composable
fun CartScreen(flashViewModel: FlashViewModel,
               onHomeButtonClick:()->Unit){
    val cartItems by flashViewModel.cartItems.collectAsState()
    val cartItemsWithQuantity = cartItems.groupBy { it }.map { (item,cartItems) ->InternetItemWithQuantity(item,cartItems.size) }
    if(cartItems.isNotEmpty()) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Column {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(10, 245, 196, 221)
                        )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.off_banner),
                            contentDescription = "Item Banner"
                        )
                    }
                }
            }
            item {
                Text(text = "Review items", fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
            items(cartItemsWithQuantity) {
                CartCard(it.item, flashViewModel, it.quantity)
            }
            item {
                Text(text = "Bill details:", fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
            val totalPrice = cartItems.sumOf { it.itemPrice * 75 / 100 }
            val handlingCharge = totalPrice * 1 / 100
            val deliveryCharge = 30
            val grandTotal = totalPrice + handlingCharge + deliveryCharge
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(236, 236, 236, 255)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        BillRow(
                            itemName = "Item Total:",
                            itemPrice = totalPrice,
                            fontWeight = FontWeight.Normal
                        )
                        BillRow(
                            itemName = "Handling Charge:",
                            itemPrice = handlingCharge,
                            fontWeight = FontWeight.Light
                        )
                        BillRow(
                            itemName = "Delivery Charge:",
                            itemPrice = deliveryCharge,
                            fontWeight = FontWeight.Normal
                        )
                        Divider(
                            thickness = 1.dp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                        BillRow(
                            itemName = "To Pay:",
                            itemPrice = grandTotal,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
    else{
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Icon(imageVector = Icons.Filled.Warning, contentDescription = "Warning",modifier = Modifier.size(80.dp))
            Text(text = "Cart is empty", fontSize = 20.sp)
            FilledTonalButton(onClick = { onHomeButtonClick()}) {
                Text(text = "Browse products")
            }

        }
    }
}

@Composable
fun CartCard(cartItem:InternetItem,
             flashViewModel: FlashViewModel,
             cartItemQuantity:Int){
    Row (modifier = Modifier
        .fillMaxWidth()
        .height(80.dp), verticalAlignment = Alignment.CenterVertically){
        AsyncImage(model = cartItem.itemUrl,
            contentDescription = "Item Images",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp)
                .weight(4f))
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .weight(4f), verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = cartItem.itemName, fontSize = 14.sp, maxLines = 1)
            Text(text = cartItem.itemQuantity, fontSize = 14.sp,maxLines = 1)
        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .weight(3f), verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Rs. ${cartItem.itemPrice}", fontSize = 12.sp, maxLines = 1, textDecoration = TextDecoration.LineThrough)
            Text(text = "Rs. ${cartItem.itemPrice*75/100}", fontSize = 18.sp,maxLines = 1)
        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp)
            .weight(3f), verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Quantity: $cartItemQuantity", fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Card(modifier = Modifier
                .clickable { flashViewModel.removeFromCart(oldItem = cartItem) }
                .fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(
                221,
                12,
                88,
                255
            )
            )) {
                    Text(text = "Remove", color =Color.White, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp))
            }
        }
    }
}

@Composable
fun BillRow(itemName:String,
            itemPrice:Int,
            fontWeight: FontWeight){
    Row (horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()){
        Text(text = itemName,fontWeight=fontWeight, color = Color.Black)
        Text(text = "Rs. $itemPrice",fontWeight=fontWeight, color = Color.Black)
    }
}