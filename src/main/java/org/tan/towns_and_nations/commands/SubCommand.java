package org.tan.towns_and_nations.commands;

import org.bukkit.entity.Player;

public abstract class SubCommand {

    //name of the subcommand ex. /prank <subcommand> <-- that
    public abstract String getName();

    //ex. "This is a subcommand that let's a shark eat someone"
    public abstract String getDescription();
    public abstract int getArguments();

    //How to use command ex. /prank freeze <player>
    public abstract String getSyntax();

    //code for the subcommand
    public abstract void perform(Player player, String args[]);

}
