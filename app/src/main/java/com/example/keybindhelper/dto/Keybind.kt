package com.example.keybindhelper.dto

import com.example.keybindhelper.dao.StringLiveDataConverter
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.keybindhelper.dao.DatabaseManager
import com.example.keybindhelper.RecyclerViewAdapters.KeybindAdapter.KeybindViewHolder
import com.example.keybindhelper.dto.Keybind
import com.example.keybindhelper.dao.CurrentProjectManager
import kotlin.Throws
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONArray

@Entity(foreignKeys = [ForeignKey(entity = Group::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("groupID"),
    onDelete = ForeignKey.CASCADE)])
class Keybind {
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @JvmField
    @ColumnInfo
    var groupID: Long = 0

    @JvmField
    @TypeConverters(StringLiveDataConverter::class)
    var name = MutableLiveData("")

    @JvmField
    @TypeConverters(StringLiveDataConverter::class)
    var kb1 = MutableLiveData("")

    @JvmField
    @TypeConverters(StringLiveDataConverter::class)
    var kb2 = MutableLiveData("")

    @JvmField
    @TypeConverters(StringLiveDataConverter::class)
    var kb3 = MutableLiveData("")

    /* @ColumnInfo
    public String name,kb1="",kb2="",kb3="";*/
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
    constructor(groupID: Long, name: String?, kb1: String?, kb2: String?, kb3: String?) {
        this.groupID = groupID
        this.name.value = name
        this.kb1.value = kb1
        this.kb2.value = kb2
        this.kb3.value = kb3
    }

    /**
     * Tells database manager to update the keybind row
     */
    fun updateDB() {
        DatabaseManager.db?.update(this)
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
    fun Clone(sameName: Boolean): Keybind {
        var newName = name.value
        if (!sameName) {
            var i = 1
            while (!CurrentProjectManager.CurrentProject!!.isKeybindNameAvailable(name.value.toString() + " (" + i + ")")) i++
            newName = name.value.toString() + " (" + i + ")"
        }
        return Keybind(groupID, newName, kb1.value, kb2.value, kb3.value)
    }

    @get:Throws(JSONException::class)
    val JSONObject: JSONObject
        get() {
            val ret = JSONObject()
            ret.put("keybindName", name.value)
            val kbs = JSONArray()
            if (kb1.value!!.isNotEmpty()) kbs.put(kb1.value)
            if (kb2.value!!.isNotEmpty()) kbs.put(kb2.value)
            if (kb3.value!!.isNotEmpty()) kbs.put(kb3.value)
            ret.put("keybinds", kbs)
            return ret
        }

    companion object {
        @Throws(JSONException::class)
        fun fromJSONObject(jKb: JSONObject): Keybind {
            val ret = Keybind()
            ret.name.value = jKb.getString("keybindName")
            val kbs = jKb.getJSONArray("keybinds")
            if (kbs.length() > 0) {
                ret.kb1.value = kbs.getString(0)
                if (kbs.length() > 1) {
                    ret.kb2.value = kbs.getString(1)
                    if (kbs.length() > 2) {
                        ret.kb3.value = kbs.getString(2)
                    }
                }
            }
            return ret
        }
    }
}