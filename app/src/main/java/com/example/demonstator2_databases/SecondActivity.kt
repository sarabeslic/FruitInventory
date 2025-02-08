package com.example.demonstator2_databases

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.demonstator2_databases.db.ShopEntity
import com.example.demonstator2_databases.ui.theme.Demonstator2databasesTheme

class SecondActivity : ComponentActivity() {
    private val fruitViewModel: FruitViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Demonstator2databasesTheme {
                InventoryScreen(fruitViewModel, this)
            }
        }
    }
}

//---------------------------------Inventory Screen---------------------------------//
@Composable
fun InventoryScreen(fruitViewModel: FruitViewModel, context: Context) {
    val fruits by fruitViewModel.allFruits.collectAsState(initial = emptyList())
    var expandedCardIndex by remember { mutableStateOf(-1) } // Track the index of the expanded card
    var isHorizontallyExpanded by remember { mutableStateOf(false) } // Track if expanded horizontally
    val gridState = rememberLazyGridState() // Remember the state of the LazyVerticalGrid to scroll to the expanded card


    //include both to remember the state in both cases!
    LaunchedEffect(expandedCardIndex, isHorizontallyExpanded) {
        if (expandedCardIndex != -1 || isHorizontallyExpanded) {
            gridState.animateScrollToItem(expandedCardIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp) // Spacing between elements
    ) {
        Text(
            text = "INVENTORY",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        //to show when the inventory is empty,
        // and to build a grid of fruits when added to the inventory
        if (fruits.isEmpty()) {
            Text(
                text = "Inventory empty",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            // LazyVerticalGrid to display the fruits in a grid and make it scrollable!!
            //does not scroll by itself so add that as well!
            LazyVerticalGrid(
                //if horizontally expanded, show one column since we need more space! else 2
                columns = if (isHorizontallyExpanded) Fixed(1) else Fixed(2),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize(),
                state = gridState


            ) {

                items(fruits.size) { index ->
                    ExpandableFruitCard(
                        fruit = fruits[index],
                        fruitViewModel = fruitViewModel,
                        isExpanded = expandedCardIndex == index,
                        isHorizontallyExpanded = expandedCardIndex == index && isHorizontallyExpanded,
                        onCardClick = {
                            if (expandedCardIndex == index) {
                                expandedCardIndex = -1
                                isHorizontallyExpanded = false
                            } else {
                                expandedCardIndex = index
                                isHorizontallyExpanded = false

                            }
                        },
                        onExpandWidth = {
                            if (expandedCardIndex == index && isHorizontallyExpanded) {
                                expandedCardIndex = -1
                                isHorizontallyExpanded = false
                            } else {
                                expandedCardIndex = index
                                isHorizontallyExpanded = true
                            }
                        }
                    )
                }
            }
        }
    }
}

//---------------------------------Fruit Card---------------------------------//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableFruitCard( //here declaring the properties of the card
    fruit: ShopEntity,
    fruitViewModel: FruitViewModel,
    isExpanded: Boolean,
    isHorizontallyExpanded: Boolean,
    onCardClick: () -> Unit,
    onExpandWidth: () -> Unit
) {

    var quantity by remember { mutableStateOf(fruit.quantity) }
    var price by remember { mutableStateOf(fruit.price) }


    val cardModifier = when {
        isHorizontallyExpanded -> Modifier
            .fillMaxWidth()
        isExpanded -> Modifier
            .fillMaxWidth()
        else -> Modifier
            .fillMaxWidth()
    }

    val imageResource = getImageResource(fruit.name)


    Card( //the card itself with the properties
        shape = RoundedCornerShape(30.dp),
        elevation = CardDefaults.cardElevation(4.dp), //so called drop shadow effect
        border = if (fruit.quantity < 3) BorderStroke(4.dp, Color.Red) // Conditional border colors
        else if (fruit.quantity < 6) BorderStroke(4.dp, Color.Yellow) // Conditional border colors
        else null,
        modifier = cardModifier
            .clickable { onCardClick() }
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)

    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageResource),
                contentDescription = "${fruit.name} image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(8.dp)
            )

            //function to show the expanded card with the details of the fruit
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = fruit.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                    )

                Text(
                    text = "${fruit.quantity} in stock",
                    fontSize = 18.sp
                )
                Text(
                    text = "EUR ${fruit.price}",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

            }

            //function for showing fields to edit the quantity and price of the fruit
            if (isHorizontallyExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = quantity.toString(),
                    onValueChange = { newQuantity ->
                        quantity = newQuantity.toIntOrNull() ?: quantity // Safely update quantity
                    },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Numeric input
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    singleLine = true,
                    //to make the fields pop more with the colors
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Green,
                            unfocusedBorderColor = Color.Gray
                )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Editable Price
                OutlinedTextField(
                    value = price.toString(),
                    onValueChange = { newPrice ->
                        price = newPrice.toDoubleOrNull() ?: price // Safely update price
                    },
                    label = { Text("Price (EUR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Numeric input
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Green,
                        unfocusedBorderColor = Color.Gray
                )
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Optional: Adds spacing between buttons
            ) {
                // First button is always visible but moved down when the card is horizontally expanded!
                Button(
                    onClick = {
                        // Update the fruit object in the ViewModel with the edited values
                        fruitViewModel.updateFruit(
                            fruit.copy(
                                quantity = quantity,
                                price = price
                            )
                        )
                        onExpandWidth() // Collapse after update
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("UPDATE")
                }

                // Second button is conditionally visible only when the card is horizontally expanded!!
                if (isHorizontallyExpanded) {
                    Button(
                        onClick = { fruitViewModel.removeFruit(fruit) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.weight(1f) // Make the button share space evenly
                    ) {
                        Text("DELETE")
                    }
                }
            }
        }
    }
}


//---------------------------------Image Resource for Fruits---------------------------------//
@Composable
fun getImageResource(fruitName: String): Int {
    return when (fruitName.lowercase()) {
        "apple" -> R.drawable.apple
        "banana" -> R.drawable.banana
        "lemon" -> R.drawable.lemon
        "strawberry" -> R.drawable.strawberry
        "orange" -> R.drawable.orange
        "grapes" -> R.drawable.grapes
        "watermelon" -> R.drawable.watermelon
        "kiwi" -> R.drawable.kiwi
        "pineapple" -> R.drawable.pineapple
        "pear" -> R.drawable.pear
        "peach" -> R.drawable.peach
        "plum" -> R.drawable.plum
        "cherry" -> R.drawable.cherry
        "mango" -> R.drawable.mango
        "avocado" -> R.drawable.avocado

        else -> R.drawable.unknown // if no image found, show the unknown image
    }
}


//how to fix closing on the image
//make a landing screen that shows up for 3 seconds using a coroutine and then goes to the main screen