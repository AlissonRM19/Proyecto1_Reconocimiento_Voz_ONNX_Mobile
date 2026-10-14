package com.example.voicecommandapp.screens.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voicecommandapp.R
import com.example.voicecommandapp.navigation.AppScreen
import com.example.voicecommandapp.ui.theme.AppColors
import com.example.voicecommandapp.voice.VoiceCommand
import java.time.LocalTime
import java.time.format.DateTimeFormatter

enum class MicState {
    IDLE,
    LISTENING,
    PROCESSING
}

/** Marco común de las 4 pantallas: pestañas, encabezado, contenido y panel de voz. */
@Composable
fun ScreenScaffold(
    screen: AppScreen,
    title: String,
    statusLabel: String,
    confirmEnabled: Boolean,
    onNavigate: (AppScreen) -> Unit,
    micState: MicState,
    voiceMessage: String,
    feedback: String,
    showCommandPad: Boolean,
    onMicClick: () -> Unit,
    onToggleCommandPad: () -> Unit,
    onCommand: (VoiceCommand) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .systemBarsPadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .background(color = AppColors.Frame, shape = RoundedCornerShape(26.dp))
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {

            TopTabs(
                current = screen,
                confirmEnabled = confirmEnabled,
                onNavigate = onNavigate
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                    fontSize = 11.sp,
                    color = Color(0xFF9AA4C1)
                )

                Text(
                    text = statusLabel,
                    fontSize = 11.sp,
                    color = Color(0xFF9AA4C1)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ListeningPill(
                hint = screen.listeningHint,
                micState = micState
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    modifier = Modifier.weight(1f),
                    text = title,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )

                Text(
                    text = "Screen ${screen.number}/4",
                    fontSize = 11.sp,
                    color = AppColors.TextMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                content = content
            )

            Spacer(modifier = Modifier.height(12.dp))

            VoicePanel(
                help = screen.voiceHelp,
                micState = micState,
                voiceMessage = voiceMessage,
                feedback = feedback,
                showCommandPad = showCommandPad,
                onMicClick = onMicClick,
                onToggleCommandPad = onToggleCommandPad,
                onCommand = onCommand
            )
        }
    }
}

@Composable
private fun TopTabs(
    current: AppScreen,
    confirmEnabled: Boolean,
    onNavigate: (AppScreen) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AppScreen.entries.forEach { screen ->

            val selected = screen == current
            val enabled = screen != AppScreen.CONFIRMATION || confirmEnabled

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (selected) AppColors.Accent else AppColors.Chip)
                    .clickable(enabled = enabled && !selected) { onNavigate(screen) }
                    .alpha(if (enabled) 1f else 0.4f)
                    .padding(horizontal = 11.dp, vertical = 6.dp)
            ) {

                Text(
                    text = screen.tabLabel,
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = AppColors.TextPrimary
                )
            }
        }
    }
}

@Composable
private fun ListeningPill(
    hint: String,
    micState: MicState
) {

    val (dotColor, text) = when (micState) {
        MicState.LISTENING -> AppColors.Recording to "Listening… say a command now"
        MicState.PROCESSING -> AppColors.Accent to "Processing audio on device…"
        MicState.IDLE -> AppColors.ListeningDot to "Listening: $hint"
    }

    Box(
        modifier = Modifier
            .background(color = AppColors.Listening, shape = RoundedCornerShape(18.dp))
            .padding(horizontal = 13.dp, vertical = 8.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(color = dotColor, shape = CircleShape)
            )

            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.ListeningText
            )
        }
    }
}

@Composable
private fun VoicePanel(
    help: String,
    micState: MicState,
    voiceMessage: String,
    feedback: String,
    showCommandPad: Boolean,
    onMicClick: () -> Unit,
    onToggleCommandPad: () -> Unit,
    onCommand: (VoiceCommand) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AppColors.Panel, shape = RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = voiceMessage,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )

                if (feedback.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = feedback,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = AppColors.Focus
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Voice flow: $help",
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = AppColors.TextSecondary
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            MicButton(
                micState = micState,
                onClick = onMicClick
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onToggleCommandPad() }
                .padding(vertical = 4.dp),
            text = if (showCommandPad) "Hide test commands ▲" else "Test commands without mic ▼",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextMuted
        )

        if (showCommandPad) {
            Spacer(modifier = Modifier.height(6.dp))
            CommandPad(onCommand = onCommand)
        }
    }
}

@Composable
private fun MicButton(
    micState: MicState,
    onClick: () -> Unit
) {

    val pulse = rememberInfiniteTransition(label = "mic")
    val scale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(animation = tween(500), repeatMode = RepeatMode.Reverse),
        label = "micScale"
    )

    Box(
        modifier = Modifier
            .size(58.dp)
            .scale(if (micState == MicState.LISTENING) scale else 1f)
            .clip(CircleShape)
            .background(
                when (micState) {
                    MicState.LISTENING -> AppColors.Recording
                    MicState.PROCESSING -> AppColors.Badge
                    MicState.IDLE -> AppColors.Accent
                }
            )
            .clickable(enabled = micState == MicState.IDLE) { onClick() },
        contentAlignment = Alignment.Center
    ) {

        if (micState == MicState.PROCESSING) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = AppColors.TextPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_mic),
                contentDescription = "Record voice command",
                tint = AppColors.TextPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

/** Botones con los 10 comandos para probar el flujo sin micrófono ni modelo. */
@Composable
private fun CommandPad(onCommand: (VoiceCommand) -> Unit) {

    VoiceCommand.entries.chunked(5).forEach { row ->

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            row.forEach { command ->

                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppColors.Chip)
                        .clickable { onCommand(command) }
                        .padding(vertical = 7.dp),
                    text = command.label,
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            }
        }
    }
}

/** Tarjeta con borde de foco, usada por todas las pantallas. */
@Composable
fun FocusCard(
    modifier: Modifier = Modifier,
    focused: Boolean,
    containerColor: Color = AppColors.Card,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {

    val shape = RoundedCornerShape(15.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(containerColor)
            .border(
                width = 2.dp,
                color = if (focused) AppColors.Focus else Color.Transparent,
                shape = shape
            )
            .clickable { onClick() }
            .padding(14.dp),
        content = content
    )
}

@Composable
fun StatusBadge(text: String) {

    val (background, textColor) = when (text) {
        "ON", "RUNNING" -> AppColors.OnBackground to AppColors.OnText
        "OFF" -> AppColors.OffBackground to AppColors.OffText
        else -> AppColors.Badge to AppColors.TextPrimary
    }

    Box(
        modifier = Modifier
            .background(color = background, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 9.dp, vertical = 5.dp)
    ) {

        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun CardTitle(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = AppColors.TextPrimary
    )
}

@Composable
fun CardSubtitle(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        color = AppColors.TextSecondary
    )
}
