package com.example.model

import com.example.R

enum class ActionState {
    IDLE,
    WALK_FORWARD,
    WALK_BACKWARD,
    CROUCH,
    JUMPING,
    PUNCH_LIGHT,
    PUNCH_HEAVY,
    KICK_LIGHT,
    KICK_HEAVY,
    SPECIAL_1,
    SUPER_ULTRA,
    BLOCKING,
    DASHING,
    HIT_STUN,
    KNOCKDOWN,
    VICTORY,
    DEFEATED
}

enum class ParticleType {
    SPARK,
    FLAME,
    SHADOW,
    RING,
    DUST,
    BLOOD_SPLAT,
    BLOOD_BURST,
    BONE_SHARDS
}

data class BloodStain(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val alpha: Float = 0.85f,
    val color: Long = 0xFF8B0000 // Deep Dark Crimson
)

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Long,
    val size: Float,
    var alpha: Float,
    var life: Float,
    val maxLife: Float,
    val type: ParticleType
)

data class DamagePopup(
    val id: Long,
    var x: Float,
    var y: Float,
    val text: String,
    val color: Long,
    val isCritical: Boolean,
    var alpha: Float,
    var life: Int
)

data class Projectile(
    val id: Long,
    val ownerId: String,
    var x: Float,
    var y: Float,
    val vx: Float,
    val radius: Float,
    val color: Long,
    val damage: Float,
    var lifeFrames: Int
)

data class FighterInstance(
    val id: String, // "P1" or "P2"
    val data: FighterData,
    var x: Float,
    var y: Float, // 0 is ground, > 0 is airborne
    var vx: Float = 0f,
    var vy: Float = 0f,
    var facingRight: Boolean = true,
    var state: ActionState = ActionState.IDLE,
    var stateTimer: Int = 0,
    var stateTotalFrames: Int = 1,
    var health: Float = 1000f,
    val maxHealth: Float = 1000f,
    var delayedHealth: Float = 1000f,
    var superMeter: Float = 0f, // 0 to 100
    var stunGauge: Float = 0f, // 0 to 100
    var isBlocking: Boolean = false,
    var isInvulnerable: Boolean = false,
    var hasDealtHitInCurrentAttack: Boolean = false,
    var comboHits: Int = 0,
    var comboDamage: Float = 0f,
    var roundsWon: Int = 0,
    var bloodDripTimer: Int = 0
)

data class StageArena(
    val id: String,
    val name: String,
    val subtitle: String,
    val drawableResId: Int,
    val floorY: Float = 340f,
    val arenaWidth: Float = 1000f,
    val bloodStainColor: Long = 0xFF880000
)

object Arenas {
    val RENEGADE_ALLEY = StageArena(
        id = "renegade_alley",
        name = "Street Fight Cage",
        subtitle = "Underground Asphalt Pit",
        drawableResId = R.drawable.arena_cyber_city
    )

    val DRAGON_CREST = StageArena(
        id = "dragon_crest",
        name = "Dragon Crest Temple",
        subtitle = "Sacred Mountain Dojo",
        drawableResId = R.drawable.arena_temple_sunset
    )

    val ALL_ARENAS = listOf(RENEGADE_ALLEY, DRAGON_CREST)
}

enum class GameMode(val displayName: String, val subtitle: String) {
    ARCADE("RENEGADE MAYHEM", "Arcade Ladder to conquer the Fight Club"),
    VERSUS_AI("NEON RIOT", "Underground 1v1 Street Clash against Brutal AI"),
    SURVIVAL("WASTELAND RUMBLE", "Endless Gauntlet with high damage & blood accumulation"),
    FINAL_CLASH("FINAL CLASH", "Ultimate Boss Encounter against Abyssal Queen"),
    DOJO_TRAINING("FIGHTER LAB", "Deep Character Breakdown, Hitboxes & Combo Trials")
}

enum class AiDifficulty(val label: String, val reactionFrames: Int, val aggression: Float) {
    EASY("Street Rookie", 18, 0.45f),
    MEDIUM("Pit Brawler", 10, 0.70f),
    HARD("Fight Club Veteran", 5, 0.88f),
    INSANE("Brutal Berserker", 2, 0.98f)
}

enum class RoundState {
    INTRO,
    ROUND_START_COUNTDOWN,
    FIGHTING,
    ROUND_OVER,
    MATCH_OVER
}
