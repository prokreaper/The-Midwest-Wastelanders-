package com.example.model

import com.example.R

data class SpecialMove(
    val name: String,
    val command: String,
    val description: String,
    val energyCost: Float,
    val damage: Float,
    val isProjectile: Boolean,
    val color: Long
)

data class MoveFrameData(
    val moveName: String,
    val command: String,
    val startup: Int,
    val active: Int,
    val recovery: Int,
    val onBlock: String, // e.g. "+2", "-4", "-12 (Punishable)"
    val onHit: String, // e.g. "+6", "Knockdown", "Wall Splat"
    val damage: Int,
    val hitType: String // "High", "Mid", "Low", "Overhead", "Unblockable"
)

data class ComboRoute(
    val title: String,
    val inputChain: String,
    val damage: Int,
    val difficulty: String, // "Basic", "Intermediate", "Fatal"
    val notes: String
)

data class AnatomyData(
    val headRadius: Float = 16f,
    val headCritMultiplier: Float = 1.35f,
    val torsoWidth: Float = 30f,
    val torsoHeight: Float = 46f,
    val guardBreakThreshold: Float = 100f
)

data class FighterRadarStats(
    val lethality: Int, // 0 to 100
    val mobility: Int,
    val durability: Int,
    val comboPotential: Int,
    val executionDifficulty: Int
)

data class FighterData(
    val id: String,
    val name: String,
    val epithet: String,
    val archetype: String,
    val difficulty: String,
    val bio: String,
    val fightingStyle: String,
    val threatLevel: String,
    val imageResId: Int?,
    val power: Int, // 1 to 5
    val speed: Int,
    val defense: Int,
    val range: Int,
    val primaryColor: Long,
    val accentColor: Long,
    val auraColor: Long,
    val specialMove1: SpecialMove,
    val superUltraMove: SpecialMove,
    val introDialogue: String,
    val victoryQuote: String,
    val defeatQuote: String,
    val radarStats: FighterRadarStats,
    val anatomy: AnatomyData = AnatomyData(),
    val moveFrameDataList: List<MoveFrameData>,
    val comboRoutes: List<ComboRoute>
)

object FighterRoster {
    val AXEL_RENEGADE = FighterData(
        id = "axel",
        name = "Axel 'Renegade'",
        epithet = "Street Fight Club Champion",
        archetype = "Brutal Brawler",
        difficulty = "Beginner",
        bio = "The undisputed king of the underground street fighting circuit. Clad in cyber-skeleton armor with glowing bio-electric green eyes, he crushes opponents with bone-splintering bare-knuckle hooks and devastating ground-and-pound fury.",
        fightingStyle = "Bare-Knuckle Street Boxing & Bone-Breaking Hooks",
        threatLevel = "LETHAL TITAN",
        imageResId = R.drawable.brawler_renegade,
        power = 5,
        speed = 4,
        defense = 4,
        range = 3,
        primaryColor = 0xFF10B981, // Glowing green
        accentColor = 0xFFEAB300,
        auraColor = 0xFF059669,
        specialMove1 = SpecialMove(
            name = "Toxic Shock Fist",
            command = "Forward + Special",
            description = "Fires a high-velocity green bio-electric shockwave that bursts on impact with heavy arterial splatter.",
            energyCost = 25f,
            damage = 125f,
            isProjectile = true,
            color = 0xFF10B981
        ),
        superUltraMove = SpecialMove(
            name = "Renegade Skullcrusher",
            command = "Max Meter + Ultra",
            description = "A vicious four-hit bare-knuckle barrage lifting the opponent by the throat and slamming them into the asphalt for catastrophic damage.",
            energyCost = 100f,
            damage = 370f,
            isProjectile = false,
            color = 0xFF059669
        ),
        introDialogue = "Rule number one of the Fight Club: You don't leave this cage alive!",
        victoryQuote = "Spitting teeth on the concrete. That's how street legends are forged!",
        defeatQuote = "Just a flesh wound... I'll be back for your skull.",
        radarStats = FighterRadarStats(
            lethality = 94,
            mobility = 78,
            durability = 88,
            comboPotential = 82,
            executionDifficulty = 45
        ),
        moveFrameDataList = listOf(
            MoveFrameData("Lead Jab", "Punch", 10, 3, 4, "+2", "+6", 55, "High"),
            MoveFrameData("Heavy Haymaker", "Kick", 22, 5, 10, "-5", "Knockdown", 135, "Mid"),
            MoveFrameData("Toxic Shock Fist", "Special", 24, 6, 12, "-3", "Stun (+10)", 125, "Mid"),
            MoveFrameData("Renegade Skullcrusher", "Ultra", 12, 18, 20, "-15", "Fatal Crush", 370, "Unblockable")
        ),
        comboRoutes = listOf(
            ComboRoute("Street Bread & Butter", "Punch ➔ Punch ➔ Kick", 220, "Basic", "Fast 3-hit string that guarantees heavy knockdown."),
            ComboRoute("Toxic Wall Wallop", "Dash ➔ Punch ➔ Special ➔ Kick", 310, "Intermediate", "High-damage juggle string causing wall bounce."),
            ComboRoute("Renegade Overkill Finisher", "Jump Kick ➔ Punch ➔ Special ➔ ULTRA", 510, "Fatal", "Full meter 4-hit combo leaving permanent blood stains.")
        )
    )

