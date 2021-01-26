# Java-SlashCommands

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
      <artifactId>Discord-SlashCommands-Java</artifactId>
      <version>1.8</version>
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
	 implementation 'com.github.jan-tennert:Discord-SlashCommands-Java:1.8'
}
```

# ToDo

- if you have an idea create an issue [here](https://github.com/jan-tennert/Java-SlashCommands/issues/new/choose)

# Demo (Guild Commands)

```java
SlashCommandBuilder builder = new SlashCommands.SlashCommandBuilder("123456789", "token");
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
        public void run(SlashCommandEvent e) {
            if(e.getCommand().getName().equals("test")) { //check if the slash command is our "test"
		 InteractionEmbed.Builder embed = new InteractionEmbed.Builder() //embed
                        .setAuthor("author")
                        .setTitle("title")
                        .addField("field", "value", false)
                        .setFooter("footer")
                        .....
                e.getInteraction().sendMessage(embed.build()); //Send an embed
		e.getInteraction().sendMessage("message"); // Or just send a message
        }
    }
}
```



#### Now we need to add the listener and build the builder:

```java
builder.addListener(new Listener()); //Add the listener
builder.build(); //Build the builder and start the websocket
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
e.getInteraction().sendMessage("You entered the number: " + e.getArgs().get(0).getValue());
```

![Command with arguments](https://cdn.discordapp.com/attachments/775406836877885504/800706541971046400/unknown.png)

## Interaction response
If the listener has received an interaction, you can response to him:
```java
public static class Listener extends SlashCommandListener {
        @Override
        public void run(SlashCommandEvent e) {
        e.callback(type, value);
    }
}
```
Type is the [response type](https://github.com/jan-tennert/Java-SlashCommands/blob/master/src/main/java/de/Jan/SlashCommands/InteractionType.java).

Value is the value. If you selected a type where you send a message, then you can enter a string

## Subcommands & Subcommand grups

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

## Global commands

Global commands are the same but you get the object with
```java
SlashCommandBuilder builder = new SlashCommandBuilder(botID, token)
builder.getGlobalCommands()
```
And they work the same as guild commands but without the guild in the functions:

```java
builder.getGlobalCommands().registerCommand()
```

*Note: Global commands need up to 60min for updating*
