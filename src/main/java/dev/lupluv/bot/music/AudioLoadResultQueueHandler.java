package dev.lupluv.bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class AudioLoadResultQueueHandler implements AudioLoadResultHandler {

    private final MusicController controller;

    public AudioLoadResultQueueHandler(MusicController controller) {
        this.controller = controller;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        System.out.println("Trying to play " + track.getInfo().title);
        controller.getPlayer().playTrack(track);
        System.out.println("Started playing " + track.getInfo().title);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {

    }

    @Override
    public void noMatches() {
        System.out.println("no matches");
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        System.out.println("load failed");
    }
}
