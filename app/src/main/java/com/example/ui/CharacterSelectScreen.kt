package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiDifficulty
import com.example.model.Arenas
import com.example.model.FighterData
import com.example.model.FighterRoster
import com.example.model.GameMode
import com.example.model.StageArena
import com.example.ui.theme.DarkArcadeBg
import com.example.ui.theme.DarkArcadeCard
import com.example.ui.theme.DarkArcadeSurface
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted

@Composable
fun CharacterSelectScreen(
    gameMode: GameMode,
    onStartFight: (p1: FighterData, p2: FighterData, arena: StageArena, difficulty: AiDifficulty) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedP1 by remember { mutableStateOf(FighterRoster.AXEL_RENEGADE) }
    var selectedP2 by remember {
        mutableStateOf(
            if (gameMode == GameMode.FINAL_CLASH) FighterRoster.ABYSSAL_QUEEN
            else if (gameMode == GameMode.ARCADE) FighterRoster.RADIO_CREEP
            else FighterRoster.DJ_REBEL
        )
    }
    var selectedArena by remember { mutableStateOf(Arenas.RENEGADE_ALLEY) }
    var selectedDifficulty by remember { mutableStateOf(AiDifficulty.MEDIUM) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkArcadeBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .shadow(4.dp, CutCornerShape(4.dp))
                    .background(DarkArcadeSurface, CutCornerShape(4.dp))
                    .border(1.dp, Color(0xFF334155), CutCornerShape(4.dp))
                    .clickable { onBack() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("back_button")
            ) {
                Text("◀ BACK", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = "SELECT YOUR FIGHTER",
                color = NeonGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            // Mode indicator tag
            Box(
                modifier = Modifier
                    .background(Color(0xFF1E293B), RoundedCornerShape(4.dp))
                    .border(1.dp, NeonCyan, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = gameMode.name.replace("_", " "),
                    color = NeonCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FIGHTER SELECTION GRID
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FighterRoster.PLAYABLE_FIGHTERS.forEach { fighter ->
                val isSelected = fighter.id == selectedP1.id
                FighterAvatarCard(
                    fighter = fighter,
                    isSelected = isSelected,
                    onClick = { selectedP1 = fighter }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SELECTED FIGHTER SPOTLIGHT CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, CutCornerShape(10.dp))
                .background(DarkArcadeSurface, CutCornerShape(10.dp))
                .border(2.dp, Color(selectedP1.primaryColor), CutCornerShape(10.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = selectedP1.name.uppercase(),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = selectedP1.epithet,
                            color = Color(selectedP1.accentColor),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ARCHETYPE: ${selectedP1.archetype.uppercase()}",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Fighter emblem circle
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(6.dp, CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    listOf(Color(selectedP1.primaryColor), Color(selectedP1.accentColor))
                                ),
                                shape = CircleShape
                            )
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selectedP1.name.take(1),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // STATS RADAR BARS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBar(label = "PWR", value = selectedP1.power, color = NeonCrimson, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    StatBar(label = "SPD", value = selectedP1.speed, color = NeonCyan, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    StatBar(label = "DEF", value = selectedP1.defense, color = NeonGold, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    StatBar(label = "RNG", value = selectedP1.range, color = NeonPurple, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SPECIAL & ULTRA MOVES PREVIEW
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚡ SPECIAL: ", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("${selectedP1.specialMove1.name} ", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("(${selectedP1.specialMove1.command})", color = TextMuted, fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💥 ULTRA: ", color = NeonGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("${selectedP1.superUltraMove.name} ", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("(${selectedP1.superUltraMove.command})", color = TextMuted, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Intro quote preview
                Text(
                    text = "\"${selectedP1.introDialogue}\"",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // OPPONENT & STAGE SELECT (For non-arcade modes)
        if (gameMode != GameMode.ARCADE) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkArcadeSurface, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("CPU OPPONENT", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FighterRoster.ALL_FIGHTERS.forEach { fighter ->
                        val isOppSelected = fighter.id == selectedP2.id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isOppSelected) Color(fighter.primaryColor).copy(alpha = 0.3f) else Color(0xFF1E293B),
                                    RoundedCornerShape(4.dp)
                                )
                                .border(
                                    1.5.dp,
                                    if (isOppSelected) Color(fighter.primaryColor) else Color(0xFF334155),
                                    RoundedCornerShape(4.dp)
                                )
                                .clickable { selectedP2 = fighter }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = fighter.name.split(" ").first(),
                                color = if (isOppSelected) Color.White else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text("AI DIFFICULTY", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AiDifficulty.values().forEach { diff ->
                        val isDiffSelected = diff == selectedDifficulty
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isDiffSelected) NeonCyan.copy(alpha = 0.25f) else Color(0xFF1E293B),
                                    RoundedCornerShape(4.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isDiffSelected) NeonCyan else Color(0xFF334155),
                                    RoundedCornerShape(4.dp)
                                )
                                .clickable { selectedDifficulty = diff }
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = diff.label,
                                color = if (isDiffSelected) NeonCyan else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // START FIGHT BUTTON
        Button(
            onClick = {
                val opponent = if (gameMode == GameMode.FINAL_CLASH) FighterRoster.ABYSSAL_QUEEN
                    else if (gameMode == GameMode.ARCADE) FighterRoster.RADIO_CREEP
                    else selectedP2
                onStartFight(selectedP1, opponent, selectedArena, selectedDifficulty)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(12.dp, CutCornerShape(8.dp))
                .testTag("fight_start_button"),
            shape = CutCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonCrimson,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "⚡ ENTER THE ARENA ⚡",
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun FighterAvatarCard(
    fighter: FighterData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag("fighter_select_${fighter.id}")
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .shadow(if (isSelected) 10.dp else 2.dp, RoundedCornerShape(10.dp))
                .background(
                    if (isSelected) Color(fighter.primaryColor).copy(alpha = 0.35f) else DarkArcadeCard,
                    RoundedCornerShape(10.dp)
                )
                .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) Color(fighter.accentColor) else Color(0xFF334155),
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (fighter.imageResId != null) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = fighter.imageResId),
                    contentDescription = fighter.name,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp))
                )
            } else {
                Text(
                    text = fighter.name.take(1),
                    color = if (isSelected) Color.White else TextMuted,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = fighter.name.split(" ").first(),
            color = if (isSelected) NeonGold else TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatBar(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text("$value/5", color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color(0xFF0F172A), RoundedCornerShape(2.dp))
                .clip(RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(value / 5f)
                    .background(color)
            )
        }
    }
}