    val RADIO_CREEP = FighterData(
        id = "radiocreep",
        name = "Valkyria 'Radio Creep'",
        epithet = "Winged Scythe Harvester",
        archetype = "Scythe Speedster",
        difficulty = "Intermediate",
        bio = "An anarchist biohazard executioner with glowing radioactive winged appendages and a double-edged execution scythe. Slices through flesh with surgical speed, soaking the arena floor in crimson spray.",
        fightingStyle = "Acrobatic Scythe Cleaves & Aerial Dive Slashes",
        threatLevel = "APOCALYPTIC REAPER",
        imageResId = R.drawable.fighter_radiocreep,
        power = 4,
        speed = 5,
        defense = 3,
        range = 5,
        primaryColor = 0xFF84CC16, // Toxic Lime
        accentColor = 0xFF22C55E,
        auraColor = 0xFF65A30D,
        specialMove1 = SpecialMove(
            name = "Acid Scythe Blade",
            command = "Back-Forward + Special",
            description = "Flings a whirling crescent scythe blade infused with bio-acid, slicing clean through defenses.",
            energyCost = 25f,
            damage = 115f,
            isProjectile = true,
            color = 0xFF84CC16
        ),
        superUltraMove = SpecialMove(
            name = "Executioners Guillotine",
            command = "Max Meter + Ultra",
            description = "Spreads bio-wings, swoops from the shadows, and decapitates the enemy in a tempest of crimson gore.",
            energyCost = 100f,
            damage = 360f,
            isProjectile = false,
            color = 0xFF4D7C0F
        ),
        introDialogue = "Hear that buzz in the air? That's the sound of your blood pouring out!",
        victoryQuote = "Another head for the biohazard collection. Pathetic!",
        defeatQuote = "The frequency is broken... retreating into shadows...",
        radarStats = FighterRadarStats(
            lethality = 92,
            mobility = 98,
            durability = 68,
            comboPotential = 94,
            executionDifficulty = 68
        ),
        moveFrameDataList = listOf(
            MoveFrameData("Scythe Hilt Strike", "Punch", 11, 4, 4, "+1", "+5", 50, "Mid"),
            MoveFrameData("Cleave Wheel Kick", "Kick", 24, 6, 11, "-4", "Knockdown", 125, "Overhead"),
            MoveFrameData("Acid Scythe Blade", "Special", 22, 5, 10, "-2", "Juggle (+8)", 115, "High"),
            MoveFrameData("Executioners Guillotine", "Ultra", 10, 16, 24, "-18", "Brutal Decap", 360, "Mid")
        ),
        comboRoutes = listOf(
            ComboRoute("Reaper's Harvest", "Punch ➔ Kick ➔ Special", 260, "Basic", "Instant projectile cancel upon kick connection."),
            ComboRoute("Aerial Wing Carnage", "Jump Kick ➔ Dash ➔ Punch ➔ Special", 330, "Intermediate", "High-speed crossup that confuses opponent guard."),
            ComboRoute("Guillotine Overdrive", "Punch ➔ Special ➔ Dash ➔ ULTRA", 490, "Fatal", "Maximum damage execution sequence.")
        )
    )

