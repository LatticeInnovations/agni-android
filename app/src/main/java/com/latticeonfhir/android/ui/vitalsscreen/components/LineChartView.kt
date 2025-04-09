package com.latticeonfhir.core.ui.vitalsscreen.components

import android.graphics.Color
import android.graphics.DashPathEffect
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.latticeonfhir.core.theme.VitalLabel
import timber.log.Timber


@Composable
fun LineChartView(
    modifier: Modifier = Modifier,
    entries1: List<Entry>?,
    entries2: List<Entry>?,
    labels: List<String>,
    isBp: Boolean = false
) {

    Timber.d("Entries: $entries1 \nLabels: $labels")
    val chartWidth = (labels.size * 50).dp

    // Calculate Y-axis minimum and maximum values dynamically
    val allEntries = (entries1.orEmpty() + entries2.orEmpty())
    val yMin = allEntries.minOfOrNull { it.y } ?: 0f  // Minimum Y value
    val yMax = allEntries.maxOfOrNull { it.y } ?: 0f  // Maximum Y value

    // Add some padding to min and max values for better visualization
    val axisMin = yMin - 10f
    val axisMax = yMax + 10f

    // Calculate a reasonable number of Y-axis labels (e.g., between 3 to 6 labels)
    val yRange = axisMax - axisMin
    val yLabelCount = (yRange / 10).coerceIn(4f, 10f)
        .toInt()  // Adjust number of labels based on range

    val gridLineColor = MaterialTheme.colorScheme.primary.toArgb()
    val fastingColor = MaterialTheme.colorScheme.primary.toArgb()
    val randomColor = VitalLabel.toArgb()
    AndroidView(
        factory = { ctx ->
            LineChart(ctx)
        },
        update = { lineChart ->

            lineChart.apply {
                description.isEnabled = false // Disable description

                // X Axis configuration
                xAxis.xAxisConfiguration(gridLineColor, labels)

                // Y Axis (Left) configuration
                axisLeft.axisLeftConfiguration(
                    fastingColor,
                    axisMin = axisMin,
                    axisMax = axisMax,
                    yLabelCount = yLabelCount
                )

                // Y Axis (Right) configuration
                axisRight.isEnabled = false // Disable the right Y Axis

                // Additional chart appearance settings
                setTouchEnabled(false)       // Disable user interaction
                legend.isEnabled = false     // Hide legend
                setDrawBorders(false)        // No borders
                setDrawGridBackground(false) // No background grid
                // In case there's no data
                setNoDataTextColor(Color.RED)
            }
            val customValueFormatter = object : ValueFormatter() {
                override fun getPointLabel(entry: Entry?): String {
                    return entry?.y?.toInt()
                        .toString() // Convert float to integer for clean display
                }
            }
            // First line data set (e.g., Line 1)
            val lineDataSet1 =
                entries1.lineDataSet(isBp, randomColor, fastingColor, customValueFormatter)


            // Second line data set (e.g., Line 2)
            val lineDataSet2 =
                entries2.lineDateSet2(isBp, randomColor, fastingColor, customValueFormatter)

            val lineData = if (lineDataSet1 != null && lineDataSet2 != null) {
                LineData(lineDataSet1, lineDataSet2)
            } else if (lineDataSet1 != null) {
                LineData(lineDataSet1)
            } else {
                LineData(lineDataSet2)
            }


            // Assign new data to the chart and refresh
            lineChart.data = lineData
            lineChart.notifyDataSetChanged()
            lineChart.invalidate() // Redraw chart
        },
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .width(chartWidth)
            .height(300.dp) // Adjust chart size as needed
    )
}

fun XAxis.xAxisConfiguration(gridLineColor: Int, labels: List<String>) {
    this.apply {
        position = XAxis.XAxisPosition.BOTTOM
        setDrawGridLines(true)
        setDrawAxisLine(false)
        textSize = 10f
        labelCount = labels.size
        textColor = Color.GRAY
        granularity = 1f
        valueFormatter = IndexAxisValueFormatter(labels)
        gridColor = gridLineColor
        setGridDashedLine(DashPathEffect(floatArrayOf(10f, 5f), 0f))

    }
}

