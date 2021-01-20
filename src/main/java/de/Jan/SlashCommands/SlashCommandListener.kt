package de.Jan.SlashCommands

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel


abstract class SlashCommandListener {

    abstract fun run(sender: Member, channel: TextChannel, command: SlashCommand, args: ArrayList<SlashCommandArgument>, sub: SlashSubCommand?)

}