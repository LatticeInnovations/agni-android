package com.latticeonfhir.android.ui.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    // Animation
    var isVisible by remember { mutableStateOf(true) }
    val transition = updateTransition(targetState = isVisible, label = "transition")

    val offsetY by transition.animateDp(
        transitionSpec = {
            tween(durationMillis = 1000)
        }
    ) {
        if (it) 0.dp else (-1000).dp
    }
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            // tween Animation
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                }))
        // Customize the delay time
        delay(1000L)
        isVisible = false
        navController.navigate(Screen.PhoneEmailScreen.route)
    }

    Surface(
        modifier = Modifier.fillMaxSize()
            .offset(y = offsetY),
        color = Color(0xFF21005D)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_agni_foreground),
            contentDescription = "logo",
            modifier = Modifier.scale(scale.value)
        )
    }
}