package com.example.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import com.example.game.CombatEngine
import com.example.model.ActionState
import com.example.model.FighterInstance
import com.example.model.DamagePopup
import com.example.model.Particle
import com.example.model.ParticleType
import com.example.model.Projectile
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun CanvasArenaRenderer(
    engine: CombatEngine,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val arenaBitmap = remember(engine.arena.drawableResId) {
        try {
            val bmp = BitmapFactory.decodeResource(context.resources, engine.arena.drawableResId)
            bmp?.asImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Camera follow between P1 and P2
        val midX = (engine.p1.x + engine.p2.x) / 2f
        val fighterDist = kotlin.math.abs(engine.p1.x - engine.p2.x)

        // Dynamic scale (zoom in when close, zoom out when far)
        val zoom = (canvasWidth / (fighterDist + 360f)).coerceIn(0.85f, 1.25f)

        // Screen shake offset
        var shakeOffsetX = 0f
        var shakeOffsetY = 0f
        if (engine.screenShake > 0f) {
            shakeOffsetX = (Random.nextFloat() - 0.5f) * engine.screenShake * 1.5f
            shakeOffsetY = (Random.nextFloat() - 0.5f) * engine.screenShake * 1.5f
        }

        // Draw Stage Background
        if (arenaBitmap != null) {
            // Slight parallax shift based on midX
            val parallaxX = (500f - midX) * 0.15f
            drawImage(
                image = arenaBitmap,
                dstOffset = androidx.compose.ui.unit.IntOffset(
                    (parallaxX + shakeOffsetX).toInt(),
                    shakeOffsetY.toInt()
                ),
                dstSize = androidx.compose.ui.unit.IntSize(
                    (canvasWidth * 1.15f).toInt(),
                    canvasHeight.toInt()
                )
            )
        } else {
            // Gradient fallback
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF020617))
                )
            )
        }

        // Ground stage floor gradient & shadow line
        val floorY = canvasHeight * 0.72f
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0x3300E5FF), Color(0xDD0B0F19)),
                startY = floorY,
                endY = canvasHeight
            ),
            topLeft = Offset(0f, floorY),
            size = Size(canvasWidth, canvasHeight - floorY)
        )
        // Neon arena floor guideline
        drawLine(
            color = Color(0x6600E5FF),
            start = Offset(0f, floorY),
            end = Offset(canvasWidth, floorY),
            strokeWidth = 3f
        )

        // Helper coordinate converter from Engine space (0..1000 X, 0..300 Y) to Canvas space
        fun toCanvasX(engineX: Float): Float {
            val centerShift = (engineX - midX) * zoom
            return (canvasWidth / 2f) + centerShift + shakeOffsetX
        }

        fun toCanvasY(engineY: Float): Float {
            return floorY - (engineY * zoom) + shakeOffsetY
        }

        // Draw Blood Stains pooling on arena floor
        for (stain in engine.bloodStains) {
            val sx = toCanvasX(stain.x)
            val sy = floorY + shakeOffsetY
            val sw = stain.width * zoom
            val sh = stain.height * zoom
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFDC2626).copy(alpha = stain.alpha),
                        Color(stain.color).copy(alpha = stain.alpha * 0.9f),
                        Color(0xFF450A0A).copy(alpha = stain.alpha * 0.7f)
                    ),
                    center = Offset(sx, sy),
                    radius = (sw / 2f).coerceAtLeast(1f)
                ),
                topLeft = Offset(sx - (sw / 2f), sy - (sh / 2f)),
                size = Size(sw, sh)
            )
        }

        // Draw Shadows beneath fighters
        drawFighterShadow(toCanvasX(engine.p1.x), floorY + shakeOffsetY, engine.p1.y, zoom)
        drawFighterShadow(toCanvasX(engine.p2.x), floorY + shakeOffsetY, engine.p2.y, zoom)

        // Draw Fighters
        drawFighter(engine.p1, toCanvasX(engine.p1.x), toCanvasY(engine.p1.y), zoom)
        drawFighter(engine.p2, toCanvasX(engine.p2.x), toCanvasY(engine.p2.y), zoom)

        // Draw Projectiles
        for (proj in engine.projectiles) {
            drawProjectile(proj, toCanvasX(proj.x), toCanvasY(proj.y), zoom)
        }

        // Draw Particles
        for (p in engine.particles) {
            drawParticle(p, toCanvasX(p.x), toCanvasY(p.y), zoom)
        }

        // Draw Floating Damage Popups
        for (pop in engine.damagePopups) {
            drawDamagePopup(pop, toCanvasX(pop.x), toCanvasY(pop.y), zoom)
        }
    }
}

