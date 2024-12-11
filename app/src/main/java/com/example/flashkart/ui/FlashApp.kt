package com.example.flashkart.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flashkart.Data.InternetItem
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.*



enum class FlashAppScreen(val title: String) {
    Start("FlashKart"),
    Items("Items"),
    Cart("Your Cart"),
    Offer("Offer")
}
val auth= FirebaseAuth.getInstance()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashApp(
    flashViewModel: FlashViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {

    val user by flashViewModel.user.collectAsState()
    auth.currentUser?.let { flashViewModel.setUser(it) }
    val isVisible by flashViewModel.isVisible.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val logoutClicked by flashViewModel.logoutClicked.collectAsState()
    val currentScreen = FlashAppScreen.valueOf(
        backStackEntry?.destination?.route ?: FlashAppScreen.Start.name
    )
    val canNavigateBack = navController.previousBackStackEntry != null
    val cartItems by flashViewModel.cartItems.collectAsState()

    if (isVisible) {
        OfferScreen()
    }else if (user == null) {
        LoginUi(flashViewModel = flashViewModel)
    }
    else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth())  {
                            Row (verticalAlignment = Alignment.CenterVertically ){
                                Text(
                                    text = currentScreen.title,
                                    fontSize = 35.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold
                                )
                                if (currentScreen == FlashAppScreen.Cart) {
                                    Text(
                                        text = "(${cartItems.size})",
                                        fontSize = 35.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold
                                    )

                                }
                            }
                            Row(modifier = Modifier.clickable{ flashViewModel.setLogoutStatus(true)
                            }) {
                                Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = "Logout", modifier = Modifier.size(20.dp))
                                Text(text = "Logout", fontSize = 15.sp)
                            }
                        }
                    },
                    navigationIcon = {
                        if (canNavigateBack) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    }
                )
            },
            bottomBar = {
                FlashAppBar(navController = navController,
                    currentScreen= currentScreen,
                    cartItems=cartItems)
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = FlashAppScreen.Start.name,
                Modifier.padding(paddingValues)
            ) {
                composable(FlashAppScreen.Start.name) {
                    StartScreen(
                        flashViewModel = flashViewModel,
                        onCategoryClicked = {
                            flashViewModel.updateSelectedCategory(it)
                            navController.navigate(FlashAppScreen.Items.name)
                        }
                    )
                }
                composable(FlashAppScreen.Items.name) {
                    InternetItemsScreen(
                        flashViewModel = flashViewModel,
                        itemUiState = flashViewModel.itemUiState
                    )
                }
                composable(FlashAppScreen.Cart.name) {
                    CartScreen(
                        flashViewModel = flashViewModel,
                        onHomeButtonClick = {
                            navController.navigate(FlashAppScreen.Start.name) {
                                popUpTo(0)
                            }
                        }
                    )
                }
                composable(FlashAppScreen.Offer.name) {
                    OfferScreen()
                }
            }
        }
        if (logoutClicked){
            AlertCheck(onYesButtonPressed = {
                flashViewModel.setLogoutStatus(false)
                auth.signOut()
                flashViewModel.clearData()
            },
                onNoButtonPressed = {
                    flashViewModel.setLogoutStatus(false)
                }
                )
        }
    }
}


@Composable
fun FlashAppBar(navController: NavHostController,
                currentScreen: FlashAppScreen,
                cartItems: List<InternetItem>) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 70.dp, vertical = 10.dp)
            .padding(bottom = 30.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.clickable {
                navController.navigate(FlashAppScreen.Start.name) {
                    popUpTo(0)
                }
            }
        ) {
            Icon(imageVector = Icons.Outlined.Home, contentDescription = "Home")
            Text(text = "Home", fontSize = 10.sp)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.clickable {
                if(currentScreen != FlashAppScreen.Cart) {
                    navController.navigate(FlashAppScreen.Cart.name) {
                    }
                }
            }
        ) {
            Box {
                Icon(imageVector = Icons.Outlined.ShoppingCart, contentDescription = "Cart")
                if(cartItems.isNotEmpty())
                Card(modifier = Modifier
                    .align(alignment = Alignment.TopEnd)
                    .padding(horizontal = 1.dp),colors=CardDefaults.cardColors(
                    containerColor = Color.Red))
                {
                    Text(text = cartItems.size.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold)
                }
            }
            Text(text = "Cart", fontSize = 10.sp)
        }
    }
}

@Composable
fun AlertCheck(onYesButtonPressed: () -> Unit, onNoButtonPressed: () -> Unit) {
    AlertDialog(
        title = {
            Text(text = "Logout?", fontWeight = FontWeight.Bold)
        },
        text = {
            Text(text = "Are you sure you want to Logout?")
        },
        confirmButton = {
            TextButton(onClick = { onYesButtonPressed() }) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = { onNoButtonPressed() }) {
                Text(text = "No")
            }
        },
        onDismissRequest = {
            onNoButtonPressed()
        }
    )
}