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
import androidx.compose.runtime.mutableIntStateOf
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
fun ConfirmationScreen(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    val backgroundColor = Color(0xFF0C142B)
    val phoneColor = Color(0xFF101A35)

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

            ConfirmationTopNavigation()

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
                    text = "Confirmation",
                    fontSize = 11.sp,
                    color = Color(0xFF9AA4C1)
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            ConfirmationListeningStatus()

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Confirm action",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Screen 4/4",
                    fontSize = 11.sp,
                    color = Color(0xFF8E98B5)
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF111A30)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Turn off all lights?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "This confirmation view appears after a sensitive command. The user must explicitly say yes to accept or no to cancel and return.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = Color(0xFFAAB3CB)
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        ConfirmationChoiceCard(
                            modifier = Modifier.weight(1f),
                            title = "yes",
                            subtitle = "Execute the\naction.",
                            selected = selectedIndex == 0,
                            onClick = {
                                selectedIndex = 0
                                onConfirm()
                            }
                        )

                        ConfirmationChoiceCard(
                            modifier = Modifier.weight(1f),
                            title = "no",
                            subtitle = "Cancel and go\nback.",
                            selected = selectedIndex == 1,
                            onClick = {
                                selectedIndex = 1
                                onCancel()
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            ConfirmationVoiceHelp()
        }
    }
}

@Composable
fun ConfirmationTopNavigation() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        ConfirmationTopNavigationItem(
            text = "Dashboard",
            selected = false
        )

        Spacer(
            modifier = Modifier.size(6.dp)
        )

        ConfirmationTopNavigationItem(
            text = "Device",
            selected = false
        )

        Spacer(
            modifier = Modifier.size(6.dp)
        )

        ConfirmationTopNavigationItem(
            text = "Routine",
            selected = false
        )

        Spacer(
            modifier = Modifier.size(6.dp)
        )

        ConfirmationTopNavigationItem(
            text = "Confirm",
            selected = true
        )
    }
}

@Composable
fun ConfirmationTopNavigationItem(
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
fun ConfirmationListeningStatus() {

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
                text = "Listening: yes / no",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFD4FFF7)
            )
        }
    }
}

@Composable
fun ConfirmationChoiceCard(
    modifier: Modifier,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .height(100.dp)
            .then(
                if (selected) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color(0xFF45E3D2),
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
            containerColor = Color(0xFF1B2540)
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
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                color = Color(0xFFAAB3CB)
            )
        }
    }
}

@Composable
fun ConfirmationVoiceHelp() {

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
            text = "Voice flow: confirmation screens reduce ambiguity and make the application safer and more structured.",
            fontSize = 10.sp,
            lineHeight = 14.sp,
            color = Color(0xFFAAB4CC)
        )
    }
}