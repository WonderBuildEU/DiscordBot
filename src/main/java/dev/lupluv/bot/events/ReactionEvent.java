package dev.lupluv.bot.events;

import dev.lupluv.bot.utils.ReactionRole;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionEvent extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e){
        long message = e.getMessageIdLong();
        Member member = e.getMember();
            Emoji emote = e.getReaction().getEmoji();
            ReactionRole rr = new ReactionRole();
            rr.setMessageID(message);
            rr.setReactionIsAnimated(true);
            rr.setReaction(emote.getName());
            if(rr.existsByReactionAndMessage()){
                rr.loadByReactionAndMessage();
                e.getGuild().addRoleToMember(member, e.getGuild().getRoleById(rr.getRoleID())).queue();
            }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent e){
        long message = e.getMessageIdLong();
        Member member = e.getMember();
        Emoji emote = e.getReaction().getEmoji();
        ReactionRole rr = new ReactionRole();
        rr.setMessageID(message);
        rr.setReactionIsAnimated(true);
        rr.setReaction(emote.getName());
        if(rr.existsByReactionAndMessage()){
            rr.loadByReactionAndMessage();
            e.getGuild().removeRoleFromMember(member, e.getGuild().getRoleById(rr.getRoleID())).queue();
        }
    }

}
