package com.example.sovereignledger.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sovereignledger.data.model.Transaction
import com.example.sovereignledger.ui.theme.BackgroundGray
import com.example.sovereignledger.ui.theme.SovereignBlue
import java.text.SimpleDateFormat
import java.util.*
import com.example.sovereignledger.ui.theme.TextSecondary
import com.example.sovereignledger.ui.theme.SovereignNavy
import com.example.sovereignledger.ui.theme.SurfaceWhite

@Composable
fun SpendingTrendChart(transactions: List<Transaction>) {
    val weeklyTotals = remember(transactions) {
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH)
        val weeklySums = mutableListOf(0.0, 0.0, 0.0, 0.0)

        transactions.filter { it.isExpense }.forEach { tx ->
            val txCal = Calendar.getInstance()
            try {
                val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
                txCal.time = sdf.parse(tx.date) ?: return@forEach
                txCal.set(Calendar.YEAR, cal.get(Calendar.YEAR))
                if (txCal.get(Calendar.MONTH) == currentMonth) {
                    val week = ((txCal.get(Calendar.DAY_OF_MONTH) - 1) / 7).coerceIn(0, 3)
                    weeklySums[week] += tx.amount
                }
            } catch (e: Exception) { }
        }
        weeklySums
    }

    val hasData = weeklyTotals.any { it > 0 }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Spending Trend",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SovereignNavy
                    )
                    val cal = Calendar.getInstance()
                    val month = SimpleDateFormat("MMM", Locale.getDefault()).format(cal.time)
                    val year = cal.get(Calendar.YEAR)
                    Text(
                        "$month 1 - $month ${cal.getActualMaximum(Calendar.DAY_OF_MONTH)}, $year",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SovereignBlue))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(BackgroundGray))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!hasData) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No trend data available", color = Color.LightGray, fontSize = 13.sp)
                }
            } else {

                //the area chart
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    val maxVal = weeklyTotals.max().coerceAtLeast(1.0)
                    val points = weeklyTotals.mapIndexed { i, v ->
                        val x = i * (size.width / 3f)
                        val y = size.height - (v / maxVal * size.height * 0.85f).toFloat()
                        Offset(x, y)
                    }

                    // Build smooth cubic bezier path
                    val linePath = Path()
                    linePath.moveTo(points[0].x, points[0].y)
                    for (i in 0 until points.size - 1) {
                        val p0 = points[i]
                        val p1 = points[i + 1]
                        val cpX = (p0.x + p1.x) / 2f
                        linePath.cubicTo(cpX, p0.y, cpX, p1.y, p1.x, p1.y)
                    }

                    // Fill path (area under curve)
                    val fillPath = Path()
                    fillPath.addPath(linePath)
                    fillPath.lineTo(points.last().x, size.height)
                    fillPath.lineTo(points.first().x, size.height)
                    fillPath.close()

                    // Draw fill
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SovereignBlue.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height
                        )
                    )

                    // Draw line
                    drawPath(
                        path = linePath,
                        color = SovereignBlue,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw dot on last point
                    drawCircle(
                        color = SovereignBlue,
                        radius = 5.dp.toPx(),
                        center = points.last()
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = points.last()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            //week labels
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("W1", "W2", "W3", "W4").forEach { label ->
                    Text(label, fontSize = 11.sp, color = TextSecondary)
                }
            }
        }
    }
}