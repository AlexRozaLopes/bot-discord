package org.example.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class HelloCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("ola")) {
            event.reply("Boa dia, amigos! Sou Bilbo Baggins, e é um prazer conhecê-los. Posso oferecer-lhes um pouco de chá e bolinhos?").queue();
        }
    }
}
