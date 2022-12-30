package dev.lupluv.bot.events;

import dev.lupluv.bot.utils.Util;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildJoinEventHandler extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e){
        User user = e.getUser();
        Member member = e.getMember();
        user.openPrivateChannel().queue((channel) ->{
            channel.sendMessageEmbeds(Util.getInstance().getJoinedPrivateEmbed(user, e.getGuild())).queue();
        });
    }

}
