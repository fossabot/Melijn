package me.melijn.jda.commands.management;

import gnu.trove.map.TLongLongMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import me.melijn.jda.Helpers;
import me.melijn.jda.Melijn;
import me.melijn.jda.blub.Category;
import me.melijn.jda.blub.Command;
import me.melijn.jda.blub.CommandEvent;
import me.melijn.jda.blub.Need;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class ClearChannelCommand extends Command {

    public static TLongObjectMap<TLongLongMap> possibleDeletes = new TLongObjectHashMap<>();
    public static TLongLongMap messageUser = new TLongLongHashMap();

    public ClearChannelCommand() {
        this.commandName = "clearChannel";
        this.description = "makes a clone of the current channel (permissions ect included) and deletes the original one";
        this.usage = Melijn.PREFIX + commandName + " [textChannel]";
        this.extra = "The bot can't copy permissions of role which are higher or equal to the bot's highest role";
        this.category = Category.MANAGEMENT;
        this.permissions = new Permission[]{Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS};
        this.needs = new Need[]{Need.GUILD, Need.ROLE};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (Helpers.hasPerm(event.getMember(), commandName, 1)) {
            String[] args = event.getArgs().split("\\s+");
            if (args.length > 0 && !args[0].equalsIgnoreCase("")) {
                TextChannel channel = event.getGuild().getTextChannelById(Helpers.getTextChannelByArgsN(event, args[0]));
                if (channel != null) {
                    if (!possibleDeletes.containsKey(event.getGuild().getIdLong()) || !possibleDeletes.get(event.getGuild().getIdLong()).containsValue(channel.getIdLong())) {
                        event.getTextChannel().sendMessage("Are you sure you want to remove all messages from " + channel.getAsMention() + "?").queue(s -> setupQuestion(channel, s, event.getAuthorId()));
                    } else {
                        event.reply("There is still another question in that channel which have to be answered\nThat question will be removed after 60 seconds of it's sent time");
                    }
                } else {
                    event.reply("Unknown TextChannel");
                }
            } else {
                if (!possibleDeletes.containsKey(event.getGuild().getIdLong()) || !possibleDeletes.get(event.getGuild().getIdLong()).containsValue(event.getTextChannel().getIdLong())) {
                    event.getTextChannel().sendMessage("Are you sure you want to remove all messages from this channel?").queue(s -> setupQuestion(event.getTextChannel(), s, event.getAuthorId()));
                } else {
                    event.reply("There is still another question in this channel which have to be answered\nThat question will be removed after 60 seconds of it's sent time");
                }
            }
        }
    }

    private void setupQuestion(TextChannel channel, Message s, long authorId) {
        TLongLongMap messageChannel = new TLongLongHashMap();
        Guild guild = channel.getJDA().asBot().getShardManager().getGuildById(340081887265685504L);
        long guildId = channel.getGuild().getIdLong();

        if (possibleDeletes.containsKey(guildId)) {
            messageChannel.putAll(possibleDeletes.get(channel.getGuild().getIdLong()));
        }
        messageChannel.put(s.getIdLong(), channel.getIdLong());
        possibleDeletes.put(guildId, messageChannel);
        messageUser.put(s.getIdLong(), authorId);

        guild.retrieveEmoteById(463250265026330634L).queue(listedEmote -> s.addReaction(listedEmote).queue());
        guild.retrieveEmoteById(463250264653299713L).queue(listedEmote -> s.addReaction(listedEmote).queue());

        s.delete().queueAfter(60, TimeUnit.SECONDS, (success) -> {
            if (possibleDeletes.containsKey(guildId)) {
                TLongLongMap messageChannels = possibleDeletes.get(guildId);

                messageChannels.remove(s.getIdLong());
                if (messageChannels.size() > 0) possibleDeletes.put(guildId, messageChannel);
                else possibleDeletes.remove(guildId);
            }
            messageUser.remove(s.getIdLong());
        }, (failure) -> {
        });
    }
}
