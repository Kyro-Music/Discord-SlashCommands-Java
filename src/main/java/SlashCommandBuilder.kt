import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.RawGatewayEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SlashCommandBuilder(val jda: JDA, private val botID: String, private val token: String) {

    val listeners = ArrayList<SlashCommandListener>()

    fun getGuildCommandsFor(id: String) : SlashCommandGuild {
        return SlashCommandGuild(this, id, botID, token)
    }

    fun getGuildCommandsFor(guild: Guild) : SlashCommandGuild {
        return SlashCommandGuild(this, guild.id, botID, token)
    }

    fun addListener(listener: SlashCommandListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: SlashCommandListener) {
        listeners.remove(listener)
    }

    fun build() {
        jda.addEventListener(Listener(this))
    }

    private class Listener(private val builder: SlashCommandBuilder) : ListenerAdapter() {
        override fun onRawGateway(event: RawGatewayEvent) {
            if(event.type == "INTERACTION_CREATE") {
                val data = JSONObject(event.`package`.toString()).getJSONObject("d")
                for (listener in builder.listeners) {
                    val guild = builder.jda.getGuildById(data.getLong("guild_id")) ?: continue
                    var member: Member? = null
                    guild.retrieveMemberById(data.getJSONObject("member").getJSONObject("user").getLong("id")).queue() {m ->
                        member = m
                    }
                    val channel = guild.getTextChannelById(data.getLong("channel_id"))
                    val command = builder.getGuildCommandsFor(guild.id).getGuildCommand(data.getJSONObject("data").getString("id").toLong().toString())
                    val args = ArrayList<SlashCommandArgument>()
                    try {
                        val options = data.getJSONObject("data").getJSONArray("options")
                        for (option in options) {
                            val op = option as JSONObject
                            args.add(SlashCommandArgument(op.getString("name"), op.get("value")))
                        }
                    } catch(ex: JSONException) {

                    }
                    if(member != null && channel != null && command != null) {
                        listener.run(member!!, channel, command, args)
                    }
                }
            }
        }
    }

    fun slashCommandToForm(command: SlashCommand): RequestBody {
        val JSON = "application/json; charset=utf-8".toMediaType()
        val commandObject  = JSONObject()
        commandObject.put("name", command.name)
        commandObject.put("description", command.description)

        val commandOptions = JSONArray()

        for (command_option in command.options) {
            val option = JSONObject()
            option.put("name", command_option.name)
            option.put("description", command_option.description)
            option.put("type", command_option.type)
            option.put("required", command_option.required)

            val choices = JSONArray()

            for (choice in command_option.choices) {
                val new_choice = JSONObject()
                new_choice.put("name", choice.name)
                new_choice.put("value", choice.value)
                choices.put(new_choice)
            }
            option.put("choices", choices)
            commandOptions.put(option)
        }
        commandObject.put("options", commandOptions)
        return commandObject.toString().toRequestBody(JSON)
    }

    fun checkIfError(string: String) {
        val ob = JSONObject(string)
        try {
            ob.getInt("code")
            throw SlashCommandError(string)
        } catch(e: JSONException) {
        }
    }

}

