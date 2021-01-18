import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.RawGatewayEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.json.JSONException
import org.json.JSONObject

class SlashCommandBuilder(private val jda: JDA, private val botID: Long, private val token: String) {

    val listeners = ArrayList<SlashCommandListener>()

    fun getGuildCommandsFor(id: Long) : SlashCommandGuild {
        return SlashCommandGuild(id, botID, token)
    }

    fun getGuildCommandsFor(guild: Guild) : SlashCommandGuild {
        return SlashCommandGuild(guild.idLong, botID, token)
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
                println("test")
                val data = JSONObject(event.`package`.toString()).getJSONObject("d")
                for (listener in builder.listeners) {
                    println("a")
                    val guild = builder.jda.getGuildById(data.getLong("guild_id")) ?: continue
                    var member: Member? = null
                    guild.retrieveMemberById(data.getJSONObject("member").getJSONObject("user").getLong("id")).queue() {m ->
                        member = m
                    }
                    val channel = guild.getTextChannelById(data.getLong("channel_id"))
                    val command = builder.getGuildCommandsFor(guild.idLong).getGuildCommand(data.getJSONObject("data").getString("id").toLong())
                    val args = ArrayList<SlashCommandArgument>()
                    try {
                        val options = data.getJSONObject("data").getJSONArray("options")
                        for (option in options) {
                            val op = option as JSONObject
                            args.add(SlashCommandArgument(op.getString("name"), op.getString("value")))
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

}

