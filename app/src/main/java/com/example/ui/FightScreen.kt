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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.CombatEngine
import com.example.model.RoundState
import com.example.ui.theme.DarkArcadeBg
import com.example.ui.theme.DarkArcadeSurface
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.TextMuted
import kotlinx.coroutines.isActive

@Composable
fun FightScreen(
    engine: CombatEngine,
    onMatchFinished: (winnerId: String, p1Score: Int) -> Unit,
    onExitToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPaused by remember { mutableStateOf(false) }
    var showCommandList by remember { mutableStateOf(false) }
    var frameTick by remember { mutableIntStateOf(0) }

    // Intercept back button to show pause menu
    BackHandler {
        isPaused = !isPaused
    }

    // 60 FPS Game Loop
    LaunchedEffect(isPaused) {
        while (isActive) {
            withFrameNanos {
                if (!isPaused) {
                    engine.tick()
                    frameTick++
                    if (engine.roundState == RoundState.MATCH_OVER && engine.matchWinner != null) {
                        onMatchFinished(engine.matchWinner!!, engine.p1.comboDamage.toInt())
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkArcadeBg)
    ) {
        // Combat Viewport: Top arena view
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.35f)
            ) {
                // Canvas Arena Renderer
                CanvasArenaRenderer(
                    engine = engine,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Fighting HUD
                FightHud(
                    engine = engine,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Pause & Move List Buttons
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 48.dp, end = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Command List Quick Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xBB0F172A), RoundedCornerShape(6.dp))
                            .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                            .clickable { showCommandList = true }
                            .testTag("command_list_quick_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Moves",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Pause Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xBB0F172A), RoundedCornerShape(6.dp))
                            .border(1.dp, NeonGold, RoundedCornerShape(6.dp))
                            .clickable { isPaused = true }
                            .testTag("pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = NeonGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Lower Arcade Controls Deck
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.95f)
                    .background(Color(0xFF090D16))
            ) {
                ArcadeControls(
                    engine = engine,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // PAUSE MODAL OVERLAY
        if (isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC000000))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .shadow(16.dp, CutCornerShape(12.dp))
                        .background(DarkArcadeSurface, CutCornerShape(12.dp))
                        .border(2.dp, NeonCyan, CutCornerShape(12.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "GAME PAUSED",
                        color = NeonGold,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )

                    Button(
                        onClick = { isPaused = false },
                        modifier = Modifier.fillMaxWidth().testTag("resume_fight_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        shape = CutCornerShape(6.dp)
                    ) {
                        Text("RESUME BATTLE", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            isPaused = false
                            engine.restartMatch()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("restart_fight_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = Color.White),
                        shape = CutCornerShape(6.dp)
                    ) {
                        Text("RESTART MATCH", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showCommandList = true },
                        modifier = Modifier.fillMaxWidth().testTag("pause_command_list_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = NeonCyan),
                        shape = CutCornerShape(6.dp)
                    ) {
                        Text("COMMAND LIST", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onExitToMenu() },
                        modifier = Modifier.fillMaxWidth().testTag("quit_fight_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson.copy(alpha = 0.3f), contentColor = NeonCrimson),
                        shape = CutCornerShape(6.dp)
                    ) {
                        Text("QUIT TO MENU", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // QUICK MOVE LIST OVERLAY DIALOG
        if (showCommandList) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xDD000000))
                    .clickable { showCommandList = false },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .shadow(16.dp, CutCornerShape(12.dp))
                        .background(DarkArcadeSurface, CutCornerShape(12.dp))
                        .border(1.5.dp, NeonGold, CutCornerShape(12.dp))
                        .padding(20.dp)
                        .clickable(enabled = false) {},
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${engine.p1.data.name.uppercase()} COMMANDS",
                        color = NeonGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MoveItem(label = "Light Jab / Punch", input = "Punch Button", desc = "Fast 12-frame strike, high priority")
                        MoveItem(label = "Heavy Roundhouse Kick", input = "Kick Button", desc = "Heavy 26-frame strike, triggers knockdown")
                        MoveItem(label = "Guard / Deflect", input = "Hold Guard", desc = "Mitigates 82% damage, prevents knockdown")
                        MoveItem(label = "Combat Dash", input = "Dash Button", desc = "Fast forward evasive glide")
                        MoveItem(
                            label = engine.p1.data.specialMove1.name,
                            input = "Special Button",
                            desc = engine.p1.data.specialMove1.description
                        )
                        MoveItem(
                            label = engine.p1.data.superUltraMove.name,
                            input = "Ultra Button (100% Meter)",
                            desc = engine.p1.data.superUltraMove.description
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showCommandList = false },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        shape = CutCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CLOSE", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MoveItem(label: String, input: String, desc: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(input, color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
        Text(desc, color = TextMuted, fontSize = 10.sp)
    }
}
