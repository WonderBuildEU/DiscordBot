package dev.lupluv.bot.utils;

import dev.lupluv.bot.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReactionRole {

    public static List<ReactionRole> inCreationRRs = new ArrayList<>();
    public static boolean isInCreation(Member m){
        for(ReactionRole rr : inCreationRRs){
            if(m.getId().equalsIgnoreCase(rr.creationMember.getId())){
                return true;
            }
        }
        return false;
    }

    public static void startCreation(Member m){
        ReactionRole rr = new ReactionRole();
        rr.creationMember = m;
        rr.creationStep = "messageID";
        inCreationRRs.add(rr);
    }

    public static ReactionRole getCreation(Member m){
        for (ReactionRole rr : inCreationRRs){
            if(m.getId().equalsIgnoreCase(rr.creationMember.getId())){
                return rr;
            }
        }
        return null;
    }

    public static List<ReactionRole> getAllReactionRoles(){
        FileConfiguration cfg = Bot.getFileManager().getReactionRoleConfiguration();
        List<ReactionRole> reactionRoles = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            ReactionRole rr = new ReactionRole(i);
            if(rr.existsByID()){
                rr.loadByID();
                reactionRoles.add(rr);
            }
        }
        return reactionRoles;
    }

    public boolean isInCreation;
    public Member creationMember;
    public String creationStep;

    public long id;
    public long roleID;
    public String reaction;
    public boolean reactionIsAnimated;
    public boolean removeIfUnreacted;
    public long messageID;

    public ReactionRole() {
    }

    public ReactionRole(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRoleID() {
        return roleID;
    }

    public void setRoleID(long roleID) {
        this.roleID = roleID;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public boolean isReactionIsAnimated() {
        return reactionIsAnimated;
    }

    public void setReactionIsAnimated(boolean reactionIsAnimated) {
        this.reactionIsAnimated = reactionIsAnimated;
    }

    public boolean isRemoveIfUnreacted() {
        return removeIfUnreacted;
    }

    public void setRemoveIfUnreacted(boolean removeIfUnreacted) {
        this.removeIfUnreacted = removeIfUnreacted;
    }

    public long getMessageID() {
        return messageID;
    }

    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }

    public boolean existsByID(){
        FileConfiguration cfg = Bot.getFileManager().getReactionRoleConfiguration();
        return cfg.getString("RR." + id + ".RoleID") != null;
    }

    public void createID(){
        for(int i = 0; i < 100; i++){
            ReactionRole rr = new ReactionRole(i);
            if(!rr.existsByID()){
                id = i;
                break;
            }
        }
    }

    public boolean existsByReactionAndMessage(){
        FileConfiguration cfg = Bot.getFileManager().getReactionRoleConfiguration();
        for(String key : cfg.getKeys(true)){
            if(key.endsWith("RoleID")){
                long thatID = Long.parseLong(key.replace("RR.", "").replace(".RoleID", ""));
                if(cfg.getString("RR." + thatID + ".Reaction").equalsIgnoreCase(reaction)
                        && cfg.getLong("RR." + thatID + ".MessageID") == messageID){
                    return true;
                }
            }
        }
        return false;
    }

    public void save(){
        FileConfiguration cfg = Bot.getFileManager().getReactionRoleConfiguration();
        cfg.set("RR." + id + ".RoleID", roleID);
        cfg.set("RR." + id + ".Reaction", reaction);
        cfg.set("RR." + id + ".IsAnimated", reactionIsAnimated);
        cfg.set("RR." + id + ".RemoveIfUnreacted", removeIfUnreacted);
        cfg.set("RR." + id + ".MessageID", messageID);
        try {
            cfg.save(new File("KirstensCat//reactionroles.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateMessage(){
        Guild guild = Bot.jda.getGuildById("821823858734530571");
        if(reactionIsAnimated) {
            Util.getInstance().addEmoteToExistingMessage(guild, String.valueOf(messageID), reaction);
        }
    }

    public void removeReaction(){
        Guild guild = Bot.jda.getGuildById("821823858734530571");
        if(reactionIsAnimated){
            Util.getInstance().removeEmoteFromExistingMessage(guild, String.valueOf(messageID), reaction);
        }
    }

    public void loadByID(){
        FileConfiguration cfg = Bot.getFileManager().getReactionRoleConfiguration();
        this.roleID = cfg.getLong("RR." + id + ".RoleID");
        this.reaction = cfg.getString("RR." + id + ".Reaction");
        this.reactionIsAnimated = cfg.getBoolean("RR." + id + ".IsAnimated");
        this.removeIfUnreacted = cfg.getBoolean("RR." + id + ".RemoveIfUnreacted");
        this.messageID = cfg.getLong("RR." + id + ".MessageID");
    }

    public void loadByReactionAndMessage(){
        FileConfiguration cfg = Bot.getFileManager().getReactionRoleConfiguration();
        for(String key : cfg.getKeys(true)){
            if(key.endsWith("RoleID")){
                long thatID = Long.parseLong(key.replace("RR.", "").replace(".RoleID", ""));
                if(cfg.getString("RR." + thatID + ".Reaction").equalsIgnoreCase(reaction)
                        && cfg.getLong("RR." + thatID + ".MessageID") == messageID){
                    this.id = thatID;
                    loadByID();
                }
            }
        }
    }

    public void deleteByID(){
        FileConfiguration cfg = Bot.getFileManager().getReactionRoleConfiguration();
        cfg.set("RR." + id + ".RoleID", null);
        cfg.set("RR." + id + ".Reaction", null);
        cfg.set("RR." + id + ".IsAnimated", null);
        cfg.set("RR." + id + ".RemoveIfUnreacted", null);
        cfg.set("RR." + id + ".MessageID", null);
        cfg.set("RR." + id, null);
        try {
            cfg.save(new File("KirstensCat//reactionroles.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