    val ABYSSAL_QUEEN = FighterData(
        id = "abyssal_queen",
        name = "Ravena 'Abyssal Queen'",
        epithet = "Empress of the Void Carnage",
        archetype = "Demonic Zoner / Boss",
        difficulty = "Advanced",
        bio = "An ancient demonic sovereign who commands biomechanical blood tendrils. She impales her victims from underground and drains their life essence into a dark matter singularity.",
        fightingStyle = "Biomechanical Blood Tendril Impalement & Void Gravity",
        threatLevel = "CALAMITY S-TIER",
        imageResId = R.drawable.fighter_abyssal_queen,
        power = 5,
        speed = 4,
        defense = 4,
        range = 5,
        primaryColor = 0xFFDC2626, // Blood Crimson
        accentColor = 0xFF991B1B,
        auraColor = 0xFF7F1D1D,
        specialMove1 = SpecialMove(
            name = "Tendril Blood Spear",
            command = "Down-Forward + Special",
            description = "Erupts sharp tendril spikes from the ground beneath the opponent, impaling them in a fountain of blood.",
            energyCost = 30f,
            damage = 135f,
            isProjectile = true,
            color = 0xFFDC2626
        ),
        superUltraMove = SpecialMove(
            name = "Abyssal Disembowelment",
            command = "Max Meter + Ultra",
            description = "Rips open the earth, dragging the enemy into a vortex of dark matter and tearing their armor to ribbons.",
            energyCost = 100f,
            damage = 395f,
            isProjectile = false,
            color = 0xFF991B1B
        ),
        introDialogue = "Your flesh will feed the abyss. Scream as loudly as you wish!",
        victoryQuote = "Not even a worthy sacrifice. Your blood tastes of fear.",
        defeatQuote = "The seal weakens... I will return in blood!",
        radarStats = FighterRadarStats(
            lethality = 99,
            mobility = 75,
            durability = 85,
            comboPotential = 89,
            executionDifficulty = 75
        ),
        moveFrameDataList = listOf(
            MoveFrameData("Tendril Slap", "Punch", 12, 4, 5, "0", "+4", 60, "Mid"),
            MoveFrameData("Demon Heel Crush", "Kick", 25, 6, 12, "-6", "Knockdown", 140, "Low"),
            MoveFrameData("Tendril Blood Spear", "Special", 28, 8, 14, "-8", "Launch (+14)", 135, "Unblockable"),
            MoveFrameData("Abyssal Disembowelment", "Ultra", 14, 22, 26, "-20", "Abyssal Gore", 395, "Mid")
        ),
        comboRoutes = listOf(
            ComboRoute("Blood Leech", "Punch ➔ Kick ➔ Special", 280, "Basic", "Pushes opponent into mid-range for tendril eruption."),
            ComboRoute("Vortex Impaler", "Special (Launch) ➔ Jump Kick ➔ Kick", 350, "Intermediate", "Anti-air juggle that catches falling opponents."),
            ComboRoute("Abyssal Extinction", "Tendril Spear ➔ Dash ➔ Punch ➔ ULTRA", 540, "Fatal", "Tears through 55% of the total health bar.")
        )
    )

    val DJ_REBEL = FighterData(
        id = "dj_rebel",
        name = "Roxie 'DJ Rebel'",
        epithet = "Neon Riot Sonic Striker",
        archetype = "High-Voltage Speed Brawler",
        difficulty = "Intermediate",
        bio = "The underground fight club's resident sound engineer and warrior. Channels 150-decibel bass drops through hydraulic audio gauntlets, shattering eardrums and bones with seismic bass shockwaves.",
        fightingStyle = "High-Octane Sonic Kickboxing & Bass Drop Slams",
        threatLevel = "ELECTRIFYING BRAWLER",
        imageResId = R.drawable.fighter_dj_rebel,
        power = 4,
        speed = 5,
        defense = 3,
        range = 4,
        primaryColor = 0xFFEC4899, // Neon Pink / Magenta
        accentColor = 0xFF06B6D4, // Neon Cyan
        auraColor = 0xFFA855F7,
        specialMove1 = SpecialMove(
            name = "Subwoofer Shockwave",
            command = "Forward + Special",
            description = "Fires a concussive neon sonic ring that ruptures opponent guard and knocks them backwards.",
            energyCost = 25f,
            damage = 110f,
            isProjectile = true,
            color = 0xFFEC4899
        ),
        superUltraMove = SpecialMove(
            name = "Drop The Bass Overdrive",
            command = "Max Meter + Ultra",
            description = "Cranks the amps to maximum, launching an explosive sonic dropkick that obliterates the stage in a neon explosion.",
            energyCost = 100f,
            damage = 345f,
            isProjectile = false,
            color = 0xFFD946EF
        ),
        introDialogue = "Turn up the volume! The beat drops when your jaw hits the floor!",
        victoryQuote = "Track finished. Absolute encore! Pick up your teeth on the way out!",
        defeatQuote = "Static on the line... needed more bass...",
        radarStats = FighterRadarStats(
            lethality = 86,
            mobility = 94,
            durability = 72,
            comboPotential = 96,
            executionDifficulty = 60
        ),
        moveFrameDataList = listOf(
            MoveFrameData("Snap Jab", "Punch", 9, 3, 3, "+3", "+7", 45, "High"),
            MoveFrameData("Sonic Dropkick", "Kick", 20, 5, 8, "-3", "Knockdown", 115, "Mid"),
            MoveFrameData("Subwoofer Shockwave", "Special", 20, 4, 9, "-1", "Stun (+6)", 110, "High"),
            MoveFrameData("Drop The Bass Overdrive", "Ultra", 9, 15, 20, "-12", "Sonic Shatter", 345, "Mid")
        ),
        comboRoutes = listOf(
            ComboRoute("Neon Pulse", "Punch ➔ Punch ➔ Kick", 210, "Basic", "Lightning-fast jab confirm into roundhouse."),
            ComboRoute("Sub-Bass Breaker", "Dash ➔ Punch ➔ Special ➔ Kick", 305, "Intermediate", "Wall bounce combo using sonic ring pressure."),
            ComboRoute("Festival of Violence", "Jump Kick ➔ Punch ➔ Punch ➔ Special ➔ ULTRA", 480, "Fatal", "High-flying 5-hit combo with zero drop window.")
        )
    )

