package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FighterData
import com.example.model.GameMode
import com.example.ui.theme.DarkArcadeBg
import com.example.ui.theme.DarkArcadeCard
import com.example.ui.theme.DarkArcadeSurface
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.TextMuted

@Composable
fun PostMatchScreen(
    winnerId: String,
    p1Data: FighterData,
    p2Data: FighterData,
    gameMode: GameMode,
    arcadeStage: Int,
    p1RoundsWon: Int,
    p2RoundsWon: Int,
    maxCombo: Int,
    totalDamageDealt: Int,
    onNextFight: () -> Unit,
    onRematch: () -> Unit,
    onCharacterSelect: () -> Unit,
    onMainMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isP1Winner = winnerId == "P1"
    val winner = if (isP1Winner) p1Data else p2Data
    val loser = if (isP1Winner) p2Data else p1Data

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkArcadeBg)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // RESULT HEADLINE
        Text(
            text = if (isP1Winner) "★ VICTORY ★" else "DEFEAT",
            color = if (isP1Winner) NeonGold else NeonCrimson,
            fontSize = 38.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 3.sp
        )

        Text(
            text = "${winner.name.uppercase()} WINS THE MATCH",
            color = NeonCyan,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // WINNER CELEBRATION CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, CutCornerShape(12.dp))
                .background(DarkArcadeSurface, CutCornerShape(12.dp))
                .border(2.dp, Color(winner.primaryColor), CutCornerShape(12.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Large Winner Avatar Circle
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .shadow(8.dp, CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                listOf(Color(winner.primaryColor), Color(winner.accentColor))
                            ),
                            shape = CircleShape
                        )
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = winner.name.take(1),
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = winner.name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = winner.epithet,
                    color = Color(winner.accentColor),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Victory Quote
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "\"${winner.victoryQuote}\"",
                        color = NeonGold,
                        fontSize = 13.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // MATCH STATS ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                label = "SCORE",
                value = "$p1RoundsWon - $p2RoundsWon",
                color = NeonCyan,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "MAX COMBO",
                value = "${maxOf(maxCombo, 1)} Hits",
                color = NeonGold,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "DAMAGE",
                value = "$totalDamageDealt",
                color = NeonCrimson,
                modifier = Modifier.weight(1f)
            )
        }

        // ARCADE LADDER PROGRESSION
        if (gameMode == GameMode.ARCADE) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkArcadeCard, RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ARCADE LADDER", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("STAGE $arcadeStage / 5", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color(0xFF0F172A), RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(arcadeStage / 5f)
                                .background(NeonGold, RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ACTION BUTTONS
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (gameMode == GameMode.ARCADE && isP1Winner && arcadeStage < 5) {
                Button(
                    onClick = { onNextFight() },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("next_stage_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson, contentColor = Color.White),
                    shape = CutCornerShape(6.dp)
                ) {
                    Text("NEXT LADDER OPPONENT ▶", fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = { onRematch() },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("rematch_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                shape = CutCornerShape(6.dp)
            ) {
                Text("REMATCH", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onCharacterSelect() },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("change_character_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = Color.White),
                shape = CutCornerShape(6.dp)
            ) {
                Text("SELECT FIGHTERS", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onMainMenu() },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("main_menu_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = TextMuted),
                shape = CutCornerShape(6.dp)
            ) {
                Text("MAIN MENU", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(DarkArcadeSurface, RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Black)
    }
}
