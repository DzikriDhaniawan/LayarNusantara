package com.example.layarnusantara.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.layarnusantara.pages.FavoritePage
import com.example.layarnusantara.pages.HomePage
import com.example.layarnusantara.pages.ProfilePage

@Composable
fun Homescreen(modifier: Modifier = Modifier, navController: NavController) {
    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Favorite", Icons.Default.Favorite),
        NavItem("Profile", Icons.Default.Person),
    )

    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == selectedIndex,
                        onClick = { selectedIndex = index },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedIndex) {
            0 -> HomePage(modifier = Modifier.padding(innerPadding), navController)
            1 -> FavoritePage(modifier = Modifier.padding(innerPadding), navController)
            2 -> ProfilePage(modifier = Modifier.padding(innerPadding))
        }
    }
}

data class NavItem(val label: String, val icon: ImageVector)
