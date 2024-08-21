package es.rgmf.riegalgoandroid.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.rgmf.riegalgoandroid.R
import es.rgmf.riegalgoandroid.model.User
import es.rgmf.riegalgoandroid.ui.auth.AuthScreen
import es.rgmf.riegalgoandroid.ui.components.ErrorScreen
import es.rgmf.riegalgoandroid.ui.components.LoadingScreen

@Composable
fun UserScreen(modifier: Modifier = Modifier) {
    val userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory)
    val uiState = userViewModel.userUiState

    when (uiState) {
        is UserUiState.Loading -> LoadingScreen()
        is UserUiState.LoadingToken -> LoadingScreen()
        is UserUiState.Success -> {
            UserInfoScreen(
                user = uiState.user,
                onLogout = userViewModel::logout,
                modifier = modifier
            )
        }
        is UserUiState.Error -> {
            ErrorScreen(
                text = uiState.message,
                retryAction = userViewModel::getUser,
                modifier = modifier
            )
        }
        is UserUiState.ErrorAuth -> {
            AuthScreen(
                text = uiState.message,
                onLogin = { username, password ->
                    userViewModel.login(username, password)
                },
                modifier = modifier
            )
        }
    }
}

@Composable
fun UserInfoScreen(
    user: User,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = stringResource(id = R.string.user)
            )
            Text(text = user.username)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = stringResource(id = R.string.email)
            )
            Text(text = user.email)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogout) {
            Text(text = stringResource(id = R.string.logout))
        }
    }
}