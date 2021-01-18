class SlashCommandOption(val name: String, val description: String, val required: Boolean, val type: Int, vararg option_choices: SlashCommandOptionChoice) {

    val choices = option_choices

}