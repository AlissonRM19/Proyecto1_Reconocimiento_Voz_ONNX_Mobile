package com.example.voicecommandapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voicecommandapp.model.ConfirmableAction
import com.example.voicecommandapp.navigation.SmartHomeState
import com.example.voicecommandapp.screens.components.CardSubtitle
import com.example.voicecommandapp.screens.components.FocusCard
import com.example.voicecommandapp.ui.theme.AppColors

@Composable
fun ConfirmationScreen(
    action: ConfirmableAction?,
    focusedChoice: Int,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AppColors.ConfirmBox, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {

        Text(
            text = action?.title ?: "Nothing to confirm",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        CardSubtitle(
            action?.description ?: "Sensitive actions will ask for confirmation here."
        )

        if (action == null) return@Column

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            ChoiceCard(
                modifier = Modifier.weight(1f),
                title = "yes",
                subtitle = "Execute the\naction.",
                focused = focusedChoice == SmartHomeState.CONFIRM_YES,
                onClick = onConfirm
            )

            ChoiceCard(
                modifier = Modifier.weight(1f),
                title = "no",
                subtitle = "Cancel and go\nback.",
                focused = focusedChoice == SmartHomeState.CONFIRM_NO,
                onClick = onCancel
            )
        }
    }
}

@Composable
private fun ChoiceCard(
    modifier: Modifier,
    title: String,
    subtitle: String,
    focused: Boolean,
    onClick: () -> Unit
) {

    FocusCard(
        modifier = modifier.height(100.dp),
        focused = focused,
        onClick = onClick
    ) {

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        CardSubtitle(subtitle)

        Spacer(modifier = Modifier.weight(1f))
    }
}
