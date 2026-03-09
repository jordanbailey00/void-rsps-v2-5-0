package content.area.karamja.tzhaar_city

import world.gregs.voidps.engine.GameLoop
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.queue.strongQueue
import world.gregs.voidps.engine.timer.CLIENT_TICKS

enum class JadTelegraphState(val encoded: Int) {
    Idle(0),
    MagicWindup(1),
    RangedWindup(2),
}

enum class JadCommittedAttackStyle(val attackId: String) {
    None(""),
    Magic("magic"),
    Ranged("range"),
}

const val JAD_HIT_TARGET_QUEUE_TICKS = 3
const val JAD_HIT_CLIENT_DELAY = 64

val JAD_HIT_RESOLVE_OFFSET_TICKS: Int = JAD_HIT_TARGET_QUEUE_TICKS + CLIENT_TICKS.toTicks(JAD_HIT_CLIENT_DELAY) + 1

private const val JAD_TELEGRAPH_STATE_KEY = "jad_telegraph_state"
private const val JAD_TELEGRAPH_START_TICK_KEY = "jad_telegraph_start_tick"
private const val JAD_HIT_RESOLVE_TICK_KEY = "jad_hit_resolve_tick"
private const val JAD_COMMITTED_ATTACK_STYLE_KEY = "jad_committed_attack_style"
private const val JAD_ATTACK_SEQUENCE_KEY = "jad_attack_sequence"
private const val JAD_TELEGRAPH_CLEAR_QUEUE = "jad_telegraph_clear"

fun jadCommittedAttackStyleForAttackId(attackId: String): JadCommittedAttackStyle? =
    when (attackId) {
        JadCommittedAttackStyle.Magic.attackId -> JadCommittedAttackStyle.Magic
        JadCommittedAttackStyle.Ranged.attackId -> JadCommittedAttackStyle.Ranged
        else -> null
    }

fun jadTelegraphStateForStyle(style: JadCommittedAttackStyle): JadTelegraphState =
    when (style) {
        JadCommittedAttackStyle.Magic -> JadTelegraphState.MagicWindup
        JadCommittedAttackStyle.Ranged -> JadTelegraphState.RangedWindup
        JadCommittedAttackStyle.None -> JadTelegraphState.Idle
    }

var NPC.jadTelegraphState: JadTelegraphState
    get() = get(JAD_TELEGRAPH_STATE_KEY, JadTelegraphState.Idle)
    set(value) = set(JAD_TELEGRAPH_STATE_KEY, value)

var NPC.jadTelegraphStartTick: Int
    get() = get(JAD_TELEGRAPH_START_TICK_KEY, -1)
    set(value) = set(JAD_TELEGRAPH_START_TICK_KEY, value)

var NPC.jadHitResolveTick: Int
    get() = get(JAD_HIT_RESOLVE_TICK_KEY, -1)
    set(value) = set(JAD_HIT_RESOLVE_TICK_KEY, value)

var NPC.jadCommittedAttackStyle: JadCommittedAttackStyle
    get() = get(JAD_COMMITTED_ATTACK_STYLE_KEY, JadCommittedAttackStyle.None)
    set(value) = set(JAD_COMMITTED_ATTACK_STYLE_KEY, value)

var NPC.jadAttackSequence: Int
    get() = get(JAD_ATTACK_SEQUENCE_KEY, 0)
    set(value) = set(JAD_ATTACK_SEQUENCE_KEY, value)

fun NPC.beginJadTelegraph(style: JadCommittedAttackStyle, onsetTick: Int = GameLoop.tick) {
    require(id == "tztok_jad") { "Jad telegraph state can only be started for tztok_jad, got '$id'." }
    require(style != JadCommittedAttackStyle.None) { "Jad telegraph requires a committed magic or ranged attack style." }

    val sequence = jadAttackSequence + 1
    jadAttackSequence = sequence
    jadCommittedAttackStyle = style
    jadTelegraphState = jadTelegraphStateForStyle(style)
    jadTelegraphStartTick = onsetTick
    jadHitResolveTick = onsetTick + JAD_HIT_RESOLVE_OFFSET_TICKS

    strongQueue(JAD_TELEGRAPH_CLEAR_QUEUE, JAD_HIT_RESOLVE_OFFSET_TICKS) {
        if (jadAttackSequence == sequence) {
            clearJadTelegraph()
        }
    }
}

fun NPC.beginJadTelegraphForAttack(attackId: String): Boolean {
    val style = jadCommittedAttackStyleForAttackId(attackId) ?: return false
    beginJadTelegraph(style)
    return true
}

fun NPC.clearJadTelegraph() {
    jadTelegraphState = JadTelegraphState.Idle
    jadCommittedAttackStyle = JadCommittedAttackStyle.None
    jadTelegraphStartTick = -1
    jadHitResolveTick = -1
}

