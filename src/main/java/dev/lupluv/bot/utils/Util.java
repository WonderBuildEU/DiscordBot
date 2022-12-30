package dev.lupluv.bot.utils;

import dev.lupluv.bot.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Util {

    private static Util instance;

    public static Util getInstance(){
        if(instance == null){
            instance = new Util();
        }
        return instance;
    }

    public String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return sdf.format(Calendar.getInstance().getTime());
    }

    public Message getMessageFromAnyChannel(Guild guild, String messageID){
        for(TextChannel tc : guild.getTextChannels()){
            for(Message msg : tc.getHistory().retrievePast(20).complete()){
                if(msg.getId().equalsIgnoreCase(messageID)){
                    return msg;
                }
            }
        }
        return null;
    }

    public void addEmoteToExistingMessage(Guild guild, String messageID, String emoteName){
        for(TextChannel tc : guild.getTextChannels()){
            tc.retrieveMessageById(messageID).queue((message) ->{
                message.addReaction(guild.getEmojisByName(emoteName, true).get(0)).queue();
            }, (failure) ->{
                if(failure instanceof ErrorResponseException){
                    ErrorResponseException ex = (ErrorResponseException) failure;
                    ex.getErrorResponse();
                }
            });
        }
    }

    public void removeEmoteFromExistingMessage(Guild guild, String messageID, String emoteName){
        for(TextChannel tc : guild.getTextChannels()){
            tc.retrieveMessageById(messageID).queue((message) ->{
                message.removeReaction(guild.getEmojisByName(emoteName, true).get(0)).queue();
            }, (failure) ->{
                if(failure instanceof ErrorResponseException){
                    ErrorResponseException ex = (ErrorResponseException) failure;
                    ex.getErrorResponse();
                }
            });
        }
    }

    public String getDateByOffsetDateTime(OffsetDateTime offsetDateTime){
        return offsetDateTime.getHour() + ":" + offsetDateTime.getMinute() + ":" + offsetDateTime.getSecond() + " "
                + offsetDateTime.getDayOfMonth() + "/" + offsetDateTime.getMonthValue() + "/" + offsetDateTime.getYear();
    }

    public MessageEmbed getUsageEmbed(String usage, Guild guild){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Usage: " + usage);
        builder.setFooter(guild.getName() + " " + getDate(), "https://cdn-icons-png.flaticon.com/512/5184/5184592.png");
        return builder.build();
    }

    public MessageEmbed getJoinedPrivateEmbed(User user, Guild guild){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.PINK);
        eb.setTitle("Welcome to kirstenjbx's shed");
        eb.setDescription("You have to read and accept the Rules in " + guild.getTextChannelById("835573296422060042").getAsMention());
        eb.setThumbnail(user.getAvatarUrl());
        return eb.build();
    }

    public MessageEmbed getHelpMainEmbed(User user, Guild guild){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Help - Home");
        eb.addField("Moderation", "Use **'.help mod'** to get Help with the Moderation Commands.", true);
        eb.addField("Tools", "Use **'.help tools'** to get Help with the Tools.", true);
        eb.addField("Fun", "Use **'.help fun'** to get Help with the Fun Commands.", true);
        eb.addField("Leveling", "Use **'.help leveling'** to get Help with the Leveling.", true);
        eb.addField("", "", true);
        eb.addField("", "", true);
        eb.setFooter("Use '.help <category>' for more Information", "https://cdn-icons-png.flaticon.com/512/5184/5184592.png");
        return eb.build();
    }

    public MessageEmbed getHelpModEmbed(User user, Guild guild){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Help - Moderation");
        eb.addField(".info <@member>", "Shows you a Embed with all Information about the Member.", true);
        eb.addField(".ban <@member> <reason>", "Bans the Member from the Discord Server.", true);
        eb.addField(".kick <@member> <reason>", "Kicks the Member from the Discord Server", true);
        eb.addField(".mute <@member> <reason>", "Mutes the Member, so he cant write anymore.", true);
        eb.addField(".unmute <@member>", "Unmutes the Members, so he can write again.", true);
        eb.addField(".unban <user>", "Unbans a User, so he can join again.", true);
        eb.addField(".clear <amount>", "Delete Messages from a Text Channel.", true);
        eb.addField("", "", true);
        eb.addField("", "", true);
        eb.setFooter("Use '.help' to see all Categories", "https://cdn-icons-png.flaticon.com/512/5184/5184592.png");
        return eb.build();
    }

    public MessageEmbed getHelpToolsEmbed(User user, Guild guild){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Help - Tools");
        eb.addField("Reaction Roles", "Use **'.rr'** to get Help with the Reaction Roles.", true);
        eb.addField("Ticket Tool", "Use **'.tickets'** to get Help with the Ticket Tool", true);
        eb.addField("", "", true);
        eb.addField("", "", true);
        eb.addField("", "", true);
        eb.addField("", "", true);
        eb.setFooter("Use '.help' to see all Categories", "https://cdn-icons-png.flaticon.com/512/5184/5184592.png");
        return eb.build();
    }

    public MessageEmbed getHelpReactionRolesEmbed(User user, Guild guild){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Help - Reaction Roles");
        eb.addField(".rr create", "Starts the Reaction Roles creation Process.", true);
        eb.addField(".rr list", "Shows a list with all created Reaction Roles.", true);
        eb.addField(".rr delete <id>", "Deletes a Reaction Role.", true);
        eb.setFooter("Use '.help' to see all Categories", "https://cdn-icons-png.flaticon.com/512/5184/5184592.png");
        return eb.build();
    }





    public MessageEmbed getInfoEmbed(User user, Member member, Guild guild){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setTitle("Info Card for **" + user.getName() + "**");
        eb.setThumbnail(user.getAvatarUrl());
        eb.addField("Account created", getDateByOffsetDateTime(user.getTimeCreated()), true);
        eb.addField("Joined this Server", getDateByOffsetDateTime(member.getTimeJoined()), true);
        if(member.getRoles().contains(guild.getRoleById("1013900300026908756"))) {
            eb.addField("Muted?", "Yes", true);
        }else if(!member.getRoles().contains(guild.getRoleById("1013900300026908756"))) {
            eb.addField("Muted?", "No", true);
        }
        StringBuilder sb = new StringBuilder();
        member.getRoles().forEach(role ->{
            sb.append(role.getAsMention() + "\r\n");
        });
        eb.addField("Roles", sb.toString(), true);
        return eb.build();
    }



    public void updateMutedRolePermissions(){
        Guild guild = Bot.jda.getGuildById("1013900300026908753");
        System.out.println("Starting to fetch");
        assert guild != null;
        for(TextChannel tc : guild.getTextChannels()){
            tc.getManager().putRolePermissionOverride(909203442415853688L, null, EnumSet.of(Permission.MESSAGE_SEND)).queue();
            System.out.println("Set permission for Channel " + tc.getName());
        }
    }

    public MessageEmbed getHelpTicketsEmbed(User author, Guild guild) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Help - Ticket Tool");
        eb.addField(".tickets send <channel>", "Sends the ticket creation message in the specified channel.", true);
        eb.addField(".tickets category <category>", "Sets the category, ticket will be displayed in.", true);
        eb.addField(".tickets transcripts", "Shows saved transcripts.", true);
        eb.setFooter("Use '.help' to see all Categories", "https://cdn-icons-png.flaticon.com/512/5184/5184592.png");
        return eb.build();
    }

    public void sendTicketsCreationMessage(Guild guild, TextChannel tc){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);
        eb.setTitle("Open a ticket!");
        eb.setDescription("To create a ticket click " + envelope.getFormatted() + "");
        eb.setFooter("Wonderbuild Network - Ticket Support", Bot.jda.getSelfUser().getAvatarUrl());
        tc.sendMessageEmbeds(eb.build()).addActionRow(Button.secondary("createTicket", "Create ticket")
                .withEmoji(envelope)
        ).queue();
    }

    public static Map<User, TextChannel> openedTickets = new HashMap<>();
    public static Map<User, TextChannel> closedTickets = new HashMap<>();
    public static Category ticketCategory = null;
    public static Collection<Role> allowedRoles = new ArrayList<>();

    public static Emoji envelope = Emoji.fromUnicode("U+1F4E9"),
                        lock = Emoji.fromUnicode("U+1F512"),
                        unlock = Emoji.fromUnicode("U+1F513"),
                        bookmark = Emoji.fromUnicode("U+1F4D1"),
                        no_entry = Emoji.fromUnicode("U+26D4"),
                        checkmark = Emoji.fromUnicode("U+2705"),
                        cross = Emoji.fromUnicode("U+274C");

    public boolean hasAllowedRoles(Member member){
        for(Role role : member.getRoles()){
            if(allowedRoles.contains(role)){
                return true;
            }
        }
        return false;
    }

    public void createTicket(User user, Guild guild, InteractionHook hook){
        if(!openedTickets.containsKey(user)){
            if(ticketCategory == null) {
                String ticketId = "error!";
                for (int i = 0; i < 1000; i++) {
                    String ac;
                    if (i < 10) {
                        ac = "000" + i;
                    } else if (i < 100) {
                        ac = "00" + i;
                    } else if (i < 1000) {
                        ac = "0" + i;
                    } else {
                        ac = "" + i;
                    }
                    if (guild.getTextChannelsByName("ticket-" + ac, true).isEmpty()) {
                        if (guild.getTextChannelsByName("closed-" + ac, true).isEmpty()) {
                            ticketId = ac;
                            break;
                        }
                    }
                }
                guild.createTextChannel("ticket-" + ticketId).queue((createdChannel) -> {
                    for(Role role : guild.getRoles()){
                        if(role.hasPermission(Permission.KICK_MEMBERS)) allowedRoles.add(role);
                    }
                    createdChannel.upsertPermissionOverride(guild.getPublicRole()).deny(Permission.VIEW_CHANNEL).queue();
                    for(Role roles : guild.getRoles()){
                        if(roles.hasPermission(Permission.KICK_MEMBERS)){
                            createdChannel.upsertPermissionOverride(roles).grant(Permission.VIEW_CHANNEL).queue();
                        }
                    }
                    createdChannel.upsertPermissionOverride(Objects.requireNonNull(guild.getMember(user))).grant(Permission.VIEW_CHANNEL).queue();
                    sendTicketChannelCreatedMessage(user, createdChannel);
                    hook.editOriginal("Ticket created " + createdChannel.getAsMention()).queue();
                    openedTickets.put(user, createdChannel);
                });
            }
        }else{
            hook.editOriginal("You already have an open ticket!").queue();
        }
    }

    public void sendTicketChannelCreatedMessage(User user, TextChannel tc){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription("Support will be with you shortly.\r\nTo close this ticket click the "
                + lock.getFormatted());
        eb.setFooter("kirstenjbx's shed - Ticket support", Bot.jda.getSelfUser().getAvatarUrl());
        tc.sendMessage(user.getAsMention() + " Welcome").setEmbeds(eb.build()).addActionRow(
                Button.secondary("closeTicket", "Close").withEmoji(lock)
        ).queue();
    }

    public void sendCloseConfirmation(ButtonInteractionEvent event){
        event.reply("Are you sure you would like to close this ticket?").addActionRow(
                Button.danger("closeTicketConfirm", "Close"),
                Button.secondary("closeTicketCancel", "Cancel")
        ).queue();
    }

    public void closeTicket(ButtonInteractionEvent event){
        event.getMessage().delete().queue();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription("Ticket Closed by " + event.getUser().getAsMention());
        eb.setColor(Color.YELLOW);
        EmbedBuilder eb2 = new EmbedBuilder();
        eb2.setColor(null);
        eb2.setDescription("```Support team ticket controls```");
        event.getChannel().asTextChannel().sendMessageEmbeds(eb.build()).queue();
        event.getChannel().asTextChannel().sendMessageEmbeds(eb2.build()).addActionRow(
                Button.secondary("ticketTranscript", "Transcript")
                        .withEmoji(bookmark),
                Button.secondary("ticketOpen", "Open")
                        .withEmoji(unlock),
                Button.secondary("ticketDelete", "Delete")
                        .withEmoji(no_entry)
        ).queue();
        event.getChannel().asTextChannel().getManager().setName("closed-" + event.getChannel().getName()
                .replace("ticket-", "")).queue();
        User creator = null;
        for(User user : openedTickets.keySet()){
            if(openedTickets.get(user).getId().equalsIgnoreCase(event.getChannel().getId())){
                creator = user;
            }
        }
        openedTickets.remove(creator);
        closedTickets.put(creator, event.getChannel().asTextChannel());
    }

    public void openTicket(ButtonInteractionEvent event){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription("Ticket Opened by " + event.getUser().getAsMention());
        eb.setColor(Color.GREEN);
        event.getMessage().delete().queue();
        event.getChannel().sendMessageEmbeds(eb.build()).queue();
        event.getChannel().asTextChannel().getManager().setName("ticket-" + event.getChannel().getName()
                .replace("closed-", "")).queue();
        User creator = null;
        for(User user : closedTickets.keySet()){
            if(closedTickets.get(user).getId().equalsIgnoreCase(event.getChannel().getId())){
                creator = user;
            }
        }
        closedTickets.remove(creator);
        openedTickets.put(creator, event.getChannel().asTextChannel());
    }

    public void deleteTicket(ButtonInteractionEvent event){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("Ticket will be deleted in a few seconds.");
        event.replyEmbeds(eb.build()).queue();
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
            event.getChannel().sendMessage("Something went wrong.").queue();
        }
        User creator = null;
        for(User user : closedTickets.keySet()){
            if(closedTickets.get(user).getId().equalsIgnoreCase(event.getChannel().getId())){
                creator = user;
            }
        }
        closedTickets.remove(creator);
        event.getChannel().delete().queue();
    }

    public void loadTranscript(ButtonInteractionEvent event) throws IOException {
        event.reply("Creating transcript for you. This will take a few seconds...").queue();
        UUID uid = UUID.randomUUID();
        File transcript = new File("/var/www/wonderbuild.net/transcripts/data/" + uid + ".html");
        File template = new File("/var/www/wonderbuild.net/transcripts/template.html");
        if(!transcript.exists()){
            User creator = null;
            for(User user : closedTickets.keySet()){
                if(closedTickets.get(user).getId().equalsIgnoreCase(event.getChannel().getId())){
                    creator = user;
                }
            }
            transcript.createNewFile();
            FileWriter fw = new FileWriter(transcript);
            Scanner fr = new Scanner(template);
            while (fr.hasNextLine()){
                String data = fr.nextLine();
                fw.append(data + "\n");
            }
            fr.close();
            TextChannel tc = event.getChannel().asTextChannel();
            List<Message> messages = tc.getHistory().retrievePast(100).complete();
            Collections.reverse(messages);
            fw.append("<div class='ticket-header'>Ticket by " + creator.getAsTag() + "</div>");
            for(Message message : messages){
                if(!message.getAuthor().isBot()) {
                    fw.append("<div class='parent-container'><div class='avatar-container'><img src='" + message.getAuthor().getAvatarUrl() + "' class='avatar'></div>" +
                            "<div class='message-container'><span>" + message.getAuthor().getAsTag() + " "
                            + formatTranscriptMessageTime(message.getTimeCreated()) + "</span><span>" + message.getContentRaw() + "</span></div></div>");
                }
            }
            fw.close();
            event.getHook().editOriginal("Successfully created the transcript :D").queue();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setDescription("Transcript ID: " + uid);
            eb.setColor(Color.CYAN);
            event.getChannel().sendMessageEmbeds(eb.build()).addActionRow(
                    Button.link("https://wonderbuild.net/transcripts/data/" + uid + ".html", "Open transcript")
            ).queue();
        }
    }

    public String formatTranscriptMessageTime(OffsetDateTime time){
        return time.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " "
                + time.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " "
                + time.getDayOfMonth() + " " + time.getYear() + " " + time.getHour() + ":" + time.getMinute() + ":" + time.getSecond();
    }

    public void sendTranscriptList(MessageReceivedEvent event){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("All Transcripts");
        eb.setColor(Color.YELLOW);
        eb.setDescription("Here you get all saved transcripts from the past.");
        File folder = new File("/var/www/wonderbuild.net/transcripts/data");
        for(String file : folder.list()){
            eb.addField("Transcript " + file.replace(".html", ""), "https://wonderbuild.net/transcripts/data/" + file, false);
        }
        event.getChannel().sendMessageEmbeds(eb.build()).addActionRow(
                Button.link("https://wonderbuild.net/transcripts/data", "Open in web")
        ).queue();
    }

    public boolean isTicketSupportTeam(Member member){
        return member.hasPermission(Permission.KICK_MEMBERS);
    }

    public void sendTicketNoPermsButton(ButtonInteractionEvent event){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setDescription("You are not allowed to do this " + event.getMember().getAsMention());
        event.replyEmbeds(eb.build()).queue();
    }

}
