package org.tan.towns_and_nations.commands;

import org.bukkit.entity.Player;

public abstract class SubCommand {

    public abstract String getName();

    public abstract String getDescription();
    public abstract int getArguments();

    public abstract String getSyntax();

    public abstract void perform(Player player, String args[]);

}
