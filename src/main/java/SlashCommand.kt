open class SlashCommand(val name: String, val description: String, vararg command_options: SlashCommandOption) {

    val options = command_options
    var id: String = ""

}