package de.Jan.SlashCommands

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.net.URI

class SlashCommandHandler(val builder: SlashCommandBuilder) : WebSocketClient(URI("wss://gateway.discord.gg/?v=6&encoding=json")) {
    override fun onOpen(e: ServerHandshake?) {
        val identify = JSONObject("{\n" +
                "  \"op\": 2,\n" +
                "  \"d\": {\n" +
                "    \"token\": \"${builder.token}\",\n" +
                "    \"intents\": 513,\n" +
                "    \"properties\": {\n" +
                "      \"\$os\": \"linux\",\n" +
                "      \"\$browser\": \"SlashCommands-Java\",\n" +
                "      \"\$device\": \"SlashCommand-Java\"\n" +
                "    }\n" +
                "  }\n" +
                "}")
        send(identify.toString())
    }

    override fun onMessage(e: String?) {
        if(JSONObject(e).has("t") && JSONObject(e).get("t") != null && JSONObject(e).get("t").toString() == "INTERACTION_CREATE") {
            val data = JSONObject(e).getJSONObject("d")
            for (listener in builder.listeners) {
                val guild = data.getString("guild_id")
                val channel = data.getString("channel_id")
                val command = builder.getGuildCommandsFor(guild).getGuildCommand(data.getJSONObject("data").getString("id").toLong().toString())
                val args = ArrayList<SlashCommandArgument>()
                var subcommand: SlashSubCommand? = null
                val interactionToken = data.getString("token")
                val member = data.getJSONObject("member").getJSONObject("user").getString("id")
                val id = data.getString("id")
                try {
                    val options = data.getJSONObject("data").getJSONArray("options")
                    for (option in options) {
                        val op = option as JSONObject
                        if (op.has("value")) { //check if the option is not a subcommand or subcommand group
                            args.add(SlashCommandArgument(op.getString("name"), op.get("value")))
                        } else { //the option is a subcommand or subcommand group
                            for (sub in op.getJSONArray("options")) {
                                val s = sub as JSONObject
                                if (s.has("value")) { //check if option is a subcommand
                                    subcommand = SlashSubCommand(op.getString("name"), null)
                                    args.add(SlashCommandArgument(s.getString("name"), s.get("value"))) //add the arguments of the command
                                } else { //the option must be a subcommand group
                                    for (any in s.getJSONArray("options")) { //iterate through the arguments of the command in the command group
                                        val g = any as JSONObject
                                        args.add(SlashCommandArgument(g.getString("name"), g.get("value")))
                                    }
                                    subcommand = SlashSubCommand(s.getString("name"), op.getString("name"))
                                }
                            }
                        }
                    }
                } catch (ex: JSONException) {

                }
                if (member != null && channel != null && command != null) {
                    listener.run(SlashCommandEvent(member, channel, command, args, subcommand, Interaction(builder, interactionToken, builder.token, builder.botID, id)))
                }
            }
        }
    }

    override fun onClose(e: Int, e1: String?, e2: Boolean) {

    }

    override fun onError(e: Exception?) {
        e?.printStackTrace()
    }
}