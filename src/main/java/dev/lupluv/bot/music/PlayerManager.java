package dev.lupluv.bot.music;


import dev.lupluv.bot.Bot;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    public ConcurrentHashMap<Long, MusicController> controller;

    public PlayerManager() {
        this.controller = new ConcurrentHashMap<>();
    }

    public MusicController getController(long guildId){
        MusicController mc = null;

        if(this.controller.containsKey(guildId)){
            mc = this.controller.get(guildId);
        }else{
            mc = new MusicController(Bot.jda.getGuildById(guildId));
            controller.put(guildId, mc);
        }

        return mc;
    }

}
