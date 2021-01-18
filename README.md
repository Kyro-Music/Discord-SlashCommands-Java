# Java-SlashCommands

# ! You need to enable Raw Events on you JDABuilder or the listeners won't work !

# Documentation

```java
JDA jda = JDABuilder.createDefault("//Token")
                .setRawEventsEnabled(true) //Without this, slash commands won't work!
                .build();
SlashCommandBuilder builder = new SlashCommandBuilder(jda, 123456789, "token");
SlashCommandGuild guild = builder.getGuildCommandsFor(123456789);//Get guild commands with ID
guild.registerGuildCommand(new SlashCommand("test", "This is a test command!")); //Register a slash command
```       




![The Command](https://cdn.discordapp.com/attachments/775406836877885504/800702840720982037/unknown.png
)

Now we can create a listener for the slash commands:

```java
public static class Listener extends SlashCommandListener {
        @Override
        public void run(@NotNull Member sender, @NotNull TextChannel channel, @NotNull SlashCommand command, @NotNull ArrayList<SlashCommandArgument> args) {
            if(command.getName().equals("test")) { //check if the slash command is our "test"
                channel.sendMessage("You entered the slash command: test").queue(); //Then just send a message
        }
    }
}
```



Now we need to add the listener and build the builder:

```java
builder.addListener(new Listener()); //Add the listener
builder.build(); //Build the builder so the listeners are ready
```
