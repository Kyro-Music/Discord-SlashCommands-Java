import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

class SlashCommandGuild(guild_id: Long, bot: Long, private val token: String) {

    private val okhttp = OkHttpClient()
    private val url = "https://discord.com/api/v8/applications/$bot/guilds/$guild_id/commands"
    private val JSON = "application/json; charset=utf-8".toMediaType()

    val commands: ArrayList<SlashCommand>
        get() {
            val commands = ArrayList<SlashCommand>()
            val builder = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bot $token")
                    .get()
                    .build()
            val result = okhttp.newCall(builder).execute()
            val array = JSONArray(result.body?.string())
            result.close()
            for (any in array) {
                val json = any as JSONObject
                val command = SlashCommand(json.getString("name"), json.getString("description"))
                command.id = json.getLong("id")
                commands.add(command)
            }
            return commands
        }

    fun registerGuildCommand(command: SlashCommand) {
        val request = Request.Builder()
                .addHeader("Authorization", "Bot $token")
                .url(url)
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
        request.post(RequestBody.Companion.create(JSON, commandObject.toString()))
        okhttp.newCall(request.build()).execute().close()
    }

    fun registerGuildCommands(vararg commands: SlashCommand) {
        for (command in commands) {
            registerGuildCommand(command)
        }
    }

    fun deleteGuildCommand(id: Long) {
        val request = Request.Builder()
                .url("$url/$id")
                .addHeader("Authorization", "Bot $token")
                .delete()
        okhttp.newCall(request.build()).execute().close()
    }

    fun deleteAllGuildCommands() {
        for (command in commands) {
            deleteGuildCommand(command.id)
        }
    }

    fun getGuildCommand(id: Long) : SlashCommand? {
        for (command in commands) {
            if(command.id == id) {
                return command
            }
        }
        return null
    }

}