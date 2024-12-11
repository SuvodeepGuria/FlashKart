package com.example.flashkart.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flashkart.Data.InternetItem
import com.example.flashkart.R

@Composable
fun InternetItemsScreen(flashViewModel: FlashViewModel, itemUiState: FlashViewModel.ItemUiState) {
    when (itemUiState) {
        is FlashViewModel.ItemUiState.Loading -> {
            LoadingScreen()
        }
        is FlashViewModel.ItemUiState.Success -> {
            ItemsScreen(flashViewModel = flashViewModel, items = itemUiState.items)
        }
        is FlashViewModel.ItemUiState.Error -> {
            ErrorScreen(flashViewModel=flashViewModel)
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(flashViewModel: FlashViewModel) {
    LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.download_removebg_preview),
            contentDescription = "Error",
            modifier = Modifier.padding(horizontal = 40.dp)
        )
        Text(
            text = "Oops !! Internet Unavailable",
            modifier = Modifier.padding(20.dp),
            textAlign = TextAlign.Center
        )
        Button(onClick = {
            flashViewModel.getFlashItems()
        }) {
            Text(text = "Retry")
        }
    }
}


@Composable
fun ItemsScreen(flashViewModel: FlashViewModel, items: List<InternetItem>) {
    val flashUiState by flashViewModel.uiState.collectAsState()
    val selectedCategory = stringResource(id = flashUiState.selectedCategory)
    val database=items.filter {
        it.itemCategory.lowercase()==selectedCategory.lowercase()
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Column {Card(colors = CardDefaults.cardColors(containerColor = Color(10, 245, 196, 221)
            )) {
                Image(
                    painter = painterResource(id = R.drawable.off_banner),
                    contentDescription = "Item Banner"
                )
            }
                Card(colors = CardDefaults.cardColors(containerColor = Color(30, 224, 15, 216)), modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)) {
                    Text(text = "${stringResource(id = flashUiState.selectedCategory)} (${database.size})", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.Black, modifier = Modifier.padding(horizontal = 10.dp))
                }
            }
        }
        items(database) {
            ItemCard(
                stringResourceId = it.itemName,
                imageResourceId = it.itemUrl,
                itemQuantity = it.itemQuantity,
                itemPrice = it.itemPrice,
                flashViewModel=flashViewModel
            )
        }
    }
}

@Composable
fun ItemCard(
    stringResourceId: String,
    imageResourceId: String,
    itemQuantity: String,
    itemPrice: Int,
    flashViewModel: FlashViewModel
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .width(150.dp)
            .padding(20.dp)
    ) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(248, 221, 248, 255))) {
        Box {
            AsyncImage(
                model = imageResourceId,
                contentDescription = stringResourceId,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.End
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(240, 35, 20, 255)
                    )
                ) {
                    Text(
                        text = "25% off",
                        color = Color.White,
                        fontSize = 8.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
            Text(
                text = stringResourceId,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                maxLines = 1,
                textAlign = TextAlign.Left
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Rs. $itemPrice",
                        maxLines = 1,
                        fontSize = 8.sp,
                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.LineThrough
                    )
                    Text(
                        text = "Rs. ${itemPrice - (itemPrice * 25 / 100)}",
                        maxLines = 1,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = itemQuantity,
                    fontSize = 14.sp,
                    color = Color(160, 131, 134, 255),
                    maxLines = 1,
                )
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        flashViewModel.addToDataBase(InternetItem(
                            itemName=stringResourceId,
                            itemUrl=imageResourceId,
                            itemQuantity=itemQuantity,
                            itemPrice= itemPrice,
                            itemCategory = ""))
                        Toast
                            .makeText(context, "Added to Cart", Toast.LENGTH_SHORT)
                            .show()
                    },
                colors = CardDefaults.cardColors(containerColor = Color(240, 35, 20, 255))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add to Cart",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }