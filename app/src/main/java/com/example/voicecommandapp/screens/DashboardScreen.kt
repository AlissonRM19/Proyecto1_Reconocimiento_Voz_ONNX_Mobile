package com.example.voicecommandapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.voicecommandapp.model.Routine
import com.example.voicecommandapp.model.SmartDevice
import com.example.voicecommandapp.screens.components.CardSubtitle
import com.example.voicecommandapp.screens.components.CardTitle
import com.example.voicecommandapp.screens.components.FocusCard
import com.example.voicecommandapp.screens.components.StatusBadge

private data class DashboardItem(
    val title: String,
    val subtitle: String,
    val status: String
)

@Composable
fun DashboardScreen(
    devices: List<SmartDevice>,
    runningRoutine: Routine?,
    focusedIndex: Int,
    onItemClick: (Int) -> Unit
) {

    val items = devices.map { device ->
        DashboardItem(
            title = device.name,
            subtitle = device.subtitle,
            status = if (device.isOn) "ON" else "OFF"
        )
    } + DashboardItem(
        title = "Routines",
        subtitle = runningRoutine?.let { "${it.title}\nis running" } ?: "Study, sleep and\nwelcome scenes",
        status = if (runningRoutine != null) "RUNNING" else "OPEN"
    )

    items.chunked(2).forEachIndexed { rowIndex, rowItems ->

        if (rowIndex > 0) {
            Spacer(modifier = Modifier.height(10.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            rowItems.forEachIndexed { columnIndex, item ->

                val index = rowIndex * 2 + columnIndex

                FocusCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(125.dp),
                    focused = index == focusedIndex,
                    onClick = { onItemClick(index) }
                ) {

                    CardTitle(item.title)
                    Spacer(modifier = Modifier.height(4.dp))
                    CardSubtitle(item.subtitle)
                    Spacer(modifier = Modifier.weight(1f))
                    StatusBadge(item.status)
                }
            }

            // Mantiene el ancho de la tarjeta si la última fila queda incompleta.
            if (rowItems.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
