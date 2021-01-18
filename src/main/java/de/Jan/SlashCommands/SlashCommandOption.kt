package de.Jan.SlashCommands

/**
 * @param name The name of the argument
 * @param description A short description
 * @param required Whether the argument is required or not
 * @param type The type of the argument (User, Channel, Integer, String...)
 * @param option_choices Specify choices so the user can only select one of them
 */
class SlashCommandOption(val name: String, val description: String, val required: Boolean, val type: Int, vararg option_choices: SlashCommandOptionChoice) {

    val choices = option_choices

}