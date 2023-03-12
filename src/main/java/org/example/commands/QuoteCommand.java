package org.example.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.example.utils.VariaveisSecretas;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class QuoteCommand extends ListenerAdapter {


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("quote")) {
            var httpClient = HttpClientBuilder.create().build();
            var request = new HttpGet("https://the-one-api.dev/v2/character/5cd99d4bde30eff6ebccfc38/quote");
            request.addHeader("Authorization", "Bearer " + VariaveisSecretas.ACESS_TOKEN);
            try {
                var response = httpClient.execute(request);
                String respondeString = EntityUtils.toString(response.getEntity());
                System.out.println(respondeString);
                var json = new JSONObject(respondeString);
                var array = json.getJSONArray("docs");
                var random = (int) (Math.random() * array.length());
                var quote = array.getJSONObject(random).getString("dialog");
                event.reply(quote).queue();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
