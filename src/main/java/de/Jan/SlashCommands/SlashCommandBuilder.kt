package de.Jan.SlashCommands

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.RawGatewayEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SlashCommandBuilder(val jda: JDA, val botID: String, val token: String) {

    val listeners = ArrayList<SlashCommandListener>()

    fun getGuildCommandsFor(id: String) : SlashCommandGuild {
        return SlashCommandGuild(this, id, botID, token)
    }

    fun getGuildCommandsFor(guild: Guild) : SlashCommandGuild {
        return SlashCommandGuild(this, guild.id, botID, token)
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
                    var subcommand: SlashSubCommand? = null
                    val interactionToken = data.getString("token")
                    val id = data.getString("id")
                    try {
                        val options = data.getJSONObject("data").getJSONArray("options")
                        for (option in options) {
                            val op = option as JSONObject
                            if(op.has("value")) { //check if the option is not a subcommand or subcommand group
                                args.add(SlashCommandArgument(op.getString("name"), op.get("value")))
                            } else { //the option is a subcommand or subcommand group
                                for (sub in op.getJSONArray("options")) {
                                    val s = sub as JSONObject
                                    if(s.has("value")) { //check if option is a subcommand
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
                    } catch(ex: JSONException) {

                    }
                    if(member != null && channel != null && command != null) {
                        listener.run(SlashCommandEvent(member!!, channel, command, args, subcommand, Interaction(interactionToken, builder.token, builder.botID, id)))
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
            if(command_option.type != 1 && command_option.type != 2) {
                option.put("required", command_option.required)
            }

            if(!command_option.hasSubOptions && command_option.choices.size != 0) { //check if it's just a command
                val choices = JSONArray()

                for (choice in command_option.choices) {
                    val new_choice = JSONObject()
                    new_choice.put("name", choice.name)
                    new_choice.put("value", choice.value)
                    choices.put(new_choice)
                }
                option.put("choices", choices)
            } else if(command_option.type == 1) { //check if its a subcommand
                val suboptions = JSONArray() //the options from the command
                for (s in command_option.suboptions) { //iterate through the options of the subcommand
                    val n = JSONObject()
                    n.put("name", s.name)
                    n.put("description", s.description)
                    n.put("type", s.type)

                    if(!s.hasSubOptions && s.choices.size != 0) { //check if the option has choices
                        val subChoices = JSONArray()

                        for (choice in command_option.choices) {
                            val newChoice = JSONObject()
                            newChoice.put("name", choice.name)
                            newChoice.put("value", choice.value)
                            subChoices.put(newChoice)
                        }
                        n.put("choices", subChoices)
                    }
                    suboptions.put(n)
                }
                option.put("options", suboptions)
            } else if(command_option.type == 2) { //check if its a subcommand group
                val subcmds = JSONArray()
                for (subcommand in command_option.suboptions) { //iterate through the subcommands of the subcommand group
                    //create a new command object
                    val cmd = JSONObject()
                    cmd.put("name", subcommand.name)
                    cmd.put("description", subcommand.description)
                    cmd.put("type", subcommand.type)
                    val subargs = JSONArray() //the options of the subcommand
                    for (arg in subcommand.suboptions) { //iterate through the options of the subcommand
                        //add the option
                        val argument = JSONObject()
                        argument.put("name", arg.name)
                        argument.put("description", arg.required)
                        argument.put("type", arg.type)
                        argument.put("required", arg.required)
                        val choices = JSONArray()
                        for (choice in arg.choices) { //check if the option has choices
                            val new_choice = JSONObject()
                            new_choice.put("name", choice.name)
                            new_choice.put("value", choice.value)
                            choices.put(new_choice)
                        }
                        argument.put("choices", choices)
                        subargs.put(argument)
                    }
                    cmd.put("options", subargs)
                    subcmds.put(cmd)
                }
                option.put("options", subcmds)
            }
            commandOptions.put(option)
        }
        commandObject.put("options", commandOptions)
        return commandObject.toString().toRequestBody(JSON)
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