package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.CombatEngine
import com.example.model.ActionState
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuperMeterMax
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun ArcadeControls(
    engine: CombatEngine,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // VIRTUAL ARCADE JOYSTICK (Left)
        VirtualJoystick(
            onDirectionChange = { dir ->
                val p1 = engine.p1
                when (dir) {
                    JoystickDirection.LEFT -> {
                        if (p1.facingRight) engine.performAction("P1", ActionState.WALK_BACKWARD)
                        else engine.performAction("P1", ActionState.WALK_FORWARD)
                    }
                    JoystickDirection.RIGHT -> {
                        if (p1.facingRight) engine.performAction("P1", ActionState.WALK_FORWARD)
                        else engine.performAction("P1", ActionState.WALK_BACKWARD)
                    }
                    JoystickDirection.UP, JoystickDirection.UP_LEFT, JoystickDirection.UP_RIGHT -> {
                        engine.performAction("P1", ActionState.JUMPING)
                    }
                    JoystickDirection.DOWN, JoystickDirection.DOWN_LEFT, JoystickDirection.DOWN_RIGHT -> {
                        engine.performAction("P1", ActionState.CROUCH)
                    }
                    JoystickDirection.NONE -> {
                        engine.releaseAction("P1", ActionState.WALK_FORWARD)
                        engine.releaseAction("P1", ActionState.WALK_BACKWARD)
                        engine.releaseAction("P1", ActionState.CROUCH)
                    }
                }
            },
            onDash = {
                engine.performAction("P1", ActionState.DASHING)
            }
        )

        // ARCADE ACTION BUTTONS CLUSTER (Right)
        ArcadeButtonsCluster(engine = engine)
    }
}

enum class JoystickDirection {
    NONE, LEFT, RIGHT, UP, DOWN, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
}

@Composable
private fun VirtualJoystick(
    onDirectionChange: (JoystickDirection) -> Unit,
    onDash: () -> Unit
) {
    var knobOffset by remember { mutableStateOf(Offset.Zero) }
    val maxRadius = 55f

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(136.dp)
                .shadow(10.dp, CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                    ),
                    shape = CircleShape
                )
                .border(2.dp, Color(0xFF334155), CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val delta = offset - center
                            val dist = sqrt(delta.x * delta.x + delta.y * delta.y)
                            val clampedDist = dist.coerceAtMost(maxRadius)
                            val angle = atan2(delta.y, delta.x)
                            knobOffset = Offset(
                                (kotlin.math.cos(angle) * clampedDist),
                                (kotlin.math.sin(angle) * clampedDist)
                            )
                            val dir = calculateDirection(knobOffset)
                            onDirectionChange(dir)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val newX = knobOffset.x + dragAmount.x
                            val newY = knobOffset.y + dragAmount.y
                            val dist = sqrt(newX * newX + newY * newY)
                            val clampedDist = dist.coerceAtMost(maxRadius)
                            val angle = atan2(newY, newX)
                            knobOffset = Offset(
                                (kotlin.math.cos(angle) * clampedDist),
                                (kotlin.math.sin(angle) * clampedDist)
                            )
                            val dir = calculateDirection(knobOffset)
                            onDirectionChange(dir)
                        },
                        onDragEnd = {
                            knobOffset = Offset.Zero
                            onDirectionChange(JoystickDirection.NONE)
                        },
                        onDragCancel = {
                            knobOffset = Offset.Zero
                            onDirectionChange(JoystickDirection.NONE)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Directional cross indicators
            Text("▲", color = Color(0x6694A3B8), fontSize = 12.sp, modifier = Modifier.align(Alignment.TopCenter).padding(top = 6.dp))
            Text("▼", color = Color(0x6694A3B8), fontSize = 12.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp))
            Text("◀", color = Color(0x6694A3B8), fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterStart).padding(start = 6.dp))
            Text("▶", color = Color(0x6694A3B8), fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 6.dp))

            // The Joystick Knob
            Box(
                modifier = Modifier
                    .offset { IntOffset(knobOffset.x.roundToInt(), knobOffset.y.roundToInt()) }
                    .size(54.dp)
                    .shadow(8.dp, CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(NeonCyan, Color(0xFF0284C7))
                        ),
                        shape = CircleShape
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Quick Dash Button
        Box(
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(6.dp))
                .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onDash() }
                    )
                }
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .testTag("dash_button"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚡ DASH",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun calculateDirection(offset: Offset): JoystickDirection {
    val dist = sqrt(offset.x * offset.x + offset.y * offset.y)
    if (dist < 15f) return JoystickDirection.NONE

    val angleDeg = Math.toDegrees(atan2(offset.y.toDouble(), offset.x.toDouble())).let {
        if (it < 0) it + 360 else it
    }

    return when (angleDeg) {
        in 22.5..67.5 -> JoystickDirection.DOWN_RIGHT
        in 67.5..112.5 -> JoystickDirection.DOWN
        in 112.5..157.5 -> JoystickDirection.DOWN_LEFT
        in 157.5..202.5 -> JoystickDirection.LEFT
        in 202.5..247.5 -> JoystickDirection.UP_LEFT
        in 247.5..292.5 -> JoystickDirection.UP
        in 292.5..337.5 -> JoystickDirection.UP_RIGHT
        else -> JoystickDirection.RIGHT
    }
}

