package org.example.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class QuitChannelCommand extends ListenerAdapter {

    private final VoiceChannelCommand voiceChannelCommand;

    public QuitChannelCommand(VoiceChannelCommand voiceChannelCommand) {
        this.voiceChannelCommand = voiceChannelCommand;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        if (!event.getMessage().getContentRaw().startsWith("!quit")) return;
        if (event.getAuthor().isBot()) return;
        voiceChannelCommand.recordCommand.stopRecording();
        event
                .getGuild()
                .getAudioManager()
                .closeAudioConnection();

        event
                .getChannel()
                .sendMessage("Saindo do canal de voz")
                .queue();


    }
}
