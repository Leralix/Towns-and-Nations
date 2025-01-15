package org.leralix.tan.commands.server;


import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;

public abstract class ServerSubCommand extends SubCommand {


    public abstract void perform(CommandSender commandSender, String[] args);



}
