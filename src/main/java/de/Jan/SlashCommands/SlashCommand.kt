package de.Jan.SlashCommands

open class SlashCommand {

    var options: Array<SlashCommandOption>
        private set
    var id: String = ""
    var name: String
        private set
    var description: String
        private set

    constructor(name: String, description: String, vararg command_options: SlashCommandOption) {
        this.name = name
        this.description = description
        this.options = command_options.toList().toTypedArray()
    }

    constructor(command: SlashCommand) {
        this.options = command.options.toList().toTypedArray()
        this.name = command.name
        this.description = command.description
        this.id = command.id
    }

    class Builder {

        var name: String? = null
            private set
        var description: String? = null
            private set
        val options: ArrayList<SlashCommandOption> = ArrayList()

        fun setName(name: String) : Builder {
            this.name = name
            return this
        }

        fun setDescription(description: String) : Builder {
            this.description = description
            return this
        }

        fun appendDescription(description: String) : Builder {
            this.description += description
            return this
        }

        fun addOption(option: SlashCommandOption) : Builder {
            options.add(option)
            return this
        }

        fun removeOption(option: SlashCommandOption) : Builder {
            options.remove(option)
            return this
        }

        fun build() : SlashCommand? {
            if(name != null && description != null) {
                return SlashCommand(name!!, description!!, *options.toTypedArray())
            } else {
                return null
            }
        }

    }

}