    val WASTELAND_WARLORD = FighterData(
        id = "warlord",
        name = "Brutus 'Wasteland Warlord'",
        epithet = "Pit Fight Executioner",
        archetype = "Colossal Heavyweight",
        difficulty = "Intermediate",
        bio = "A scarred desert arena gladiator who survived a hundred illegal deathmatches. Swings a blood-stained war axe with enough brute force to shatter concrete pillars and cleave armor in two.",
        fightingStyle = "Heavy Executioner Axe Cleaves & Earth-Shattering Slams",
        threatLevel = "UNSTOPPABLE JUGGERNAUT",
        imageResId = R.drawable.fighter_wasteland_warlord,
        power = 5,
        speed = 2,
        defense = 5,
        range = 4,
        primaryColor = 0xFFF97316, // Rust Orange
        accentColor = 0xFFB45309,
        auraColor = 0xFF7C2D12,
        specialMove1 = SpecialMove(
            name = "Seismic Earthbreaker",
            command = "Down + Special",
            description = "Drives his axe deep into the asphalt, causing a rolling trench of jagged rock and shrapnel.",
            energyCost = 30f,
            damage = 140f,
            isProjectile = true,
            color = 0xFFF97316
        ),
        superUltraMove = SpecialMove(
            name = "Pit of Carnage Berserk",
            command = "Max Meter + Ultra",
            description = "Goes berserk with a triple spinning executioner cleave that drenches the entire stage floor in blood.",
            energyCost = 100f,
            damage = 385f,
            isProjectile = false,
            color = 0xFFEA580C
        ),
        introDialogue = "Fresh meat for the pit! I'll grind your bones to dust!",
        victoryQuote = "Crushed! Broken! Cleaved! The pit claims another fool!",
        defeatQuote = "The sand... tastes like iron... impossible...",
        radarStats = FighterRadarStats(
            lethality = 97,
            mobility = 52,
            durability = 98,
            comboPotential = 70,
            executionDifficulty = 50
        ),
        moveFrameDataList = listOf(
            MoveFrameData("Heavy Overhead Fist", "Punch", 14, 5, 8, "-2", "+3", 75, "Overhead"),
            MoveFrameData("Cleaver Axe Chop", "Kick", 28, 7, 14, "-8", "Hard Knockdown", 155, "Mid"),
            MoveFrameData("Seismic Earthbreaker", "Special", 30, 8, 16, "-10", "Unblockable Ground", 140, "Low"),
            MoveFrameData("Pit of Carnage Berserk", "Ultra", 15, 24, 28, "-24", "Fatal Cleave", 385, "Mid")
        ),
        comboRoutes = listOf(
            ComboRoute("Pit Brawler Stomp", "Punch ➔ Kick", 230, "Basic", "Massive 2-hit stagger dealing huge guard damage."),
            ComboRoute("Earthquake Juggle", "Seismic Earthbreaker ➔ Dash ➔ Punch", 295, "Intermediate", "Catches falling opponent after trench eruption."),
            ComboRoute("Executioner's Wrath", "Jump Heavy ➔ Punch ➔ Special ➔ ULTRA", 525, "Fatal", "Crushes 53% HP with explosive blood fountain.")
        )
    )

    val ALL_FIGHTERS = listOf(AXEL_RENEGADE, RADIO_CREEP, ABYSSAL_QUEEN, DJ_REBEL, WASTELAND_WARLORD)
    val PLAYABLE_FIGHTERS = listOf(AXEL_RENEGADE, RADIO_CREEP, DJ_REBEL, WASTELAND_WARLORD)
}
