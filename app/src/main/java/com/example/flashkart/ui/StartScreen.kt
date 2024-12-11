package com.example.flashkart.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashkart.Data.DataSource
import com.example.flashkart.R

@Composable
fun StartScreen(flashViewModel: FlashViewModel,
                onCategoryClicked:(Int) -> Unit
                ){
    val context= LocalContext.current
    LazyVerticalGrid(columns = GridCells.Adaptive(128.dp),
        contentPadding = PaddingValues(30.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
        item(span = {GridItemSpan(2)}) {
            Column {Card(colors = CardDefaults.cardColors(containerColor = Color(129, 10, 150, 255)
            )) {
                Image(
                    painter = painterResource(id = R.drawable.flashkart_banner),
                    contentDescription = "FlashKart Banner"
                )
            }
                Card(colors = CardDefaults.cardColors(containerColor = Color(30, 224, 15, 216)), modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)) {
                        Text(text = "Shop by Category", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.Black, modifier = Modifier.padding(horizontal = 10.dp))
                }
            }
        }
        items(DataSource.loadCategories()){
            CategoryCard(context = context,
                stringResourceId = it.stringResourceId,
                imageResourceId = it.imageResourceId,
                flashViewModel = flashViewModel,
                onCategoryClicked= onCategoryClicked
                )
        }

    }
    }
@Composable
fun CategoryCard(context: Context,
                 stringResourceId: Int,
                 imageResourceId: Int,
                 flashViewModel: FlashViewModel,
                 onCategoryClicked: (Int) -> Unit
                 ){
    val categoryName = stringResource(id = stringResourceId)
    Card(modifier = Modifier
        .size(width = 150.dp, height = 150.dp)
        .clickable {
            flashViewModel.updateClickedText(categoryName)
            Toast
                .makeText(context, categoryName, Toast.LENGTH_SHORT)
                .show()
            flashViewModel.updateSelectedCategory(stringResourceId)
            onCategoryClicked(stringResourceId)
        },
        colors = CardDefaults.cardColors(containerColor = Color(248, 221, 248, 255))
    ) {
        Text(text = stringResource(id= stringResourceId),
            fontSize = 15.sp,
//            maxLines = 1,
            modifier = Modifier.padding( 5.dp),
            color = Color.Black
        )

        Image(painter=painterResource(imageResourceId ),
            contentDescription = "Fresh Fruits",
            modifier = Modifier.size(150.dp)
        )
    }
}