package openrs.cache.type;

public enum CacheIndex {
    ANIMATIONS(0),
    SKELETONS(1),
    CONFIGURATION(2),
    COMPONENTS(3),
    SYNTH_SOUNDS(4),
    MAPS(5),
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
    VARBITS(22),
    WORLD_MAP(23),
    QUICK_CHAT_MESSAGES(24),
    QUICK_CHAT_MENUS(25),
    TEXTURE_DEFINITIONS(26),
    PARTICLES(26),
    DEFAULTS(28),
    BILLBOARDS(29),
    NATIVES(30),
    SHADERS(31),
    LOADING_SPRITES(32),
    LOADING_SCREENS(33),
    LOADING_SPRITES_RAW(34),
    CUTSCENES(35);

    private final int id;

    CacheIndex(int id) {
        this.id = id;
    }

    public final int getID() {
        return id;
    }

}