package dev.lupluv.bot.events;

import dev.lupluv.bot.Bot;
import dev.lupluv.bot.utils.ReactionRole;
import dev.lupluv.bot.utils.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class MessageReceivedEventHandler extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        User author = e.getAuthor();
        Message message = e.getMessage();
        String raw = message.getContentRaw();
        if(e.isFromType(ChannelType.TEXT)){
            System.out.println(e.getGuild().getName() + " | " + author.getName() + " : " + raw);
            Member member = e.getMember();
            if(e.isFromType(ChannelType.TEXT)){
                TextChannel channel = e.getChannel().asTextChannel();

                if (ReactionRole.isInCreation(member)) {
                    if(ReactionRole.getCreation(member).creationStep.equalsIgnoreCase("messageID")){
                        Message thatMessage = Util.getInstance().getMessageFromAnyChannel(e.getGuild(), raw);
                        if(thatMessage != null){
                            ReactionRole.getCreation(member).messageID = thatMessage.getIdLong();
                            ReactionRole.getCreation(member).creationStep = "roleID";
                            channel.sendMessage("**1. Step:** You have set the Message ID to *" + thatMessage.getId() + "*").queue();
                            channel.sendMessage("**2. Step:** Tell me the Role ID for the Reaction Role. Just paste it in the Chat.").queue();
                        }else{
                            channel.sendMessage("**1. Step:** There is no Message with the ID *" + raw + "*. Please enter a valid Message ID.").queue();
                        }
                    }else if(ReactionRole.getCreation(member).creationStep.equalsIgnoreCase("roleID")){
                        Role thatRole = e.getGuild().getRoleById(raw);
                        if(thatRole != null){
                            ReactionRole.getCreation(member).roleID = thatRole.getIdLong();
                            ReactionRole.getCreation(member).creationStep = "isAnimated";
                            channel.sendMessage("**2. Step:** You have set the Role to *" + thatRole.getName() + "*").queue();
                            channel.sendMessage("**3. Step:** Tell me if the Reaction is animated. Just write true or false.").queue();
                        }else{
                            channel.sendMessage("**2. Step:** There is no Role with the ID *" + raw + "*. Please enter a valid Role ID.").queue();
                        }
                    }else if(ReactionRole.getCreation(member).creationStep.equalsIgnoreCase("isAnimated")){
                        if(raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false")){
                            boolean isAnimatedBool = Boolean.parseBoolean(raw);
                            ReactionRole.getCreation(member).reactionIsAnimated = isAnimatedBool;
                            ReactionRole.getCreation(member).creationStep = "reaction";
                            channel.sendMessage("**3. Step:** You have set if the Reaction is animated to *" + isAnimatedBool + "*").queue();
                            channel.sendMessage("**4. Step:** Tell me the Reaction that i should use. Just paste the Name in the Chat.").queue();
                        }else{
                            channel.sendMessage("**3. Step:** There is no Boolean with the Value *" + raw + "*. Please enter a valid Boolean (true or false).").queue();
                        }
                    }else if(ReactionRole.getCreation(member).creationStep.equalsIgnoreCase("reaction")){
                        if(ReactionRole.getCreation(member).reactionIsAnimated) {
                            Emoji thatEmote = e.getGuild().getEmojisByName(raw, true).get(0);
                            if (thatEmote != null) {
                                ReactionRole.getCreation(member).reaction = thatEmote.getName();
                                ReactionRole.getCreation(member).creationStep = "removeIfUn";
                                channel.sendMessage("**4. Step:** You have set if the Reaction to *" + thatEmote.getFormatted() + "*").queue();
                                channel.sendMessage("**5. Step:** Tell if the Role should be removed from a member if he removes his reaction on the Message. " +
                                        "Just write true or false.").queue();
                            } else {
                                channel.sendMessage("**4. Step:** There is no Emote with the Name *" + raw + "*. Please enter a valid Emote Name.").queue();
                            }
                        }else{

                        }
                    }else if(ReactionRole.getCreation(member).creationStep.equalsIgnoreCase("removeIfUn")){
                        if(raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false")){
                            boolean removeIfUn = Boolean.parseBoolean(raw);
                            ReactionRole.getCreation(member).removeIfUnreacted = removeIfUn;
                            ReactionRole.getCreation(member).creationStep = "finished";
                            ReactionRole.getCreation(member).createID();
                            ReactionRole.getCreation(member).save();
                            ReactionRole.getCreation(member).updateMessage();
                            ReactionRole.inCreationRRs.remove(ReactionRole.getCreation(member));
                            channel.sendMessage("**5. Step:** You have set if the role should be removed with removing a reaction to *" + removeIfUn + "*").queue();
                            channel.sendMessage("**Finished:** You have finished the Setup, the Reaction Role can now be used.").queue();
                        }else{
                            channel.sendMessage("**5. Step:** There is no Boolean with the Value *" + raw + "*. Please enter a valid Boolean (true or false).").queue();
                        }
                    }
                }

                String command = raw.split(" ")[0];
                String[] args = raw.split(" ");
                if(command.startsWith(".")){
                    if(command.equalsIgnoreCase(".help")){
                        if(!member.getPermissions().contains(Permission.MESSAGE_MANAGE)){
                            message.reply("You have no Permissions to do that!").queue();
                            return;
                        }
                        if(args.length == 1) {
                            channel.sendMessageEmbeds(Util.getInstance().getHelpMainEmbed(author, e.getGuild())).queue();
                        }else if(args.length == 2){
                            if (args[1].equalsIgnoreCase("mod")){
                                channel.sendMessageEmbeds(Util.getInstance().getHelpModEmbed(author, e.getGuild())).queue();
                            }else if (args[1].equalsIgnoreCase("tools")){
                                channel.sendMessageEmbeds(Util.getInstance().getHelpToolsEmbed(author, e.getGuild())).queue();
                            }else if (args[1].equalsIgnoreCase("fun")){

                            }else if (args[1].equalsIgnoreCase("leveling")){

                            }else{
                                channel.sendMessageEmbeds(Util.getInstance().getUsageEmbed(".help <category>", e.getGuild())).queue();
                            }
                        }else{
                            channel.sendMessageEmbeds(Util.getInstance().getUsageEmbed(".help <category>", e.getGuild())).queue();
                        }
                    }else if(command.equalsIgnoreCase(".info")){
                        if(!member.getPermissions().contains(Permission.KICK_MEMBERS)){
                            message.reply("You have no Permissions to do that!").queue();
                            return;
                        }
                        if(args.length == 2){
                            if(!message.getMentions().getMembers().isEmpty()){
                                Member mentionedMember = message.getMentions().getMembers().get(0);
                                User mentionedUser = mentionedMember.getUser();
                                channel.sendMessageEmbeds(Util.getInstance().getInfoEmbed(mentionedUser, mentionedMember, e.getGuild())).queue();
                            }else{
                                String searchedMember = args[1];
                                List<Member> foundMembers = e.getGuild().getMembersByName(searchedMember, true);
                                if(!foundMembers.isEmpty()){
                                    Member foundMemberFinal = foundMembers.get(0);
                                    User foundUserFinal = foundMemberFinal.getUser();
                                    channel.sendMessageEmbeds(Util.getInstance().getInfoEmbed(foundUserFinal, foundMemberFinal, e.getGuild())).queue();
                                }else{
                                    channel.sendMessage("There is no Member with the Name '" + searchedMember + "'").queue();
                                }
                            }
                        }else{
                            channel.sendMessageEmbeds(Util.getInstance().getUsageEmbed(".info <@member>", e.getGuild())).queue();
                        }
                    }else if(command.equalsIgnoreCase(".ban")){
                        if(!member.getPermissions().contains(Permission.BAN_MEMBERS)){
                            message.reply("You have no Permissions to do that!").queue();
                            return;
                        }
                        if(args.length == 3){
                            if(!message.getMentions().getMembers().isEmpty()){
                                Member mentionedMember = message.getMentions().getMembers().get(0);
                                User mentionedUser = mentionedMember.getUser();
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setColor(Color.BLACK);
                                eb.setTitle("You have been banned from " + e.getGuild().getName() + "!");
                                eb.setThumbnail(Bot.jda.getSelfUser().getAvatarUrl());
                                eb.setDescription("You can send me a Unban Application, and we will discuss if you get Unbanned.");
                                eb.addField("Reason for the Ban", args[2], true);
                                eb.addField("Duration of the Ban", "Permanent", true);
                                eb.setFooter("kirstenjbx Moderation", "https://emoji.discord.st/emojis/abe39c64-0064-4fa4-846b-0c219e6489a7.png");
                                mentionedUser.openPrivateChannel().queue((privateChannel) ->{
                                    privateChannel.sendMessageEmbeds(eb.build()).queue();
                                });
                                mentionedMember.ban(0, TimeUnit.SECONDS).reason(args[0]).queue();
                                channel.sendMessage("You have successfully banned **" + mentionedUser.getName() + "**").queue();
                            }else{
                                String searchedMember = args[1];
                                List<Member> foundMembers = e.getGuild().getMembersByName(searchedMember, true);
                                if(!foundMembers.isEmpty()){
                                    Member foundMemberFinal = foundMembers.get(0);
                                    User foundUserFinal = foundMemberFinal.getUser();
                                    EmbedBuilder eb = new EmbedBuilder();
                                    eb.setColor(Color.PINK);
                                    eb.setTitle("You have been banned from " + e.getGuild().getName() + "!");
                                    eb.setThumbnail(Bot.jda.getSelfUser().getAvatarUrl());
                                    eb.setDescription("You can send me a Unban Application, and we will discuss if you get Unbanned.");
                                    eb.addField("Reason for the Ban", args[2], true);
                                    eb.addField("Duration of the Ban", "Permanent", true);
                                    eb.setFooter("kirstenjbx Moderation", "https://emoji.discord.st/emojis/abe39c64-0064-4fa4-846b-0c219e6489a7.png");
                                    foundUserFinal.openPrivateChannel().queue((privateChannel) ->{
                                        privateChannel.sendMessageEmbeds(eb.build()).queue();
                                    });
                                    foundMemberFinal.ban(0, TimeUnit.SECONDS).queue();
                                    channel.sendMessage("You have successfully banned **" + foundUserFinal.getName() + "**").queue();
                                }else{
                                    channel.sendMessage("There is no Member with the Name '" + searchedMember + "'").queue();
                                }
                            }
                        }else{
                            channel.sendMessageEmbeds(Util.getInstance().getUsageEmbed(".ban <@member> <reason>", e.getGuild())).queue();
                        }
                    }else if(command.equalsIgnoreCase(".unban")){
                        if(!member.getPermissions().contains(Permission.BAN_MEMBERS)){
                            message.reply("You have no Permissions to do that!").queue();
                            return;
                        }
                        if(args.length == 2){
                            String searchedMember = args[1];
                            AtomicReference<Guild.Ban> foundBan = new AtomicReference<>();
                            e.getGuild().retrieveBanList().complete().forEach(bans ->{
                                if(bans.getUser().getName().equalsIgnoreCase(searchedMember)){
                                    foundBan.set(bans);
                                }
                            });
                            if(foundBan.get() != null){
                                e.getGuild().unban(foundBan.get().getUser()).queue();
                                channel.sendMessage("You have successfully unbanned **" + foundBan.get().getUser().getName() + "**").queue();
                                foundBan.get().getUser().openPrivateChannel().queue((privateChannel) ->{
                                    privateChannel.sendMessage("You have been unbanned from **" + e.getGuild().getName() + "**").queue();
                                    privateChannel.sendMessage("**Invite: https://discord.gg/ege9tqxhYH**").queue();
                                });
                            }else{
                                channel.sendMessage("There is no User banned with the Name '" + searchedMember + "'").queue();
                            }
                        }else{
                            channel.sendMessageEmbeds(Util.getInstance().getUsageEmbed(".unban <user>", e.getGuild())).queue();
                        }
                    }else if(command.equalsIgnoreCase(".kick")){
                        if(!member.getPermissions().contains(Permission.KICK_MEMBERS)){
                            message.reply("You have no Permissions to do that!").queue();
                            return;
                        }
                        if(args.length == 3){
                            if(!message.getMentions().getMembers().isEmpty()){
                                Member mentionedMember = message.getMentions().getMembers().get(0);
                                User mentionedUser = mentionedMember.getUser();
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setColor(Color.BLACK);
                                eb.setTitle("You have been kicked from " + e.getGuild().getName() + "!");
                                eb.setThumbnail(Bot.jda.getSelfUser().getAvatarUrl());
                                eb.setDescription("You can join again, but dont break the Rules.");
                                eb.addField("Reason for the Kick", args[2], true);
                                eb.addField("", "", true);
                                eb.setFooter("kirstenjbx Moderation", "https://emoji.discord.st/emojis/abe39c64-0064-4fa4-846b-0c219e6489a7.png");
                                mentionedUser.openPrivateChannel().queue((privateChannel) ->{
                                    privateChannel.sendMessageEmbeds(eb.build()).queue();
                                });
                                mentionedMember.kick(args[2]).queue();
                                channel.sendMessage("You have successfully kicked **" + mentionedUser.getName() + "**").queue();
                            }else{
                                String searchedMember = args[1];
                                List<Member> foundMembers = e.getGuild().getMembersByName(searchedMember, true);
                                if(!foundMembers.isEmpty()){
                                    Member foundMemberFinal = foundMembers.get(0);
                                    User foundUserFinal = foundMemberFinal.getUser();
                                    EmbedBuilder eb = new EmbedBuilder();
                                    eb.setColor(Color.PINK);
                                    eb.setTitle("You have been kicked from " + e.getGuild().getName() + "!");
                                    eb.setThumbnail(Bot.jda.getSelfUser().getAvatarUrl());
                                    eb.setDescription("You can join again, but dont break the Rules.");
                                    eb.addField("Reason for the Kick", args[2], true);
                                    eb.addField("", "", true);
                                    eb.setFooter("kirstenjbx Moderation", "https://emoji.discord.st/emojis/abe39c64-0064-4fa4-846b-0c219e6489a7.png");
                                    foundUserFinal.openPrivateChannel().queue((privateChannel) ->{
                                        privateChannel.sendMessageEmbeds(eb.build()).queue();
                                        privateChannel.sendMessage("**Invite: https://discord.gg/ege9tqxhYH**").queue();
                                    });
                                    foundMemberFinal.kick(args[2]).queue();
                                    channel.sendMessage("You have successfully kicked **" + foundUserFinal.getName() + "**").queue();
                                }else{
                                    channel.sendMessage("There is no Member with the Name '" + searchedMember + "'").queue();
                                }
                            }
                        }else{
                            channel.sendMessageEmbeds(Util.getInstance().getUsageEmbed(".kick <@member> <reason>", e.getGuild())).queue();
                        }
                    }else if(command.equalsIgnoreCase(".mute")){
                        if(!member.getPermissions().contains(Permission.MESSAGE_MANAGE)){
                            message.reply("You have no Permissions to do that!").queue();
                            return;
                        }
                        if(args.length == 3){
                            if(!message.getMentions().getMembers().isEmpty()){
                                Member mentionedMember = message.getMentions().getMembers().get(0);
                                User mentionedUser = mentionedMember.getUser();
                                if(!mentionedMember.getRoles().contains(e.getGuild().getRoleById("909203442415853688"))) {
                                    EmbedBuilder eb = new EmbedBuilder();
                                    eb.setColor(Color.BLACK);
                                    eb.setTitle("You have been muted from " + e.getGuild().getName() + "!");
                                    eb.setThumbnail(Bot.jda.getSelfUser().getAvatarUrl());
                                    eb.setDescription("You got muted for an unknown duration.");
                                    eb.addField("Reason for the Mute", args[2], true);
                                    eb.addField("Duration of the Mute", "Permanent", true);
                                    eb.setFooter("kirstenjbx Moderation", "https://emoji.discord.st/emojis/abe39c64-0064-4fa4-846b-0c219e6489a7.png");
                                    mentionedUser.openPrivateChannel().queue((privateChannel) -> {
                                        privateChannel.sendMessageEmbeds(eb.build()).queue();
                                    });
                                    e.getGuild().addRoleToMember(mentionedMember, e.getGuild().getRoleById("909203442415853688")).queue();
                                    channel.sendMessage("You have successfully muted **" + mentionedUser.getName() + "**").queue();
                                }else{
                                    channel.sendMessage("The Member **" + mentionedUser + "** is already muted").queue();
                                }
                            }else{
                                String searchedMember = args[1];
                                List<Member> foundMembers = e.getGuild().getMembersByName(searchedMember, true);
                                if(!foundMembers.isEmpty()){
                                    Member foundMemberFinal = foundMembers.get(0);
                                    User foundUserFinal = foundMemberFinal.getUser();
                                    if(!foundMemberFinal.getRoles().contains(e.getGuild().getRoleById("909203442415853688"))) {
                                        EmbedBuilder eb = new EmbedBuilder();
                                        eb.setColor(Color.BLACK);
                                        eb.setTitle("You have been muted from " + e.getGuild().getName() + "!");
                                        eb.setThumbnail(Bot.jda.getSelfUser().getAvatarUrl());
                                        eb.setDescription("You got muted for an unknown duration.");
                                        eb.addField("Reason for the Mute", args[2], true);
                                        eb.addField("Duration of the Mute", "Permanent", true);
                                        eb.setFooter("kirstenjbx Moderation", "https://emoji.discord.st/emojis/abe39c64-0064-4fa4-846b-0c219e6489a7.png");
                                        foundUserFinal.openPrivateChannel().queue((privateChannel) -> {
                                            privateChannel.sendMessageEmbeds(eb.build()).queue();
                                        });
                                        e.getGuild().addRoleToMember(foundMemberFinal, e.getGuild().getRoleById("909203442415853688")).queue();
                                        channel.sendMessage("You have successfully muted **" + foundUserFinal.getName() + "**").queue();
                                    }else{
                                        channel.sendMessage("The Member **" + foundUserFinal + "** is already muted").queue();
                                    }
                                }else{
                                    channel.sendMessage("There is no Member with the Name '" + searchedMember + "'").queue();
                                }
                            }
                        }else{
                            channel.sendMessageEmbeds(Util.getInstance().getUsageEmbed(".mute <@member> <reason>", e.getGuild())).queue();
                        }
                    }else if(command.equalsIgnoreCase(".unmute")){
                        if(!member.getPermissions().contains(Permission.MESSAGE_MANAGE)){
                            message.reply("You have no Permissions to do that!").queue();
                            return;
                        }
                        if(args.length == 2){
                            if(!message.getMentions().getMembers().isEmpty()){
                                Member mentionedMember = message.getMentions().getMembers().get(0);
                                User mentionedUser = mentionedMember.getUser();
                                if(mentionedMember.getRoles().contains(e.getGuild().getRoleById("909203442415853688"))) {
                                    mentionedUser.openPrivateChannel().queue((privateChannel) -> {
                                        privateChannel.sendMessage("You have been unmuted on **" + e.getGuild().getName() + "**").queue();
                                        privateChannel.sendMessage("**Link: https://discord.gg/ege9tqxhYH**").queue();
                                    });
                                    e.getGuild().removeRoleFromMember(mentionedMember, e.getGuild().getRoleById("909203442415853688")).queue();
                                    channel.sendMessage("You have successfully unmuted **" + mentionedUser.getName() + "**").queue();
                                }else{
                                    channel.sendMessage("The Member **" + mentionedUser.getAsMention() + "** is not muted").queue();
                                }
                            }else{
                                String searchedMember = args[1];
                                List<Member> foundMembers = e.getGuild().getMembersByName(searchedMember, true);
                                if(!foundMembers.isEmpty()){
                                    Member foundMemberFinal = foundMembers.get(0);
                                    User foundUserFinal = foundMemberFinal.getUser();
                                    if(foundMemberFinal.getRoles().contains(e.getGuild().getRoleById("909203442415853688"))) {
                                        foundUserFinal.openPrivateChannel().queue((privateChannel) -> {
                                            privateChannel.sendMessage("You have been unmuted on **" + e.getGuild().getName() + "**").queue();
                                            privateChannel.sendMessage("**Link: https://discord.gg/ege9tqxhYH**").queue();
                                        });
                                        e.getGuild().removeRoleFromMember(foundMemberFinal, e.getGuild().getRoleById("909203442415853688")).queue();
                                        channel.sendMessage("You have successfully unmuted **" + foundUserFinal.getName() + "**").queue();
                                    }else{
                                        channel.sendMessage("The Member **" + foundUserFinal + "** is not muted").queue();
                                    }
                                }else{
                                    channel.sendMessage("There is no Member with the Name '" + searchedMember + "'").queue();
                                }
                            }
                        }else{
                            channel.sendMessageEmbeds(Util.getInstance().getUsageEmbed(".unmute <@member>", e.getGuild())).queue();
                        }
                    }else if(command.equalsIgnoreCase(".clear")){
                        if(!member.getPermissions().contains(Permission.MESSAGE_MANAGE)){
                            message.reply("You have no Permissions to do that!").queue();
                            return;
                        }
                        if(args.length == 2){
                            try {
                                Integer amount = Integer.parseInt(args[1]);
                                channel.getHistory().retrievePast(amount).queue((list) ->{
                                    list.forEach(all ->{
                                        all.delete().queue();
                                    });
                                });
                                channel.sendMessage("You have deleted **" + amount + "** Messages!").delay(3, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            }catch (NumberFormatException exception){
                                channel.sendMessageEmbeds(Util.getInstance().getUsageEmbed(".clear <amount>", e.getGuild())).queue();
                            }
                        }else{
                            channel.sendMessageEmbeds(Util.getInstance().getUsageEmbed(".clear <amount>", e.getGuild())).queue();
                        }
                    }else if(command.equalsIgnoreCase(".rr")){
                        if(!member.getPermissions().contains(Permission.MANAGE_ROLES)){
                            message.reply("You have no Permissions to do that!").queue();
                            return;
                        }
                        if(args.length == 1){
                            channel.sendMessageEmbeds(Util.getInstance().getHelpReactionRolesEmbed(author, e.getGuild())).queue();
                        }else if(args.length == 2){
                            if(args[1].equalsIgnoreCase("create")){
                                if(!ReactionRole.isInCreation(member)){
                                    channel.sendMessage("You have successfully started a Reaction Role creation").queue();
                                    channel.sendMessage("**1. Step:** Tell me the Message ID, the Reaction Role should be added for. Just paste it in the Chat.").queue();
                                    ReactionRole.startCreation(member);
                                }else{
                                    channel.sendMessage("You have already started a Reaction Role creation!").queue();
                                }
                            }else if(args[1].equalsIgnoreCase("list")){
                                EmbedBuilder eb = new EmbedBuilder();
                                List<ReactionRole> reactionRoles = ReactionRole.getAllReactionRoles();
                                eb.setTitle("Reaction Roles - List (" + reactionRoles.size() + ")");
                                if(reactionRoles.isEmpty()){
                                    eb.setDescription("The List is empty because there are no Reaction Roles.");
                                    eb.setThumbnail("https://cdn-icons-png.flaticon.com/512/5084/5084125.png");
                                }else{
                                    for(ReactionRole rr : reactionRoles){
                                        eb.addField("ID: #" + rr.getId(), "Message ID: *" + rr.getMessageID() + "* | Role ID: *" +
                                                rr.getRoleID() + "* | Reaction: " + e.getGuild().getEmojisByName(rr.getReaction(), true).get(0).getAsMention() +
                                                "\r\nIsAnimated: *" + rr.isReactionIsAnimated() + "* | IsRemoveIfUnreact: *" + rr.isRemoveIfUnreacted() + "*", false);
                                    }
                                }
                                channel.sendMessageEmbeds(eb.build()).queue();
                            }else{
                                channel.sendMessageEmbeds(Util.getInstance().getHelpReactionRolesEmbed(author, e.getGuild())).queue();
                            }
                        }else if(args.length == 3){
                            if(args[1].equalsIgnoreCase("delete")){
                                try {
                                    Long id = Long.parseLong(args[2]);
                                    ReactionRole rr = new ReactionRole(id);
                                    if(rr.existsByID()){
                                        rr.loadByID();
                                        rr.removeReaction();
                                        rr.deleteByID();
                                        channel.sendMessage("Successfully deleted the Reaction Role with the ID **#" + id + "**").queue();
                                    }else{
                                        channel.sendMessage("There is no Reaction Role with the ID **#" + id + "**").queue();
                                    }
                                }catch (NumberFormatException exception){
                                    channel.sendMessageEmbeds(Util.getInstance().getUsageEmbed(".rr delete <id>", e.getGuild())).queue();
                                }
                            }else{
                                channel.sendMessageEmbeds(Util.getInstance().getHelpReactionRolesEmbed(author, e.getGuild())).queue();
                            }
                        }else{
                            channel.sendMessageEmbeds(Util.getInstance().getHelpReactionRolesEmbed(author, e.getGuild())).queue();
                        }
                    }else if(command.equalsIgnoreCase(".sendrules")){
                        if(!member.getPermissions().contains(Permission.MANAGE_CHANNEL)){
                            message.reply("You have no Permissions to do that!").queue();
                            return;
                        }
                        if(args.length == 1){
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setAuthor("English", null, "https://i.ibb.co/kXGmNHX/flag-united-kingdom.png");
                            eb.setTitle("» Rules");
                            eb.setThumbnail("https://i.ibb.co/TvNXrg3/rules-book.png");
                            eb.setDescription("You can always find an up-to-date copy of the rules here in " + e.getGuild().getTextChannelById("835573296422060042").getAsMention());

                            EmbedBuilder eb2 = new EmbedBuilder();
                            eb2.setDescription("**• §1 Discord Guidelines**\n" +
                                    "Any content distributed must be in accordance with the Discord ToS (https://discord.com/terms) and the Community Server Guidelines (https://discord.com/guidelines).\n" +
                                    "**• §2 Behaviour**\n" +
                                    "We expect respectful and orderly behaviour from all users and towards all other users at all times. Accordingly, non-target answers to questions asked are not allowed.\n" +
                                    "**• §3 Harassment**\n" +
                                    "Any kind of harassment (whether in text or voice channels) is to be refrained from. This includes, but is not limited to, channel hopping, spamming or unnecessary responses to messages.\n" +
                                    "**• §4 Private matters**\n" +
                                    "Private misunderstandings and disputes are also to be treated privately and do not belong here on the server.\n" +
                                    "**• §5 Evasion of punishments**\n" +
                                    "Evading a punishment is not allowed and will be dealt with by a permanent ban of the evading account.\n" +
                                    "**• §6 Advertising & Trading**\n" +
                                    "Our Discord server is not a trading, exchange, job or advertising platform. Any messages of this kind are only allowed in form of a post reviewed by the team in our advertisements channel. This includes private messages to server members.\n" +
                                    "**• §7 Politics**\n" +
                                    "The expression of political attitudes and the spreading of conspiracy theories are not allowed on this Discord.\n" +
                                    "**• §8 Right of authority**\n" +
                                    "The server team has full right of authority, who does not follow the instructions of a team member must expect a punishment. Furthermore, the team does not have to justify punishments to users in any way.\n" +
                                    "**• §9 Tagging of Team Members**\n" +
                                    "The tagging of a team member is not allowed. Excluded from this is the *reply* function of Discord and to report any rule violations. Nevertheless, only one team member should be tagged in this case.\n" +
                                    "**• §10 Modifications of the rules**\n" +
                                    "The rules can be changed at any time, these changes are valid with immediate effect even without repeated approval. We will inform about changes to the rules in #news.");
                            eb2.setFooter("© kirstenjbx's Shed", Bot.jda.getSelfUser().getAvatarUrl());

                            EmbedBuilder eb3 = new EmbedBuilder();
                            eb3.setDescription("By reacting with the " + e.getGuild().getEmojisByName("Verify", true).get(0).getAsMention() + " emote I confirm I have read the rules and accept them.");

                            eb.setColor(Color.CYAN);
                            eb2.setColor(Color.CYAN);
                            eb3.setColor(Color.CYAN);

                            e.getGuild().getTextChannelById("1013900300219850815").sendMessageEmbeds(eb.build(), eb2.build(), eb3.build()).queue();
                        }
                    }else if(command.equalsIgnoreCase(".clearchannel")){
                        if(!member.getPermissions().contains(Permission.MESSAGE_MANAGE)){
                            message.reply("You have no Permissions to do that!").queue();
                            return;
                        }
                        if(args.length == 2){
                            TextChannel tc = e.getGuild().getTextChannelById(args[1]);
                            if(tc != null){
                                tc.getHistory().retrievePast(10).queue((messageT) ->{
                                    messageT.forEach(all ->{
                                        all.delete().queue();
                                    });
                                });
                                channel.sendMessage("Success").queue();
                            }else{
                                channel.sendMessage("No Text Channel found!").queue();
                            }
                        }
                    }else if(command.equalsIgnoreCase(".tickets")){
                        if(!member.getPermissions().contains(Permission.KICK_MEMBERS)){
                            message.reply("You have no permission to do that!").queue();
                            return;
                        }
                        if(args.length == 1){
                            channel.sendMessageEmbeds(Util.getInstance().getHelpTicketsEmbed(author, e.getGuild())).queue();
                        }else if(args.length == 3){
                            if(args[1].equalsIgnoreCase("send")){
                                TextChannel tc = e.getGuild().getTextChannelById(args[2]);
                                if(tc != null){
                                    Util.getInstance().sendTicketsCreationMessage(e.getGuild(), tc);
                                }else{
                                    channel.sendMessage("No Text Channel found!").queue();
                                }
                            }
                        }else if(args.length == 2){
                            if(args[1].equalsIgnoreCase("transcripts")){
                                Util.getInstance().sendTranscriptList(e);
                            }
                        }
                    }else if(command.equalsIgnoreCase(".sendrep")){
                        if(args.length == 1){
                            TextChannel tc = e.getGuild().getTextChannelById("1032646856494301195");
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setTitle("Wanna create a report?");
                            eb.setDescription("Just click the button below.");
                            tc.sendMessageEmbeds(eb.build()).addActionRow(
                                    Button.success("createReport", "Create report")
                            ).queue((cM) ->{
                                lastRepMsg = cM;
                            });
                        }
                    }
                }else{
                    if(e.getChannel().getId().equalsIgnoreCase("1032646856494301195")){
                        if(!message.getEmbeds().isEmpty()){
                            MessageEmbed eb = message.getEmbeds().get(0);
                            if(eb.getTitle().contains("Wanna create a report"))return;
                        }
                        if(lastRepMsg != null){
                            lastRepMsg.delete().queue();
                        }
                        TextChannel tc = e.getGuild().getTextChannelById("1032646856494301195");
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("Wanna create a report?");
                        eb.setDescription("Just click the button below.");
                        tc.sendMessageEmbeds(eb.build()).addActionRow(
                                Button.success("createReport", "Create report")
                        ).queue((createdMessage) ->{
                            lastRepMsg = createdMessage;
                        });
                    }else if(e.getChannel().getId().equalsIgnoreCase("1032698293966348441")){
                        message.addReaction(Util.checkmark).queue();
                        message.addReaction(Util.cross).queue();
                    }
                }
            }
        }else{
            System.out.println("not from guild");
        }
    }

    public static Message lastRepMsg;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("createTicket")) {
            event.reply("Creating ticket for you").setEphemeral(true).queue();
            Util.getInstance().createTicket(event.getUser(), event.getGuild(), event.getHook());
        }else if(event.getComponentId().equals("closeTicket")){
            Util.getInstance().sendCloseConfirmation(event);
        }else if(event.getComponentId().equals("closeTicketConfirm")){
            Util.getInstance().closeTicket(event);
        }else if(event.getComponentId().equals("closeTicketCancel")){
            event.getMessage().delete().queue();
        }else if(event.getComponentId().equals("ticketOpen")){
            Util.getInstance().openTicket(event);
        }else if(event.getComponentId().equals("ticketDelete")){
            if(Util.getInstance().isTicketSupportTeam(event.getMember())) {
                Util.getInstance().deleteTicket(event);
            }else{
                Util.getInstance().sendTicketNoPermsButton(event);
            }
        }else if(event.getComponentId().equals("ticketTranscript")){
            if(Util.getInstance().isTicketSupportTeam(event.getMember())) {
                try {
                    Util.getInstance().loadTranscript(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Util.getInstance().sendTicketNoPermsButton(event);
            }
        }else if(event.getComponentId().equals("createReport")){
            TextInput name = TextInput.create("username", "Name of user", TextInputStyle.SHORT)
                    .setPlaceholder("Enter the name of the user you report")
                    .setMinLength(1)
                    .setMaxLength(20) // or setRequiredRange(10, 100)
                    .build();

            TextInput reason = TextInput.create("reason", "Reason", TextInputStyle.SHORT)
                    .setPlaceholder("Why are you reporting this user?")
                    .setMinLength(5)
                    .setMaxLength(20)
                    .build();

            TextInput punishment = TextInput.create("punishment", "Type of punishment", TextInputStyle.SHORT)
                    .setPlaceholder("What punishment did you decide?")
                    .setMinLength(4)
                    .setMaxLength(20)
                    .build();

            Modal modal = Modal.create("createReportModal", "Create report")
                    .addActionRows(ActionRow.of(name), ActionRow.of(reason), ActionRow.of(punishment))
                    .build();

            event.replyModal(modal).queue();
        }else if(event.getComponentId().equals("approveReport")){
            if(event.getMember().hasPermission(Permission.MANAGE_SERVER)){
                event.getMessage().getActionRows().clear();
                EmbedBuilder eb = new EmbedBuilder();
                MessageEmbed me = event.getMessage().getEmbeds().get(0);
                eb.setTitle(me.getTitle());
                eb.setDescription(me.getDescription());
                me.getFields().forEach(eb::addField);
                eb.setFooter("Approved :)");
                eb.setColor(Color.GREEN);
                event.getMessage().editMessageEmbeds(eb.build()).queue();
                event.reply("You approved the report :)").setEphemeral(true).queue();
            }else{
                event.reply("You dont have the required permission :(").setEphemeral(true).queue();
            }
        }else if(event.getComponentId().equals("denyReport")){
            if(event.getMember().hasPermission(Permission.MANAGE_SERVER)){
                event.getMessage().getActionRows().clear();
                EmbedBuilder eb = new EmbedBuilder();
                MessageEmbed me = event.getMessage().getEmbeds().get(0);
                eb.setTitle(me.getTitle());
                eb.setDescription(me.getDescription());
                me.getFields().forEach(eb::addField);
                eb.setFooter("Denied :(");
                eb.setColor(Color.RED);
                event.getMessage().editMessageEmbeds(eb.build()).queue();
                event.reply("You denied the report :)").setEphemeral(true).queue();
            }else{
                event.reply("You dont have the required permission :(").setEphemeral(true).queue();
            }
        }else if(event.getComponentId().equals("deleteReport")){
            if(event.getMember().hasPermission(Permission.MANAGE_SERVER)){
                event.getMessage().delete().queue();
                event.reply("You have deleted the report :)").setEphemeral(true).queue();
            }else{
                event.reply("You dont have the required permission :(").setEphemeral(true).queue();
            }
        }
    }
    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals("createReportModal")) {
            String username = event.getValue("username").getAsString();
            String reason = event.getValue("reason").getAsString();
            String punishment = event.getValue("punishment").getAsString();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Report by " + event.getMember().getEffectiveName());
            eb.setDescription("Reported at " + Util.getInstance().formatTranscriptMessageTime(event.getTimeCreated()));
            eb.setColor(Color.YELLOW);
            eb.addField("User", username, true);
            eb.addField("Reason", reason, true);
            eb.addField("Punishment", punishment, true);
            eb.setFooter("Not approved yet");

            event.replyEmbeds(eb.build()).addActionRow(
                    Button.success("approveReport", "Approve"),
                    Button.danger("denyReport", "Deny"),
                    Button.secondary("deleteReport", "Delete")
            ).queue();
        }
    }

}
