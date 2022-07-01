package com.example.keybindhelper.dto

import androidx.room.*
import com.example.keybindhelper.Adapters.KeybindAdapter.KeybindViewHolder
import com.example.keybindhelper.dao.CurrentProjectManager.isKeybindNameAvailable
import com.example.keybindhelper.dao.DatabaseManager

@Entity(
    foreignKeys = [ForeignKey(
        entity = Group::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("groupID"),
        onDelete = ForeignKey.CASCADE
    )]
)
class Keybind {
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @JvmField
    @ColumnInfo
    var groupID: Long = 0

    @JvmField
    @ColumnInfo
    var name: String? = null

    @JvmField
    @ColumnInfo
    var kb1 = ""

    @JvmField
    @ColumnInfo
    var kb2 = ""

    @JvmField
    @ColumnInfo
    var kb3 = ""

    @JvmField
    @ColumnInfo
    var index = 0

    /**
     * Blank for Room
     */
    constructor() {}

    /**
     * Used Exclusively for cloning keybinds
     * @param groupID
     * @param name
     * @param kb1
     * @param kb2
     * @param kb3
     */
    constructor(groupID: Long, name: String?, kb1: String, kb2: String, kb3: String) {
        this.groupID = groupID
        this.name = name
        this.kb1 = kb1
        this.kb2 = kb2
        this.kb3 = kb3
    }

    /**
     * Tells database manager to update the keybind row
     */
    fun updateDB() {
        DatabaseManager.db!!.update(this)
    }

    @Ignore
    var group: Group? = null

    @Ignore
    var viewHolder: KeybindViewHolder? = null

    /**
     * Clones the keybind
     * @param sameName Should name be autogenerated and unique
     * @return cloned keybind
     */
    fun clone(sameName: Boolean): Keybind {
        var newName = name
        if (!sameName) {
            var i = 1
            while (!isKeybindNameAvailable("$name ($i)")) i++
            newName = "$name ($i)"
        }
        return Keybind(groupID, newName, kb1, kb2, kb3)
    }
}