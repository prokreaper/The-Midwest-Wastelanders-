package com.example.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.FighterRoster
import com.example.ui.theme.DarkArcadeBg
import com.example.ui.theme.DarkArcadeCard
import com.example.ui.theme.DarkArcadeSurface
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted

@Composable
fun FighterDossierScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    var selectedFighter by remember { mutableStateOf(FighterRoster.AXEL_RENEGADE) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkArcadeBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
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
                    .testTag("dossier_back_button")
            ) {
                Text("◀ MENU", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = "FIGHTER DOSSIER",
                color = NeonGold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Box(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // FIGHTER SELECTION CHIPS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FighterRoster.ALL_FIGHTERS.forEach { fighter ->
                val isSelected = fighter.id == selectedFighter.id
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSelected) Color(fighter.primaryColor).copy(alpha = 0.35f) else DarkArcadeCard,
                            RoundedCornerShape(6.dp)
                        )
                        .border(
                            1.5.dp,
                            if (isSelected) Color(fighter.accentColor) else Color(0xFF334155),
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { selectedFighter = fighter }
                        .padding(vertical = 8.dp)
                        .testTag("dossier_tab_${fighter.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = fighter.name.split(" ").first(),
                        color = if (isSelected) Color.White else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // HERO BIO CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, CutCornerShape(10.dp))
                .background(DarkArcadeSurface, CutCornerShape(10.dp))
                .border(2.dp, Color(selectedFighter.primaryColor), CutCornerShape(10.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    listOf(Color(selectedFighter.primaryColor), Color(selectedFighter.accentColor))
                                ),
                                shape = CircleShape
                            )
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selectedFighter.name.take(1),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = selectedFighter.name.uppercase(),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = selectedFighter.epithet,
                            color = Color(selectedFighter.accentColor),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = selectedFighter.bio,
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // MOVE LIST & FRAME DATA TABLE
        Text(
            text = "MARTIAL ARTS ARSENAL",
            color = NeonCyan,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CommandRow(name = "Light Jab", command = "Punch", speed = "12 Frames", damage = "50 DMG", desc = "Rapid priority poke")
            CommandRow(name = "Heavy Roundhouse", command = "Kick", speed = "26 Frames", damage = "130 DMG", desc = "Hard launcher knockdown")
            CommandRow(name = "Guard / Parry", command = "Hold Guard", speed = "Instant", damage = "-82% DMG", desc = "Deflects strikes and cancels knockdown")
            CommandRow(name = "Combat Dash", command = "Dash Button", speed = "12 Frames", damage = "--", desc = "Rapid evasive reposition")
            CommandRow(
                name = selectedFighter.specialMove1.name,
                command = selectedFighter.specialMove1.command,
                speed = "30 Frames",
                damage = "${selectedFighter.specialMove1.damage.toInt()} DMG",
                desc = selectedFighter.specialMove1.description,
                isSpecial = true
            )
            CommandRow(
                name = selectedFighter.superUltraMove.name,
                command = selectedFighter.superUltraMove.command,
                speed = "45 Frames",
                damage = "${selectedFighter.superUltraMove.damage.toInt()} DMG",
                desc = selectedFighter.superUltraMove.description,
                isUltra = true
            )
        }
    }
}

@Composable
private fun CommandRow(
    name: String,
    command: String,
    speed: String,
    damage: String,
    desc: String,
    isSpecial: Boolean = false,
    isUltra: Boolean = false
) {
    val borderColor = when {
        isUltra -> NeonGold
        isSpecial -> NeonPurple
        else -> Color(0xFF334155)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkArcadeSurface, RoundedCornerShape(6.dp))
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    color = if (isUltra) NeonGold else if (isSpecial) NeonCyan else Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = command,
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(desc, color = TextMuted, fontSize = 11.sp, modifier = Modifier.weight(1f))
                Text("$speed • $damage", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
