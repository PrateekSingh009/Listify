package com.example.listify.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.listify.presentation.screens.home.HomeScreen
import com.example.listify.presentation.screens.list.ListScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController,startDestination = "home") {
        composable("home") {
            HomeScreen(
                onClick = { categoryId -> navController.navigate("list/$categoryId")}
            )
        }
        composable(
            route = "list/{categoryId}",
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
//            val id = backStackEntry.arguments?.getLong("groupId") ?: return@composable
            ListScreen(
                onClick = {}
            )
        }

    }
}
