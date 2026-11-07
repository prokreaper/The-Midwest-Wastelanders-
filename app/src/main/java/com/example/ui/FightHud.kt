package com.example.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.CombatEngine
import com.example.model.FighterInstance
import com.example.model.GameMode
import com.example.model.RoundState
import com.example.ui.theme.DarkArcadeBg
import com.example.ui.theme.HealthGreen
import com.example.ui.theme.HealthYellowDelayed
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuperMeterBlue
import com.example.ui.theme.SuperMeterMax

@Composable
fun FightHud(
    engine: CombatEngine,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hudPulse")
    val maxPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "maxPulse"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // TOP HUD: Health Bars, Timer, Round Badges
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // P1 HEALTH BAR (fills from right to left)
                Box(modifier = Modifier.weight(1f)) {
                    FighterHealthBar(
                        fighter = engine.p1,
                        isReversed = true
                    )
                }

                // CENTER ROUND TIMER
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(54.dp)
                        .shadow(8.dp, CutCornerShape(8.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                            ),
                            shape = CutCornerShape(8.dp)
                        )
                        .border(1.5.dp, NeonCyan, CutCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (engine.mode == GameMode.DOJO_TRAINING) "∞" else "${engine.roundTimerSeconds}",
                        color = if (engine.roundTimerSeconds <= 10 && engine.mode != GameMode.DOJO_TRAINING) NeonCrimson else NeonGold,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // P2 HEALTH BAR (fills from left to right)
                Box(modifier = Modifier.weight(1f)) {
                    FighterHealthBar(
                        fighter = engine.p2,
                        isReversed = false
                    )
                }
            }

            // SUPER METERS ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // P1 Super Meter
                SuperMeterBar(
                    meterValue = engine.p1.superMeter,
                    isP1 = true,
                    pulseAlpha = maxPulseAlpha,
                    modifier = Modifier.width(150.dp)
                )

                // P2 Super Meter
                SuperMeterBar(
                    meterValue = engine.p2.superMeter,
                    isP1 = false,
                    pulseAlpha = maxPulseAlpha,
                    modifier = Modifier.width(150.dp)
                )
            }
        }

        // COMBO COUNTER OVERLAYS
        if (engine.p1.comboHits >= 2) {
            ComboBadge(
                comboHits = engine.p1.comboHits,
                damage = engine.p1.comboDamage,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp, top = 20.dp)
            )
        }
        if (engine.p2.comboHits >= 2) {
            ComboBadge(
                comboHits = engine.p2.comboHits,
                damage = engine.p2.comboDamage,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp, top = 20.dp)
            )
        }

        // ANNOUNCER TEXT BANNER OVERLAY
        if (engine.announceTimerFrames > 0 &&
            (engine.roundState == RoundState.INTRO ||
             engine.roundState == RoundState.ROUND_START_COUNTDOWN ||
             engine.roundState == RoundState.ROUND_OVER ||
             engine.roundState == RoundState.MATCH_OVER)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 20.dp)
                    .shadow(16.dp, RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color.Transparent, Color(0xDD0B0F19), Color(0xDD0B0F19), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = engine.roundAnnounceText,
                        color = NeonGold,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = engine.roundAnnounceSubtext,
                        color = NeonCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FighterHealthBar(
    fighter: FighterInstance,
    isReversed: Boolean
) {
    val healthFrac = (fighter.health / fighter.maxHealth).coerceIn(0f, 1f)
    val delayedFrac = (fighter.delayedHealth / fighter.maxHealth).coerceIn(0f, 1f)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isReversed) Alignment.Start else Alignment.End
    ) {
        // Name & Round Win Orbs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = if (isReversed) Arrangement.SpaceBetween else Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isReversed) {
                Text(
                    text = fighter.data.name.uppercase(),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                RoundOrbs(roundsWon = fighter.roundsWon)
            } else {
                RoundOrbs(roundsWon = fighter.roundsWon)
                Text(
                    text = fighter.data.name.uppercase(),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // The Bar Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .shadow(4.dp, CutCornerShape(4.dp))
                .background(Color(0xFF0F172A), CutCornerShape(4.dp))
                .border(1.dp, Color(0xFF334155), CutCornerShape(4.dp))
                .clip(CutCornerShape(4.dp))
        ) {
            // Delayed Damage Bar (Yellow)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(delayedFrac)
                    .align(if (isReversed) Alignment.CenterEnd else Alignment.CenterStart)
                    .background(HealthYellowDelayed)
            )

            // Current Live Health Bar (Green to Red gradient)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(healthFrac)
                    .align(if (isReversed) Alignment.CenterEnd else Alignment.CenterStart)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                if (healthFrac > 0.35f) HealthGreen else NeonCrimson,
                                Color(0xFF34D399)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun RoundOrbs(roundsWon: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(2) { index ->
            val hasWon = index < roundsWon
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = if (hasWon) NeonCrimson else Color(0xFF334155),
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = if (hasWon) NeonGold else Color(0xFF64748B),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun SuperMeterBar(
    meterValue: Float,
    isP1: Boolean,
    pulseAlpha: Float,
    modifier: Modifier = Modifier
) {
    val meterFrac = (meterValue / 100f).coerceIn(0f, 1f)
    val isMax = meterValue >= 100f

    Column(
        modifier = modifier,
        horizontalAlignment = if (isP1) Alignment.Start else Alignment.End
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = if (isMax) "★ SUPER MAX ★" else "METER ${(meterFrac * 100).toInt()}%",
                color = if (isMax) SuperMeterMax.copy(alpha = pulseAlpha) else SuperMeterBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(Color(0xFF0F172A), RoundedCornerShape(3.dp))
                .border(
                    width = 1.dp,
                    color = if (isMax) SuperMeterMax else Color(0xFF1E293B),
                    shape = RoundedCornerShape(3.dp)
                )
                .clip(RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(meterFrac)
                    .align(if (isP1) Alignment.CenterStart else Alignment.CenterEnd)
                    .background(
                        brush = Brush.horizontalGradient(
                            if (isMax) listOf(NeonPurple, SuperMeterMax)
                            else listOf(SuperMeterBlue, NeonCyan)
                        )
                    )
            )
        }
    }
}

@Composable
private fun ComboBadge(
    comboHits: Int,
    damage: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(12.dp, CutCornerShape(8.dp))
            .background(Color(0xDD0B0F19), CutCornerShape(8.dp))
            .border(1.5.dp, NeonGold, CutCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = "$comboHits HITS!",
            color = NeonGold,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "${damage.toInt()} DMG",
            color = NeonCrimson,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
