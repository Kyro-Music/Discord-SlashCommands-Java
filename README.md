# Java-SlashCommands [![1.5](https://jitpack.io/v/jan-tennert/Java-SlashCommands.svg)](https://jitpack.io/#jan-tennert/Java-SlashCommands/1.5)

# ! You need to enable Raw Events on your JDABuilder or the listeners won't work !

# Installation

#### Maven

```xml
<repositories>
     <repository>
	   <id>jitpack.io</id>
	   <url>https://jitpack.io</url>
     </repository>
</repositories
```
```xml
<dependency>
      <groupId>com.github.jan-tennert</groupId>
      <artifactId>Java-SlashCommands</artifactId>
      <version>1.5</version>
</dependency>
```

#### Gradle
```gradle
allprojects {
	repositories {
	  maven { url 'https://jitpack.io' }
    }
}
```
```gradle
dependencies {
	 implementation 'com.github.jan-tennert:Java-SlashCommands:1.5'
}
```
# Requirements

- latest [Discord.JDA](https://github.com/DV8FromTheWorld/JDA) version

# ToDo

- if you have an idea create an issue [here](https://github.com/jan-tennert/Java-SlashCommands/issues/new/choose)

# Demo (Guild Commands)

```java
JDA jda = JDABuilder.createDefault("//Token")
                .setRawEventsEnabled(true) //Without this, slash commands won't work!
                .build();
SlashCommandBuilder builder = new de.Jan.SlashCommands.SlashCommandBuilder(jda, "123456789", "token");
SlashCommandGuild guild = builder.getGuildCommandsFor("123456789");//Get guild commands with ID

//You can use the builder:
SlashCommand command = new SlashCommand.Builder()
       .setName("test")
       .setDescription("This is a command")
       .appendDescription("\nThis is another line")
       .build();

//Or just the constructor (but you should use the builder):
SlashCommand command = new SlashCommand("test", "This is a command")

guild.registerGuildCommand(command)
//To delete a command you have to iterate through guild.commands and find your command. Then just run guild.deleteGuildCommand(command.id). 
```       




![The Command](https://cdn.discordapp.com/attachments/775406836877885504/800702840720982037/unknown.png)

#### Now we can create a listener for the slash commands:

```java
public static class Listener extends SlashCommandListener {
        @Override
        public void run(@NotNull Member sender, @NotNull TextChannel channel, @NotNull SlashCommand command, @NotNull ArrayList<SlashCommandArgument> args, @Nullable SlashSubCommand sub) {
            if(command.getName().equals("test")) { //check if the slash command is our "test"
                channel.sendMessage("You entered the slash command: test").queue(); //Then just send a message
        }
    }
}
```



#### Now we need to add the listener and build the builder:

```java
builder.addListener(new Listener()); //Add the listener
builder.build(); //Build the builder so the listeners are ready
```


#### You can also add arguments to your command:

```java
SlashCommandOption option = new SlashCommandOption.Builder()
       .setName("argument")
       .setDescription("Just an argument")
       .setType(SlashCommandOptionType.STRING)
       .setRequired(true)
       .build();
SlashCommand command = new SlashCommand.Builder()
       .setName("test")
       .setDescription("This is a command")
       .addOption(option)
       .appendDescription("\nThis is another line")
       .build();

 guild.registerGuildCommand(command); //Register a slash command.

//Then in your listener:
channel.sendMessage("You entered the number: " + args.get(0).getValue()).queue();
```

![Command with arguments](https://cdn.discordapp.com/attachments/775406836877885504/800706541971046400/unknown.png)

### Subcommands & Subcommand grups

```java
SlashCommandOption option = new SlashCommandOption.Builder()
                .setName("eat")
                .setType(SlashCommandOptionType.SUB_COMMAND)
                .setDescription("Eat something")
                .addSubOption(new SlashCommandOption.Builder()
                    .setType(SlashCommandOptionType.STRING)
                    .setRequired(true)
                    .setName("food")
                    .setDescription("The food you wanna eat")
                    .build())
                .build();
SlashCommand command = new SlashCommand("test", "This is just a test command", option);
```
If you want to use a subcommand group, select the type SUB_COMMAND_GROUP and add subcommands with .addSubOption() and under these comments add options for example: User, Channel etc.
In the listeners there's this new argument: SlashSubCommand (which is null when there is no subcommand)
.You can get the subcommand name with SlashSubCommand.getName() and the group (if there is one) SlashSubCommand.getGroup()

#### Global commands

Global commands are the same but you get the object with
```java
SlashCommandBuilder builder = new SlashCommandBuilder(jda, botID, token)
builder.getGlobalCommands()
```
And they work the same as guild commands but without the guild in the functions:

```java
builder.getGlobalCommands().registerCommand()
```

*Note: Global commands need up to 60min for updating*
