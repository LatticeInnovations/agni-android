package com.latticeonfhir.android.ui.main.landingscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.navigation.Screen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
    ) {
        Column {
            Column(
                modifier = Modifier.verticalScroll(scrollState).weight(1f)
            ) {
                repeat(30){
                    SearchHistory()
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth().padding(top = 0.dp, bottom = 14.dp, start = 14.dp, end = 14.dp),
                onClick = {
                    navController.navigate(Screen.SearchPatientScreen.route)
                }
            ) {
                Text(text = "Advanced search")
            }
        }

        val keyboardController = LocalSoftwareKeyboardController.current
//        if (scrollState.isScrollInProgress){
//            keyboardController?.hide()
//        }
        if (scrollState.canScrollBackward)
            keyboardController?.hide()
        else
            keyboardController?.show()
    }
}

@Composable
fun SearchHistory() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
    verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(id = R.drawable.search_history), contentDescription = "")
        Spacer(modifier = Modifier.width(15.dp))
        Text(text = "List item", style = MaterialTheme.typography.bodyLarge)
    }
}