package com.pixelatedsource.jda.commands.fun;

import com.pixelatedsource.jda.Helpers;
import com.pixelatedsource.jda.blub.Category;
import com.pixelatedsource.jda.blub.Command;
import com.pixelatedsource.jda.blub.CommandEvent;
import com.pixelatedsource.jda.utils.MessageHelper;
import com.pixelatedsource.jda.utils.WebUtils;
import net.dv8tion.jda.core.entities.User;

import static com.pixelatedsource.jda.PixelSniper.PREFIX;

public class WastedCommand extends Command {

    public WastedCommand() {
        this.commandName = "wasted";
        this.description = "Be wasted or make wasted";
        this.usage = PREFIX + commandName + " [user]";
        this.category = Category.FUN;
        webUtils = WebUtils.getWebUtilsInstance();
    }

    private WebUtils webUtils;

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || Helpers.hasPerm(event.getMember(), this.commandName, 0)) {
            String[] args = event.getArgs().split("\\s+");
            if (args.length == 0 || args[0].equalsIgnoreCase("")) {
                webUtils.getImage("wasted",
                        image -> MessageHelper.sendFunText("**" + event.getAuthor().getName() + "** got WASTED", image.getUrl(), event)
                );
            } else if (args.length == 1) {
                User target = Helpers.getUserByArgsN(event, args[0]);
                if (target == null) {
                    event.reply("Wind got wasted.. wait whatt!");
                } else {
                    webUtils.getImage("wasted",
                            image -> MessageHelper.sendFunText("**" + event.getAuthor().getName() + "** got wasted by **" + target.getName() + "**", image.getUrl(), event)
                    );
                }
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}