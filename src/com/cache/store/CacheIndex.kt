package core.cache

/**
 * Cache index.
 *
 * @property id the id.
 * @constructor Create empty Cache index.
 */
enum class CacheIndex(
    val id: Int,
) {
    ANIMATIONS(0),
    SKELETONS(1),
    CONFIGURATION(2),
    COMPONENTS(3),
    SYNTH_SOUNDS(4),
    LANDSCAPES(5),
    MUSIC(6),
    MODELS(7),
    SPRITES(8),
    TEXTURES(9),
    HUFFMAN_ENCODING(10),
    MIDI_JINGLES(11),
    CLIENT_SCRIPTS(12),
    FONTMETRICS(13),
    VORBIS(14),
    MIDI_INSTRUMENTS(15),
    SCENERY_CONFIGURATION(16),
    ENUM_CONFIGURATION(17),
    NPC_CONFIGURATION(18),
    ITEM_CONFIGURATION(19),
    SEQUENCE_CONFIGURATION(20),
    GRAPHICS(21),
    VAR_BIT(22),
    WORLD_MAP(23),
    QUICK_CHAT_MESSAGES(24),
    QUICK_CHAT_MENUS(25),
    TEXTURE_DEFINITIONS(26),
}
