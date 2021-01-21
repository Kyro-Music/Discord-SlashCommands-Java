package de.Jan.SlashCommands


/**
 * @param sender The member who sent the slash command
 * @param channel The channel where the slash command was executed
 * @param command The command which was executed
 * @param args The arguments which were used
 * @param sub The sub command, if the sender used one
 */
class SlashCommandEvent(val guildID: String, val memberID: String, val channelID: String, val command: SlashCommand, val args: ArrayList<SlashCommandArgument>, val subCommand: SlashSubCommand?, val interaction: Interaction) {
}