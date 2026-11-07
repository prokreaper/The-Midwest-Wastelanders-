package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ComboRoute
import com.example.model.FighterData
import com.example.model.FighterRoster
import com.example.model.MoveFrameData
import com.example.ui.theme.DarkArcadeBg
import com.example.ui.theme.DarkArcadeCard
import com.example.ui.theme.DarkArcadeSurface
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted

@Composable
fun CharacterBreakdownLabScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    var selectedFighter by remember { mutableStateOf(FighterRoster.AXEL_RENEGADE) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview & Anatomy, 1: Frame Data, 2: Combo Trials

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkArcadeBg)
            .padding(14.dp)
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
                    .testTag("lab_back_button")
            ) {
                Text("◀ BACK", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = "FIGHTER BREAKDOWN LAB",
                color = NeonGold,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Box(
                modifier = Modifier
                    .background(NeonCrimson.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .border(1.dp, NeonCrimson, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = selectedFighter.threatLevel,
                    color = NeonCrimson,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // FIGHTER SELECTION STRIP
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FighterRoster.ALL_FIGHTERS.forEach { fighter ->
                val isSelected = fighter.id == selectedFighter.id
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(6.dp))
                        .background(
                            if (isSelected) Color(fighter.primaryColor).copy(alpha = 0.35f) else DarkArcadeCard,
                            RoundedCornerShape(6.dp)
                        )
                        .border(
                            1.5.dp,
                            if (isSelected) Color(fighter.primaryColor) else Color(0xFF334155),
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { selectedFighter = fighter }
                        .padding(vertical = 8.dp)
                        .testTag("lab_select_${fighter.id}"),
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

        Spacer(modifier = Modifier.height(14.dp))

        // HERO POSTER / ARTWORK BANNER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .shadow(12.dp, CutCornerShape(10.dp))
                .clip(CutCornerShape(10.dp))
                .border(2.dp, Color(selectedFighter.primaryColor), CutCornerShape(10.dp))
        ) {
            if (selectedFighter.imageResId != null) {
                Image(
                    painter = painterResource(id = selectedFighter.imageResId!!),
                    contentDescription = selectedFighter.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Dark vignette overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0x660B0F19), Color(0xEE0B0F19))
                        )
                    )
            )

            // Fighter Details Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = selectedFighter.name.uppercase(),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = selectedFighter.epithet,
                    color = Color(selectedFighter.accentColor),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "STYLE: ${selectedFighter.fightingStyle}",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // TAB BAR: 0: Overview & Anatomy, 1: Frame Data, 2: Combo Trials
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkArcadeSurface,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonGold,
                    height = 3.dp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("ANATOMY & STATS", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("FRAME DATA", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("COMBO TRIALS", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // TAB CONTENT
        when (selectedTab) {
            0 -> AnatomyAndStatsTab(fighter = selectedFighter)
            1 -> FrameDataTab(fighter = selectedFighter)
            2 -> ComboTrialsTab(fighter = selectedFighter)
        }
    }
}

@Composable
private fun AnatomyAndStatsTab(fighter: FighterData) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // HITBOX & ANATOMY BREAKDOWN CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(8.dp))
                .background(DarkArcadeSurface, RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = NeonCyan)
                    Text(
                        text = "ANATOMY & HITBOX ARCHITECTURE",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Visual Hitbox Model
                    Box(
                        modifier = Modifier
                            .size(130.dp, 160.dp)
                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val cx = size.width / 2f
                            val cy = size.height / 2f

                            // Head Hitbox (Yellow/Red - Critical zone)
                            drawCircle(
                                color = Color(0x66FF1744),
                                radius = 24f,
                                center = Offset(cx, cy - 46f)
                            )
                            drawCircle(
                                color = Color(0xFFFF1744),
                                radius = 24f,
                                center = Offset(cx, cy - 46f),
                                style = Stroke(width = 2f)
                            )

                            // Torso Hurtbox (Cyan - Armor core)
                            drawRoundRect(
                                color = Color(0x4400E5FF),
                                topLeft = Offset(cx - 24f, cy - 18f),
                                size = Size(48f, 52f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                            )
                            drawRoundRect(
                                color = Color(0xFF00E5FF),
                                topLeft = Offset(cx - 24f, cy - 18f),
                                size = Size(48f, 52f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                                style = Stroke(width = 2f)
                            )

                            // Limb/Reach Hitboxes (Green - Strike zones)
                            drawCircle(
                                color = Color(0x5510B981),
                                radius = 18f,
                                center = Offset(cx + 36f, cy - 10f)
                            )
                            drawCircle(
                                color = Color(0xFF10B981),
                                radius = 18f,
                                center = Offset(cx + 36f, cy - 10f),
                                style = Stroke(width = 2f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Hitbox Parameters List
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HitboxParamRow(
                            label = "HEAD CRIT",
                            value = "${fighter.anatomy.headCritMultiplier}x Damage",
                            color = NeonCrimson,
                            note = "Vulnerable to jump-ins"
                        )
                        HitboxParamRow(
                            label = "GUARD ARMOR",
                            value = "${fighter.anatomy.guardBreakThreshold.toInt()} pts",
                            color = NeonCyan,
                            note = "82% strike mitigation"
                        )
                        HitboxParamRow(
                            label = "ACTIVE REACH",
                            value = "${fighter.range * 24}px Arc",
                            color = NeonGold,
                            note = "Optimal spacing distance"
                        )
                    }
                }
            }
        }

        // COMBAT RADAR STATS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(8.dp))
                .background(DarkArcadeSurface, RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = NeonGold)
                    Text(
                        text = "STREET BRAWLER PERFORMANCE RADAR",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                RadarStatBar("Lethality / Damage Potential", fighter.radarStats.lethality, NeonCrimson)
                RadarStatBar("Mobility / Dash Velocity", fighter.radarStats.mobility, NeonCyan)
                RadarStatBar("Durability / Armor Rating", fighter.radarStats.durability, NeonGold)
                RadarStatBar("Combo Potential / Cancel Depth", fighter.radarStats.comboPotential, NeonPurple)
                RadarStatBar("Execution Complexity", fighter.radarStats.executionDifficulty, Color(0xFF10B981))
            }
        }
    }
}

@Composable
private fun FrameDataTab(fighter: FighterData) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("MOVE / COMMAND", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
            Text("STARTUP", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f))
            Text("ON BLOCK", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
            Text("ON HIT", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
            Text("DMG", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
        }

        // Move rows
        fighter.moveFrameDataList.forEach { move ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkArcadeSurface, RoundedCornerShape(6.dp))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(6.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text(move.moveName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(move.command, color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }

                    Text("${move.startup}f", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f))

                    // On Block badge
                    val isAdvantage = move.onBlock.startsWith("+")
                    val isPunishable = move.onBlock.contains("-1") || move.onBlock.contains("-2")
                    val blockColor = if (isAdvantage) Color(0xFF10B981) else if (isPunishable) NeonCrimson else NeonGold
                    Text(
                        text = move.onBlock,
                        color = blockColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.weight(0.9f)
                    )

                    Text(
                        text = move.onHit,
                        color = if (move.onHit.contains("Knockdown") || move.onHit.contains("Crush") || move.onHit.contains("Fatal")) NeonGold else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.9f)
                    )

                    Text(
                        text = "${move.damage}",
                        color = NeonCrimson,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.weight(0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ComboTrialsTab(fighter: FighterData) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "RECOMMENDED COMBAT COMBOS & STRATEGIES",
            color = NeonGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black
        )

        fighter.comboRoutes.forEach { combo ->
            val diffColor = when (combo.difficulty) {
                "Fatal" -> NeonCrimson
                "Intermediate" -> NeonGold
                else -> NeonCyan
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(8.dp))
                    .background(DarkArcadeSurface, RoundedCornerShape(8.dp))
                    .border(1.5.dp, diffColor, RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(combo.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Box(
                            modifier = Modifier
                                .background(diffColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .border(1.dp, diffColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(combo.difficulty.uppercase(), color = diffColor, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Input chain display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = combo.inputChain,
                            color = NeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(combo.notes, color = TextMuted, fontSize = 11.sp, modifier = Modifier.weight(1f))
                        Text(
                            text = "EST. ${combo.damage} DMG",
                            color = NeonCrimson,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HitboxParamRow(label: String, value: String, color: Color, note: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
        Text(note, color = Color(0xFF64748B), fontSize = 9.sp)
    }
}

@Composable
private fun RadarStatBar(label: String, value: Int, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text("$value / 100", color = color, fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color(0xFF0F172A), RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(value / 100f)
                    .background(color)
            )
        }
    }
}
