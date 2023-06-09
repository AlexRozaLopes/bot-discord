package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.example.commands.HelloCommand;
import org.example.commands.QuitChannelCommand;
import org.example.commands.QuoteCommand;
import org.example.commands.VoiceChannelCommand;
import org.example.model.Mensagem;
import org.example.utils.VariaveisSecretas;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MessageListener extends ListenerAdapter {


    public static void main(String[] args)  {
        JDA jda = JDABuilder.createDefault(VariaveisSecretas.TOKEN)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_PRESENCES,GatewayIntent.GUILD_MESSAGES)// enables explicit access to message.getContentDisplay()
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                .enableIntents(GatewayIntent.DIRECT_MESSAGE_REACTIONS)
                .enableIntents(GatewayIntent.DIRECT_MESSAGE_TYPING)
                .enableIntents(GatewayIntent.GUILD_MESSAGE_TYPING)
                .enableIntents(GatewayIntent.GUILD_INVITES)
                .build();
        //You can also add event listeners to the already built JDA instance
        // Note that some events may not be received if the listener is added after calling build()
        // This includes events such as the ReadyEvent
       var voiceChannelCommand = new VoiceChannelCommand(jda);
        jda.addEventListener(new MessageListener());
        jda.addEventListener(new HelloCommand());
        jda.addEventListener(new QuoteCommand());
        jda.addEventListener(voiceChannelCommand);
        jda.addEventListener(new QuitChannelCommand(voiceChannelCommand));
        jda.updateCommands().addCommands(
                Commands.slash("ola", "Saudação do Bolseiro!"),
                Commands.slash("quote", "Citação aleatória do Bilbo Baggins")
        ).queue();

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.isFromType(ChannelType.PRIVATE)) {
            System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
                    event.getMessage().getContentDisplay());
            var mensagem = new Mensagem();

            mensagem.setConteudo(event.getMessage().getContentDisplay());
            mensagem.setAutor(event.getAuthor().getName());

            chamadaPost(mensagem);

        } else {
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                    event.getChannel().getName(), event.getMember().getEffectiveName(),
                    event.getMessage().getContentDisplay());

            var mensagem = new Mensagem();

            mensagem.setConteudo(event.getMessage().getContentDisplay());
            mensagem.setAutor(event.getMember().getEffectiveName());
            mensagem.setCanal(event.getChannel().getName());
            mensagem.setServidor(event.getGuild().getName());

            chamadaPost(mensagem);

        }
    }

    private static void chamadaPost(Mensagem mensagem) {
        var objectMapper = new ObjectMapper();
        var json = "";
        try {
            json= objectMapper.writeValueAsString(mensagem);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        var httpClient = HttpClientBuilder.create().build();
        var post = new HttpPost("http://localhost:8080/mensagem/create");
        post.setHeader("Content-Type", "application/json");
        try {
            post.setEntity(new StringEntity(json));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        try {
            httpClient.execute(post);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
