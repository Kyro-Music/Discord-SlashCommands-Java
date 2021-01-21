package de.Jan.SlashCommands;

public class InteractionType {

    /**
     * Tell the api you received the interaction
     */
    public final static int pong = 1;

    /**
     * Tell the api you received the interaction and don't send the user input in the channel
     */
    public final static int acknowledge = 2;

    /**
     * Respond with a message and don't send the user input in the channel
     */
    public final static int channelMessage = 3;

    /**
     * Respond with a message and send the user input in the channel
     */
    public final static int channelMessageWithSource = 4;

    /**
     * Tell the api you received the interaction and send the user input in the channel
     */
    public final static int acknowledgeWithSource = 5;
}
