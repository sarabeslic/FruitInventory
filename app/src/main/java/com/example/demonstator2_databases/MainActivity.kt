package com.example.demonstator2_databases

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.demonstator2_databases.db.ShopEntity
import com.example.demonstator2_databases.ui.theme.Demonstator2databasesTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val fruitViewModel: FruitViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Demonstator2databasesTheme {
                MainApp(fruitViewModel=fruitViewModel)
            }
        }
    }
}

//---------------------------landing page before the main app-------------------------//

@Composable //splash screen that dissapears after 5 seconds and show the main screen
fun SplashScreen(onSplashFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(5000) // Wait for 5 seconds
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green)
            .padding(horizontal = 16.dp), // Add horizontal padding to the Box
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image( //adds the logo image
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(250.dp)
            )
            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "FRUIT SHOP" +
                        " INVENTORY",
                color = Color.White,
                lineHeight = 40.sp,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Easily manage your fruits " +
                        "inventory, anytime, anywhere!",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.fillMaxWidth()
                                   .padding(top = 16.dp)
            )
        }
    }
}


//-------------------------------Main App and Bottom Navigation Bar---------------------//

//define the screens!
enum class Screens {
    AddFruit,
    Inventory
}

//contains the main app and the bottom navigation bar
@Composable
fun MainApp(fruitViewModel: FruitViewModel) {
    val navController = rememberNavController()

    // Splash screen
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) { //when the splash screen is shown
        SplashScreen {
            showSplash = false
        }
    } else {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { BottomNavigationBar(navController) } //BottomNavigationBar attached
        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = Screens.AddFruit.name, // Start with AddFruit screen
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screens.AddFruit.name) {
                    MainScreen(fruitViewModel)
                }
                composable(Screens.Inventory.name) {
                    InventoryScreen(fruitViewModel, context = LocalContext.current)
                }//context used to show the snackbar
            }
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavController) {

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        ?: Screens.AddFruit.name // Default to AddFruit if no route is selected

    val currentScreen = Screens.valueOf(currentRoute)

    NavigationBar(containerColor = Color.Transparent) {

        NavigationBarItem( //defining the add fruit navigation item
            selected = currentScreen == Screens.AddFruit,
            onClick = { navController.navigate(Screens.AddFruit.name) },
            icon = {
                IconWithCircle(
                    isSelected = currentScreen == Screens.AddFruit,
                    icon = Icons.Default.Add
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent    // Removes the shadow effect! leaves just green
            )
        )

        NavigationBarItem(
            selected = currentScreen == Screens.Inventory,
            onClick = { navController.navigate(Screens.Inventory.name) },
            icon = {
                IconWithCircle(
                    isSelected = currentScreen == Screens.Inventory,
                    icon = Icons.Default.Home
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent    // Removes the shadow effect
            )
        )
    }
}



// Custom Composable for Circle Around Icons in the Bottom Navigation Bar
@Composable
fun IconWithCircle(isSelected: Boolean, icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(70.dp)
            .background(
                color = if (isSelected) Color.Green else Color(0xFF10821D), // Light green for selected, dark green otherwise
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center // Centers the icon inside the circle
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(40.dp)
        )
    }
}

//------------------------------Add Fruit Screen------------------------------------//

@OptIn(ExperimentalMaterial3Api::class) // Required for using the Snackbar
@Composable

fun MainScreen(fruitViewModel: FruitViewModel) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    if (showSnackbar) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(snackbarMessage)
            showSnackbar = false
        }
    }

    Scaffold(
        snackbarHost = { // for showing the snackbar message at the bottom
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color.Black, // Background color of the Snackbar
                    contentColor = Color.White // Text color of the Snackbar message
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 42.dp, vertical = 52.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ADD NEW FRUIT",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField( //to add the name of the fruit
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Green,
                    unfocusedBorderColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField( //to add the quantity of the fruit
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)), // Round the corners
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Green,
                    unfocusedBorderColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField( //to add the price of the fruit
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)), // Round the corners
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Green,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

        /*
        checking the fields input values before adding the fruit!!
        very important not to have double fruits
        (only issue is if the fruit is wrongly written it wont be recognised and will be added to the database but without the picture,
        as unknown, same goes for any fruit that I don't have a picture for, but for now I will assume that the user will write the fruit name correctly)
        */
            Button(
                onClick = {
                    val fruit = ShopEntity(
                        name = name,
                        quantity = quantity.toIntOrNull() ?: 0,
                        price = price.toDoubleOrNull() ?: 0.0
                    )

                    //to give a messahge if the fields are empty or the fruit already exists
                    if (fruit.name.isEmpty() || fruit.quantity == 0 || fruit.price == 0.0) {
                        snackbarMessage = "Please fill all the fields!"
                        showSnackbar = true
                        return@Button
                    }

                    // Check if the fruit name already exists
                    fruitViewModel.viewModelScope.launch {
                        val exists = fruitViewModel.isFruitNameExists(fruit.name)
                        if (exists) {
                            snackbarMessage = "Fruit with this name already exists!"
                            showSnackbar = true
                        } else {
                            fruitViewModel.addFruit(fruit)
                            name = ""
                            quantity = ""
                            price = ""
                            snackbarMessage = "${fruit.name} added to the inventory!"
                            showSnackbar = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Fruit")
            }
            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}
