package es.rgmf.riegalgoandroid.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import es.rgmf.riegalgoandroid.R
import es.rgmf.riegalgoandroid.ui.ephemeris.EphemerisScreen
import es.rgmf.riegalgoandroid.ui.user.UserScreen

@Composable
fun RieGalgoApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = RieGalgoScreens.valueOf(
        backStackEntry?.destination?.route ?: RieGalgoScreens.Start.name
    )

    Scaffold(
        topBar = {
            RieGalgoTopBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                menuItemClicked = { handleMenuItemClicked(it, navController) }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = RieGalgoScreens.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = RieGalgoScreens.Start.name) { EphemerisScreen() }
            composable(route = RieGalgoScreens.User.name) { UserScreen() }
        }
    }
}

private fun handleMenuItemClicked(action: RieGalgoMenuActions, navController: NavHostController) {
    when (action) {
        RieGalgoMenuActions.Home -> {
            navController.popBackStack()
            navController.navigate(RieGalgoScreens.Start.name)
        }
        RieGalgoMenuActions.UserAccount -> {
            navController.popBackStack()
            navController.navigate(RieGalgoScreens.User.name)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RieGalgoTopBar(
    currentScreen: RieGalgoScreens,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    menuItemClicked: (RieGalgoMenuActions) -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(id = currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button)
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { menuItemClicked(RieGalgoMenuActions.Home) }) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = stringResource(id = R.string.home_button)
                )
            }
            IconButton(onClick = { menuItemClicked(RieGalgoMenuActions.UserAccount) }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = stringResource(id = R.string.user)
                )
            }
        }
    )
}