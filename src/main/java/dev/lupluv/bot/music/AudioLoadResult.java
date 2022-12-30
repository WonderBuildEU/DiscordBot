package dev.lupluv.bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class AudioLoadResult implements AudioLoadResultHandler {

    private final SlashCommandInteractionEvent event;
    private final MusicController controller;

    public AudioLoadResult(SlashCommandInteractionEvent event, MusicController controller) {
        this.event = event;
        this.controller = controller;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        controller.getQueue().addTrack(track);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("You added: " + track.getInfo().title);
        eb.addField("Url", track.getInfo().uri, false);
        eb.addField("Author", track.getInfo().author, false);
        eb.setThumbnail("https://i3.ytimg.com/vi/" + Utils.extractYTId(track.getInfo().uri) + "/maxresdefault.jpg");
        System.out.println("Thumbnail url" + eb.build().getThumbnail().getUrl());
        event.getHook().setEphemeral(false).editOriginal("**Successful**").setEmbeds(eb.build()).queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        controller.getQueue().addPlaylist(playlist);
        event.getHook().setEphemeral(false).editOriginal("added playlist with " + playlist.getTracks().size() + " tracks :D");
    }

    @Override
    public void noMatches() {
        event.getHook().setEphemeral(true).editOriginal("No Matches.").queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        exception.printStackTrace();
        event.getHook().setEphemeral(true).editOriginal("Load Failed.").queue();
    }
}
