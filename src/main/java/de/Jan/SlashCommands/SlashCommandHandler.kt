package de.Jan.SlashCommands

import okhttp3.internal.wait
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList

class SlashCommandHandler(val builder: SlashCommandBuilder) : WebSocketClient(URI("wss://gateway.discord.gg/?v=8&encoding=json")) {

    private var reconnect = false
    private var sessionID = ""

    override fun onOpen(e: ServerHandshake?) {
        if(!reconnect && sessionID == "") {
            val identify = JSONObject("{\n" +
                    "  \"op\": 2,\n" +
                    "  \"d\": {\n" +
                    "    \"token\": \"ODAwODIyMjg0ODg1NTU3MjQ5.YAXt3w.0g7YCvbYWN7eIO_NGTFPlNgz3-o\",\n" +
                    "    \"intents\": 513,\n" +
                    "    \"properties\": {\n" +
                    "      \"\$os\": \"linux\",\n" +
                    "      \"\$browser\": \"Discord-SlashCommands-Java\",\n" +
                    "      \"\$device\": \"Discord-SlashCommands-Java\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}")
            send(identify.toString())
        } else {
            val resume = JSONObject("{\n" +
                    "  \"op\": 6,\n" +
                    "  \"d\": {\n" +
                    "    \"token\": \"${builder.token}\",\n" +
                    "    \"session_id\": \"${sessionID}\",\n" +
                    "    \"seq\": 1337\n" +
                    "  }\n" +
                    "}")
            send(resume.toString())
        }
        Thread { while(true) {
            if(isClosed) {
                reconnect = true
                reconnect()
            }
        } }.start()
    }

    override fun onMessage(e: String?) {
        val json = JSONObject(e)
        if(json.has("op") && json.getInt("op") == 10) {
            val timer = Timer()
            timer.schedule(object: TimerTask() {
                override fun run() {
                    send("{\"op\": 1}")
                }
            }, json.getJSONObject("d").getLong("heartbeat_interval"))
        }
        if(json.has("t") && json.get("t").toString() == "READY") {
            this.sessionID = json.getJSONObject("d").getString("session_id")
        }
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
                    listener.run(SlashCommandEvent(guild, member, channel, command, args, subcommand, Interaction(builder, interactionToken, builder.token, builder.botID, id)))
                }
            }
        }
    }

    override fun onClose(e: Int, e1: String?, e2: Boolean) {
        println(e1)
    }

    override fun onError(e: Exception?) {
        e?.printStackTrace()
    }
}