@Composable
private fun ArcadeButtonsCluster(
    engine: CombatEngine
) {
    val p1 = engine.p1
    val isSuperReady = p1.superMeter >= 100f

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top Row: SPECIAL & ULTRA
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // SPECIAL MOVE BUTTON
            ArcadeActionButton(
                label = "SPECIAL",
                subtext = "⚡ ${p1.data.specialMove1.energyCost.toInt()}%",
                color = NeonPurple,
                size = 58.dp,
                testTag = "special_button",
                isEnabled = p1.superMeter >= p1.data.specialMove1.energyCost,
                onClick = { engine.performAction("P1", ActionState.SPECIAL_1) }
            )

            // ULTRA SUPER FINISHER BUTTON
            ArcadeActionButton(
                label = "ULTRA",
                subtext = if (isSuperReady) "MAX READY" else "${p1.superMeter.toInt()}%",
                color = if (isSuperReady) SuperMeterMax else Color(0xFF475569),
                size = 64.dp,
                testTag = "ultra_button",
                isEnabled = isSuperReady,
                isPulsing = isSuperReady,
                onClick = { engine.performAction("P1", ActionState.SUPER_ULTRA) }
            )
        }

        // Bottom Row: PUNCH, KICK, GUARD
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // GUARD / BLOCK BUTTON (hold to block)
            ArcadeHoldButton(
                label = "GUARD",
                subtext = "🛡️ BLOCK",
                color = Color(0xFF38BDF8),
                size = 56.dp,
                testTag = "guard_button",
                onPressStart = { engine.performAction("P1", ActionState.BLOCKING) },
                onPressEnd = { engine.releaseAction("P1", ActionState.BLOCKING) }
            )

            // LIGHT PUNCH
            ArcadeActionButton(
                label = "PUNCH",
                subtext = "👊 JAB",
                color = NeonCyan,
                size = 60.dp,
                testTag = "punch_button",
                onClick = { engine.performAction("P1", ActionState.PUNCH_LIGHT) }
            )

            // HEAVY KICK
            ArcadeActionButton(
                label = "KICK",
                subtext = "🦵 ROUND",
                color = NeonGold,
                size = 60.dp,
                testTag = "kick_button",
                onClick = { engine.performAction("P1", ActionState.KICK_HEAVY) }
            )
        }
    }
}

@Composable
private fun ArcadeActionButton(
    label: String,
    subtext: String,
    color: Color,
    size: androidx.compose.ui.unit.Dp,
    testTag: String,
    isEnabled: Boolean = true,
    isPulsing: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(if (isEnabled) 8.dp else 2.dp, CircleShape)
            .background(
                brush = Brush.radialGradient(
                    if (isEnabled) listOf(color, color.copy(alpha = 0.6f))
                    else listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                ),
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                color = if (isEnabled) Color.White.copy(alpha = 0.8f) else Color(0xFF334155),
                shape = CircleShape
            )
            .clip(CircleShape)
            .pointerInput(isEnabled) {
                if (isEnabled) {
                    detectTapGestures(onTap = { onClick() })
                }
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = if (isEnabled) Color.White else Color(0xFF64748B),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = subtext,
                color = if (isEnabled) Color.White.copy(alpha = 0.9f) else Color(0xFF475569),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ArcadeHoldButton(
    label: String,
    subtext: String,
    color: Color,
    size: androidx.compose.ui.unit.Dp,
    testTag: String,
    onPressStart: () -> Unit,
    onPressEnd: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(size)
            .shadow(if (isPressed) 12.dp else 6.dp, CircleShape)
            .background(
                brush = Brush.radialGradient(
                    if (isPressed) listOf(NeonCyan, Color(0xFF0369A1))
                    else listOf(color.copy(alpha = 0.8f), color.copy(alpha = 0.4f))
                ),
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                color = if (isPressed) Color.White else color,
                shape = CircleShape
            )
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onPressStart()
                        tryAwaitRelease()
                        isPressed = false
                        onPressEnd()
                    }
                )
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = subtext,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
