package com.example.game

import com.example.audio.HapticManager
import com.example.audio.SoundSynthesizer
import com.example.model.ActionState
import com.example.model.AiDifficulty
import com.example.model.BloodStain
import com.example.model.DamagePopup
import com.example.model.FighterData
import com.example.model.FighterInstance
import com.example.model.GameMode
import com.example.model.Particle
import com.example.model.ParticleType
import com.example.model.Projectile
import com.example.model.RoundState
import com.example.model.StageArena
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class CombatEngine(
    val p1Data: FighterData,
    val p2Data: FighterData,
    val arena: StageArena,
    val mode: GameMode,
    val difficulty: AiDifficulty,
    private val soundSynth: SoundSynthesizer,
    private val haptics: HapticManager
) {
    var p1 = createFighter("P1", p1Data, 300f, true)
    var p2 = createFighter("P2", p2Data, 700f, false)

    var roundState: RoundState = RoundState.INTRO
    var currentRound: Int = 1
    var roundTimerSeconds: Int = 99
    private var timerSubFrames: Int = 0

    var matchWinner: String? = null // "P1" or "P2"
    var roundAnnounceText: String = "ROUND 1"
    var roundAnnounceSubtext: String = "READY... FIGHT!"
    var announceTimerFrames: Int = 90

    val projectiles = mutableListOf<Projectile>()
    val particles = mutableListOf<Particle>()
    val damagePopups = mutableListOf<DamagePopup>()
    val bloodStains = mutableListOf<BloodStain>()

    var screenShake: Float = 0f
    private var hitStopFrames: Int = 0
    private var projectileIdSeq: Long = 0
    private var popupIdSeq: Long = 0

    // AI state tracking
    private var aiDecisionTimer: Int = 0
    private var aiDesiredState: ActionState = ActionState.IDLE

    init {
        soundSynth.playFightGong()
    }

    private fun createFighter(id: String, data: FighterData, x: Float, facingRight: Boolean): FighterInstance {
        return FighterInstance(
            id = id,
            data = data,
            x = x,
            y = 0f,
            facingRight = facingRight,
            health = 1000f,
            maxHealth = 1000f,
            delayedHealth = 1000f,
            superMeter = if (mode == GameMode.DOJO_TRAINING) 100f else 20f
        )
    }

    fun restartMatch() {
        p1 = createFighter("P1", p1Data, 300f, true)
        p2 = createFighter("P2", p2Data, 700f, false)
        currentRound = 1
        roundState = RoundState.ROUND_START_COUNTDOWN
        roundTimerSeconds = 99
        timerSubFrames = 0
        matchWinner = null
        roundAnnounceText = "ROUND 1"
        roundAnnounceSubtext = "FIGHT!"
        announceTimerFrames = 75
        projectiles.clear()
        particles.clear()
        damagePopups.clear()
        soundSynth.playFightGong()
    }

    fun nextRound() {
        currentRound++
        p1.x = 300f
        p1.y = 0f
        p1.vx = 0f
        p1.vy = 0f
        p1.state = ActionState.IDLE
        p1.health = 1000f
        p1.delayedHealth = 1000f
        p1.comboHits = 0

        p2.x = 700f
        p2.y = 0f
        p2.vx = 0f
        p2.vy = 0f
        p2.state = ActionState.IDLE
        p2.health = 1000f
        p2.delayedHealth = 1000f
        p2.comboHits = 0

        roundTimerSeconds = 99
        timerSubFrames = 0
        roundState = RoundState.ROUND_START_COUNTDOWN
        roundAnnounceText = "ROUND $currentRound"
        roundAnnounceSubtext = "READY... FIGHT!"
        announceTimerFrames = 75
        projectiles.clear()
        particles.clear()
        soundSynth.playFightGong()
    }

    /**
     * Executes one frame tick (60 FPS standard).
     */
    fun tick() {
        if (hitStopFrames > 0) {
            hitStopFrames--
            return
        }

        if (screenShake > 0f) {
            screenShake = max(0f, screenShake - 0.9f)
        }

        // Announcer countdown handling
        if (announceTimerFrames > 0) {
            announceTimerFrames--
            if (announceTimerFrames == 0) {
                if (roundState == RoundState.INTRO || roundState == RoundState.ROUND_START_COUNTDOWN) {
                    roundState = RoundState.FIGHTING
                } else if (roundState == RoundState.ROUND_OVER) {
                    if (matchWinner != null) {
                        roundState = RoundState.MATCH_OVER
                    } else {
                        nextRound()
                    }
                }
            }
        }

        // Update Round Timer during combat
        if (roundState == RoundState.FIGHTING && mode != GameMode.DOJO_TRAINING) {
            timerSubFrames++
            if (timerSubFrames >= 60) {
                timerSubFrames = 0
                if (roundTimerSeconds > 0) {
                    roundTimerSeconds--
                    if (roundTimerSeconds == 0) {
                        handleTimeOut()
                    }
                }
            }
        }

        // AI update for Player 2
        if (roundState == RoundState.FIGHTING) {
            updateAi()
        }

        // Update Fighters
        updateFighter(p1, p2)
        updateFighter(p2, p1)

        // Face each other unless in attack/stun
        if (p1.state == ActionState.IDLE || p1.state == ActionState.WALK_FORWARD || p1.state == ActionState.WALK_BACKWARD) {
            p1.facingRight = p1.x < p2.x
        }
        if (p2.state == ActionState.IDLE || p2.state == ActionState.WALK_FORWARD || p2.state == ActionState.WALK_BACKWARD) {
            p2.facingRight = p2.x < p1.x
        }

        // Check melee attack collisions
        if (roundState == RoundState.FIGHTING) {
            checkMeleeHit(p1, p2)
            checkMeleeHit(p2, p1)
        }

        // Update Projectiles
        updateProjectiles()

        // Update Particles
        updateParticles()

        // Update Damage Popups
        updatePopups()

        // Smoothly drain delayed health
        smoothDelayedHealth(p1)
        smoothDelayedHealth(p2)

        // Passive Dojo refill
        if (mode == GameMode.DOJO_TRAINING) {
            if (p2.health < 400f) p2.health = 1000f
            if (p1.superMeter < 100f) p1.superMeter = 100f
        }
    }

    private fun updateFighter(f: FighterInstance, opponent: FighterInstance) {
        f.stateTimer++

        // Physics: Gravity & Vertical movement
        if (f.y > 0 || f.vy != 0f) {
            f.y += f.vy
            f.vy -= 0.85f // gravity
            if (f.y <= 0f) {
                f.y = 0f
                f.vy = 0f
                if (f.state == ActionState.JUMPING) {
                    f.state = ActionState.IDLE
                    spawnDust(f.x, 10f)
                }
            }
        }

        // Horizontal velocity
        f.x += f.vx
        f.vx *= 0.82f // friction

        // Clamp inside arena bounds
        val minX = 70f
        val maxX = 930f
        f.x = f.x.coerceIn(minX, maxX)

        // Don't let fighters walk completely through each other
        val dist = abs(f.x - opponent.x)
        if (dist < 40f && f.y == 0f && opponent.y == 0f) {
            val push = (40f - dist) / 2f
            if (f.x < opponent.x) {
                f.x -= push
                opponent.x += push
            } else {
                f.x += push
                opponent.x -= push
            }
        }

        // State Machine Transition on frame expiration
        when (f.state) {
            ActionState.PUNCH_LIGHT -> {
                if (f.stateTimer >= 12) {
                    f.state = ActionState.IDLE
                }
            }
            ActionState.PUNCH_HEAVY -> {
                if (f.stateTimer >= 22) {
                    f.state = ActionState.IDLE
                }
            }
            ActionState.KICK_LIGHT -> {
                if (f.stateTimer >= 15) {
                    f.state = ActionState.IDLE
                }
            }
            ActionState.KICK_HEAVY -> {
                if (f.stateTimer >= 26) {
                    f.state = ActionState.IDLE
                }
            }
            ActionState.SPECIAL_1 -> {
                if (f.stateTimer >= 30) {
                    f.state = ActionState.IDLE
                }
            }
            ActionState.SUPER_ULTRA -> {
                if (f.stateTimer >= 45) {
                    f.state = ActionState.IDLE
                }
            }
            ActionState.HIT_STUN -> {
                if (f.stateTimer >= f.stateTotalFrames) {
                    f.state = ActionState.IDLE
                }
            }
            ActionState.KNOCKDOWN -> {
                if (f.stateTimer >= 40) {
                    f.state = ActionState.IDLE
                    f.isInvulnerable = false
                }
            }
            ActionState.DASHING -> {
                if (f.stateTimer >= 12) {
                    f.state = ActionState.IDLE
                }
            }
            else -> {}
        }
    }

    private fun checkMeleeHit(attacker: FighterInstance, defender: FighterInstance) {
        if (attacker.hasDealtHitInCurrentAttack) return
        if (defender.state == ActionState.KNOCKDOWN || defender.isInvulnerable) return

        val (isHitFrame, reach, baseDamage, isHeavy, isKnockdown) = when (attacker.state) {
            ActionState.PUNCH_LIGHT -> Tuple5(attacker.stateTimer in 3..6, 75f, 50f * (attacker.data.power / 4f), false, false)
            ActionState.PUNCH_HEAVY -> Tuple5(attacker.stateTimer in 6..12, 95f, 105f * (attacker.data.power / 4f), true, false)
            ActionState.KICK_LIGHT -> Tuple5(attacker.stateTimer in 4..8, 85f, 65f * (attacker.data.power / 4f), false, false)
            ActionState.KICK_HEAVY -> Tuple5(attacker.stateTimer in 8..14, 110f, 130f * (attacker.data.power / 4f), true, true)
            ActionState.SUPER_ULTRA -> Tuple5(attacker.stateTimer in 10..30, 160f, attacker.data.superUltraMove.damage, true, true)
            else -> Tuple5(false, 0f, 0f, false, false)
        }

        if (!isHitFrame) return

        // Check distance
        val dir = if (attacker.facingRight) 1f else -1f
        val relDist = (defender.x - attacker.x) * dir

        if (relDist in 5f..reach && abs(attacker.y - defender.y) < 70f) {
            attacker.hasDealtHitInCurrentAttack = true
            applyDamage(attacker, defender, baseDamage, isHeavy, isKnockdown, isMelee = true)
        }
    }

    private fun applyDamage(
        attacker: FighterInstance,
        defender: FighterInstance,
        rawDamage: Float,
        isHeavy: Boolean,
        isKnockdown: Boolean,
        isMelee: Boolean
    ) {
        // Check if defending fighter is actively blocking
        val isBlocked = defender.isBlocking || defender.state == ActionState.BLOCKING

        val damage: Float
        if (isBlocked) {
            damage = rawDamage * 0.18f // 82% damage reduction
            soundSynth.playBlock()
            haptics.lightHit()
            spawnHitSpark(defender.x, defender.y + 70f, 0xFF00E5FF, 6)
            addDamagePopup(defender.x, defender.y + 90f, "BLOCKED", 0xFF00E5FF, false)
            attacker.superMeter = (attacker.superMeter + 4f).coerceAtMost(100f)
            defender.superMeter = (defender.superMeter + 8f).coerceAtMost(100f)
            // Pushback on block
            val pushDir = if (attacker.facingRight) 1f else -1f
            defender.vx += pushDir * 5f
            return
        }

        // Clean Hit!
        damage = rawDamage
        defender.health = max(0f, defender.health - damage)

        // Super meter gain
        attacker.superMeter = (attacker.superMeter + (if (isHeavy) 15f else 8f)).coerceAtMost(100f)
        defender.superMeter = (defender.superMeter + (if (isHeavy) 20f else 12f)).coerceAtMost(100f)

        // Combo counter
        attacker.comboHits++
        attacker.comboDamage += damage

        // Audio & Haptic feedback
        if (isHeavy || attacker.state == ActionState.SUPER_ULTRA) {
            soundSynth.playHitHeavy()
            soundSynth.playBoneSnap()
            soundSynth.playGoreSplat()
            haptics.heavyHit()
            screenShake = 20f
            hitStopFrames = 5
        } else {
            soundSynth.playHitLight()
            soundSynth.playGoreSplat()
            haptics.lightHit()
            screenShake = 7f
            hitStopFrames = 2
        }

        // Visual effects & Blood Sprays
        val hitY = defender.y + (if (attacker.state == ActionState.CROUCH) 35f else 75f)
        spawnHitSpark(defender.x, hitY, attacker.data.auraColor, if (isHeavy) 18 else 10)
        spawnBlood(defender.x, hitY, attacker.facingRight, isHeavy, isKnockdown)

        // Damage popup with brutal fight club callouts
        val isCrit = attacker.comboHits > 1
        val callout = when {
            attacker.comboHits >= 4 -> "ULTRA GORE! ${attacker.comboHits}x"
            attacker.comboHits == 3 -> "BONE SHATTER! 3x"
            attacker.comboHits == 2 -> "BRUTAL 2x"
            isHeavy -> "HEAVY CRUNCH!"
            else -> "-${damage.toInt()}"
        }
        val popText = if (attacker.comboHits > 1) "$callout -${damage.toInt()}" else callout
        addDamagePopup(defender.x, hitY + 20f, popText, if (isCrit) 0xFFFFB300 else 0xFFFF1744, isCrit)

        // Stun or Knockdown
        val pushDir = if (attacker.facingRight) 1f else -1f
        if (isKnockdown || defender.health <= 0f) {
            defender.state = ActionState.KNOCKDOWN
            defender.stateTimer = 0
            defender.stateTotalFrames = 42
            defender.vx = pushDir * 11f
            defender.vy = 8f
            spawnDust(defender.x, 15f)
        } else {
            defender.state = ActionState.HIT_STUN
            defender.stateTimer = 0
            defender.stateTotalFrames = if (isHeavy) 18 else 10
            defender.vx = pushDir * (if (isHeavy) 6f else 3.5f)
        }

        // Check K.O.
        if (defender.health <= 0f) {
            handleKnockout(winner = attacker, loser = defender)
        }
    }

    private fun handleKnockout(winner: FighterInstance, loser: FighterInstance) {
        roundState = RoundState.ROUND_OVER
        loser.state = ActionState.DEFEATED
        winner.state = ActionState.VICTORY
        winner.roundsWon++
        soundSynth.playKoChime()
        soundSynth.playBoneSnap()
        haptics.superImpact()
        screenShake = 28f

        // Massive arterial blood burst upon fatal knockout
        spawnBloodBurst(loser.x, loser.y + 60f, 48)

        roundAnnounceText = "FATAL K.O.!"
        if (winner.health >= winner.maxHealth) {
            roundAnnounceSubtext = "FLAWLESS CARNAGE!"
        } else if (winner.comboHits >= 4) {
            roundAnnounceSubtext = "ULTRA BRUTALITY FINISH!"
        } else {
            roundAnnounceSubtext = "${winner.data.name.uppercase()} DESTROYED THE OPPONENT!"
        }

        // Best of 3 check (2 rounds to win match)
        if (mode == GameMode.DOJO_TRAINING) {
            announceTimerFrames = 100
        } else if (winner.roundsWon >= 2) {
            matchWinner = winner.id
            announceTimerFrames = 130
        } else {
            announceTimerFrames = 110
        }
    }

    private fun handleTimeOut() {
        roundState = RoundState.ROUND_OVER
        roundAnnounceText = "TIME UP!"
        if (p1.health > p2.health) {
            p1.roundsWon++
            p1.state = ActionState.VICTORY
            p2.state = ActionState.DEFEATED
            roundAnnounceSubtext = "${p1.data.name} WINS ON HEALTH!"
            if (p1.roundsWon >= 2) matchWinner = "P1"
        } else if (p2.health > p1.health) {
            p2.roundsWon++
            p2.state = ActionState.VICTORY
            p1.state = ActionState.DEFEATED
            roundAnnounceSubtext = "${p2.data.name} WINS ON HEALTH!"
            if (p2.roundsWon >= 2) matchWinner = "P2"
        } else {
            roundAnnounceSubtext = "DRAW GAME!"
        }
        announceTimerFrames = 120
    }

    private fun updateProjectiles() {
        val iter = projectiles.iterator()
        while (iter.hasNext()) {
            val proj = iter.next()
            proj.x += proj.vx
            proj.lifeFrames--

            // Spawn trail particle
            if (Random.nextFloat() < 0.6f) {
                particles.add(
                    Particle(
                        x = proj.x,
                        y = proj.y + Random.nextFloat() * 10f - 5f,
                        vx = -proj.vx * 0.15f,
                        vy = Random.nextFloat() * 1.5f - 0.75f,
                        color = proj.color,
                        size = proj.radius * 0.6f,
                        alpha = 0.8f,
                        life = 0f,
                        maxLife = 12f,
                        type = ParticleType.SPARK
                    )
                )
            }

            // Check hit against target
            val target = if (proj.ownerId == "P1") p2 else p1
            val attacker = if (proj.ownerId == "P1") p1 else p2

            val dist = abs(proj.x - target.x)
            if (dist < proj.radius + 35f && abs(proj.y - (target.y + 60f)) < 65f) {
                if (target.state != ActionState.KNOCKDOWN && !target.isInvulnerable) {
                    applyDamage(attacker, target, proj.damage, isHeavy = false, isKnockdown = false, isMelee = false)
                    spawnHitSpark(target.x, target.y + 60f, proj.color, 12)
                    iter.remove()
                    continue
                }
            }

            // Remove out of bounds or dead
            if (proj.x < 30f || proj.x > 970f || proj.lifeFrames <= 0) {
                spawnHitSpark(proj.x, proj.y, proj.color, 5)
                iter.remove()
            }
        }
    }

    private fun updateParticles() {
        val iter = particles.iterator()
        while (iter.hasNext()) {
            val p = iter.next()
            p.x += p.vx
            p.y += p.vy
            p.life++
            p.alpha = max(0f, 1f - (p.life / p.maxLife))

            // Realistic blood trajectory & ground splatting
            if (p.type == ParticleType.BLOOD_SPLAT || p.type == ParticleType.BLOOD_BURST || p.type == ParticleType.BONE_SHARDS) {
                p.vy -= 0.65f // gravity
                if (p.y <= 0f) {
                    p.y = 0f
                    p.vx *= 0.5f
                    p.vy = 0f
                }
            }

            if (p.life >= p.maxLife) {
                iter.remove()
            }
        }
    }

    private fun updatePopups() {
        val iter = damagePopups.iterator()
        while (iter.hasNext()) {
            val pop = iter.next()
            pop.y += 1.2f // Float upwards
            pop.life--
            pop.alpha = (pop.life / 35f).coerceIn(0f, 1f)
            if (pop.life <= 0) {
                iter.remove()
            }
        }
    }

    private fun smoothDelayedHealth(f: FighterInstance) {
        if (f.delayedHealth > f.health) {
            f.delayedHealth = max(f.health, f.delayedHealth - 4f)
        }
    }

    // Input actions for Player
    fun performAction(fighterId: String, action: ActionState) {
        val f = if (fighterId == "P1") p1 else p2
        val opponent = if (fighterId == "P1") p2 else p1

        if (roundState != RoundState.FIGHTING && roundState != RoundState.ROUND_START_COUNTDOWN) return
        if (f.state == ActionState.HIT_STUN || f.state == ActionState.KNOCKDOWN || f.state == ActionState.DEFEATED || f.state == ActionState.VICTORY) return

        when (action) {
            ActionState.WALK_FORWARD -> {
                val dir = if (f.facingRight) 1f else -1f
                f.vx = dir * 4.5f * (f.data.speed / 4f)
                f.state = ActionState.WALK_FORWARD
            }
            ActionState.WALK_BACKWARD -> {
                val dir = if (f.facingRight) -1f else 1f
                f.vx = dir * 3.5f * (f.data.speed / 4f)
                f.state = ActionState.WALK_BACKWARD
            }
            ActionState.CROUCH -> {
                f.state = ActionState.CROUCH
            }
            ActionState.JUMPING -> {
                if (f.y == 0f) {
                    f.vy = 15.5f
                    f.y = 1f
                    f.state = ActionState.JUMPING
                    soundSynth.playWhoosh()
                    spawnDust(f.x, 5f)
                }
            }
            ActionState.DASHING -> {
                val dir = if (f.facingRight) 1f else -1f
                f.vx = dir * 13f
                f.state = ActionState.DASHING
                f.stateTimer = 0
                soundSynth.playWhoosh()
                spawnDust(f.x, 8f)
            }
            ActionState.BLOCKING -> {
                f.state = ActionState.BLOCKING
                f.isBlocking = true
            }
            ActionState.IDLE -> {
                f.state = ActionState.IDLE
                f.isBlocking = false
            }
            ActionState.PUNCH_LIGHT -> {
                f.state = ActionState.PUNCH_LIGHT
                f.stateTimer = 0
                f.hasDealtHitInCurrentAttack = false
                soundSynth.playWhoosh()
            }
            ActionState.PUNCH_HEAVY -> {
                f.state = ActionState.PUNCH_HEAVY
                f.stateTimer = 0
                f.hasDealtHitInCurrentAttack = false
                soundSynth.playWhoosh()
            }
            ActionState.KICK_LIGHT -> {
                f.state = ActionState.KICK_LIGHT
                f.stateTimer = 0
                f.hasDealtHitInCurrentAttack = false
                soundSynth.playWhoosh()
            }
            ActionState.KICK_HEAVY -> {
                f.state = ActionState.KICK_HEAVY
                f.stateTimer = 0
                f.hasDealtHitInCurrentAttack = false
                soundSynth.playWhoosh()
            }
            ActionState.SPECIAL_1 -> {
                if (f.superMeter >= f.data.specialMove1.energyCost || mode == GameMode.DOJO_TRAINING) {
                    f.superMeter -= f.data.specialMove1.energyCost
                    f.state = ActionState.SPECIAL_1
                    f.stateTimer = 0
                    f.hasDealtHitInCurrentAttack = false
                    soundSynth.playEnergyBlast()
                    haptics.lightHit()

                    if (f.data.specialMove1.isProjectile) {
                        val dir = if (f.facingRight) 1f else -1f
                        projectiles.add(
                            Projectile(
                                id = ++projectileIdSeq,
                                ownerId = f.id,
                                x = f.x + (dir * 45f),
                                y = f.y + 65f,
                                vx = dir * 10f,
                                radius = 22f,
                                color = f.data.specialMove1.color,
                                damage = f.data.specialMove1.damage,
                                lifeFrames = 90
                            )
                        )
                    } else {
                        // Dash thrust special
                        val dir = if (f.facingRight) 1f else -1f
                        f.vx = dir * 12f
                    }
                }
            }
            ActionState.SUPER_ULTRA -> {
                if (f.superMeter >= 100f || mode == GameMode.DOJO_TRAINING) {
                    f.superMeter = 0f
                    f.state = ActionState.SUPER_ULTRA
                    f.stateTimer = 0
                    f.hasDealtHitInCurrentAttack = false
                    soundSynth.playSuperActivation()
                    haptics.superImpact()
                    screenShake = 24f

                    // Huge explosion aura particles around user
                    for (i in 0 until 30) {
                        val angle = Random.nextFloat() * 6.28f
                        val spd = Random.nextFloat() * 7f + 2f
                        particles.add(
                            Particle(
                                x = f.x,
                                y = f.y + 70f,
                                vx = kotlin.math.cos(angle) * spd,
                                vy = kotlin.math.sin(angle) * spd,
                                color = f.data.superUltraMove.color,
                                size = Random.nextFloat() * 12f + 4f,
                                alpha = 1f,
                                life = 0f,
                                maxLife = 35f,
                                type = ParticleType.FLAME
                            )
                        )
                    }

                    // Dash forward to strike opponent
                    val dir = if (f.facingRight) 1f else -1f
                    f.vx = dir * 16f
                }
            }
            else -> {}
        }
    }

    fun releaseAction(fighterId: String, action: ActionState) {
        val f = if (fighterId == "P1") p1 else p2
        if (action == ActionState.BLOCKING && f.state == ActionState.BLOCKING) {
            f.state = ActionState.IDLE
            f.isBlocking = false
        } else if ((action == ActionState.WALK_FORWARD || action == ActionState.WALK_BACKWARD) &&
            (f.state == ActionState.WALK_FORWARD || f.state == ActionState.WALK_BACKWARD)) {
            f.state = ActionState.IDLE
        } else if (action == ActionState.CROUCH && f.state == ActionState.CROUCH) {
            f.state = ActionState.IDLE
        }
    }

    private fun updateAi() {
        aiDecisionTimer++
        if (aiDecisionTimer < difficulty.reactionFrames) return
        aiDecisionTimer = 0

        if (p2.state == ActionState.HIT_STUN || p2.state == ActionState.KNOCKDOWN) return

        val dist = abs(p2.x - p1.x)
        val p1IsAttacking = p1.state in listOf(
            ActionState.PUNCH_LIGHT, ActionState.PUNCH_HEAVY,
            ActionState.KICK_LIGHT, ActionState.KICK_HEAVY,
            ActionState.SPECIAL_1, ActionState.SUPER_ULTRA
        )

        // Incoming projectile detection
        val incomingProj = projectiles.find { it.ownerId == "P1" && abs(it.x - p2.x) < 220f }
        if (incomingProj != null && Random.nextFloat() < difficulty.aggression) {
            if (Random.nextFloat() < 0.5f) {
                performAction("P2", ActionState.JUMPING)
            } else {
                performAction("P2", ActionState.BLOCKING)
            }
            return
        }

        // Reaction to player attack (blocking)
        if (p1IsAttacking && dist < 120f && Random.nextFloat() < difficulty.aggression) {
            performAction("P2", ActionState.BLOCKING)
            return
        }

        // Offensive decision
        if (p2.superMeter >= 100f && dist < 180f && Random.nextFloat() < (difficulty.aggression * 0.9f)) {
            performAction("P2", ActionState.SUPER_ULTRA)
            return
        }

        if (dist > 250f) {
            // Far away: either zone with special projectile or close distance
            if (p2.superMeter >= p2.data.specialMove1.energyCost && Random.nextFloat() < 0.5f) {
                performAction("P2", ActionState.SPECIAL_1)
            } else {
                performAction("P2", ActionState.WALK_FORWARD)
            }
        } else if (dist in 80f..250f) {
            // Mid-range: kick or dash in
            val roll = Random.nextFloat()
            when {
                roll < 0.35f -> performAction("P2", ActionState.WALK_FORWARD)
                roll < 0.65f -> performAction("P2", ActionState.KICK_HEAVY)
                roll < 0.85f -> performAction("P2", ActionState.KICK_LIGHT)
                else -> performAction("P2", ActionState.DASHING)
            }
        } else {
            // Close range: mix up jab, heavy punch, or special
            val roll = Random.nextFloat()
            when {
                roll < 0.35f -> performAction("P2", ActionState.PUNCH_LIGHT)
                roll < 0.65f -> performAction("P2", ActionState.PUNCH_HEAVY)
                roll < 0.85f -> performAction("P2", ActionState.KICK_LIGHT)
                else -> {
                    if (p2.superMeter >= p2.data.specialMove1.energyCost) {
                        performAction("P2", ActionState.SPECIAL_1)
                    } else {
                        performAction("P2", ActionState.WALK_BACKWARD)
                    }
                }
            }
        }
    }

    private fun spawnHitSpark(x: Float, y: Float, color: Long, count: Int) {
        for (i in 0 until count) {
            val angle = Random.nextFloat() * 6.28f
            val spd = Random.nextFloat() * 6f + 2f
            particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = kotlin.math.cos(angle) * spd,
                    vy = kotlin.math.sin(angle) * spd,
                    color = color,
                    size = Random.nextFloat() * 7f + 3f,
                    alpha = 1f,
                    life = 0f,
                    maxLife = 18f,
                    type = ParticleType.SPARK
                )
            )
        }
    }

    private fun spawnBlood(x: Float, y: Float, facingRight: Boolean, isHeavy: Boolean, isKnockdown: Boolean) {
        val sprayDir = if (facingRight) 1f else -1f
        val count = if (isHeavy) 32 else 16
        val bloodColor = 0xFFDC2626 // Crimson red blood
        val darkBlood = 0xFF991B1B // Deep arterial blood

        for (i in 0 until count) {
            val angle = if (facingRight) Random.nextFloat() * 1.8f - 0.9f else Random.nextFloat() * 1.8f + 2.24f
            val speed = Random.nextFloat() * (if (isHeavy) 9f else 5.5f) + 2f
            particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = (kotlin.math.cos(angle) * speed) + (sprayDir * 3f),
                    vy = (kotlin.math.sin(angle) * speed) + (Random.nextFloat() * 4f),
                    color = if (i % 2 == 0) bloodColor else darkBlood,
                    size = Random.nextFloat() * (if (isHeavy) 6f else 4f) + 2.5f,
                    alpha = 1f,
                    life = 0f,
                    maxLife = 28f,
                    type = ParticleType.BLOOD_SPLAT
                )
            )
        }

        // Bone fragments on heavy strikes
        if (isHeavy) {
            for (i in 0 until 4) {
                particles.add(
                    Particle(
                        x = x,
                        y = y,
                        vx = sprayDir * (Random.nextFloat() * 5f + 2f),
                        vy = Random.nextFloat() * 6f + 2f,
                        color = 0xFFE2E8F0,
                        size = Random.nextFloat() * 3f + 2f,
                        alpha = 1f,
                        life = 0f,
                        maxLife = 30f,
                        type = ParticleType.BONE_SHARDS
                    )
                )
            }
        }

        // Pool blood stain onto the arena floor
        val stainWidth = if (isHeavy) Random.nextFloat() * 38f + 26f else Random.nextFloat() * 20f + 12f
        val stainHeight = Random.nextFloat() * 9f + 5f
        if (bloodStains.size < 70) {
            bloodStains.add(
                BloodStain(
                    x = x + (sprayDir * Random.nextFloat() * 18f),
                    y = 0f,
                    width = stainWidth,
                    height = stainHeight,
                    alpha = Random.nextFloat() * 0.25f + 0.65f,
                    color = if (Random.nextBoolean()) 0xFF7F1D1D else 0xFF991B1B
                )
            )
        }
    }

    private fun spawnBloodBurst(x: Float, y: Float, count: Int) {
        soundSynth.playGoreSplat()
        for (i in 0 until count) {
            val angle = Random.nextFloat() * 6.28f
            val speed = Random.nextFloat() * 12f + 3f
            particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = kotlin.math.cos(angle) * speed,
                    vy = kotlin.math.sin(angle) * speed + 3f,
                    color = if (i % 2 == 0) 0xFFDC2626 else 0xFF7F1D1D,
                    size = Random.nextFloat() * 7f + 3.5f,
                    alpha = 1f,
                    life = 0f,
                    maxLife = 38f,
                    type = ParticleType.BLOOD_BURST
                )
            )
        }
        // Huge final puddle on knockout
        bloodStains.add(
            BloodStain(
                x = x,
                y = 0f,
                width = 80f,
                height = 16f,
                alpha = 0.95f,
                color = 0xFF450A0A
            )
        )
    }

    private fun spawnDust(x: Float, size: Float) {
        for (i in 0 until 5) {
            particles.add(
                Particle(
                    x = x + Random.nextFloat() * 20f - 10f,
                    y = 5f,
                    vx = Random.nextFloat() * 4f - 2f,
                    vy = Random.nextFloat() * 2f + 0.5f,
                    color = 0xFFCCCCCC,
                    size = size + Random.nextFloat() * 4f,
                    alpha = 0.6f,
                    life = 0f,
                    maxLife = 15f,
                    type = ParticleType.DUST
                )
            )
        }
    }

    private fun addDamagePopup(x: Float, y: Float, text: String, color: Long, isCritical: Boolean) {
        damagePopups.add(
            DamagePopup(
                id = ++popupIdSeq,
                x = x + Random.nextFloat() * 10f - 5f,
                y = y,
                text = text,
                color = color,
                isCritical = isCritical,
                alpha = 1f,
                life = 35
            )
        )
    }

    private data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
}
