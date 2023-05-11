package com.latticeonfhir.android.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ButtonLoader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center,
    ){
        CircularProgressIndicator(
            modifier = Modifier
                .width(20.dp)
                .height(20.dp),
            strokeWidth = 3.dp,
            color = MaterialTheme.colorScheme.primaryContainer
        )
    }
}