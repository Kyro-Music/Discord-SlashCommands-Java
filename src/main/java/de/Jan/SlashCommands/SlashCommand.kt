package de.Jan.SlashCommands

open class SlashCommand(val name: String, val description: String, vararg command_options: SlashCommandOption) {

    val options = command_options
    var id: String = ""

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