fun YAxis.axisLeftConfiguration(
    fastingColor: Int,
    axisMin: Float,
    axisMax: Float,
    yLabelCount: Int
) {
    this.apply {
        setDrawGridLines(true)
        setDrawAxisLine(false)
        textSize = 12f
        textColor = Color.GRAY
        axisMinimum = axisMin
        axisMaximum = axisMax
        gridColor = fastingColor
        setLabelCount(yLabelCount, true)
        setGridDashedLine(DashPathEffect(floatArrayOf(10f, 5f), 0f))
    }
}

fun List<Entry>?.lineDataSet(
    isBp: Boolean,
    randomColor: Int,
    fastingColor: Int,
    customValueFormatter: ValueFormatter
): LineDataSet? {
    var lineDatSet: LineDataSet? = null
    this?.let {
        lineDatSet = LineDataSet(this, "Line 1").apply {
            color =
                if (isBp) randomColor else fastingColor // Custom color for the first line
            lineWidth = 1f
            circleRadius = 4f
            setDrawCircles(true) // Hide data point circles
            setCircleColor(if (isBp) randomColor else fastingColor)
            setDrawCircleHole(false)    // Remove the hole from the circles
            setDrawValues(true)  // Hide values
            valueTextColor = if (isBp) randomColor else fastingColor  // Color of the value text
            valueTextSize = 10f         // Size of the value text
            valueFormatter = customValueFormatter // Display value next to dots
        }
    }
    return lineDatSet
}

fun List<Entry>?.lineDateSet2(
    isBp: Boolean,
    randomColor: Int,
    fastingColor: Int,
    customValueFormatter: ValueFormatter,
): LineDataSet? {
    var lineDatSet: LineDataSet? = null

    this?.let {
        lineDatSet = LineDataSet(it, "Line 2").apply {
            color =
                if (isBp) fastingColor else randomColor // Different color for the second line
            lineWidth = 1f
            circleRadius = 4f
            setCircleColor(if (isBp) fastingColor else randomColor)   // Circle color for random data points
            setDrawCircleHole(false)    // Remove the hole from the circles
            setDrawCircles(true) // Hide data point circles
            setDrawValues(true)  // Hide values
            valueTextColor = if (isBp) fastingColor else randomColor  // Color of the value text
            valueTextSize = 10f         // Size of the value text
            valueFormatter = customValueFormatter // Display value next to dots
        }
    }
    return lineDatSet
}

