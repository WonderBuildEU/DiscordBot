package dev.lupluv.bot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lupluv.bot.events.*;
import dev.lupluv.bot.files.FileManager;
import dev.lupluv.bot.music.PlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Bot implements EventListener {

    public static JDA jda;
    private static FileManager fileManager;
    public static Bot bot;
    public AudioPlayerManager audioPlayerManager;
    public PlayerManager playerManager;

    public static void main(String[] args){
        new Bot();
    }

    public Bot() {

        bot = this;

        fileManager = new FileManager();
        try {
            fileManager.loadFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JDABuilder jdaBuilder = JDABuilder.create(fileManager.getConfig().getString("Token")
                , GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_MESSAGE_REACTIONS
                , GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES
                , GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGES);
        jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        jdaBuilder.setActivity(Activity.playing("wonderbuild.net"));
        jdaBuilder.addEventListeners(new MessageReceivedEventHandler(), new GuildJoinEventHandler(), new ReactionEvent(), this, new SlashCommandHandler());
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.playerManager = new PlayerManager();
        jda = jdaBuilder.build();
        jda.upsertCommand("ping", "Calculate ping of the bot").queue();
        jda.updateCommands().addCommands(
                Commands.slash("play", "Plays a Song for you.")
                        .addOption(OptionType.STRING, "name", "The Song name or a url.", true),
                Commands.slash("stop", "Stops the Track"),
                Commands.slash("volume", "Sets volume")
                        .addOption(OptionType.INTEGER, "volume", "What volume?", false),
                Commands.slash("skip", "Skip")
                        .addOption(OptionType.INTEGER, "amount", "Skip a specific amount of songs"),
                Commands.slash("queue", "Shows the queue"),
                Commands.slash("pause", "Pause the player"),
                Commands.slash("resume", "Resume the player"),
                Commands.slash("leave", "Leaves"),
                Commands.slash("clear", "clears chat")
                        .addOption(OptionType.INTEGER, "amount", "amount"),
                Commands.slash("dm_members", "Dm all users a invite message")
        ).queue();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);


    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if(genericEvent instanceof ReadyEvent){
            System.out.println("Bot Ready");
        }else if(genericEvent instanceof ShutdownEvent){
            jda.getShardManager().setStatus(OnlineStatus.OFFLINE);
            System.out.println("Bot offnline.");
        }
    }

    public static FileManager getFileManager() {
        return fileManager;
    }

    public static void startSche(){
        while (true){

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
