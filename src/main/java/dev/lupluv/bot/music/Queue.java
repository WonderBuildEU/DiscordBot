package dev.lupluv.bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lupluv.bot.Bot;

import java.util.ArrayList;
import java.util.List;

public class Queue {

    List<AudioTrack> queue;

    MusicController controller;

    public Queue(MusicController controller) {
        this.controller = controller;
        queue = new ArrayList<>();
    }

    public int addTrack(AudioTrack track){
        queue.add(track);
        if(queue.size() == 1){
            controller.getPlayer().playTrack(queue.get(0));
            System.out.println("Playing " + track.getInfo().title);
            return 0;
        }
        return 1;
    }

    public int addPlaylist(AudioPlaylist playlist){
        boolean playDirectly = false;
        if(queue.isEmpty()){
            playDirectly = true;
        }
        queue.addAll(playlist.getTracks());
        if(playDirectly){
            controller.getPlayer().playTrack(queue.get(0));
            return 0;
        }
        return 1;
    }

    public int nextTrack(){
        if(!queue.isEmpty()){
            queue.remove(0);
        }
        if(!queue.isEmpty()) {
            controller.getPlayer().playTrack(queue.get(0));
            return 0;
        }else{
            return 1;
        }
    }

    public int skipTracks(int amount){
        for (int i = 0; i < amount; i++){
            nextTrack();
        }
        return 0;
    }

    public void clearQueue(){
        queue.clear();
    }

    public AudioPlayerManager getAudioPlayerManager(){
        return Bot.bot.audioPlayerManager;
    }

    public MusicController getMusicController(){
        return controller;
    }

    public AudioTrack getCurrentTrack(){
        return queue.get(0);
    }

    public List<AudioTrack> getQueue() {
        return queue;
    }

    public void setQueue(List<AudioTrack> queue) {
        this.queue = queue;
    }

    public MusicController getController() {
        return controller;
    }

    public void setController(MusicController controller) {
        this.controller = controller;
    }
}
