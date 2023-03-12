package org.example.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.sound.sampled.*;
import java.io.File;
import java.nio.ByteBuffer;

public class RecordCommand implements AudioSendHandler, AudioReceiveHandler {

    private TargetDataLine mic;
    private JDA jda;

    public RecordCommand(JDA jda) {
        this.jda = jda;
    }

    @Override
    public boolean canReceiveCombined() {
        return true;
    }

    @Override
    public boolean canReceiveUser() {
        return false;
    }

    @Override
    public boolean canProvide() {
        return true;
    }

    @Override
    public void handleUserAudio(UserAudio userAudio) {
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(new byte[0]);
    }

    public void startRecording(MessageReceivedEvent event) {
        try {
            AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
            String nomeAudio = "audio.wav";

            File file = new File(nomeAudio);

            // Configuração do microfone
            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            mic = (TargetDataLine) AudioSystem.getLine(info);
            mic.open(format);
            mic.start();

            // Configuração do bot
            jda.getGuilds().forEach(guild -> {
                guild.getAudioManager().setReceivingHandler(this);
                guild.getAudioManager().setSendingHandler(this);
                guild.getAudioManager().openAudioConnection(guild.getVoiceChannels().get(0));
            });

            // Gravação do áudio
            byte[] buffer = new byte[mic.getBufferSize() / 5];
            AudioInputStream audioInputStream = new AudioInputStream(mic);
            AudioSystem.write(audioInputStream, fileType, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void stopRecording() {
        mic.stop();
        mic.close();
    }

}