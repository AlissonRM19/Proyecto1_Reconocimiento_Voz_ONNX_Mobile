package com.example.voicecommandapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun DeviceScreen(
    deviceName: String,
    onBack: () -> Unit,
    onRequestConfirmation: () -> Unit
) {

    var isOn by remember {
        mutableStateOf(true)
    }

    var selectedAction by remember {
        mutableStateOf("ON")
    }

    val backgroundColor = Color(0xFF0C142B)
    val phoneColor = Color(0xFF101A35)
    val cardColor = Color(0xFF1B2540)
    val selectedColor = Color(0xFF45E3D2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 22.dp,
                    vertical = 18.dp
                )
                .background(
                    color = phoneColor,
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(
                    horizontal = 18.dp,
                    vertical = 16.dp
                )
        ) {

            DeviceTopNavigation()

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = LocalTime.now().format(
                        DateTimeFormatter.ofPattern("HH:mm")
                    ),
                    fontSize = 11.sp,
                    color = Color(0xFF9AA4C1)
                )

                Text(
                    text = "Device Control",
                    fontSize = 11.sp,
                    color = Color(0xFF9AA4C1)
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            DeviceListeningStatus()

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = if (deviceName == "Lights") {
                        "Living Room Lights"
                    } else {
                        deviceName
                    },
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Screen 2/4",
                    fontSize = 11.sp,
                    color = Color(0xFF8E98B5)
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            CurrentStateCard(
                isOn = isOn
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                DeviceActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Turn On",
                    subtitle = "Activate with\ncommand on.",
                    selected = selectedAction == "ON",
                    selectedColor = selectedColor,
                    containerColor = cardColor,
                    onClick = {
                        selectedAction = "ON"
                        isOn = true
                    }
                )

                DeviceActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Turn Off",
                    subtitle = "Deactivate with\ncommand off.",
                    selected = selectedAction == "OFF",
                    selectedColor = selectedColor,
                    containerColor = Color(0xFF302634),
                    onClick = {
                        selectedAction = "OFF"
                        isOn = false
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            DeviceMenuCard(
                title = "Brightness mode",
                subtitle = "Adjust scene intensity after\nconfirmation.",
                badge = "YES",
                onClick = {
                    onRequestConfirmation()
                }
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            DeviceMenuCard(
                title = "Back to dashboard",
                subtitle = "Return using the command no.",
                badge = "BACK",
                onClick = {
                    onBack()
                }
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            DeviceVoiceHelp()
        }
    }
}

@Composable
fun DeviceTopNavigation() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        DeviceTopNavigationItem(
            text = "Dashboard",
            selected = false
        )

        Spacer(
            modifier = Modifier.size(6.dp)
        )

        DeviceTopNavigationItem(
            text = "Device",
            selected = true
        )

        Spacer(
            modifier = Modifier.size(6.dp)
        )

        DeviceTopNavigationItem(
            text = "Routine",
            selected = false
        )

        Spacer(
            modifier = Modifier.size(6.dp)
        )

        DeviceTopNavigationItem(
            text = "Confirm",
            selected = false
        )
    }
}

@Composable
fun DeviceTopNavigationItem(
    text: String,
    selected: Boolean
) {

    Box(
        modifier = Modifier
            .background(
                color = if (selected) {
                    Color(0xFF7D83F7)
                } else {
                    Color(0xFF202A46)
                },
                shape = RoundedCornerShape(14.dp)
            )
            .padding(
                horizontal = 11.dp,
                vertical = 6.dp
            )
    ) {

        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            },
            color = Color.White
        )
    }
}

@Composable
fun DeviceListeningStatus() {

    Box(
        modifier = Modifier
            .background(
                color = Color(0xFF143D4B),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(
                horizontal = 13.dp,
                vertical = 8.dp
            )
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(
                        color = Color(0xFF50E3C2),
                        shape = CircleShape
                    )
            )

            Spacer(
                modifier = Modifier.size(6.dp)
            )

            Text(
                text = "Listening: on / off / yes / no",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFD4FFF7)
            )
        }
    }
}

@Composable
fun CurrentStateCard(
    isOn: Boolean
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B2540)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "Current state",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "Primary device selected",
                    fontSize = 11.sp,
                    color = Color(0xFFAAB3CB)
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        color = if (isOn) {
                            Color(0xFF164B52)
                        } else {
                            Color(0xFF4A2935)
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(
                        horizontal = 9.dp,
                        vertical = 5.dp
                    )
            ) {

                Text(
                    text = if (isOn) "ON" else "OFF",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOn) {
                        Color(0xFF77EEE0)
                    } else {
                        Color(0xFFFFA9B9)
                    }
                )
            }
        }
    }
}

@Composable
fun DeviceActionCard(
    modifier: Modifier,
    title: String,
    subtitle: String,
    selected: Boolean,
    selectedColor: Color,
    containerColor: Color,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .height(110.dp)
            .then(
                if (selected) {
                    Modifier.border(
                        width = 2.dp,
                        color = selectedColor,
                        shape = RoundedCornerShape(15.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(15.dp),

        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = Color(0xFFAAB3CB)
            )
        }
    }
}

@Composable
fun DeviceMenuCard(
    title: String,
    subtitle: String,
    badge: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(15.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B2540)
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = Color(0xFFAAB3CB)
                )
            }

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFF303D64),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(
                        horizontal = 9.dp,
                        vertical = 5.dp
                    )
            ) {

                Text(
                    text = badge,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun DeviceVoiceHelp() {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(14.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A243D)
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Text(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 12.dp
            ),

            text = "Voice flow: on/off control the selected device; yes confirms a highlighted action; no returns.",

            fontSize = 10.sp,

            lineHeight = 14.sp,

            color = Color(0xFFAAB4CC)
        )
    }
}