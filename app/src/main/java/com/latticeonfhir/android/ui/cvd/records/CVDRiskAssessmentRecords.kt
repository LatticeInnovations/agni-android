package com.latticeonfhir.android.ui.cvd.records

import android.graphics.Color
import android.graphics.DashPathEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
import com.latticeonfhir.android.ui.cvd.CVDRiskAssessmentViewModel
import com.latticeonfhir.android.ui.theme.Black
import com.latticeonfhir.android.ui.theme.White
import com.latticeonfhir.android.ui.vitalsscreen.toEpocDays
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.formatDateToDayMonth
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toddMMMyyyy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.ceil

@Composable
fun CVDRiskAssessmentRecords(
    viewModel: CVDRiskAssessmentViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.getRecords()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (viewModel.previousRecords.groupBy {
                it.createdOn.formatDateToDayMonth()
            }.size < 2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = if (isSystemInDarkTheme()) Black else White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.graph_info),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(20.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // line chart
            LineChartView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                    .height(200.dp),
                entries1 = getChartEntries(viewModel.previousRecords.reversed().groupBy {
                    it.createdOn.formatDateToDayMonth()
                }.map { (_, entries) ->
                    entries.maxBy { it.createdOn }
                }),
                entries2 = null
            )
        }
        if (viewModel.previousRecords.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = stringResource(R.string.no_record_available),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 120.dp)
                )
            }
        } else {
            Text(
                text = stringResource(R.string.total_records),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            )
            viewModel.previousRecords.forEach { record ->
                RecordDetailsComposable(
                    record = record,
                    onClick = {
                        viewModel.selectedRecord = record
                    }
                )
            }
        }
    }
}

@Composable
private fun RecordDetailsComposable(
    record: CVDResponse,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.percentage, record.risk.toString()),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = record.createdOn.toddMMMyyyy(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun LineChartView(
    modifier: Modifier = Modifier,
    entries1: List<Entry>?,
    entries2: List<Entry>?
) {
    // Calculate Y-axis minimum and maximum values dynamically
    val allEntries = (entries1.orEmpty() + entries2.orEmpty())
    val yMax = allEntries.maxOfOrNull { it.y } ?: 0f  // Maximum Y value

    // Add some padding to min and max values for better visualization
    val axisMin = 0f
    val roundedMaxY = (ceil(yMax / 10) * 10)

    val gridLineColor = MaterialTheme.colorScheme.primary.toArgb()
    val fastingColor = MaterialTheme.colorScheme.primary.toArgb()
    val circleHoleColorArgb = MaterialTheme.colorScheme.inversePrimary.toArgb()
    val randomColor = androidx.compose.ui.graphics.Color(0xFFCB4470).toArgb()
    AndroidView(
        factory = { ctx ->
            LineChart(ctx)
        },
        update = { lineChart ->

            lineChart.apply {
                description.isEnabled = false // Disable description

                val xAxisValueFormatter = object : ValueFormatter() {

                    private val dateFormat =
                        SimpleDateFormat("dd MMM", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }

                    override fun getFormattedValue(value: Float): String {
                        val timeUTC = value.toLong() * (1000L * 60L * 60L * 24L)
                        return dateFormat.format(Date(timeUTC))
                    }
                }

                // X Axis configuration
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(true)
                    setDrawAxisLine(false)
                    textSize = 10f
                    textColor = Color.GRAY
                    granularity = 1f
                    valueFormatter = xAxisValueFormatter
                    gridColor = gridLineColor
                    setGridDashedLine(DashPathEffect(floatArrayOf(10f, 5f), 0f))
                }

                // Y Axis (Left) configuration
                axisLeft.apply {
                    setDrawGridLines(true)
                    setDrawAxisLine(false)
                    textSize = 10f
                    textColor = Color.GRAY
                    axisMinimum = axisMin
                    axisMaximum = roundedMaxY
                    granularity = 10f
                    gridColor = fastingColor
                    valueFormatter = PercentageValueFormatter()
                    setGridDashedLine(DashPathEffect(floatArrayOf(10f, 5f), 0f))
                }

                // Y Axis (Right) configuration
                axisRight.isEnabled = false // Disable the right Y Axis

                // Additional chart appearance settings
                legend.isEnabled = false     // Hide legend
                setDrawBorders(false)        // No borders
                setDrawGridBackground(false) // No background grid
                setNoDataTextColor(Color.RED)

                setTouchEnabled(true)

                isDragEnabled = true
                setScaleEnabled(true)

                setPinchZoom(true)

                isScaleXEnabled = true
                isScaleYEnabled = false
            }
            // First line data set (e.g., Line 1)
            val lineDataSet1 = entries1?.let {
                LineDataSet(entries1, "Line 1").apply {
                    color = fastingColor // Custom color for the first line
                    lineWidth = 1.5f
                    circleRadius = 4f
                    circleHoleRadius = 3f
                    circleHoleColor = circleHoleColorArgb
                    setDrawCircles(true) // Hide data point circles
                    setCircleColor(fastingColor)
                    setDrawCircleHole(true)// Remove the hole from the circles
                    setDrawValues(false)  // Hide values
                }
            }

            // Second line data set (e.g., Line 2)
            val lineDataSet2 = entries2?.let {
                LineDataSet(it, "Line 2").apply {
                    color = randomColor // Different color for the second line
                    lineWidth = 1f
                    circleRadius = 4f
                    setCircleColor(randomColor)   // Circle color for random data points
                    setDrawCircleHole(false)    // Remove the hole from the circles
                    setDrawCircles(true) // Hide data point circles
                    setDrawValues(false)  // Hide values
                }
            }
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
            .fillMaxWidth()
            .height(300.dp) // Adjust chart size as needed
    )
}


fun getChartEntries(list: List<CVDResponse>): List<Entry> {

    return list
        .groupBy { it.createdOn.toEpocDays() }
        .mapNotNull { (date, records) ->

            val averageRisk = records
                .map { it.risk.toFloat() }
                .average()

            if (averageRisk.isNaN()) {
                null
            } else {
                Entry(date.toFloat(), averageRisk.toFloat())
            }
        }
        .sortedBy { it.x }
}

class PercentageValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.toInt()}%" // Converts value to integer and appends '%'
    }
}