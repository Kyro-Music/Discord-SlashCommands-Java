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

    class Builder {

        var name: String? = null
            private set
        var description: String? = null
            private set
        var required: Boolean = false
            private set
        var type: Int? = null
            private set
        val choices: ArrayList<SlashCommandOptionChoice> = ArrayList()

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

        fun setRequired(required: Boolean) : Builder {
            this.required = required
            return this
        }

        fun setType(type: Int) : Builder {
            this.type = type
            return this
        }

        fun addChoice(choice: SlashCommandOptionChoice) : Builder {
            choices.add(choice)
            return this
        }

        fun removeChoice(choice: SlashCommandOptionChoice) : Builder {
            choices.remove(choice)
            return this
        }

        fun build() : SlashCommandOption? {
            if(name != null && description != null && type != null) {
                return SlashCommandOption(name!!, description!!, required, type!!, *choices.toTypedArray())
            } else {
                return null
            }
        }

    }

}