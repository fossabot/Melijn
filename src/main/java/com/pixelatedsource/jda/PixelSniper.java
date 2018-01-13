package com.pixelatedsource.jda;

import com.pixelatedsource.jda.blub.CommandClient;
import com.pixelatedsource.jda.blub.CommandClientBuilder;
import com.pixelatedsource.jda.commands.HelpCommand;
import com.pixelatedsource.jda.commands.animals.CatCommand;
import com.pixelatedsource.jda.commands.animals.DogCommand;
import com.pixelatedsource.jda.commands.management.PermCommand;
import com.pixelatedsource.jda.commands.management.SetPrefixCommand;
import com.pixelatedsource.jda.commands.music.*;
import com.pixelatedsource.jda.commands.util.*;
import com.pixelatedsource.jda.db.MySQL;
import com.pixelatedsource.jda.events.AddReaction;
import com.pixelatedsource.jda.events.Channels;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class PixelSniper extends ListenerAdapter {

    public static MySQL mySQL;
    private static final Config config = new Config();
    private static String OWNERID = config.getValue("ownerid");
    private static String TOKEN = config.getValue("token");
    public static String PREFIX = config.getValue("prefix");
    private static String IP = config.getValue("ipadress");
    private static String USER = config.getValue("username");
    private static String PASS = config.getValue("password");
    private static String DBNAME = config.getValue("database");
    public static HashMap<Guild, Boolean> looped = new HashMap<>();
    public static CommandClient commandClient;

    public static void main(String[] args) throws LoginException {
        CommandClientBuilder client = new CommandClientBuilder();
        client.setOwnerId(OWNERID);
        client.setPrefix(PREFIX);
        client.addCommands(
                new HelpCommand(),
                new PingCommand(),
                new PlayCommand(),
                new QueueCommand(),
                new CatCommand(),
                new SkipCommand(),
                new ClearCommand(),
                new StopCommand(),
                new ResumeCommand(),
                new VolumeCommand(),
                new AboutCommand(),
                new PlayerinfoCommand(),
                new LoopCommand(),
                new TexttoemojiCommand(),
                new SkipXCommand(),
                new PermCommand(),
                new NowPlayingCommand(),
                new RemoveCommand(),
                new GuildInfoCommand(),
                new RoleInfoCommand(),
                new DogCommand(),
                new SetPrefixCommand()
        );
        commandClient = client.build();
        new JDABuilder(AccountType.BOT)
                .setToken(TOKEN)
                .setAudioSendFactory(new NativeAudioSendFactory())
                .setGame(Game.streaming(PREFIX + "help", "https://www.twitch.tv/pixelhamster"))
                .addEventListener(commandClient)
                .addEventListener(new AddReaction())
                .addEventListener(new Channels())
                .buildAsync();
        Helpers.starttime = System.currentTimeMillis();
        mySQL = new MySQL(IP, USER, PASS, DBNAME);
    }

    public void onDisconnect(DisconnectEvent e) {
        for (Guild guild : e.getJDA().getGuilds()) {
            guild.getAudioManager().closeAudioConnection();
        }
    }
}