package de.Jan.SlashCommands

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel


abstract class SlashCommandListener {

    /**
     * @param sender The member who sent the slash command
     * @param channel The channel where the slash command was executed
     * @param command The command which was executed
     * @param args The arguments which were used
     * @param sub The sub command, if the sender used one
     */
    abstract fun run(sender: Member, channel: TextChannel, command: SlashCommand, args: ArrayList<SlashCommandArgument>, sub: SlashSubCommand?)

}