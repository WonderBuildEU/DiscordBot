package dev.lupluv.bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import dev.lupluv.bot.Bot;
import net.dv8tion.jda.api.entities.Guild;

public class MusicController {

    public static MusicController getMusicControllerFromAudioPlayer(AudioPlayer player){
        for(MusicController controller : Bot.bot.playerManager.controller.values()){
            if(controller.getPlayer() == player){
                return controller;
            }
        }
        return null;
    }

    private Guild guild;
    private AudioPlayer player;
    private Queue queue;

    public MusicController(Guild guild){
        this.guild = guild;
        this.player = Bot.bot.audioPlayerManager.createPlayer();
        this.player.addListener(new AudioEventScheduler());
        this.guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
        this.player.setVolume(50);
        this.queue = new Queue(this);
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public Guild getGuild() {
        return guild;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

}
