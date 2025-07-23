package core.cache

/**
 * Cache archive.
 *
 * @property id the id.
 * @constructor Create empty Cache archive.
 */
enum class CacheArchive(
    val id: Int,
) {
    FLOOR_UNDERLAYS(1),
    PLAYER_KIT(3),
    FLOOR_OVERLAYS(4),
    INVENTORIES(5),
    PARAM_TYPE(11),
    STRUCT_TYPE(26),
    BAS_TYPE(32),
}
