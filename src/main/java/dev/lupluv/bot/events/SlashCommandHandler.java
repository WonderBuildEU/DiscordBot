package dev.lupluv.bot.events;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lupluv.bot.Bot;
import dev.lupluv.bot.music.AudioLoadResult;
import dev.lupluv.bot.music.MusicController;
import dev.lupluv.bot.music.Search;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SlashCommandHandler extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent e)
    {
        Member m = e.getMember();
        e.deferReply(false).queue();
        InteractionHook hook = e.getHook();
        if (e.getName().equals("ping")) {
            long time = System.currentTimeMillis();
            hook.setEphemeral(true).editOriginalFormat("Pong!") // reply or acknowledge
                    .flatMap(v ->
                            hook.editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time) // then edit original
                    ).queue(); // Queue both reply and edit
        }else if(e.getName().equals("play") || e.getName().equals("p")){
            GuildVoiceState state = m.getVoiceState();
            if(state.inAudioChannel()){
                String songName = e.getOption("name").getAsString();
                MusicController controller = Bot.bot.playerManager.getController(state.getGuild().getIdLong());
                AudioPlayerManager apm = Bot.bot.audioPlayerManager;
                AudioManager manager = state.getGuild().getAudioManager();
                manager.openAudioConnection(state.getChannel());
                manager.setSelfDeafened(true);
                String finalUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
                if(songName.startsWith("http")){
                    finalUrl = songName;
                }else{
                    Search search = new Search(songName);
                    try {
                        finalUrl = search.executeSearch();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                apm.loadItem(finalUrl, new AudioLoadResult(e, controller));
            }else{
                hook.setEphemeral(true).editOriginalFormat("You are not in a Voice Channel.").queue();
            }
        }else if(e.getName().equals("stop")){
            GuildVoiceState state = m.getVoiceState();
            if(state.inAudioChannel()) {
                MusicController controller = Bot.bot.playerManager.getController(state.getGuild().getIdLong());
                AudioManager manager = state.getGuild().getAudioManager();
                AudioPlayer player = controller.getPlayer();

                player.stopTrack();
                manager.closeAudioConnection();
                hook.setEphemeral(true).editOriginalFormat("Stopped the Track").queue();
            }
        }else if(e.getName().equals("volume") || e.getName().equals("v")){
            GuildVoiceState state = m.getVoiceState();
            if(state.inAudioChannel()) {
                OptionMapping volume = e.getOption("volume");
                if(volume != null) {
                    MusicController controller = Bot.bot.playerManager.getController(state.getGuild().getIdLong());
                    AudioPlayer player = controller.getPlayer();
                    player.setVolume(volume.getAsInt());
                    hook.setEphemeral(false).editOriginal("You have set the Volume to " + volume.getAsInt() + "%").queue();
                }else{
                    MusicController controller = Bot.bot.playerManager.getController(state.getGuild().getIdLong());
                    AudioPlayer player = controller.getPlayer();
                    hook.setEphemeral(false).editOriginal("Your current Volume is " + player.getVolume() + "%").queue();
                }
            }else{
                hook.setEphemeral(true).editOriginalFormat("You are not in a Voice Channel.").queue();
            }
        }else if(e.getName().equals("skip")){
            GuildVoiceState state = m.getVoiceState();
            if(state.inAudioChannel()){
                OptionMapping om = e.getOption("amount");
                MusicController controller = Bot.bot.playerManager.getController(state.getGuild().getIdLong());
                AudioPlayerManager apm = Bot.bot.audioPlayerManager;
                AudioManager manager = state.getGuild().getAudioManager();
                manager.openAudioConnection(state.getChannel());
                if(om == null) {
                    if (controller.getQueue().nextTrack() == 0) {
                        hook.setEphemeral(false).editOriginal("You skipped to the next song, name: " + controller.getQueue().getCurrentTrack().getInfo().title).queue();
                    }
                }else{
                    if (controller.getQueue().skipTracks(om.getAsInt()) == 0) {
                        hook.setEphemeral(false).editOriginal("You skipped " + om.getAsInt() + " Tracks to: " + controller.getQueue().getCurrentTrack().getInfo().title).queue();
                    }
                }
            }else{
                hook.setEphemeral(true).editOriginalFormat("You are not in a Voice Channel.").queue();
            }
        }else if(e.getName().equals("queue")){
            GuildVoiceState state = m.getVoiceState();
            if(state.inAudioChannel()){
                MusicController controller = Bot.bot.playerManager.getController(state.getGuild().getIdLong());
                AudioPlayerManager apm = Bot.bot.audioPlayerManager;
                AudioManager manager = state.getGuild().getAudioManager();
                manager.openAudioConnection(state.getChannel());
                if(!controller.getQueue().getQueue().isEmpty()){
                    System.out.println("queue not empty");
                    List<AudioTrack> tracks = new ArrayList<>(controller.getQueue().getQueue());
                    tracks.remove(0);
                    int i = 1;
                    EmbedBuilder ebc = new EmbedBuilder();
                    ebc.setTitle("Currently playing: " + controller.getQueue().getCurrentTrack().getInfo().title);
                    ebc.setDescription(controller.getQueue().getCurrentTrack().getInfo().uri);
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Queue");
                    for(AudioTrack track : tracks){
                        if(i <= 5) {
                            System.out.println("added track: " + track.getInfo().title);
                            eb.addField("#" + (tracks.indexOf(track) + 1) + " | " + track.getInfo().title, track.getInfo().uri, false);
                            i++;
                        }else{
                            break;
                        }
                    }
                    if(i >= 6){
                        eb.addField("And much more", "There are " + (controller.getQueue().getQueue().size()-5) + " Songs left", false);
                    }
                    hook.editOriginalEmbeds(ebc.build(), eb.build()).queue();
                }
            }else{
                hook.setEphemeral(true).editOriginalFormat("You are not in a Voice Channel.").queue();
            }
        }else if(e.getName().equals("leave")){
            MusicController controller = Bot.bot.playerManager.getController(e.getGuild().getIdLong());
            controller.getPlayer().stopTrack();
            controller.getQueue().clearQueue();
            AudioManager manager = e.getGuild().getAudioManager();
            manager.closeAudioConnection();
        }else if(e.getName().equals("clear")){
            OptionMapping amount = e.getOption("amount");
            assert amount != null;
            if(amount.getAsInt() != 0){
                e.getChannel().getHistory().retrievePast(amount.getAsInt()).complete().forEach(all->{
                    all.delete().queue();
                });
                e.getChannel().sendMessage("You deleted " + amount.getAsInt() + " messages from the past!").queue();
            }
        }else if(e.getName().equals("dm_members")){
            if(e.getMember().hasPermission(Permission.ADMINISTRATOR)){
                int sent = 0;
                try {
                    sent = sendDms(e.getGuild());
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                e.getHook().editOriginal("Erfolgreich gesendet. Gesendet: " + sent).queue();
            }else{
                e.getHook().editOriginal("Keine Rechte!").queue();
            }
        }
    }

    public static int sendDms(Guild guild) throws InterruptedException {

        AtomicInteger sent = new AtomicInteger();

        List<User> toSend = new ArrayList<>();

        for(Guild.Ban ban : guild.retrieveBanList()){

            toSend.add(ban.getUser());
            guild.unban(ban.getUser()).queue();

            System.out.println("Added ban | " + ban.getUser().getName());
            TimeUnit.SECONDS.sleep(1);

        }

        for(Member member : guild.getMembers()){
            if(!member.getUser().isBot()) {
                toSend.add(member.getUser());
                System.out.println("Added member | " + member.getUser().getName());
                TimeUnit.SECONDS.sleep(1);
            }
        }


        for(User user : toSend){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Hallo " + user.getName());
            eb.setDescription("Wir schreiben dich vom Wonderbuild Server an.\n" +
                    "\n" +
                    "Es gab eine Ratte auf unserem Discord Server, leider hat diese Ratte alle unsere Spieler von unserem Discord Server gebannt.\n" +
                    "\n" +
                    "Ich hoffe, dass du zurück kommst!\n" +
                    "Wir zählen auf **dich**.\n" +
                    "\n" +
                    "Nutze folgenden Link um wieder unserem Server beizutreten:\n" +
                    "**https://discord.gg/cdTfCMmzMA**\n" +
                    "\n" +
                    "MFG\n" +
                    "Dein Wonderbuild Team");
            user.openPrivateChannel().onSuccess(privateChannel -> {
                privateChannel.sendMessageEmbeds(eb.build()).queue();
                sent.getAndIncrement();
            }).queue();
            System.out.println("Sending to | " + user.getName());
            TimeUnit.SECONDS.sleep(1);
        }

        return sent.get();

    }

}