private fun DrawScope.drawFighterShadow(cx: Float, floorY: Float, heightAboveGround: Float, zoom: Float) {
    val scale = (1f - (heightAboveGround / 220f)).coerceIn(0.3f, 1f)
    val shadowWidth = 70f * zoom * scale
    val shadowHeight = 16f * zoom * scale
    drawOval(
        color = Color(0x88000000),
        topLeft = Offset(cx - (shadowWidth / 2f), floorY - (shadowHeight / 2f)),
        size = Size(shadowWidth, shadowHeight)
    )
}

private fun DrawScope.drawFighter(f: FighterInstance, cx: Float, cy: Float, zoom: Float) {
    val facing = if (f.facingRight) 1f else -1f
    val baseColor = Color(f.data.primaryColor)
    val accentColor = Color(f.data.accentColor)
    val auraColor = Color(f.data.auraColor)

    // Aura flare when Super Meter is full or during Special/Super
    if (f.superMeter >= 100f || f.state == ActionState.SUPER_ULTRA || f.state == ActionState.SPECIAL_1) {
        val auraRadius = (60f + sin(f.stateTimer * 0.4f) * 8f) * zoom
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(auraColor.copy(alpha = 0.5f), Color.Transparent),
                center = Offset(cx, cy - 65f * zoom),
                radius = auraRadius
            ),
            radius = auraRadius,
            center = Offset(cx, cy - 65f * zoom)
        )
    }

    // Shield barrier bubble if blocking
    if (f.isBlocking || f.state == ActionState.BLOCKING) {
        val barrierRadius = 65f * zoom
        drawCircle(
            color = Color(0x6600E5FF),
            radius = barrierRadius,
            center = Offset(cx + (facing * 20f * zoom), cy - 60f * zoom)
        )
        drawCircle(
            color = Color(0xFF00E5FF),
            radius = barrierRadius,
            center = Offset(cx + (facing * 20f * zoom), cy - 60f * zoom),
            style = Stroke(width = 3f * zoom)
        )
    }

    // Kinematic animation parameters based on ActionState
    val isCrouch = f.state == ActionState.CROUCH
    val crouchOffset = if (isCrouch) 25f * zoom else 0f

    val idleBob = if (f.state == ActionState.IDLE) sin(f.stateTimer * 0.15f) * 3f * zoom else 0f
    val torsoY = cy - (65f * zoom) + crouchOffset + idleBob
    val headY = torsoY - (38f * zoom)

    // HEAD
    drawCircle(
        color = Color(0xFFFFD1A4), // Skin tone
        radius = 16f * zoom,
        center = Offset(cx + (facing * 4f * zoom), headY)
    )
    // Headband / Hair / Helmet in primary/accent color
    drawArc(
        color = baseColor,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(cx + (facing * 4f * zoom) - 17f * zoom, headY - 17f * zoom),
        size = Size(34f * zoom, 24f * zoom)
    )
    // Eye visor / Martial arts eye spark
    val eyeColor = if (f.superMeter >= 100f) auraColor else Color(0xFF00E5FF)
    drawCircle(
        color = eyeColor,
        radius = 3.5f * zoom,
        center = Offset(cx + (facing * 12f * zoom), headY + 1f * zoom)
    )

    // TORSO / GI / ARMOR
    val torsoWidth = (if (f.data.power >= 5) 34f else 28f) * zoom
    val torsoHeight = (if (isCrouch) 30f else 46f) * zoom
    drawRoundRect(
        color = baseColor,
        topLeft = Offset(cx - (torsoWidth / 2f), torsoY - 12f * zoom),
        size = Size(torsoWidth, torsoHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f * zoom, 6f * zoom)
    )
    // Belt / Sash in accent color
    drawRect(
        color = accentColor,
        topLeft = Offset(cx - (torsoWidth / 2f) - 2f * zoom, torsoY + torsoHeight - 16f * zoom),
        size = Size(torsoWidth + 4f * zoom, 8f * zoom)
    )

    // LEGS
    val hipY = torsoY + torsoHeight - 10f * zoom
    val legColor = Color(0xFF1E293B) // Dark gi pants/armor
    when (f.state) {
        ActionState.KICK_LIGHT, ActionState.KICK_HEAVY -> {
            // Extended kicking leg
            val kickReach = (if (f.state == ActionState.KICK_HEAVY) 85f else 65f) * zoom
            val kickY = torsoY + (if (f.state == ActionState.KICK_HEAVY) -15f else 15f) * zoom
            // Kicking leg line
            drawLine(
                color = baseColor,
                start = Offset(cx, hipY),
                end = Offset(cx + (facing * kickReach), kickY),
                strokeWidth = 14f * zoom
            )
            // Kick impact swoosh / particle arc
            drawArc(
                brush = Brush.sweepGradient(listOf(auraColor, Color.Transparent)),
                startAngle = if (f.facingRight) 270f else 90f,
                sweepAngle = if (f.facingRight) 90f else -90f,
                useCenter = false,
                topLeft = Offset(cx - 30f * zoom, kickY - 40f * zoom),
                size = Size(kickReach + 40f * zoom, 80f * zoom),
                style = Stroke(width = 8f * zoom)
            )
            // Support leg planted
            drawLine(
                color = legColor,
                start = Offset(cx, hipY),
                end = Offset(cx - (facing * 15f * zoom), cy),
                strokeWidth = 14f * zoom
            )
        }
        ActionState.KNOCKDOWN, ActionState.DEFEATED -> {
            // Horizontal fallen posture
            drawLine(
                color = legColor,
                start = Offset(cx, hipY),
                end = Offset(cx - (facing * 45f * zoom), cy),
                strokeWidth = 14f * zoom
            )
        }
        else -> {
            // Standing / Crouching stance legs
            val stanceSpread = if (isCrouch) 30f * zoom else 18f * zoom
            // Back leg
            drawLine(
                color = legColor,
                start = Offset(cx, hipY),
                end = Offset(cx - (facing * stanceSpread), cy),
                strokeWidth = 13f * zoom
            )
            // Front leg
            drawLine(
                color = legColor,
                start = Offset(cx, hipY),
                end = Offset(cx + (facing * (stanceSpread * 0.7f)), cy),
                strokeWidth = 13f * zoom
            )
        }
    }

    // ARMS & ATTACKS
    val shoulderY = torsoY + 2f * zoom
    when (f.state) {
        ActionState.PUNCH_LIGHT, ActionState.PUNCH_HEAVY -> {
            // Thrusting punch arm with speed blur
            val punchReach = (if (f.state == ActionState.PUNCH_HEAVY) 75f else 55f) * zoom
            val punchY = torsoY + (if (f.state == ActionState.PUNCH_HEAVY) -4f else 8f) * zoom
            // Arm
            drawLine(
                color = Color(0xFFFFD1A4),
                start = Offset(cx, shoulderY),
                end = Offset(cx + (facing * punchReach), punchY),
                strokeWidth = 12f * zoom
            )
            // Fist / Gauntlet
            drawCircle(
                color = accentColor,
                radius = 11f * zoom,
                center = Offset(cx + (facing * punchReach), punchY)
            )
            // Motion punch speed trail
            drawLine(
                color = auraColor.copy(alpha = 0.7f),
                start = Offset(cx + (facing * (punchReach * 0.4f)), punchY),
                end = Offset(cx + (facing * punchReach), punchY),
                strokeWidth = 16f * zoom
            )
        }
        ActionState.SUPER_ULTRA -> {
            // Grand power pose with dual glowing gauntlets
            val reach = 85f * zoom
            drawLine(
                color = auraColor,
                start = Offset(cx, shoulderY),
                end = Offset(cx + (facing * reach), shoulderY - 20f * zoom),
                strokeWidth = 16f * zoom
            )
            drawCircle(
                color = Color.White,
                radius = 18f * zoom,
                center = Offset(cx + (facing * reach), shoulderY - 20f * zoom)
            )
        }
        ActionState.BLOCKING -> {
            // Crossed guard arms
            drawLine(
                color = accentColor,
                start = Offset(cx, shoulderY),
                end = Offset(cx + (facing * 20f * zoom), headY + 5f * zoom),
                strokeWidth = 12f * zoom
            )
        }
        else -> {
            // Guard / Ready martial arts stance arms
            drawLine(
                color = Color(0xFFFFD1A4),
                start = Offset(cx, shoulderY),
                end = Offset(cx + (facing * 22f * zoom), torsoY + 10f * zoom),
                strokeWidth = 11f * zoom
            )
            drawCircle(
                color = accentColor,
                radius = 8f * zoom,
                center = Offset(cx + (facing * 22f * zoom), torsoY + 10f * zoom)
            )
        }
    }
}

