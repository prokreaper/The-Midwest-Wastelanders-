package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.audio.HapticManager
import com.example.audio.SoundSynthesizer
import com.example.game.CombatEngine
import com.example.model.AiDifficulty
import com.example.model.Arenas
import com.example.model.FighterData
import com.example.model.FighterRoster
import com.example.model.GameMode
import com.example.model.StageArena
import com.example.ui.CharacterBreakdownLabScreen
import com.example.ui.CharacterSelectScreen
import com.example.ui.FightScreen
import com.example.ui.FighterDossierScreen
import com.example.ui.MainMenuScreen
import com.example.ui.PostMatchScreen
import com.example.ui.theme.ApexStrikerTheme

enum class Screen {
    MAIN_MENU,
    CHARACTER_SELECT,
    FIGHT,
    POST_MATCH,
    FIGHTER_DOSSIER
}

class MainActivity : ComponentActivity() {

    private lateinit var soundSynth: SoundSynthesizer
    private lateinit var haptics: HapticManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        soundSynth = SoundSynthesizer()
        haptics = HapticManager(this)

        setContent {
            ApexStrikerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ApexStrikerApp(
                        soundSynth = soundSynth,
                        haptics = haptics,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ApexStrikerApp(
    soundSynth: SoundSynthesizer,
    haptics: HapticManager,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(Screen.MAIN_MENU) }
    var currentGameMode by remember { mutableStateOf(GameMode.ARCADE) }
    var isSoundMuted by remember { mutableStateOf(false) }

    // Fighters and Match Settings
    var p1Fighter by remember { mutableStateOf(FighterRoster.AXEL_RENEGADE) }
    var p2Fighter by remember { mutableStateOf(FighterRoster.RADIO_CREEP) }
    var currentArena by remember { mutableStateOf(Arenas.RENEGADE_ALLEY) }
    var currentDifficulty by remember { mutableStateOf(AiDifficulty.MEDIUM) }

    // Arcade Ladder progression
    var arcadeStage by remember { mutableIntStateOf(1) }
    val arcadeLadderOpponents = remember {
        listOf(
            FighterRoster.RADIO_CREEP,
            FighterRoster.DJ_REBEL,
            FighterRoster.WASTELAND_WARLORD,
            FighterRoster.AXEL_RENEGADE,
            FighterRoster.ABYSSAL_QUEEN // Final Boss!
        )
    }

    // Active Combat Engine
    var activeEngine by remember { mutableStateOf<CombatEngine?>(null) }

    // Post-Match Stats
    var lastWinnerId by remember { mutableStateOf("P1") }
    var lastP1Rounds by remember { mutableIntStateOf(2) }
    var lastP2Rounds by remember { mutableIntStateOf(0) }
    var lastMaxCombo by remember { mutableIntStateOf(3) }
    var lastDamageDealt by remember { mutableIntStateOf(1000) }

    fun startBattle(p1: FighterData, p2: FighterData, arena: StageArena, difficulty: AiDifficulty) {
        p1Fighter = p1
        p2Fighter = p2
        currentArena = arena
        currentDifficulty = difficulty

        val engine = CombatEngine(
            p1Data = p1,
            p2Data = p2,
            arena = arena,
            mode = currentGameMode,
            difficulty = difficulty,
            soundSynth = soundSynth,
            haptics = haptics
        )
        activeEngine = engine
        currentScreen = Screen.FIGHT
    }

    when (currentScreen) {
        Screen.MAIN_MENU -> {
            MainMenuScreen(
                onSelectMode = { mode ->
                    currentGameMode = mode
                    soundSynth.playUiClick()
                    arcadeStage = 1
                    currentScreen = Screen.CHARACTER_SELECT
                },
                onOpenDossier = {
                    soundSynth.playUiClick()
                    currentScreen = Screen.FIGHTER_DOSSIER
                },
                onToggleSound = {
                    isSoundMuted = !isSoundMuted
                    soundSynth.setMuted(isSoundMuted)
                },
                isSoundMuted = isSoundMuted,
                modifier = modifier
            )
        }

        Screen.CHARACTER_SELECT -> {
            CharacterSelectScreen(
                gameMode = currentGameMode,
                onStartFight = { p1, p2, arena, diff ->
                    soundSynth.playUiClick()
                    val actualP2 = if (currentGameMode == GameMode.ARCADE) {
                        arcadeLadderOpponents[0]
                    } else {
                        p2
                    }
                    startBattle(p1, actualP2, arena, diff)
                },
                onBack = {
                    soundSynth.playUiClick()
                    currentScreen = Screen.MAIN_MENU
                },
                modifier = modifier
            )
        }

        Screen.FIGHT -> {
            val engine = activeEngine
            if (engine != null) {
                FightScreen(
                    engine = engine,
                    onMatchFinished = { winnerId, p1Score ->
                        lastWinnerId = winnerId
                        lastP1Rounds = engine.p1.roundsWon
                        lastP2Rounds = engine.p2.roundsWon
                        lastMaxCombo = engine.p1.comboHits
                        lastDamageDealt = engine.p1.comboDamage.toInt().coerceAtLeast(600)
                        currentScreen = Screen.POST_MATCH
                    },
                    onExitToMenu = {
                        soundSynth.playUiClick()
                        currentScreen = Screen.MAIN_MENU
                    },
                    modifier = modifier
                )
            }
        }

        Screen.POST_MATCH -> {
            PostMatchScreen(
                winnerId = lastWinnerId,
                p1Data = p1Fighter,
                p2Data = p2Fighter,
                gameMode = currentGameMode,
                arcadeStage = arcadeStage,
                p1RoundsWon = lastP1Rounds,
                p2RoundsWon = lastP2Rounds,
                maxCombo = lastMaxCombo,
                totalDamageDealt = lastDamageDealt,
                onNextFight = {
                    soundSynth.playUiClick()
                    arcadeStage++
                    val nextOpponent = if (arcadeStage <= arcadeLadderOpponents.size) {
                        arcadeLadderOpponents[arcadeStage - 1]
                    } else {
                        FighterRoster.ABYSSAL_QUEEN
                    }
                    startBattle(p1Fighter, nextOpponent, currentArena, currentDifficulty)
                },
                onRematch = {
                    soundSynth.playUiClick()
                    startBattle(p1Fighter, p2Fighter, currentArena, currentDifficulty)
                },
                onCharacterSelect = {
                    soundSynth.playUiClick()
                    currentScreen = Screen.CHARACTER_SELECT
                },
                onMainMenu = {
                    soundSynth.playUiClick()
                    currentScreen = Screen.MAIN_MENU
                },
                modifier = modifier
            )
        }

        Screen.FIGHTER_DOSSIER -> {
            CharacterBreakdownLabScreen(
                onBack = {
                    soundSynth.playUiClick()
                    currentScreen = Screen.MAIN_MENU
                },
                modifier = modifier
            )
        }
    }
}
