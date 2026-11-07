package com.example.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GameMode
import com.example.ui.theme.DarkArcadeBg
import com.example.ui.theme.DarkArcadeCard
import com.example.ui.theme.DarkArcadeSurface
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted

@Composable
fun MainMenuScreen(
    onSelectMode: (GameMode) -> Unit,
    onOpenDossier: () -> Unit,
    onToggleSound: () -> Unit,
    isSoundMuted: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkArcadeBg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // HERO BANNER SECTION (Using Street Fight Club poster from video)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.poster_underground_brawl),
                contentDescription = "Street Fight Club Underground Brawl",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient vignette overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0x770B0F19),
                                DarkArcadeBg
                            )
                        )
                    )
            )

            // Mute / Sound Toggle Top End
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(42.dp)
                    .shadow(6.dp, CircleShape(4.dp))
                    .background(Color(0xDD1E293B), CircleShape(4.dp))
                    .border(1.dp, NeonCyan, CircleShape(4.dp))
                    .clickable { onToggleSound() }
                    .testTag("sound_toggle_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Toggle Audio",
                    tint = if (isSoundMuted) TextMuted else NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Title Typography
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "STREET FIGHT CLUB",
                    color = NeonCrimson,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "THE ULTIMATE UNDERGROUND BRAWL",
                    color = NeonGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // GAME MODE SELECT CARDS (From video poster)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuModeCard(
                title = "RENEGADE MAYHEM",
                subtitle = "Underground gauntlet to conquer the street fighting circuit",
                accentColor = NeonCrimson,
                badge = "ARCADE LADDER",
                testTag = "mode_arcade_button",
                onClick = { onSelectMode(GameMode.ARCADE) }
            )

            MenuModeCard(
                title = "NEON RIOT",
                subtitle = "Street 1v1 duel with brutal damage against AI or friend",
                accentColor = NeonCyan,
                badge = "STREET DUEL",
                testTag = "mode_versus_button",
                onClick = { onSelectMode(GameMode.VERSUS_AI) }
            )

            MenuModeCard(
                title = "WASTELAND RUMBLE",
                subtitle = "Endless pit brawl with bone-breaking strikes & blood pooling",
                accentColor = NeonGold,
                badge = "PIT SURVIVAL",
                testTag = "mode_survival_button",
                onClick = { onSelectMode(GameMode.SURVIVAL) }
            )

            MenuModeCard(
                title = "FINAL CLASH",
                subtitle = "Boss fight against Ravena the Abyssal Queen",
                accentColor = NeonPurple,
                badge = "CALAMITY BOSS",
                testTag = "mode_final_clash_button",
                onClick = { onSelectMode(GameMode.FINAL_CLASH) }
            )

            // Primary Highlight: Fighter Breakdown Lab
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, CutCornerShape(8.dp))
                    .background(Color(0xFF131B2E), CutCornerShape(8.dp))
                    .border(1.5.dp, NeonGold, CutCornerShape(8.dp))
                    .clickable { onOpenDossier() }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("fighter_dossier_button"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = NeonGold,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FIGHTER BREAKDOWN LAB",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Anatomy, Hurtbox/Hitbox visualizer, Frame Data & Combos",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
                Text("➔", color = NeonGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MenuModeCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    badge: String,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, CutCornerShape(8.dp))
            .background(DarkArcadeSurface, CutCornerShape(8.dp))
            .border(1.5.dp, accentColor, CutCornerShape(8.dp))
            .clip(CutCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .background(accentColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .border(1.dp, accentColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            color = accentColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(accentColor.copy(alpha = 0.25f), CutCornerShape(6.dp))
                    .border(1.dp, accentColor, CutCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    color = accentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

private fun CircleShape(radius: androidx.compose.ui.unit.Dp) = RoundedCornerShape(radius)