@Composable
fun LineChartViewGlucose(
    modifier: Modifier = Modifier,
    entriesRandom: List<Entry>?,   // Random glucose values
    entriesFasting: List<Entry>?,  // Fasting glucose values
    labels: List<String>
) {

    val chartWidth = (labels.size * 50).dp
    Timber.d("Entries: $entriesRandom")

    // Calculate Y-axis minimum and maximum values dynamically
    val allEntries = (entriesRandom.orEmpty() + entriesFasting.orEmpty())
    val yMin = allEntries.minOfOrNull { it.y } ?: 0f  // Minimum Y value
    val yMax = allEntries.maxOfOrNull { it.y } ?: 0f  // Maximum Y value

    // Add some padding to min and max values for better visualization
    val axisMin = yMin - 10f
    val axisMax = yMax + 10f

    // Calculate a reasonable number of Y-axis labels (e.g., between 3 to 6 labels)
    val yRange = axisMax - axisMin
    val yLabelCount =
        (yRange / 10).coerceIn(6f, 6f).toInt()  // Adjust number of labels based on range

    val gridLineColor = MaterialTheme.colorScheme.primary.toArgb()
    val fastingColor = MaterialTheme.colorScheme.primary.toArgb()
    val randomColor = VitalLabel.toArgb()
    AndroidView(
        factory = { ctx ->
            LineChart(ctx)
        },
        update = { lineChart ->

            lineChart.apply {
                description.isEnabled = false // Disable description

                // X Axis configuration
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(true)
                    setDrawAxisLine(false)
                    textSize = 10f
                    labelCount = labels.size
                    textColor = Color.GRAY
                    granularity = 1f
                    valueFormatter = IndexAxisValueFormatter(labels)
                    gridColor = gridLineColor
                    setGridDashedLine(DashPathEffect(floatArrayOf(10f, 5f), 0f))
                }

                // Y Axis (Left) configuration
                axisLeft.apply {
                    setDrawGridLines(true)
                    setDrawAxisLine(false)
                    textSize = 12f
                    textColor = Color.GRAY
                    axisMinimum = axisMin  // Set the calculated minimum
                    axisMaximum = axisMax  // Set the calculated maximum
                    gridColor = gridLineColor
                    setLabelCount(yLabelCount, true)  // Dynamic label count based on range
                    setGridDashedLine(DashPathEffect(floatArrayOf(10f, 5f), 0f))
                }

                // Y Axis (Right) configuration
                axisRight.isEnabled = false // Disable the right Y Axis

                // Additional chart appearance settings
                setTouchEnabled(false)       // Disable user interaction
                legend.isEnabled = false     // Hide legend
                setDrawBorders(false)        // No borders
                setDrawGridBackground(false) // No background grid
                setNoDataTextColor(Color.RED)
            }

            // Custom value formatter to display values on the right side of the dots
            val customValueFormatter = object : ValueFormatter() {
                override fun getPointLabel(entry: Entry?): String {
                    return entry?.y?.toInt()
                        .toString() // Convert float to integer for clean display
                }
            }

            // Random glucose data set (red dots, with values beside the dots)
            val randomDataSet = entriesRandom?.let {
                LineDataSet(entriesRandom, "Random Glucose").apply {
                    setDrawCircles(true)        // Show dots
                    setDrawValues(true)         // Show values next to the dots
                    setDrawCircleHole(false)    // Remove the hole from the circles
                    color = Color.TRANSPARENT   // No line color, make it invisible
                    circleRadius = 5f           // Circle radius
                    setCircleColor(randomColor)   // Circle color for random data points
                    lineWidth = 0f              // Disable line by setting line width to 0
                    valueTextColor = randomColor  // Color of the value text
                    valueTextSize = 10f         // Size of the value text
                    valueFormatter = customValueFormatter // Display value next to dots
//                    isDrawValuesEnabled = true  // Enable drawing values
                }
            }

            // Fasting glucose data set (blue dots, with values beside the dots)
            val fastingDataSet = entriesFasting?.let {
                LineDataSet(entriesFasting, "Fasting Glucose").apply {
                    setDrawCircles(true)        // Show dots
                    setDrawValues(true)         // Show values next to the dots
                    setDrawCircleHole(false)    // Remove the hole from the circles
                    color = Color.TRANSPARENT   // No line color, make it invisible
                    circleRadius = 5f           // Circle radius
                    setCircleColor(fastingColor)  // Circle color for fasting data points
                    lineWidth = 0f              // Disable line by setting line width to 0
                    valueTextColor = fastingColor // Color of the value text
                    valueTextSize = 10f         // Size of the value text
                    valueFormatter = customValueFormatter // Display value next to dots
//                    isDrawValuesEnabled = true  // Enable drawing values
                }
            }

            // Combine datasets if both are available
            val lineData = when {
                randomDataSet != null && fastingDataSet != null -> {
                    LineData(randomDataSet, fastingDataSet)
                }

                randomDataSet != null -> LineData(randomDataSet)
                fastingDataSet != null -> LineData(fastingDataSet)
                else -> null
            }

            // Assign new data to the chart and refresh
            if (lineData != null) {
                lineChart.data = lineData
                lineChart.notifyDataSetChanged()
                lineChart.invalidate() // Redraw chart
            }
        },
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .width(chartWidth)
            .height(300.dp) // Adjust chart size as needed
    )
}

@Preview(showBackground = true)
@Composable
private fun LineCharPreview() {
    Column {
        val entries1 = listOf(
            Entry(0f, 50f),
            Entry(1f, 60f),

            )

        // Second line data entries

        val labels = listOf(
            "14 Oct",
            "13 Oct",
            "12 Oct"
        )
        LineChartView(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            entries1 = entries1,
            entries2 = null,
            labels = labels,
        )

        val randomEntries = listOf(
            Entry(0f, 150f), // Corresponds to 24 Oct
            Entry(2f, 170f)  // Corresponds to 22 Oct
        )
        val fastingEntries = listOf(
            Entry(1f, 20f), // Corresponds to 23 Oct
            Entry(3f, 600f), // Corresponds to 21 Oct
            Entry(4f, 60f)  // Corresponds to 20 Oct
        )
        val labels2 = listOf(
            "24 Oct", "23 Oct", "22 Oct", "21 Oct", "20 Oct"
        )
        LineChartViewGlucose(
            entriesRandom = randomEntries,
            entriesFasting = fastingEntries,
            labels = labels2,
        )

    }
}
