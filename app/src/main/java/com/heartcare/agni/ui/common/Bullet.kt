package com.heartcare.agni.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BulletCircle(
    color: Color
) {
    Canvas(
        modifier = Modifier
            .padding(
                start = 8.dp,
                end = 8.dp
            )
            .size(4.dp)
    ) {
        drawCircle(color = color)
    }
}