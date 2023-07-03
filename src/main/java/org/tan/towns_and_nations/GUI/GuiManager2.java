package org.tan.towns_and_nations.GUI;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.utils.ChatUtils;
import org.tan.towns_and_nations.utils.HeadUtils;
import org.tan.towns_and_nations.storage.PlayerChatListenerStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;
import static org.tan.towns_and_nations.storage.TownDataStorage.getTownList;

import java.util.*;



public class GuiManager2 {

    //done
    public static void OpenMainMenu(Player player){

        if(PlayerStatStorage.getStat(player) == null){
            PlayerStatStorage.createPlayerDataClass(player);
        }

        String name = "Main menu";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        ItemStack KingdomHead = HeadUtils.makeSkull("Kingdom","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY5MTk2YjMzMGM2Yjg5NjJmMjNhZDU2MjdmYjZlY2NlNDcyZWFmNWM5ZDQ0Zjc5MWY2NzA5YzdkMGY0ZGVjZSJ9fX0=");
        ItemStack RegionHead = HeadUtils.makeSkull("Region","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=");
        ItemStack TownHead = HeadUtils.makeSkull("Town","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=");
        ItemStack PlayerHead = HeadUtils.getPlayerHead("Profil",player);
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, "Quit", null);


        GuiItem Kingdom = ItemBuilder.from(KingdomHead).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage("En developpement");
        });
        GuiItem Region = ItemBuilder.from(RegionHead).asGuiItem(event -> {
            event.setCancelled(true);
            player.sendMessage("En developpement");
        });
        GuiItem Town = ItemBuilder.from(TownHead).asGuiItem(event -> {
            event.setCancelled(true);
            if(Objects.requireNonNull(PlayerStatStorage.getStat(player)).haveTown()){
                OpenTownMenuHaveTown(player);
            }
            else{
                openTownMenuNoTown(player);
            }
        });
        GuiItem Player = ItemBuilder.from(PlayerHead).asGuiItem(event -> {
            event.setCancelled(true);
            openProfileMenu(player);
        });
        GuiItem Back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            player.closeInventory();
        });

        gui.setItem(10,Kingdom);
        gui.setItem(12,Region);
        gui.setItem(14,Town);
        gui.setItem(16,Player);
        gui.setItem(18,Back);

        gui.open(player);
    }
    //Done
    public static void openProfileMenu(Player player){
        String name = "Profile";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        ItemStack PlayerHead = HeadUtils.getPlayerHead("Votre Profil",player);
        ItemStack GoldPurse = HeadUtils.getCustomLoreItem(Material.GOLD_NUGGET, "Balance","You have " + PlayerStatStorage.getStat(player.getUniqueId().toString()).getBalance() + " gold");
        ItemStack killList = HeadUtils.getCustomLoreItem(Material.IRON_SWORD, "Kills","You killed " + player.getStatistic(Statistic.MOB_KILLS) + " mobs");
        int time = player.getStatistic(Statistic.PLAY_ONE_MINUTE) /20 / 86400;
        ItemStack lastDeath = HeadUtils.getCustomLoreItem(Material.SKELETON_SKULL, "Time Alive","You survived for " + time + " days");
        ItemStack totalRpKills = HeadUtils.getCustomLoreItem(Material.SKELETON_SKULL, "Murder","You killed " + "//En developpement//" + " players");
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, "Back", null);

        GuiItem Head = ItemBuilder.from(PlayerHead).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem Gold = ItemBuilder.from(GoldPurse).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem Kill = ItemBuilder.from(killList).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem LD = ItemBuilder.from(lastDeath).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem RPkill = ItemBuilder.from(totalRpKills).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem Back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenMainMenu(player);
        });

        gui.setItem(4, Head);
        gui.setItem(10, Gold);
        gui.setItem(12, Kill);
        gui.setItem(14, LD);
        gui.setItem(16, RPkill);
        gui.setItem(18, Back);

        gui.open(player);
    }
    //Done
    public static void openTownMenuNoTown(Player player){


        PlayerDataClass playerStat = PlayerStatStorage.getStat(player);

        String name = "Town";
        int nRow = 3;
        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        ItemStack createNewLand = HeadUtils.getCustomLoreItem(Material.GRASS_BLOCK, "Create new Town","Cost: 100 gold");
        ItemStack joinLand = HeadUtils.getCustomLoreItem(Material.ANVIL, "Join a Town","Look at every public town");
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, "Back", null);

        GuiItem _create = ItemBuilder.from(createNewLand).asGuiItem(event -> {
            event.setCancelled(true);
            assert playerStat != null;
                if (playerStat.getBalance() < 100) {
                    player.sendMessage("You don't have enough money");
                } else {
                    player.sendMessage("Write the name of the town in the chat");
                    player.closeInventory();
                    PlayerChatListenerStorage.addPlayer("creationVille",player);
                }
        });

        GuiItem _join = ItemBuilder.from(joinLand).asGuiItem(event -> {
            event.setCancelled(true);
            OpenSearchTownMenu(player);
        });
        GuiItem _back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenMainMenu(player);
        });

        gui.setItem(11, _create);
        gui.setItem(15, _join);
        gui.setItem(18, _back);

        gui.open(player);
    }
    //Done
    public static void OpenSearchTownMenu(Player player) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        HashMap<String, TownDataClass> townDataStorage = getTownList();

        int i = 0;
        for (Map.Entry<String, TownDataClass> entry : townDataStorage.entrySet()) {


            TownDataClass townDataClass = entry.getValue();
            String townId = townDataClass.getTownId();
            ItemStack townIcon = HeadUtils.getTownIconWithInformations(townId);

            GuiItem _townIteration = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
                player.sendMessage("en dev");
            });

            gui.setItem(i, _townIteration);
            i++;

        }
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, "Quit", null);
        GuiItem _back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenMainMenu(player);
        });
        gui.setItem(3,1, _back);

        gui.open(player);
    }
    //Done
    public static void OpenTownMenuHaveTown(Player player) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        ItemStack TownIcon = HeadUtils.getTownIcon(PlayerStatStorage.getStat(player).getTownId());
        ItemStack GoldIcon = HeadUtils.makeSkull("Treasury","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack SkullIcon = HeadUtils.makeSkull("Members","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q0ZDQ5NmIxZGEwNzUzNmM5NGMxMzEyNGE1ODMzZWJlMGM1MzgyYzhhMzM2YWFkODQ2YzY4MWEyOGQ5MzU2MyJ9fX0=");
        ItemStack ClaimIcon = HeadUtils.makeSkull("Claims","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=");
        ItemStack RelationIcon = HeadUtils.makeSkull("Relations","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUwN2Q2ZGU2MzE4MzhlN2E3NTcyMGU1YjM4ZWYxNGQyOTY2ZmRkODQ4NmU3NWQxZjY4MTJlZDk5YmJjYTQ5OSJ9fX0=");
        ItemStack LevelIcon = HeadUtils.makeSkull("Town Level","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmJlNTI5YWI2YjJlYTdjNTBkOTE5MmQ4OWY4OThmZDdkYThhOWU3NTBkMzc4Mjk1ZGY3MzIwNWU3YTdlZWFlMCJ9fX0=");
        ItemStack SettingIcon = HeadUtils.makeSkull("Settings","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkMmNiMzg0NThkYTE3ZmI2Y2RhY2Y3ODcxNjE2MDJhMjQ5M2NiZjkzMjMzNjM2MjUzY2ZmMDdjZDg4YTljMCJ9fX0=");
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, "Back", null);

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _goldIcon = ItemBuilder.from(GoldIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownEconomics(player);
        });
        GuiItem _membersIcon = ItemBuilder.from(SkullIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMemberList(player);
        });
        GuiItem _claimIcon = ItemBuilder.from(ClaimIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownChunkMenu(player);
        });
        GuiItem _relationIcon = ItemBuilder.from(RelationIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelations(player);
        });
        GuiItem _levelIcon = ItemBuilder.from(LevelIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownLevel(player);
        });
        GuiItem _settingsIcon = ItemBuilder.from(SettingIcon).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownSettings(player);
        });
        GuiItem _backIcon = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenMainMenu(player);
        });


        gui.setItem(4, _townIcon);
        gui.setItem(10, _goldIcon);
        gui.setItem(11, _membersIcon);
        gui.setItem(12, _claimIcon);
        gui.setItem(14, _relationIcon);
        gui.setItem(15, _levelIcon);
        gui.setItem(16, _settingsIcon);
        gui.setItem(18, _backIcon);

        gui.open(player);
    }
    //Done
    public static void OpenTownMemberList(Player player) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        TownDataClass town = TownDataStorage.getTown(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId());
        ArrayList<String> players = town.getPlayerList();

        int i = 0;
        for (String playerUUID: players) {

            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate.getName(),playerIterate);

            GuiItem _playerIcon = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
            });

            gui.setItem(i, _playerIcon);
            i++;
        }
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, "Back", null);
        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });

        gui.setItem(3,1, _getBackArrow);

        gui.open(player);

    }
    //Done
    public static void OpenTownEconomics(Player player) {

        String name = "Town";
        int nRow = 4;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        TownDataClass town = TownDataStorage.getTown(player);


        ItemStack goldIcon = HeadUtils.makeSkull("Treasury","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack goldSpendingIcon = HeadUtils.makeSkull("Spendings","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack lowerTax = HeadUtils.makeSkull("Lower the tax","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=");
        ItemStack increaseTax = HeadUtils.makeSkull("Increase the tax","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        ItemStack taxInfo = HeadUtils.makeSkull("Flat tax","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk4ZGY0MmY0NzdmMjEzZmY1ZTlkN2ZhNWE0Y2M0YTY5ZjIwZDljZWYyYjkwYzRhZTRmMjliZDE3Mjg3YjUifX19");
        ItemStack taxHistory = HeadUtils.makeSkull("Tax history","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmU1OWYyZDNiOWU3ZmI5NTBlOGVkNzkyYmU0OTIwZmI3YTdhOWI5MzQ1NjllNDQ1YjJiMzUwM2ZlM2FiOTAyIn19fQ==");
        ItemStack salarySpending = HeadUtils.makeSkull("Salary history","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlhNjAwYWIwYTgzMDk3MDY1Yjk1YWUyODRmODA1OTk2MTc3NDYwOWFkYjNkYmQzYTRjYTI2OWQ0NDQwOTU1MSJ9fX0=");
        ItemStack chunkSpending = HeadUtils.makeSkull("Chunk spending history","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVjOWNjY2Y2MWE2ZTYyODRmZTliYmU2NDkxNTViZTRkOWNhOTZmNzhmZmNiMjc5Yjg0ZTE2MTc4ZGFjYjUyMiJ9fX0=");
        ItemStack workbenchSpending = HeadUtils.makeSkull("Workbench spending","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMzNjA0NTIwOGY5YjVkZGNmOGM0NDMzZTQyNGIxY2ExN2I5NGY2Yjk2MjAyZmIxZTUyNzBlZThkNTM4ODFiMSJ9fX0=");
        ItemStack donation = HeadUtils.getCustomLoreItem(Material.DIAMOND,"Make a donation","Donate money to the town to help development");
        ItemStack donationHistory = HeadUtils.getCustomLoreItem(Material.PAPER,"Donation history");
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, "Back");

        goldIcon = HeadUtils.addLore(goldIcon, ChatColor.WHITE +"Town current treasury: " + ChatColor.YELLOW + town.getBalance());
        goldSpendingIcon = HeadUtils.addLore(goldSpendingIcon, ChatColor.WHITE +"Town current spending: " + ChatColor.RED + 0);

        lowerTax = HeadUtils.addLore(lowerTax, ChatColor.WHITE +"Decrease the tax by " + ChatColor.YELLOW + "1$");
        taxInfo = HeadUtils.addLore(taxInfo, ChatColor.WHITE +"Town current tax: " + ChatColor.YELLOW + town.getTreasury().getFlatTax() + "$" + ChatColor.WHITE +" weekly");
        increaseTax = HeadUtils.addLore(increaseTax, ChatColor.WHITE +"Increase the tax by " + ChatColor.YELLOW + "1$");

        salarySpending = HeadUtils.addLore(salarySpending, ChatColor.WHITE +"Salary spending are up to: " + ChatColor.YELLOW + "0$");
        chunkSpending = HeadUtils.addLore(chunkSpending, ChatColor.WHITE +"Chunk spending are up to: " + ChatColor.YELLOW + "0$", ChatColor.WHITE +"Chunk spending cost: " + ChatColor.YELLOW + "1$" + ChatColor.WHITE + " for every "+ ChatColor.YELLOW + 100 + ChatColor.WHITE + " Chunks claimed" );
        workbenchSpending = HeadUtils.addLore(workbenchSpending, ChatColor.WHITE +"Miscellanous spendings: are not yet implemented");

        donation = HeadUtils.addLore(donation, ChatColor.WHITE +"Donate money to help it grow");
        donationHistory = HeadUtils.addLore(donationHistory, town.getTreasury().getDonationLimitedHistory(5));

        GuiItem _goldInfo = ItemBuilder.from(goldIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _goldSpendingIcon = ItemBuilder.from(goldSpendingIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _taxHistory = ItemBuilder.from(taxHistory).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _salarySpending = ItemBuilder.from(salarySpending).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _chunkSpending = ItemBuilder.from(chunkSpending).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _workbenchSpending = ItemBuilder.from(workbenchSpending).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _donation = ItemBuilder.from(donation).asGuiItem(event -> {
            player.sendMessage(ChatUtils.getTANString() + "Write the amount of money you want to give");
            PlayerChatListenerStorage.addPlayer("donation",player);
            player.closeInventory();
            event.setCancelled(true);

        });
        GuiItem _donationHistory = ItemBuilder.from(donationHistory).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _lessTax = ItemBuilder.from(lowerTax).asGuiItem(event -> {
            town.getTreasury().remove1FlatTax();
            OpenTownEconomics(player);
            event.setCancelled(true);

        });
        GuiItem _taxInfo = ItemBuilder.from(taxInfo).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownEconomics(player);
        });
        GuiItem _moreTax = ItemBuilder.from(increaseTax).asGuiItem(event -> {
            town.getTreasury().add1FlatTax();
            event.setCancelled(true);
            OpenTownEconomics(player);
        });

        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE)).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });


        gui.setItem(1,1, _decorativeGlass);
        gui.setItem(1,2, _decorativeGlass);
        gui.setItem(1,3, _decorativeGlass);
        gui.setItem(1,5, _decorativeGlass);
        gui.setItem(1,7, _decorativeGlass);
        gui.setItem(1,8, _decorativeGlass);
        gui.setItem(1,9, _decorativeGlass);

        gui.setItem(1,4, _goldInfo);
        gui.setItem(1,6, _goldSpendingIcon);

        gui.setItem(2,1, _lessTax);
        gui.setItem(2,2, _taxInfo);
        gui.setItem(2,3, _moreTax);
        gui.setItem(2,4, _taxHistory);

        gui.setItem(2,6, _salarySpending);
        gui.setItem(2,7, _chunkSpending);
        gui.setItem(2,8, _workbenchSpending);

        gui.setItem(3,2, _donation);
        gui.setItem(3,3, _donationHistory);



        gui.setItem(4,1, _getBackArrow);

        gui.open(player);

    }

    public static void OpenTownLevel(Player player){
        String name = "Town";
        int nRow = 3;
        PlayerDataClass playerStat = PlayerStatStorage.getStat(player);
        TownDataClass townData = TownDataStorage.getTown(player);
        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        ItemStack TownIcon = HeadUtils.getTownIcon(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId());
        ItemStack upgradeTownLevel = HeadUtils.getCustomLoreItem(Material.EMERALD, "Upgrade town level");
        ItemStack upgradeChunkCap = HeadUtils.makeSkull("Upgrade claim cap","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=");
        ItemStack upgradePlayerCap = HeadUtils.makeSkull("Upgrade player Cap","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I0M2IyMzE4OWRjZjEzMjZkYTQyNTNkMWQ3NTgyZWY1YWQyOWY2YzI3YjE3MWZlYjE3ZTMxZDA4NGUzYTdkIn19fQ==");

        upgradeTownLevel = HeadUtils.addLore(upgradeTownLevel,"Town's current level is : " + townData.getTownLevel().getTownLevel(),"Upgrade cost: " +townData.getTownLevel().getMoneyRequiredTownLevel());



        GuiItem _TownIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _upgradeTownLevel = ItemBuilder.from(upgradeTownLevel).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _upgradeChunkCap = ItemBuilder.from(upgradeChunkCap).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _upgradePlayerCap = ItemBuilder.from(upgradePlayerCap).asGuiItem(event -> {
            event.setCancelled(true);
        });



        gui.setItem(1,5, _TownIcon);
        gui.setItem(2,3, _upgradeTownLevel);
        gui.setItem(2,5, _upgradeChunkCap);
        gui.setItem(2,7, _upgradePlayerCap);

        gui.open(player);

    }

    //Done
    public static void OpenTownSettings(Player player) {

        String name = "Town";
        int nRow = 3;
        PlayerDataClass playerStat = PlayerStatStorage.getStat(player.getUniqueId().toString());

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        ItemStack TownIcon = HeadUtils.getTownIcon(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId());
        ItemStack leaveTown = HeadUtils.getCustomLoreItem(Material.BARRIER, "Leave Town", "Quit the town \"" + TownDataStorage.getTown(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId()).getTownName() + "\" ?");
        ItemStack deleteTown = HeadUtils.getCustomLoreItem(Material.BARRIER, "Delete Town", "Delete the town \"" + TownDataStorage.getTown(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId()).getTownName() + "\" ?");
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, "Back", null);

        GuiItem _townIcon = ItemBuilder.from(TownIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _leaveTown = ItemBuilder.from(leaveTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (TownDataStorage.getTown(playerStat.getTownId()).getUuidLeader().equals(playerStat.getUuid())) {
                player.sendMessage("You can't leave a town you are the leader, you need to disband it or give the leadership to someone else");
            } else {
                TownDataStorage.getTown(playerStat.getTownId()).removePlayer(player.getUniqueId().toString());
                playerStat.setTownId(null);
                player.sendMessage("You left the town");
                player.closeInventory();
            }
        });
        GuiItem _deleteTown = ItemBuilder.from(deleteTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (!TownDataStorage.getTown(playerStat.getTownId()).getUuidLeader().equals(playerStat.getUuid())) {
                player.sendMessage("You can't delete a town if you are not the leader");
            } else {
                TownDataStorage.removeTown(playerStat.getTownId());
                playerStat.setTownId(null);
                player.closeInventory();
                player.sendMessage("Town deleted");
            }
        });
        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });


        gui.setItem(4, _townIcon);
        gui.setItem(10, _leaveTown);
        gui.setItem(11, _deleteTown);
        gui.setItem(18, _getBackArrow);

        gui.open(player);
    }

    public static void OpenTownRelations(Player player) {

        String name = "Town";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        ItemStack warCategory = HeadUtils.getCustomLoreItem(Material.IRON_SWORD,"War","Manage town you are at war with");
        ItemStack EmbargoCategory = HeadUtils.getCustomLoreItem(Material.BARRIER,"Embargo","Manage town you are at war with");
        ItemStack NAPCategory = HeadUtils.getCustomLoreItem(Material.WRITABLE_BOOK,"Non-aggression pact","Manage town you are at war with");
        ItemStack AllianceCategory = HeadUtils.getCustomLoreItem(Material.CAMPFIRE,"Alliance","Manage town you are allied with");

        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, "Back", null);

        GuiItem _warCategory = ItemBuilder.from(warCategory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,"war");
        });
        GuiItem _EmbargoCategory = ItemBuilder.from(EmbargoCategory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,"embargo");

        });
        GuiItem _NAPCategory = ItemBuilder.from(NAPCategory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,"nap");

        });
        GuiItem _AllianceCategory = ItemBuilder.from(AllianceCategory).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,"alliance");
        });
        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });
        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).asGuiItem(event -> {
            event.setCancelled(true);
        });
        gui.setItem(0, _decorativeGlass);
        gui.setItem(1, _decorativeGlass);
        gui.setItem(2, _decorativeGlass);
        gui.setItem(3, _decorativeGlass);
        gui.setItem(4, _decorativeGlass);
        gui.setItem(5, _decorativeGlass);
        gui.setItem(6, _decorativeGlass);
        gui.setItem(7, _decorativeGlass);
        gui.setItem(8, _decorativeGlass);


        gui.setItem(10, _warCategory);
        gui.setItem(12, _EmbargoCategory);
        gui.setItem(14, _NAPCategory);
        gui.setItem(16, _AllianceCategory);

        gui.setItem(18, _getBackArrow);

        gui.setItem(19, _decorativeGlass);
        gui.setItem(20, _decorativeGlass);
        gui.setItem(21, _decorativeGlass);
        gui.setItem(22, _decorativeGlass);
        gui.setItem(23, _decorativeGlass);
        gui.setItem(24, _decorativeGlass);
        gui.setItem(25, _decorativeGlass);
        gui.setItem(26, _decorativeGlass);

        gui.open(player);
    }

    public static void OpenTownRelation(Player player, String relation) {

        String name = "Town - Relation";
        int nRow = 4;
        PlayerDataClass playerStat = PlayerStatStorage.getStat(player.getUniqueId().toString());

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        TownDataClass playerTown = TownDataStorage.getTown(playerStat.getTownId());

        ArrayList<String> TownListUUID = playerTown.getRelations().getOne(relation);
        int i = 0;
        for(String townUUID : TownListUUID){
            ItemStack townIcon = HeadUtils.getTownIconWithInformations(townUUID);

            GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
            });
            gui.setItem(i, _town);

            i = i+1;
        }


        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, "Back", null);

        ItemStack addTownButton = HeadUtils.makeSkull("add town","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        ItemStack removeTownButton = HeadUtils.makeSkull("remove town","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=");

        ItemStack nextPageButton = HeadUtils.makeSkull("next page","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0=");
        ItemStack previousPageButton = HeadUtils.makeSkull("previous page","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19");

        GuiItem _back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelations(player);
        });
        GuiItem _add = ItemBuilder.from(addTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelationModification(player,"add",relation);
        });
        GuiItem _remove = ItemBuilder.from(removeTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelationModification(player,"remove",relation);
        });
        GuiItem _next = ItemBuilder.from(nextPageButton).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _previous = ItemBuilder.from(previousPageButton).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.WHITE_STAINED_GLASS_PANE)).asGuiItem(event -> {
            event.setCancelled(true);
        });

        gui.setItem(4,1, _back);
        gui.setItem(4,4,_add);
        gui.setItem(4,5,_remove);

        gui.setItem(4,7,_next);
        gui.setItem(4,8,_previous);


        gui.setItem(4,2, _decorativeGlass);
        gui.setItem(4,3, _decorativeGlass);
        gui.setItem(4,6, _decorativeGlass);
        gui.setItem(4,9, _decorativeGlass);

        gui.open(player);
    }

    public static void OpenTownRelationModification(Player player, String action, String relation) {

        String name = "Town - Relation";
        int nRow = 4;
        PlayerDataClass playerStat = PlayerStatStorage.getStat(player.getUniqueId().toString());

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();


        TownDataClass playerTown = TownDataStorage.getTown(PlayerStatStorage.getStat(player.getUniqueId().toString()).getTownId());

        LinkedHashMap<String, TownDataClass> allTown = getTownList();
        ArrayList<String> TownListUUID = playerTown.getRelations().getOne(relation);
        GuiItem _decorativeGlass = ItemBuilder.from(new ItemStack(Material.WHITE_STAINED_GLASS)).asGuiItem(event -> {
            event.setCancelled(true);
        });
        if(action.equals("add")){
            List<String> townNoRelation = new ArrayList<>(allTown.keySet());
            townNoRelation.removeAll(TownListUUID);
            townNoRelation.remove(playerTown.getTownId());
            int i = 0;
            for(String townUUID : townNoRelation){
                ItemStack townIcon = HeadUtils.getTownIconWithInformations(townUUID);

                GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);
                    String message = "Relation modifiée avec : " + townIcon.getItemMeta().getDisplayName();
                    player.sendMessage(message);
                    TownDataStorage.getTown(playerTown.getTownId()).addTownRelations(relation,townUUID);
                    OpenTownRelation(player,relation);
                });
                gui.setItem(i, _town);
                i = i+1;
                _decorativeGlass = ItemBuilder.from(new ItemStack(Material.GREEN_STAINED_GLASS_PANE)).asGuiItem(event -> {
                    event.setCancelled(true);
                });
            }


        }
        else if(action.equals("remove")){
            int i = 0;
            player.sendMessage("remove");
            for(String townUUID : TownListUUID){
                ItemStack townIcon = HeadUtils.getTownIconWithInformations(townUUID);
                GuiItem _town = ItemBuilder.from(townIcon).asGuiItem(event -> {
                    event.setCancelled(true);
                    String message = "Guerre retirée à : " + townIcon.getItemMeta().getDisplayName();
                    player.sendMessage(message);
                    TownDataStorage.getTown(playerTown.getTownId()).removeTownRelations(relation,townUUID);
                    OpenTownRelation(player,relation);
                });
                gui.setItem(i, _town);
                i = i+1;
            }
            TownDataStorage.getTown(playerTown.getTownId()).removeTownRelations(relation,player.getUniqueId().toString());
            _decorativeGlass = ItemBuilder.from(new ItemStack(Material.RED_STAINED_GLASS_PANE)).asGuiItem(event -> {
                event.setCancelled(true);
            });
        }






        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, "Back", null);

        ItemStack nextPageButton = HeadUtils.makeSkull("next page","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0=");
        ItemStack previousPageButton = HeadUtils.makeSkull("previous page","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19");

        GuiItem _back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownRelation(player,relation);
        });
        GuiItem _next = ItemBuilder.from(nextPageButton).asGuiItem(event -> {
            event.setCancelled(true);
        });
        GuiItem _previous = ItemBuilder.from(previousPageButton).asGuiItem(event -> {
            event.setCancelled(true);
        });

        gui.setItem(4,1, _back);

        gui.setItem(4,7,_next);
        gui.setItem(4,8,_previous);

        gui.setItem(4,2, _decorativeGlass);
        gui.setItem(4,3, _decorativeGlass);
        gui.setItem(4,4, _decorativeGlass);
        gui.setItem(4,5, _decorativeGlass);
        gui.setItem(4,6, _decorativeGlass);
        gui.setItem(4,9, _decorativeGlass);


        gui.open(player);
    }

    public static void OpenTownChunkMenu(Player player){
        String name = "Town";
        int nRow = 3;
        PlayerDataClass playerStat = PlayerStatStorage.getStat(player.getUniqueId().toString());
        TownDataClass townClass = TownDataStorage.getTown(player);

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        ItemStack doorAccess = HeadUtils.getCustomLoreItem(Material.OAK_DOOR, "Manage doors access", "Current permission: " + townClass.getChunkSettings().getDoorAuth());
        ItemStack chestAccess = HeadUtils.getCustomLoreItem(Material.CHEST, "Manage Chest access", "Current permission: " + townClass.getChunkSettings().getChestAuth());
        ItemStack placeBlockAccess = HeadUtils.getCustomLoreItem(Material.BRICKS, "Manage building rights", "Current permission: "  + townClass.getChunkSettings().getPlaceAuth());
        ItemStack breakBlockAccess = HeadUtils.getCustomLoreItem(Material.IRON_PICKAXE, "Manage destroying rights",  "Current permission: "  + townClass.getChunkSettings().getBreakAuth());
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, "Back", null);



        GuiItem _doorAccessManager = ItemBuilder.from(doorAccess).asGuiItem(event -> {
            townClass.getChunkSettings().nextDoorAuth();
            OpenTownChunkMenu(player);
        });
        GuiItem _chestAccessManager = ItemBuilder.from(chestAccess).asGuiItem(event -> {
            townClass.getChunkSettings().nextChestAuth();
            OpenTownChunkMenu(player);
        });
        GuiItem _placeBlockAccessManager = ItemBuilder.from(placeBlockAccess).asGuiItem(event -> {
            townClass.getChunkSettings().nextPlaceAuth();
            OpenTownChunkMenu(player);
        });
        GuiItem _breakBlockAccessManager = ItemBuilder.from(breakBlockAccess).asGuiItem(event -> {
            townClass.getChunkSettings().nextBreakAuth();
            OpenTownChunkMenu(player);
        });

        GuiItem _getBackArrow = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuHaveTown(player);
        });

        gui.setItem(10, _doorAccessManager);
        gui.setItem(12, _chestAccessManager);
        gui.setItem(14, _placeBlockAccessManager);
        gui.setItem(16, _breakBlockAccessManager);

        gui.setItem(18, _getBackArrow);

        gui.open(player);
    }

}
