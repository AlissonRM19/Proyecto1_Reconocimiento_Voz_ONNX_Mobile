package com.example.voicecommandapp.screens

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
import com.example.voicecommandapp.model.Routine
import com.example.voicecommandapp.screens.components.CardSubtitle
import com.example.voicecommandapp.screens.components.CardTitle
import com.example.voicecommandapp.screens.components.FocusCard
import com.example.voicecommandapp.screens.components.StatusBadge

@Composable
fun RoutineScreen(
    routines: List<Routine>,
    focusedIndex: Int,
    onRoutineClick: (Int) -> Unit
) {

    routines.forEachIndexed { index, routine ->

        if (index > 0) {
            Spacer(modifier = Modifier.height(10.dp))
        }

        FocusCard(
            modifier = Modifier.fillMaxWidth(),
            focused = index == focusedIndex,
            onClick = { onRoutineClick(index) }
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Column(modifier = Modifier.weight(1f)) {
                    CardTitle(routine.title)
                    Spacer(modifier = Modifier.height(4.dp))
                    CardSubtitle(
                        if (routine.isRunning) "Running now · say stop to end it"
                        else routine.subtitle
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                // El badge indica el comando que aplica a esta rutina.
                StatusBadge(if (routine.isRunning) "STOP" else "GO")
            }
        }
    }
}
