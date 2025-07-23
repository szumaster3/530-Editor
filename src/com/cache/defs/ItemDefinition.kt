package com.cache.defs

import com.cache.store.Cache
import com.cache.io.InputStream
import com.cache.io.OutputStream

/**
 * The type Item definitions.
 */
class ItemDefinition
@JvmOverloads
constructor(
    cache: Cache,
    /**
     * The Id.
     */
    @JvmField var id: Int,
    load: Boolean = true,
) : Cloneable {
    /**
     * Gets id.
     *
     * @return the id
     */
    /**
     * Sets id.
     *
     * @param id the id
     */
    /**
     * Is loaded boolean.
     *
     * @return the boolean
     */
    /**
     * The Loaded.
     */
    var isLoaded: Boolean = false
    /**
     * Gets inv model id.
     *
     * @return the inv model id
     */
    /**
     * Sets inv model id.
     *
     * @param modelId the model id
     */
    /**
     * The Model id.
     */
    var invModelId: Int = 0

    /**
     * Gets name.
     *
     * @return the name
     */
    /**
     * Sets name.
     *
     * @param name the name
     */
    /**
     * The Name.
     */
    @JvmField
    var name: String? = null
    /**
     * Gets inv model zoom.
     *
     * @return the inv model zoom
     */
    /**
     * Sets inv model zoom.
     *
     * @param modelZoom the model zoom
     */
    /**
     * The Zoom 2 d.
     */
    var invModelZoom: Int = 0

    /**
     * Gets xan 2 d.
     *
     * @return the xan 2 d
     */
    /**
     * Sets xan 2 d.
     *
     * @param xan2d the xan 2 d
     */
    /**
     * The Xan 2 d.
     */
    @JvmField
    var xan2d: Int = 0

    /**
     * Gets yan 2 d.
     *
     * @return the yan 2 d
     */
    /**
     * Sets yan 2 d.
     *
     * @param yan2d the yan 2 d
     */
    /**
     * The Yan 2 d.
     */
    @JvmField
    var yan2d: Int = 0

    /**
     * The X offset 2 d.
     */
    var xOffset2d: Int = 0

    /**
     * The Y offset 2 d.
     */
    var yOffset2d: Int = 0

    /**
     * Gets equip slot.
     *
     * @return the equip slot
     */
    /**
     * Sets equip slot.
     *
     * @param equipSlot the equip slot
     */
    /**
     * The Equip slot.
     */
    @JvmField
    var equipSlot: Int = 0

    /**
     * Gets equip type.
     *
     * @return the equip type
     */
    /**
     * Sets equip type.
     *
     * @param equipType the equip type
     */
    /**
     * The Equip type.
     */
    @JvmField
    var equipType: Int = 0

    /**
     * Gets stackable.
     *
     * @return the stackable
     */
    /**
     * Sets stackable.
     *
     * @param stackable the stackable
     */
    /**
     * The Stackable.
     */
    @JvmField
    var stackable: Int = 0

    /**
     * Gets cost.
     *
     * @return the cost
     */
    /**
     * Sets cost.
     *
     * @param cost the cost
     */
    /**
     * The Cost.
     */
    @JvmField
    var cost: Int = 0
    /**
     * Is members only boolean.
     *
     * @return the boolean
     */
    /**
     * Sets members only.
     *
     * @param membersOnly the members only
     */
    /**
     * The Members only.
     */
    var isMembersOnly: Boolean = false
    /**
     * Gets male equip model id 1.
     *
     * @return the male equip model id 1
     */
    /**
     * Sets male equip model id 1.
     *
     * @param maleEquipModelId1 the male equip model id 1
     */
    /**
     * The Male equip 1.
     */
    var maleEquipModelId1: Int = 0
    /**
     * Gets female equip model id 1.
     *
     * @return the female equip model id 1
     */
    /**
     * Sets female equip model id 1.
     *
     * @param femaleEquipModelId1 the female equip model id 1
     */
    /**
     * The Female equip 1.
     */
    var femaleEquipModelId1: Int = 0
    /**
     * Gets male equip model id 2.
     *
     * @return the male equip model id 2
     */
    /**
     * Sets male equip model id 2.
     *
     * @param maleEquipModelId2 the male equip model id 2
     */
    /**
     * The Male equip 2.
     */
    var maleEquipModelId2: Int = 0
    /**
     * Gets female equip model id 2.
     *
     * @return the female equip model id 2
     */
    /**
     * Sets female equip model id 2.
     *
     * @param femaleEquipModelId2 the female equip model id 2
     */
    /**
     * The Female equip 2.
     */
    var femaleEquipModelId2: Int = 0

    /**
     * Gets male equip model id 3.
     *
     * @return the male equip model id 3
     */
    /**
     * Sets male equip model id 3.
     *
     * @param maleEquipModelId3 the male equip model id 3
     */
    /**
     * The Male equip model id 3.
     */
    @JvmField
    var maleEquipModelId3: Int = 0

    /**
     * Gets female equip model id 3.
     *
     * @return the female equip model id 3
     */
    /**
     * Sets female equip model id 3.
     *
     * @param femaleEquipModelId3 the female equip model id 3
     */
    /**
     * The Female equip model id 3.
     */
    @JvmField
    var femaleEquipModelId3: Int = 0
    /**
     * Get ground options string [ ].
     *
     * @return the string [ ]
     */
    /**
     * The Ground options.
     */
    lateinit var groundOptions: Array<String?>

    /**
     * Get inventory options string [ ].
     *
     * @return the string [ ]
     */
    /**
     * The Inventory options.
     */
    @JvmField
    var inventoryOptions: Array<String?>? = null

    /**
     * Get original model colors int [ ].
     *
     * @return the int [ ]
     */
    /**
     * Sets original model colors.
     *
     * @param originalModelColors the original model colors
     */
    /**
     * The Original model colors.
     */
    @JvmField
    var originalModelColors: IntArray? = null

    /**
     * Get modified model colors int [ ].
     *
     * @return the int [ ]
     */
    /**
     * Sets modified model colors.
     *
     * @param modifiedModelColors the modified model colors
     */
    /**
     * The Modified model colors.
     */
    @JvmField
    var modifiedModelColors: IntArray? = null

    /**
     * The Original texture colors.
     */
    @JvmField
    var originalTextureColors: ShortArray? = null

    /**
     * The Modified texture colors.
     */
    @JvmField
    var modifiedTextureColors: ShortArray? = null

    /**
     * The Recolor palette.
     */
    @JvmField
    var recolorPalette: ByteArray? = null

    /**
     * The Unknown array 2.
     */
    @JvmField
    var unknownArray2: IntArray? = null

    /**
     * The Unknown array 4.
     */
    lateinit var unknownArray4: IntArray

    /**
     * The Unknown array 5.
     */
    lateinit var unknownArray5: IntArray

    /**
     * The Unknown array 6.
     */

    lateinit var unknownArray6: ByteArray

    /**
     * The Unknown array 3.
     */

    lateinit var unknownArray3: ByteArray
    /**
     * Is unnoted boolean.
     *
     * @return the boolean
     */
    /**
     * Sets unnoted.
     *
     * @param unnoted the unnoted
     */
    /**
     * The Unnoted.
     */
    var isUnnoted: Boolean = false

    /**
     * The Primary male dialogue head.
     */
    @JvmField
    var primaryMaleDialogueHead: Int = 0

    /**
     * The Primary female dialogue head.
     */
    @JvmField
    var primaryFemaleDialogueHead: Int = 0

    /**
     * The Secondary male dialogue head.
     */
    @JvmField
    var secondaryMaleDialogueHead: Int = 0

    /**
     * The Secondary female dialogue head.
     */
    @JvmField
    var secondaryFemaleDialogueHead: Int = 0

    /**
     * The Zan 2 d.
     */
    @JvmField
    var Zan2d: Int = 0

    /**
     * The Dummy item.
     */
    @JvmField
    var dummyItem: Int = 0

    /**
     * The Switch note item id.
     */
    @JvmField
    var switchNoteItemId: Int = 0

    /**
     * The Noted item id.
     */
    @JvmField
    var notedItemId: Int = 0

    /**
     * Get stack ids int [ ].
     *
     * @return the int [ ]
     */
    /**
     * Sets stack ids.
     *
     * @param stackIds the stack ids
     */
    /**
     * The Stack ids.
     */
    @JvmField
    var stackIds: IntArray? = null
    /**
     * Get stack amounts int [ ].
     *
     * @return the int [ ]
     */
    /**
     * Sets stack amounts.
     *
     * @param stackAmounts the stack amounts
     */
    /**
     * The Stack amounts.
     */
    var stackAmounts: IntArray? = null

    /**
     * The Floor scale x.
     */
    @JvmField
    var floorScaleX: Int = 0

    /**
     * The Floor scale y.
     */
    @JvmField
    var floorScaleY: Int = 0

    /**
     * The Floor scale z.
     */
    @JvmField
    var floorScaleZ: Int = 0

    /**
     * The Ambience.
     */
    @JvmField
    var ambience: Int = 0

    /**
     * The Diffusion.
     */
    @JvmField
    var diffusion: Int = 0

    /**
     * Gets team id.
     *
     * @return the team id
     */
    /**
     * Sets team id.
     *
     * @param teamId the team id
     */
    /**
     * The Team id.
     */
    @JvmField
    var teamId: Int = 0

    /**
     * Gets switch lend item id.
     *
     * @return the switch lend item id
     */
    /**
     * Sets switch lend item id.
     *
     * @param switchLendItemId the switch lend item id
     */
    /**
     * The Switch lend item id.
     */
    @JvmField
    var switchLendItemId: Int = 0

    /**
     * Gets lended item id.
     *
     * @return the lended item id
     */
    /**
     * Sets lended item id.
     *
     * @param lendedItemId the lended item id
     */
    /**
     * The Lended item id.
     */
    @JvmField
    var lendedItemId: Int = 0

    /**
     * The Male wield x.
     */
    @JvmField
    var maleWieldX: Int = 0

    /**
     * The Male wield y.
     */
    @JvmField
    var maleWieldY: Int = 0

    /**
     * The Male wield z.
     */
    @JvmField
    var maleWieldZ: Int = 0

    /**
     * The Female wield x.
     */
    @JvmField
    var femaleWieldX: Int = 0

    /**
     * The Female wield y.
     */
    @JvmField
    var femaleWieldY: Int = 0

    /**
     * The Female wield z.
     */
    @JvmField
    var femaleWieldZ: Int = 0

    /**
     * The Unknown int 18.
     */
    @JvmField
    var unknownInt18: Int = 0

    /**
     * The Unknown int 19.
     */
    @JvmField
    var unknownInt19: Int = 0

    /**
     * The Unknown int 20.
     */
    @JvmField
    var unknownInt20: Int = 0

    /**
     * The Unknown int 21.
     */
    @JvmField
    var unknownInt21: Int = 0

    /**
     * The Unknown int 22.
     */
    @JvmField
    var unknownInt22: Int = 0

    /**
     * The Unknown int 23.
     */
    @JvmField
    var unknownInt23: Int = 0

    /**
     * The Unknown value 1.
     */
    var unknownValue1: Int = 0

    /**
     * The Unknown value 2.
     */
    var unknownValue2: Int = 0

    /**
     * The Client script data.
     */
    @JvmField
    var clientScriptData: HashMap<Int, Any>? = null
    private var opcode218 = 0
    private var opcode219 = 0

    /**
     * Instantiates a new Item definitions.
     *
     * @param cache the cache
     * @param id    the id
     * @param load  the load
     */

    /**
     * Instantiates a new Item definitions.
     *
     * @param cache the cache
     * @param id    the id
     */
    init {
        this.setDefaultsVariableValules()
        this.setDefaultOptions()
        if (load) {
            this.loadItemDefinition(cache)
        }
    }

    /**
     * Gets offset 2 d.
     *
     * @return the offset 2 d
     */
    fun getxOffset2d(): Int = this.xOffset2d

    /**
     * Sets offset 2 d.
     *
     * @param xOffset2d the x offset 2 d
     */
    fun setxOffset2d(xOffset2d: Int) {
        this.xOffset2d = xOffset2d
    }

    /**
     * Gets offset 2 d.
     *
     * @return the offset 2 d
     */
    fun getyOffset2d(): Int = this.yOffset2d

    /**
     * Sets offset 2 d.
     *
     * @param yOffset2d the y offset 2 d
     */
    fun setyOffset2d(yOffset2d: Int) {
        this.yOffset2d = yOffset2d
    }

    /**
     * Write.
     *
     * @param cache the cache
     */
    fun write(cache: Cache) {
        cache.indexes[19].putFile(this.archiveId, this.fileId, this.encode())
    }

    private fun loadItemDefinition(cache: Cache) {
        val data =
            cache.indexes[19].getFile(
                this.archiveId,
                fileId,
            )
        if (data != null) {
            try {
                this.readOpcodeValues(InputStream(data))
            } catch (var4: RuntimeException) {
                var4.printStackTrace()
            }

            if (this.notedItemId != -1) {
                this.toNote(cache)
            }

            if (this.lendedItemId != -1) {
                this.toLend(cache)
            }

            this.isLoaded = true
        }
    }

    private fun toNote(cache: Cache) {
        val realItem = getItemDefinition(cache, this.switchNoteItemId)
        this.isMembersOnly = realItem.isMembersOnly
        this.cost = realItem.cost
        this.name = realItem.name
        this.stackable = 1
    }

    private fun toLend(cache: Cache) {
        val realItem = getItemDefinition(cache, this.switchLendItemId)
        this.originalModelColors = realItem.originalModelColors
        this.modifiedModelColors = realItem.modifiedModelColors
        this.teamId = realItem.teamId
        this.cost = 0
        this.isMembersOnly = realItem.isMembersOnly
        this.name = realItem.name
        this.inventoryOptions = arrayOfNulls(5)
        this.groundOptions = realItem.groundOptions
        if (realItem.inventoryOptions != null) {
            System.arraycopy(realItem.inventoryOptions, 0, this.inventoryOptions, 0, 4)
        }

        inventoryOptions!![4] = "Discard"
        this.maleEquipModelId1 = realItem.maleEquipModelId1
        this.maleEquipModelId2 = realItem.maleEquipModelId2
        this.femaleEquipModelId1 = realItem.femaleEquipModelId1
        this.femaleEquipModelId2 = realItem.femaleEquipModelId2
        this.maleEquipModelId3 = realItem.maleEquipModelId3
        this.femaleEquipModelId3 = realItem.femaleEquipModelId3
        this.equipType = realItem.equipType
        this.equipSlot = realItem.equipSlot
    }

    val archiveId: Int
        /**
         * Gets archive id.
         *
         * @return the archive id
         */
        get() = this.id ushr 8

    val fileId: Int
        /**
         * Gets file id.
         *
         * @return the file id
         */
        get() = 0xff and this.id

    /**
     * Has special bar boolean.
     *
     * @return the boolean
     */
    fun hasSpecialBar(): Boolean {
        if (this.clientScriptData == null) {
            return false
        } else {
            val specialBar = clientScriptData!![686]
            return specialBar != null && specialBar is Int && specialBar == 1
        }
    }

    val renderAnimId: Int
        /**
         * Gets render anim id.
         *
         * @return the render anim id
         */
        get() {
            if (this.clientScriptData == null) {
                return 1426
            } else {
                val animId = clientScriptData!![644]
                return if (animId != null && animId is Int) animId else 1426
            }
        }

    val questId: Int
        /**
         * Gets quest id.
         *
         * @return the quest id
         */
        get() {
            if (this.clientScriptData == null) {
                return -1
            } else {
                println(this.clientScriptData)
                val questId = clientScriptData!![861]
                return if (questId != null && questId is Int) questId else -1
            }
        }

    val wearingSkillRequiriments: HashMap<*, *>?
        /**
         * Gets wearing skill requiriments.
         *
         * @return the wearing skill requiriments
         */
        get() {
            // Return null if clientScriptData is null
            if (this.clientScriptData == null) {
                return null
            }

            // Using a type-safe HashMap for skills
            val skills = hashMapOf<Int, Int>()
            var nextLevel = -1
            var nextSkill = -1

            // Iterating over the keys in the clientScriptData
            val iterator = clientScriptData!!.keys.iterator()

            while (iterator.hasNext()) {
                val key = iterator.next()
                val value = clientScriptData!![key]

                // Skip the entry if the value is not a String
                if (value !is String) {
                    when (key) {
                        // Handle case where key == 23
                        23 -> {
                            skills[4] = value as Int
                            skills[11] = 61
                        }
                        // Handle keys in the range 749 to 797
                        in 749 until 797 -> {
                            if (key % 2 == 0) {
                                nextLevel = value as Int
                            } else {
                                nextSkill = value as Int
                            }

                            // If both nextLevel and nextSkill are available, add them to skills
                            if (nextLevel != -1 && nextSkill != -1) {
                                skills[nextSkill] = nextLevel
                                nextLevel = -1
                                nextSkill = -1
                            }
                        }
                    }
                }
            }

            return skills
        }

    /**
     * Print client script data.
     */
    fun printClientScriptData() {
        val key2: Iterator<*> = clientScriptData!!.keys.iterator()

        while (key2.hasNext()) {
            val requiriments = (key2.next() as Int)
            val value = clientScriptData!![requiriments]
            println("KEY: $requiriments, VALUE: $value")
        }

        val requiriments1 = this.wearingSkillRequiriments
        if (requiriments1 == null) {
            println("null.")
        } else {
            println(requiriments1.keys.size)
            val value1: Iterator<*> = requiriments1.keys.iterator()

            while (value1.hasNext()) {
                val key21 = (value1.next() as Int)
                val value2 = requiriments1[key21]
                println("SKILL: $key21, LEVEL: $value2")
            }
        }
    }

    private fun setDefaultOptions() {
        this.groundOptions = arrayOf(null, null, "Take", null, null)
        this.inventoryOptions = arrayOf(null, null, null, null, "Drop")
    }

    private fun setDefaultsVariableValules() {
        this.name = "null"
        this.maleEquipModelId1 = -1
        this.maleEquipModelId2 = -1
        this.femaleEquipModelId1 = -1
        this.femaleEquipModelId2 = -1
        this.invModelZoom = 2000
        this.switchLendItemId = -1
        this.lendedItemId = -1
        this.switchNoteItemId = -1
        this.notedItemId = -1
        this.floorScaleZ = 128
        this.floorScaleX = 128
        this.floorScaleY = 128
        this.cost = 1
        this.maleEquipModelId3 = -1
        this.femaleEquipModelId3 = -1
        this.teamId = -1
        this.equipType = -1
        this.equipSlot = -1
        this.primaryMaleDialogueHead = -1
        this.secondaryMaleDialogueHead = -1
        this.primaryFemaleDialogueHead = -1
        this.secondaryFemaleDialogueHead = -1
        this.Zan2d = 0
    }

    /**
     * Encode byte [ ].
     *
     * @return the byte [ ]
     */
    fun encode(): ByteArray {
        val stream = OutputStream()
        stream.writeByte(1)
        stream.writeBigSmart(this.invModelId)
        if (this.name != "null" && this.notedItemId == -1) {
            stream.writeByte(2)
            stream.writeString(name!!)
        }

        if (this.invModelZoom != 2000) {
            stream.writeByte(4)
            stream.writeShort(this.invModelZoom)
        }

        if (this.xan2d != 0) {
            stream.writeByte(5)
            stream.writeShort(this.xan2d)
        }

        if (this.yan2d != 0) {
            stream.writeByte(6)
            stream.writeShort(this.yan2d)
        }

        var data: Int
        if (this.xOffset2d != 0) {
            stream.writeByte(7)
            var translateX = this.xOffset2d
            if (translateX < -32767) {
                translateX += 65536
            }
            stream.writeShort(translateX)
        }

        if (this.yOffset2d != 0) {
            stream.writeByte(8)
            var translateY = this.yOffset2d
            if (translateY < -32767) {
                translateY += 65536
            }
            stream.writeShort(translateY)
        }

        if (this.stackable >= 1 && this.notedItemId == -1) {
            stream.writeByte(11)
        }

        if (this.cost != 1 && this.lendedItemId == -1) {
            stream.writeByte(12)
            stream.writeInt(this.cost)
        }

        if (this.equipSlot != -1) {
            stream.writeByte(13)
            stream.writeByte(this.equipSlot)
        }

        if (this.equipType != -1) {
            stream.writeByte(14)
            stream.writeByte(this.equipType)
        }

        if (this.isMembersOnly && this.notedItemId == -1) {
            stream.writeByte(16)
        }

        if (this.maleEquipModelId1 != -1) {
            stream.writeByte(23)
            stream.writeBigSmart(this.maleEquipModelId1)
        }

        if (this.maleEquipModelId2 != -1) {
            stream.writeByte(24)
            stream.writeBigSmart(this.maleEquipModelId2)
        }

        if (this.femaleEquipModelId1 != -1) {
            stream.writeByte(25)
            stream.writeBigSmart(this.femaleEquipModelId1)
        }

        if (this.femaleEquipModelId2 != -1) {
            stream.writeByte(26)
            stream.writeBigSmart(this.femaleEquipModelId2)
        }

        for (index in 0..4) {
            val option = groundOptions[index]
            if ((index == 5 && option == "Examine") || (index == 2 && option == "Take") || option == null) {
                continue
            }
            stream.writeByte(30 + index)
            stream.writeString(groundOptions[index]!!)
        }

        for (index in 0..4) {
            val option = inventoryOptions!![index]
            if (index == 4 && option == "Drop" || option == null) {
                continue
            }
            stream.writeByte(35 + index)
            stream.writeString(inventoryOptions!![index]!!)
        }

        if (this.originalModelColors != null && this.modifiedModelColors != null) {
            stream.writeByte(40)
            stream.writeByte(originalModelColors!!.size)

            data = 0
            while (data < originalModelColors!!.size) {
                stream.writeShort(originalModelColors!![data])
                stream.writeShort(modifiedModelColors!![data])
                ++data
            }
        }

        if (this.originalTextureColors != null && this.modifiedTextureColors != null) {
            stream.writeByte(41)
            stream.writeByte(originalTextureColors!!.size)

            data = 0
            while (data < originalTextureColors!!.size) {
                stream.writeShort(originalTextureColors!![data].toInt())
                stream.writeShort(modifiedTextureColors!![data].toInt())
                ++data
            }
        }

        if (this.recolorPalette != null) {
            stream.writeByte(42)
            stream.writeByte(recolorPalette!!.size)

            data = 0
            while (data < recolorPalette!!.size) {
                stream.writeByte(recolorPalette!![data].toInt())
                ++data
            }
        }

        if (this.isUnnoted) {
            stream.writeByte(65)
        }

        if (this.maleEquipModelId3 != -1) {
            stream.writeByte(78)
            stream.writeBigSmart(this.maleEquipModelId3)
        }

        if (this.femaleEquipModelId3 != -1) {
            stream.writeByte(79)
            stream.writeBigSmart(this.femaleEquipModelId3)
        }

        if (this.primaryMaleDialogueHead != -1) {
            stream.writeByte(90)
            stream.writeBigSmart(this.primaryMaleDialogueHead)
        }

        if (this.primaryFemaleDialogueHead != -1) {
            stream.writeByte(91)
            stream.writeBigSmart(this.primaryFemaleDialogueHead)
        }

        if (this.secondaryMaleDialogueHead != -1) {
            stream.writeByte(92)
            stream.writeBigSmart(this.secondaryMaleDialogueHead)
        }

        if (this.secondaryFemaleDialogueHead != -1) {
            stream.writeByte(93)
            stream.writeBigSmart(this.secondaryFemaleDialogueHead)
        }

        if (this.Zan2d != 0) {
            stream.writeByte(95)
            stream.writeShort(this.Zan2d)
        }

        if (this.switchNoteItemId != -1) {
            stream.writeByte(97)
            stream.writeShort(this.switchNoteItemId)
        }

        if (this.notedItemId != -1) {
            stream.writeByte(98)
            stream.writeShort(this.notedItemId)
        }

        if (this.stackIds != null && this.stackAmounts != null) {
            data = 0
            while (data < stackIds!!.size) {
                if (stackIds!![data] != 0 || stackAmounts!![data] != 0) {
                    stream.writeByte(100 + data)
                    stream.writeShort(stackIds!![data])
                    stream.writeShort(stackAmounts!![data])
                }
                ++data
            }
        }

        if (this.floorScaleX != 128) {
            stream.writeByte(110)
            stream.writeShort(this.floorScaleX)
        }

        if (this.floorScaleY != 128) {
            stream.writeByte(111)
            stream.writeShort(this.floorScaleY)
        }

        if (this.floorScaleZ != 128) {
            stream.writeByte(112)
            stream.writeShort(this.floorScaleZ)
        }

        if (this.ambience != 0) {
            stream.writeByte(113)
            stream.writeByte(this.ambience)
        }

        if (this.diffusion != 0) {
            stream.writeByte(114)
            stream.writeByte(this.diffusion)
        }

        if (this.teamId != 0) {
            stream.writeByte(115)
            stream.writeByte(this.teamId)
        }

        if (this.switchLendItemId != -1) {
            stream.writeByte(121)
            stream.writeShort(this.switchLendItemId)
        }

        if (this.lendedItemId != -1) {
            stream.writeByte(122)
            stream.writeShort(this.lendedItemId)
        }

        if (this.maleWieldX != 0 || this.maleWieldY != 0 || this.maleWieldZ != 0) {
            stream.writeByte(125)
            stream.writeByte(this.maleWieldX)
            stream.writeByte(this.maleWieldY)
            stream.writeByte(this.maleWieldZ)
        }

        if (this.femaleWieldX != 0 || this.femaleWieldY != 0 || this.femaleWieldZ != 0) {
            stream.writeByte(126)
            stream.writeByte(this.femaleWieldX)
            stream.writeByte(this.femaleWieldY)
            stream.writeByte(this.femaleWieldZ)
        }

        if (this.unknownArray2 != null) {
            stream.writeByte(132)
            stream.writeByte(unknownArray2!!.size)

            data = 0
            while (data < unknownArray2!!.size) {
                stream.writeShort(unknownArray2!![data])
                ++data
            }
        }

        if (this.clientScriptData != null) {
            stream.writeByte(249)
            stream.writeByte(clientScriptData!!.size)
            val var5: Iterator<*> = clientScriptData!!.keys.iterator()

            while (var5.hasNext()) {
                data = (var5.next() as Int)
                val value2 = clientScriptData!![data]
                stream.writeByte(if (value2 is String) 1 else 0)
                stream.writeMedium(data)
                if (value2 is String) {
                    stream.writeString(value2)
                } else {
                    stream.writeInt((value2 as Int))
                }
            }
        }

        stream.writeByte(0)
        val var6 = ByteArray(stream.offset)
        stream.offset = 0
        stream.getBytes(var6, 0, var6.size)
        return var6
    }

    private fun readValues(
        stream: InputStream,
        opcode: Int,
    ) {
        if (opcode == 1) {
            this.invModelId = stream.readUnsignedShort() // stream.readBigSmart();
        } else if (opcode == 2) {
            this.name = stream.readString()
        } else if (opcode == 4) {
            this.invModelZoom = stream.readUnsignedShort()
        } else if (opcode == 5) {
            this.xan2d = stream.readUnsignedShort()
        } else if (opcode == 6) {
            this.yan2d = stream.readUnsignedShort()
        } else if (opcode == 7) {
            this.xOffset2d = stream.readUnsignedShort()
            if (this.xOffset2d > Short.MAX_VALUE) {
                // if(this.modelOffset1 > 32767) {
                this.xOffset2d -= 65536
            }
        } else if (opcode == 8) {
            this.yOffset2d = stream.readUnsignedShort()
            if (this.yOffset2d > Short.MAX_VALUE) {
                // if(this.modelOffset2 > 32767) {
                this.yOffset2d -= 65536
            }
        } else if (opcode == 11) {
            this.stackable = 1
        } else if (opcode == 12) {
            this.cost = stream.readInt()
        } else if (opcode == 13) {
            this.equipSlot = stream.readUnsignedByte()
        } else if (opcode == 14) {
            this.equipType = stream.readUnsignedByte()
        } else if (opcode == 16) {
            this.isMembersOnly = true
        } else if (opcode == 18) {
            stream.readUnsignedShortLE()
        } else if (opcode == 23) {
            this.maleEquipModelId1 = stream.readUnsignedShort() // stream.readBigSmart();
        } else if (opcode == 24) {
            this.maleEquipModelId2 = stream.readUnsignedShort() // stream.readBigSmart();
        } else if (opcode == 25) {
            this.femaleEquipModelId1 = stream.readUnsignedShort() // stream.readBigSmart();
        } else if (opcode == 26) {
            this.femaleEquipModelId2 = stream.readUnsignedShort() // stream.readBigSmart();
        } else if (opcode == 27) {
            stream.readUnsignedByte()
        } else if (opcode >= 30 && opcode < 35) {
            groundOptions[opcode - 30] = stream.readString()
        } else if (opcode >= 35 && opcode < 40) {
            inventoryOptions!![opcode - 35] = stream.readString()
        } else {
            val length: Int
            var index: Int
            if (opcode == 40) {
                length = stream.readUnsignedByte()
                this.originalModelColors = IntArray(length)
                this.modifiedModelColors = IntArray(length)

                index = 0
                while (index < length) {
                    originalModelColors!![index] = stream.readUnsignedShort()
                    modifiedModelColors!![index] = stream.readUnsignedShort()
                    ++index
                }
            } else if (opcode == 41) {
                length = stream.readUnsignedByte()
                this.originalTextureColors = ShortArray(length)
                this.modifiedTextureColors = ShortArray(length)

                index = 0
                while (index < length) {
                    originalTextureColors!![index] = stream.readUnsignedShort().toShort()
                    modifiedTextureColors!![index] = stream.readUnsignedShort().toShort()
                    ++index
                }
            } else if (opcode == 42) {
                length = stream.readUnsignedByte()
                this.recolorPalette = ByteArray(length)

                index = 0
                while (index < length) {
                    recolorPalette!![index] = stream.readByte().toByte()
                    ++index
                }
            } else if (opcode == 65) {
                this.isUnnoted = true
            } else if (opcode == 78) {
                this.maleEquipModelId3 = stream.readUnsignedShort() // stream.readBigSmart();
            } else if (opcode == 79) {
                this.femaleEquipModelId3 = stream.readUnsignedShort() // stream.readBigSmart();
            } else if (opcode == 90) {
                this.primaryMaleDialogueHead = stream.readUnsignedShort() // stream.readBigSmart();
            } else if (opcode == 91) {
                this.primaryFemaleDialogueHead = stream.readUnsignedShort() // stream.readBigSmart();
            } else if (opcode == 92) {
                this.secondaryMaleDialogueHead = stream.readUnsignedShort() // stream.readBigSmart();
            } else if (opcode == 93) {
                this.secondaryFemaleDialogueHead = stream.readUnsignedShort() // stream.readBigSmart();
            } else if (opcode == 95) {
                this.Zan2d = stream.readUnsignedShort()
            } else if (opcode == 96) {
                this.dummyItem = stream.readUnsignedByte()
            } else if (opcode == 97) {
                this.switchNoteItemId = stream.readUnsignedShort()
            } else if (opcode == 98) {
                this.notedItemId = stream.readUnsignedShort()
            } else if (opcode >= 100 && opcode < 110) {
                if (this.stackIds == null) {
                    this.stackIds = IntArray(10)
                    this.stackAmounts = IntArray(10)
                }

                stackIds!![opcode - 100] = stream.readUnsignedShort()
                stackAmounts!![opcode - 100] = stream.readUnsignedShort()
            } else if (opcode == 110) {
                this.floorScaleX = stream.readUnsignedShort()
            } else if (opcode == 111) {
                this.floorScaleY = stream.readUnsignedShort()
            } else if (opcode == 112) {
                this.floorScaleZ = stream.readUnsignedShort()
            } else if (opcode == 113) {
                this.ambience = stream.readByte()
            } else if (opcode == 114) {
                this.diffusion = stream.readByte()
            } else if (opcode == 115) {
                this.teamId = stream.readUnsignedByte()
            } else if (opcode == 121) {
                this.switchLendItemId = stream.readUnsignedShort()
            } else if (opcode == 122) {
                this.lendedItemId = stream.readUnsignedShort()
            } else if (opcode == 125) {
                this.maleWieldX = stream.readByte()
                this.maleWieldY = stream.readByte()
                this.maleWieldZ = stream.readByte()
            } else if (opcode == 126) {
                this.femaleWieldX = stream.readByte()
                this.femaleWieldY = stream.readByte()
                this.femaleWieldZ = stream.readByte()
            } else if (opcode == 127) {
                this.unknownInt18 = stream.readUnsignedByte()
                this.unknownInt19 = stream.readUnsignedShort()
            } else if (opcode == 128) {
                this.unknownInt20 = stream.readUnsignedByte()
                this.unknownInt21 = stream.readUnsignedShort()
            } else if (opcode == 129) {
                this.unknownInt20 = stream.readUnsignedByte()
                this.unknownInt21 = stream.readUnsignedShort()
            } else if (opcode == 130) {
                this.unknownInt22 = stream.readUnsignedByte()
                this.unknownInt23 = stream.readUnsignedShort()
            } else if (opcode == 132) {
                length = stream.readUnsignedByte()
                this.unknownArray2 = IntArray(length)

                index = 0
                while (index < length) {
                    unknownArray2!![index] = stream.readUnsignedShort()
                    ++index
                }
            } else if (opcode == 134) {
                stream.readUnsignedByte()
            } else if (opcode == 139) {
                this.unknownValue2 = stream.readUnsignedShort()
            } else if (opcode == 140) {
                this.unknownValue1 = stream.readUnsignedShort()
            } else if (opcode == 191) {
                // int opcode191 = 0;
            } else if (opcode == 218) {
                // int opcode218 = 0;
            } else if (opcode == 219) {
            } else if (opcode == 249) {
                length = stream.readUnsignedByte()

                if (this.clientScriptData == null) {
                    this.clientScriptData = HashMap<Int, Any>(length)
                }

                index = 0
                while (index < length) {
                    val stringInstance = stream.readUnsignedByte() == 1
                    val key = stream.read24BitInt()

                    val value: Any =
                        if (stringInstance) {
                            stream.readString()
                        } else {
                            stream.readInt()
                        }

                    clientScriptData?.let {
                        it[key] = value
                    }
                    ++index
                }
            } else {
                throw RuntimeException("MISSING OPCODE " + opcode + " FOR ITEM " + this.id)
            }
        }
    }

    private fun readOpcodeValues(stream: InputStream) {
        while (true) {
            val opcode = stream.readUnsignedByte()
            if (opcode == 0) {
                return
            }

            this.readValues(stream, opcode)
        }
    }

    /**
     * Reset texture colors.
     */
    fun resetTextureColors() {
        this.originalTextureColors = null
        this.modifiedTextureColors = null
    }

    val isWearItem: Boolean
        /**
         * Is wear item boolean.
         *
         * @return the boolean
         */
        get() = this.equipSlot != -1

    /**
     * Change texture color.
     *
     * @param originalModelColor the original model color
     * @param modifiedModelColor the modified model color
     */
    fun changeTextureColor(
        originalModelColor: Short,
        modifiedModelColor: Short,
    ) {
        if (this.originalTextureColors != null) {
            for (newOriginalModelColors in originalTextureColors!!.indices) {
                if (originalTextureColors!![newOriginalModelColors] == originalModelColor) {
                    modifiedTextureColors!![newOriginalModelColors] = modifiedModelColor
                    return
                }
            }

            val var5 = originalTextureColors!!.copyOf(originalTextureColors!!.size + 1)
            val newModifiedModelColors =
                modifiedTextureColors!!.copyOf(
                    modifiedTextureColors!!.size + 1,
                )
            var5[var5.size - 1] = originalModelColor
            newModifiedModelColors[newModifiedModelColors.size - 1] = modifiedModelColor
            this.originalTextureColors = var5
            this.modifiedTextureColors = newModifiedModelColors
        } else {
            this.originalTextureColors = shortArrayOf(originalModelColor)
            this.modifiedTextureColors = shortArrayOf(modifiedModelColor)
        }
    }

    /**
     * Reset model colors.
     */
    fun resetModelColors() {
        this.originalModelColors = null
        this.modifiedModelColors = null
    }

    /**
     * Change model color.
     *
     * @param originalModelColor the original model color
     * @param modifiedModelColor the modified model color
     */
    fun changeModelColor(
        originalModelColor: Int,
        modifiedModelColor: Int,
    ) {
        if (this.originalModelColors != null) {
            for (newOriginalModelColors in originalModelColors!!.indices) {
                if (originalModelColors!![newOriginalModelColors] == originalModelColor) {
                    modifiedModelColors!![newOriginalModelColors] = modifiedModelColor
                    return
                }
            }

            val var5 = originalModelColors!!.copyOf(originalModelColors!!.size + 1)
            val newModifiedModelColors = modifiedModelColors!!.copyOf(modifiedModelColors!!.size + 1)
            var5[var5.size - 1] = originalModelColor
            newModifiedModelColors[newModifiedModelColors.size - 1] = modifiedModelColor
            this.originalModelColors = var5
            this.modifiedModelColors = newModifiedModelColors
        } else {
            this.originalModelColors = intArrayOf(originalModelColor)
            this.modifiedModelColors = intArrayOf(modifiedModelColor)
        }
    }

    public override fun clone(): Any = super.clone()

    override fun toString(): String = id.toString() + " - " + this.name

    companion object {
        /**
         * Gets item definition.
         *
         * @param cache  the cache
         * @param itemId the item id
         * @return the item definition
         */
        @JvmStatic
        fun getItemDefinition(
            cache: Cache,
            itemId: Int,
        ): ItemDefinition = getItemDefinition(cache, itemId, true)

        /**
         * Gets item definition.
         *
         * @param cache  the cache
         * @param itemId the item id
         * @param load   the load
         * @return the item definition
         */
        fun getItemDefinition(
            cache: Cache,
            itemId: Int,
            load: Boolean,
        ): ItemDefinition = ItemDefinition(cache, itemId, load)
    }
}
