package es.rgmf.riegalgoandroid.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import es.rgmf.riegalgoandroid.R

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
            composable(route = RieGalgoScreens.Start.name) { HomeScreen() }
            composable(route = RieGalgoScreens.User.name) { UserScreen() }
        }
    }
}

private fun handleMenuItemClicked(action: RieGalgoMenuActions, navController: NavHostController) {
    Log.d("RieGalgoApp", action.name)
    when (action) {
        RieGalgoMenuActions.Home -> navController.navigate(RieGalgoScreens.Start.name)
        RieGalgoMenuActions.UserAccount -> navController.navigate(RieGalgoScreens.User.name)
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val apiViewModel: ApiViewModel = viewModel(factory = ApiViewModel.Factory)
    val uiState = apiViewModel.apiUiState

    when (uiState) {
        is ApiUiState.LoadingToken -> LoadingScreen()
        is ApiUiState.Loading -> LoadingScreen()
        is ApiUiState.Success -> {
            EphemerisScreen(
                medias = uiState.medias,
                modifier = modifier
            )
        }
        is ApiUiState.Error -> {
            ErrorScreen(
                text = uiState.message,
                retryAction = apiViewModel::getEphemeris,
                modifier = modifier
            )
        }
        is ApiUiState.ErrorAuth -> {
            AuthScreen(
                text = uiState.message,
                onLogin = { username, password ->
                    apiViewModel.login(username, password)
                },
                modifier = modifier
            )
        }
    }
}

@Composable
fun UserScreen() {
    Text(text = "User")
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
                    contentDescription = stringResource(id = R.string.user_account_button)
                )
            }
        }
    )
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.loading),
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun ErrorScreen(text: String, retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}