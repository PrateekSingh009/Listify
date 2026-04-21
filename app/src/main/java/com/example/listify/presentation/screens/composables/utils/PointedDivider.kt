package com.example.listify.presentation.screens.composables.utils



import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp


@Composable
fun PointedDivider(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp) // The thickness in the middle
    ) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            // 1. Start at the bottom left "point"
            moveTo(0f, height)

            // 2. Draw the straight lower border to the bottom right
            lineTo(width, height)

            // 3. Draw the rounded upper border back to the start
            // The control point (width/2, -height) pulls the curve upward in the center
            quadraticBezierTo(
                x1 = width / 2f, y1 = -height,
                x2 = 0f, y2 = height
            )
            close()
        }

        drawPath(
            path = path,
            color = color
        )
    }
}