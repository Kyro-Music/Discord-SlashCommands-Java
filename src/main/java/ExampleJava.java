import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;

public class ExampleJava {
    public static void main(String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault("token")
                .setRawEventsEnabled(true) //Without this, slash commands won't work!
                .build();
        SlashCommandBuilder builder = new SlashCommandBuilder(jda, "botID", "token");
        SlashCommandGuild guild = builder.getGuildCommandsFor("guildID");//Get guild commands with ID
        guild.registerGuildCommand(new SlashCommand("lal", "This is a test command!",
                new SlashCommandOption("number", "Enter a number", true, SlashCommandOptionType.INTEGER))); //Register a slash command
        builder.addListener(new Listener()); //Add the listener
        builder.build(); //Build the builder so
    }

    public static class Listener extends SlashCommandListener {
        @Override
        public void run(@NotNull Member sender, @NotNull TextChannel channel, @NotNull SlashCommand command, @NotNull ArrayList<SlashCommandArgument> args) {
            if(command.getName().equals("test")) {
                channel.sendMessage("You entered the number: " + args.get(0).getValue()).queue();
            }
        }
    }
}