private fun DrawScope.drawProjectile(proj: Projectile, cx: Float, cy: Float, zoom: Float) {
    val r = proj.radius * zoom
    val col = Color(proj.color)

    // Outer glowing energy halo
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(col.copy(alpha = 0.8f), col.copy(alpha = 0.3f), Color.Transparent),
            center = Offset(cx, cy),
            radius = r * 1.8f
        ),
        radius = r * 1.8f,
        center = Offset(cx, cy)
    )
    // Solid energy core
    drawCircle(
        color = Color.White,
        radius = r * 0.6f,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = col,
        radius = r,
        center = Offset(cx, cy),
        style = Stroke(width = 3f * zoom)
    )
}

private fun DrawScope.drawParticle(p: Particle, cx: Float, cy: Float, zoom: Float) {
    val r = (p.size * zoom).coerceAtLeast(1.5f)
    val col = Color(p.color).copy(alpha = p.alpha)

    when (p.type) {
        ParticleType.SPARK -> {
            drawCircle(color = col, radius = r, center = Offset(cx, cy))
        }
        ParticleType.FLAME -> {
            drawCircle(
                brush = Brush.radialGradient(listOf(col, Color.Transparent), center = Offset(cx, cy), radius = r * 1.5f),
                radius = r * 1.5f,
                center = Offset(cx, cy)
            )
        }
        ParticleType.BLOOD_SPLAT -> {
            // Directional elongated droplet with tail
            val tailX = cx - (p.vx * 1.5f * zoom)
            val tailY = cy - (p.vy * 1.5f * zoom)
            drawLine(
                color = col,
                start = Offset(tailX, tailY),
                end = Offset(cx, cy),
                strokeWidth = (r * 1.6f).coerceAtLeast(2f)
            )
            drawCircle(color = col, radius = r, center = Offset(cx, cy))
        }
        ParticleType.BLOOD_BURST -> {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFF2233).copy(alpha = p.alpha), col, Color(0xFF450A0A).copy(alpha = p.alpha)),
                    center = Offset(cx, cy),
                    radius = r * 1.6f
                ),
                radius = r * 1.6f,
                center = Offset(cx, cy)
            )
        }
        ParticleType.BONE_SHARDS -> {
            drawCircle(
                color = Color.White.copy(alpha = p.alpha),
                radius = (r * 0.85f).coerceAtLeast(1.5f),
                center = Offset(cx, cy)
            )
        }
        else -> {
            drawCircle(color = col, radius = r, center = Offset(cx, cy))
        }
    }
}

private fun DrawScope.drawDamagePopup(pop: DamagePopup, cx: Float, cy: Float, zoom: Float) {
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = (if (pop.isCritical) 32f else 22f) * zoom
            isFakeBoldText = true
            setShadowLayer(8f, 0f, 0f, android.graphics.Color.BLACK)
            alpha = (pop.alpha * 255).toInt().coerceIn(0, 255)
        }
        val textColor = if (pop.isCritical) android.graphics.Color.rgb(255, 179, 0) else android.graphics.Color.WHITE
        paint.color = textColor
        drawText(pop.text, cx - 25f * zoom, cy, paint)
    }
}
