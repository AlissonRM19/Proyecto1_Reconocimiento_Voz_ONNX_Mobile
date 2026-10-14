package com.example.voicecommandapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.voicecommandapp.model.SmartDevice
import com.example.voicecommandapp.navigation.SmartHomeState
import com.example.voicecommandapp.screens.components.CardSubtitle
import com.example.voicecommandapp.screens.components.CardTitle
import com.example.voicecommandapp.screens.components.FocusCard
import com.example.voicecommandapp.screens.components.StatusBadge
import com.example.voicecommandapp.ui.theme.AppColors

@Composable
fun DeviceScreen(
    device: SmartDevice,
    focusedItem: Int,
    onItemClick: (Int) -> Unit
) {

    FocusCard(
        modifier = Modifier.fillMaxWidth(),
        focused = false,
        onClick = {}
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Column(modifier = Modifier.weight(1f)) {
                CardTitle("Current state")
                Spacer(modifier = Modifier.height(3.dp))
                CardSubtitle("${device.name} · ${device.subtitle.replace("\n", " ")}")
            }

            StatusBadge(if (device.isOn) "ON" else "OFF")
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        FocusCard(
            modifier = Modifier
                .weight(1f)
                .height(110.dp),
            focused = focusedItem == SmartHomeState.DEVICE_TURN_ON,
            onClick = { onItemClick(SmartHomeState.DEVICE_TURN_ON) }
        ) {
            Spacer(modifier = Modifier.weight(1f))
            CardTitle("Turn On")
            Spacer(modifier = Modifier.height(10.dp))
            CardSubtitle("Activate with\ncommand on.")
            Spacer(modifier = Modifier.weight(1f))
        }

        FocusCard(
            modifier = Modifier
                .weight(1f)
                .height(110.dp),
            focused = focusedItem == SmartHomeState.DEVICE_TURN_OFF,
            containerColor = AppColors.CardDanger,
            onClick = { onItemClick(SmartHomeState.DEVICE_TURN_OFF) }
        ) {
            Spacer(modifier = Modifier.weight(1f))
            CardTitle("Turn Off")
            Spacer(modifier = Modifier.height(10.dp))
            CardSubtitle("Deactivate with\ncommand off.")
            Spacer(modifier = Modifier.weight(1f))
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    DeviceMenuCard(
        title = "All devices off",
        subtitle = "Sensitive action: asks for\nconfirmation with yes.",
        badge = "YES",
        focused = focusedItem == SmartHomeState.DEVICE_ALL_OFF,
        onClick = { onItemClick(SmartHomeState.DEVICE_ALL_OFF) }
    )

    Spacer(modifier = Modifier.height(10.dp))

    DeviceMenuCard(
        title = "Back to dashboard",
        subtitle = "Return using the command no.",
        badge = "BACK",
        focused = focusedItem == SmartHomeState.DEVICE_BACK,
        onClick = { onItemClick(SmartHomeState.DEVICE_BACK) }
    )
}

@Composable
private fun DeviceMenuCard(
    title: String,
    subtitle: String,
    badge: String,
    focused: Boolean,
    onClick: () -> Unit
) {

    FocusCard(
        modifier = Modifier.fillMaxWidth(),
        focused = focused,
        onClick = onClick
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Column(modifier = Modifier.weight(1f)) {
                CardTitle(title)
                Spacer(modifier = Modifier.height(3.dp))
                CardSubtitle(subtitle)
            }

            Spacer(modifier = Modifier.size(8.dp))

            StatusBadge(badge)
        }
    }
}
