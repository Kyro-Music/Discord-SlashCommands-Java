package de.Jan.SlashCommands

import org.json.JSONException
import org.json.JSONObject

class SlashCommandBuilder (val botID: String, val token: String) {

    val listeners = ArrayList<SlashCommandListener>()

    fun getGuildCommandsFor(id: String) : SlashCommandGuild {
        return SlashCommandGuild(this, id, botID, token)
    }

    fun getGlobalCommands() : GlobalSlashCommands {
        return GlobalSlashCommands(this, botID, token)
    }

    fun addListener(listener: SlashCommandListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: SlashCommandListener) {
        listeners.remove(listener)
    }

    fun build() {
        SlashCommandHandler(this).connectBlocking()
    }

    fun checkIfError(string: String) {
        try {
            val ob = JSONObject(string)
            if(ob.has("code")) {
                throw SlashCommandError(string)
            }
        } catch(e: JSONException) {

        }

    }

}