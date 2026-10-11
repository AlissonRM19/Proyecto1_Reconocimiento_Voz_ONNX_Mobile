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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voicecommandapp.model.HomeOption
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    onDeviceSelected: (String) -> Unit
) {

    val options = listOf(
        HomeOption(
            title = "Lights",
            subtitle = "Living room and\nbedroom lights",
            status = "ON",
            icon = Icons.Default.Home
        ),
        HomeOption(
            title = "Fan",
            subtitle = "Air circulation\nand comfort mode",
            status = "OFF",
            icon = Icons.Default.Settings
        ),
        HomeOption(
            title = "TV",
            subtitle = "Media and\nambient screen",
            status = "OFF",
            icon = Icons.Default.Info
        ),
        HomeOption(
            title = "Routines",
            subtitle = "Study, sleep and\nwelcome scenes",
            status = "OPEN",
            icon = Icons.Default.PlayArrow
        )
    )

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

            DashboardTopNavigation()

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            DashboardPhoneStatus()

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            DashboardListeningStatus()

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Home Dashboard",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Screen 1/4",
                    fontSize = 11.sp,
                    color = Color(0xFF8E98B5)
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            LazyVerticalGrid(
                modifier = Modifier.height(270.dp),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                userScrollEnabled = false
            ) {

                itemsIndexed(options) { index, option ->

                    DashboardCard(
                        option = option,
                        selected = index == selectedIndex,
                        onClick = {

                            if (selectedIndex == index) {

                                onDeviceSelected(option.title)

                            } else {

                                selectedIndex = index
                            }
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            DashboardVoiceHelp()
        }
    }
}

@Composable
fun DashboardTopNavigation() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        TopNavigationItem(
            text = "Dashboard",
            selected = true
        )

        Spacer(
            modifier = Modifier.size(6.dp)
        )

        TopNavigationItem(
            text = "Device",
            selected = false
        )

        Spacer(
            modifier = Modifier.size(6.dp)
        )

        TopNavigationItem(
            text = "Routine",
            selected = false
        )

        Spacer(
            modifier = Modifier.size(6.dp)
        )

        TopNavigationItem(
            text = "Confirm",
            selected = false
        )
    }
}

@Composable
fun TopNavigationItem(
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
fun DashboardPhoneStatus() {

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
            text = "Voice Ready",
            fontSize = 11.sp,
            color = Color(0xFF9AA4C1)
        )
    }
}

@Composable
fun DashboardListeningStatus() {

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
                text = "Listening: navigation commands",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFD4FFF7)
            )
        }
    }
}

@Composable
fun DashboardCard(
    option: HomeOption,
    selected: Boolean,
    onClick: () -> Unit
) {

    val cardColor = Color(0xFF1B2540)
    val selectedColor = Color(0xFF45E3D2)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(125.dp)
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
            containerColor = cardColor
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column {

                Text(
                    text = option.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = option.subtitle,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFFAAB3CB)
                )
            }

            DashboardStatusIndicator(
                status = option.status
            )
        }
    }
}

@Composable
fun DashboardStatusIndicator(
    status: String
) {

    val backgroundColor = when (status) {

        "ON" -> Color(0xFF164B52)

        "OFF" -> Color(0xFF4A2935)

        "OPEN" -> Color(0xFF343E59)

        else -> Color(0xFF343E59)
    }

    val textColor = when (status) {

        "ON" -> Color(0xFF77EEE0)

        "OFF" -> Color(0xFFFFA9B9)

        else -> Color(0xFFD5DCF1)
    }

    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(
                horizontal = 9.dp,
                vertical = 4.dp
            )
    ) {

        Text(
            text = status,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun DashboardVoiceHelp() {

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

            text = "Voice flow: use up/down/left/right to move the focus and yes to open the selected card.",

            fontSize = 10.sp,

            lineHeight = 14.sp,

            color = Color(0xFFAAB4CC)
        )
    }
}