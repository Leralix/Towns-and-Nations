package org.leralix.tan.gui.legacy;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.MobChunkSpawnEnum;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;
import org.leralix.tan.gui.landmark.LandmarkNoOwnerMenu;
import org.leralix.tan.gui.user.territory.*;
import org.leralix.tan.gui.user.territory.hierarchy.VassalsMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.DonateToTerritory;
import org.leralix.tan.storage.MobChunkSpawnStorage;
import org.leralix.tan.storage.legacy.UpgradeStorage;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.*;
import java.util.function.Consumer;

import static org.leralix.lib.data.SoundEnum.*;

public class PlayerGUI {

    private PlayerGUI() {
        throw new IllegalStateException("Utility class");
    }

    public static void dispatchPlayerRegion(Player player) {
        RegionData regionData = RegionDataStorage.getInstance().get(player);
        if (regionData != null) {
            new RegionMenu(player, regionData);
        } else {
            new NoRegionMenu(player);
        }
    }

    public static void dispatchPlayerTown(Player player) {
        TownData townData = TownDataStorage.getInstance().get(player);
        if (townData != null) {
            new TownMenu(player, townData);
        } else {
            new NoTownMenu(player);
        }
    }

    //Landmarks, to update
    public static void openOwnedLandmark(Player player, TownData townData, int page) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_TOWN_OWNED_LANDMARK.get(tanPlayer, String.valueOf(page + 1)), 6);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        ArrayList<GuiItem> landmarkGui = new ArrayList<>();

        for (Landmark landmark : LandmarkStorage.getInstance().getLandmarkOf(townData)) {

            GuiItem landmarkButton = ItemBuilder.from(landmark.getIcon(tanPlayer.getLang())).asGuiItem(event -> event.setCancelled(true));
            landmarkGui.add(landmarkButton);
        }
        GuiUtil.createIterator(gui, landmarkGui, page, player,
                p -> new TownMenu(player, townData),
                p -> openOwnedLandmark(player, townData, page + 1),
                p -> openOwnedLandmark(player, townData, page - 1)
        );

        gui.open(player);

    }


    //Town level to rework
    public static void openTownLevel(Player player, int level) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_TOWN_UPGRADE.get(tanPlayer, String.valueOf(level + 1)), 6);

        TownData townData = TownDataStorage.getInstance().get(player);
        Level townLevel = townData.getLevel();

        ItemStack whitePanel = HeadUtils.createCustomItemStack(Material.WHITE_STAINED_GLASS_PANE, "");
        ItemStack ironBars = HeadUtils.createCustomItemStack(Material.IRON_BARS, Lang.LEVEL_LOCKED.get(tanPlayer));

        GuiItem townIcon = GuiUtil.townUpgradeResume(tanPlayer.getLang(), townData);

        GuiItem whitePanelIcon = ItemBuilder.from(whitePanel).asGuiItem(event -> event.setCancelled(true));
        GuiItem ironBarsIcon = ItemBuilder.from(ironBars).asGuiItem(event -> event.setCancelled(true));
        ItemStack greenLevelIcon = HeadUtils.createCustomItemStack(Material.GREEN_STAINED_GLASS_PANE, "");

        gui.setItem(1, 1, townIcon);
        gui.setItem(2, 1, whitePanelIcon);
        gui.setItem(3, 1, whitePanelIcon);
        gui.setItem(4, 1, whitePanelIcon);
        gui.setItem(5, 1, whitePanelIcon);
        gui.setItem(6, 2, whitePanelIcon);
        gui.setItem(6, 3, whitePanelIcon);
        gui.setItem(6, 4, whitePanelIcon);
        gui.setItem(6, 5, whitePanelIcon);
        gui.setItem(6, 6, whitePanelIcon);
        gui.setItem(6, 9, whitePanelIcon);

        GuiItem pannelIcon;
        GuiItem bottomIcon;

        for (int i = 2; i < 10; i++) {
            if (townLevel.getTownLevel() > (i - 2 + level)) {
                ItemStack fillerGreen = HeadUtils.createCustomItemStack(Material.LIME_STAINED_GLASS_PANE, "Level " + (i - 1 + level));

                pannelIcon = ItemBuilder.from(greenLevelIcon).asGuiItem(event -> event.setCancelled(true));
                bottomIcon = ItemBuilder.from(fillerGreen).asGuiItem(event -> event.setCancelled(true));
            } else if (townLevel.getTownLevel() == (i - 2 + level)) {
                pannelIcon = ironBarsIcon;
                ItemStack upgradeTownLevel = HeadUtils.createCustomItemStack(Material.ORANGE_STAINED_GLASS_PANE,
                        Lang.GUI_TOWN_LEVEL_UP.get(tanPlayer),
                        Lang.GUI_TOWN_LEVEL_UP_DESC1.get(tanPlayer, Integer.toString(townLevel.getTownLevel())),
                        Lang.GUI_TOWN_LEVEL_UP_DESC2.get(tanPlayer, Integer.toString(townLevel.getTownLevel() + 1), Integer.toString(townLevel.getMoneyRequiredForLevelUp())));

                bottomIcon = ItemBuilder.from(upgradeTownLevel).asGuiItem(event -> {
                    event.setCancelled(true);
                    townData.upgradeTown(player);
                    openTownLevel(player, level);
                });
            } else {
                pannelIcon = ironBarsIcon;
                ItemStack redLevel = HeadUtils.createCustomItemStack(Material.RED_STAINED_GLASS_PANE, "Town level " + (i + level - 1) + " locked");
                bottomIcon = ItemBuilder.from(redLevel).asGuiItem(event -> event.setCancelled(true));
            }
            gui.setItem(1, i, pannelIcon);
            gui.setItem(2, i, pannelIcon);
            gui.setItem(3, i, pannelIcon);
            gui.setItem(4, i, pannelIcon);
            gui.setItem(5, i, bottomIcon);
        }

        for (TownUpgrade townUpgrade : UpgradeStorage.getUpgrades()) {
            GuiItem guiButton = townUpgrade.createGuiItem(player, townData, level);
            if (level + 1 <= townUpgrade.getCol() && townUpgrade.getCol() <= level + 7) {
                gui.setItem(townUpgrade.getRow(), townUpgrade.getCol() + (1 - level), guiButton);
            }
        }

        ItemStack nextPageButton = HeadUtils.makeSkullB64(
                Lang.GUI_NEXT_PAGE.get(tanPlayer),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0="
        );

        ItemStack previousPageButton = HeadUtils.makeSkullB64(
                Lang.GUI_PREVIOUS_PAGE.get(tanPlayer),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19"
        );

        GuiItem previousButton = ItemBuilder.from(previousPageButton).asGuiItem(event -> {
            event.setCancelled(true);
            if (level > 0)
                openTownLevel(player, level - 1);
        });
        GuiItem nextButton = ItemBuilder.from(nextPageButton).asGuiItem(event -> {
            event.setCancelled(true);
            int townMaxLevel = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TownMaxLevel", 10);
            if (level < (townMaxLevel - 7))
                openTownLevel(player, level + 1);
        });


        gui.setItem(6, 1, GuiUtil.createBackArrow(player, p -> dispatchPlayerTown(player)));
        gui.setItem(6, 7, previousButton);
        gui.setItem(6, 8, nextButton);

        gui.open(player);

    }


    public static void openChunkGeneralSettings(Player player, TerritoryData territoryData) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_CHUNK_GENERAL_SETTINGS.get(tanPlayer), 3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));
        Map<GeneralChunkSetting, Boolean> generalSettings = territoryData.getChunkSettings().getChunkSetting();

        for (GeneralChunkSetting generalChunkSetting : GeneralChunkSetting.values()) {


            GuiItem guiItem = ItemBuilder.from(generalChunkSetting.getIcon(generalSettings.getOrDefault(generalChunkSetting, false), tanPlayer.getLang())).asGuiItem(event -> {
                event.setCancelled(true);
                if (!territoryData.doesPlayerHavePermission(player, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                    TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                    return;
                }
                generalSettings.put(generalChunkSetting, !generalSettings.get(generalChunkSetting));
                SoundUtil.playSound(player, ADD);
                openChunkGeneralSettings(player, territoryData);
            });
            gui.addItem(guiItem);
        }


        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new ChunkSettingsMenu(player, territoryData)));
        gui.open(player);
    }

    public static void openTownChunkMobSettings(Player player, int page) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_MOB_SETTINGS.get(tanPlayer, Integer.toString(page + 1)), 6);

        TownData townData = TownDataStorage.getInstance().get(player);
        ClaimedChunkSettings chunkSettings = townData.getChunkSettings();

        ArrayList<GuiItem> guiLists = new ArrayList<>();
        Collection<MobChunkSpawnEnum> mobCollection = MobChunkSpawnStorage.getMobSpawnStorage().values();

        for (MobChunkSpawnEnum mobEnum : mobCollection) {

            UpgradeStatus upgradeStatus = chunkSettings.getSpawnControl(mobEnum);

            List<String> status = new ArrayList<>();
            int cost = MobChunkSpawnStorage.getMobSpawnCost(mobEnum);
            if (upgradeStatus.isUnlocked()) {
                if (upgradeStatus.canSpawn()) {
                    status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_ACTIVATED.get(tanPlayer));
                } else {
                    status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_DEACTIVATED.get(tanPlayer));
                }
            } else {
                status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_LOCKED.get(tanPlayer));
                status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_LOCKED2.get(tanPlayer, Integer.toString(cost)));
            }
            ItemStack mobIcon = HeadUtils.makeSkullB64(mobEnum.name(), mobEnum.getTexture(), status);

            GuiItem mobItem = new GuiItem(mobIcon, event -> {
                event.setCancelled(true);
                if (!townData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_MOB_SPAWN)) {
                    TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                    return;
                }
                if (upgradeStatus.isUnlocked()) {
                    upgradeStatus.setActivated(!upgradeStatus.canSpawn());
                    SoundUtil.playSound(player, ADD);
                } else {
                    if (townData.getBalance() < cost) {
                        TanChatUtils.message(player, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(tanPlayer, townData.getColoredName(), Double.toString(cost - townData.getBalance())));
                        return;
                    }
                    townData.removeFromBalance(cost);
                    SoundUtil.playSound(player, GOOD);
                    upgradeStatus.setUnlocked(true);
                }

                openTownChunkMobSettings(player, page);

            });
            guiLists.add(mobItem);
        }

        GuiUtil.createIterator(gui, guiLists, page, player, p -> new ChunkSettingsMenu(player, townData),
                p -> openTownChunkMobSettings(player, page + 1),
                p -> openTownChunkMobSettings(player, page - 1));


        gui.open(player);
    }

    public static void openPlayerListForChunkPermission(Player player, TerritoryData territoryData, ChunkPermissionType type, int page) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(type.getLabel(tanPlayer.getLang()), 6);

        ITanPlayer playerStat = PlayerDataStorage.getInstance().get(player.getUniqueId().toString());
        List<GuiItem> guiItems = new ArrayList<>();

        for (String authorizedPlayerID : territoryData.getPermission(type).getAuthorizedPlayers()) {
            OfflinePlayer authorizedPlayer = Bukkit.getOfflinePlayer(UUID.fromString(authorizedPlayerID));
            ItemStack icon = HeadUtils.getPlayerHead(authorizedPlayer.getName(), authorizedPlayer,
                    Lang.GUI_TOWN_MEMBER_DESC3.get(tanPlayer));

            GuiItem guiItem = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);
                if (!territoryData.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                    TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                    return;
                }
                if (event.isRightClick()) {
                    territoryData.getPermission(type).removeSpecificPlayerPermission(authorizedPlayerID);
                    openPlayerListForChunkPermission(player, territoryData, type, page);
                }
            });
            guiItems.add(guiItem);
        }

        GuiUtil.createIterator(gui, guiItems, 0, player,
                p -> new TerritoryChunkSettingsMenu(player, territoryData),
                p -> openPlayerListForChunkPermission(player, territoryData, type, page + 1),
                p -> openPlayerListForChunkPermission(player, territoryData, type, page - 1));


        ItemStack addIcon = HeadUtils.makeSkullB64(Lang.GUI_GENERIC_ADD_BUTTON.get(tanPlayer), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");

        GuiItem addButton = ItemBuilder.from(addIcon).asGuiItem(event -> {
            event.setCancelled(true);
            if (!territoryData.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                return;
            }
            openAddPlayerForChunkPermission(player, territoryData, type, 0);
        });

        gui.setItem(6, 3, addButton);

        gui.open(player);
    }

    public static void openAddPlayerForChunkPermission(Player player, TerritoryData territoryData, ChunkPermissionType type, int page) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_AUTHORIZE_PLAYER.get(tanPlayer), 6);

        ITanPlayer playerStat = PlayerDataStorage.getInstance().get(player.getUniqueId().toString());

        List<GuiItem> guiItems = new ArrayList<>();

        for (Player playerToAdd : Bukkit.getOnlinePlayers()) {

            ITanPlayer playerToAddData = PlayerDataStorage.getInstance().get(playerToAdd);
            if (territoryData.getPermission(type).isAllowed(territoryData, playerToAddData))
                continue;

            ItemStack icon = HeadUtils.getPlayerHead(playerToAdd.getName(), playerToAdd,
                    Lang.GUI_GENERIC_ADD_BUTTON.get(tanPlayer));

            GuiItem guiItem = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);
                if (!territoryData.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                    TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                    return;
                }
                territoryData.getPermission(type).addSpecificPlayerPermission(playerToAdd.getUniqueId().toString());
                openPlayerListForChunkPermission(player, territoryData, type, 0);
                SoundUtil.playSound(player, ADD);

            });
            guiItems.add(guiItem);
        }

        GuiUtil.createIterator(gui, guiItems, 0, player,
                p -> territoryData.openMainMenu(player),
                p -> openAddPlayerForChunkPermission(player, territoryData, type, page + 1),
                p -> openAddPlayerForChunkPermission(player, territoryData, type, page - 1));

        gui.open(player);
    }


    public static void openAddVassal(Player player, TerritoryData territoryData, int page) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_VASSALS.get(tanPlayer, Double.toString(page + 1)), 6);

        List<GuiItem> guiItems = new ArrayList<>();

        for (TerritoryData potentialVassal : territoryData.getPotentialVassals()) {
            if (territoryData.isVassal(potentialVassal) || potentialVassal.containsVassalisationProposal(territoryData))
                continue;

            ItemStack territoryIcon = potentialVassal.getIconWithInformationAndRelation(territoryData, tanPlayer.getLang());
            HeadUtils.addLore(territoryIcon, Lang.GUI_REGION_INVITE_TOWN_DESC1.get(tanPlayer));

            GuiItem townButton = ItemBuilder.from(territoryIcon).asGuiItem(event -> {
                event.setCancelled(true);
                potentialVassal.addVassalisationProposal(territoryData);
                openAddVassal(player, territoryData, page);
            });
            guiItems.add(townButton);
        }

        GuiUtil.createIterator(gui, guiItems, page, player, p -> new VassalsMenu(player, territoryData),
                p -> openAddVassal(player, territoryData, page + 1),
                p -> openAddVassal(player, territoryData, page - 1));

        gui.open(player);
    }

    public static void openRegionChangeOwnership(Player player, int page) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_CHANGE_OWNERSHIP.get(tanPlayer), 6);
        RegionData regionData = tanPlayer.getRegion();

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (String playerID : regionData.getPlayerIDList()) {

            ITanPlayer iteratetanPlayer = PlayerDataStorage.getInstance().get(playerID);
            ItemStack switchPlayerIcon = HeadUtils.getPlayerHead(Bukkit.getOfflinePlayer(UUID.fromString(playerID)));

            GuiItem switchPlayerButton = ItemBuilder.from(switchPlayerIcon).asGuiItem(event -> {
                event.setCancelled(true);

                openConfirmMenu(player, Lang.GUI_CONFIRM_CHANGE_LEADER.get(tanPlayer, iteratetanPlayer.getNameStored()), confirm -> {
                    FileUtil.addLineToHistory(Lang.HISTORY_REGION_CAPITAL_CHANGED.get(player.getName(), regionData.getCapital().getName(), tanPlayer.getTown().getName()));
                    regionData.setLeaderID(iteratetanPlayer.getID());

                    regionData.broadcastMessageWithSound(Lang.GUI_REGION_SETTINGS_REGION_CHANGE_LEADER_BROADCAST.get(iteratetanPlayer.getNameStored()), GOOD);

                    if (!regionData.getCapital().getID().equals(iteratetanPlayer.getTown().getID())) {
                        regionData.broadCastMessage(Lang.GUI_REGION_SETTINGS_REGION_CHANGE_CAPITAL_BROADCAST.get(iteratetanPlayer.getTown().getName()));
                        regionData.setCapital(iteratetanPlayer.getTownId());
                    }
                    new RegionSettingsMenu(player, regionData);
                }, remove -> openRegionChangeOwnership(player, page));
            });
            guiItems.add(switchPlayerButton);
        }

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> new RegionSettingsMenu(player, regionData),
                p -> openRegionChangeOwnership(player, page + 1),
                p -> openRegionChangeOwnership(player, page - 1));

        gui.open(player);
    }

    public static void dispatchLandmarkGui(Player player, Landmark landmark) {

        TownData townData = TownDataStorage.getInstance().get(player);
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        if (!landmark.isOwned()) {
            new LandmarkNoOwnerMenu(player, landmark);
            return;
        }
        if (landmark.isOwnedBy(townData)) {
            openPlayerOwnLandmark(player, landmark);
            return;
        }
        TownData owner = TownDataStorage.getInstance().get(landmark.getOwnerID());
        TanChatUtils.message(player, Lang.LANDMARK_ALREADY_CLAIMED.get(tanPlayer, owner.getName()), MINOR_BAD);
    }

    private static void openPlayerOwnLandmark(Player player, Landmark landmark) {
        TownData townData = TownDataStorage.getInstance().get(landmark.getOwnerID());
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_LANDMARK_CLAIMED.get(tanPlayer, townData.getName()), 3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        int quantity = landmark.computeStoredReward(townData);

        ItemStack removeTown = HeadUtils.makeSkullB64(
                Lang.GUI_REMOVE_LANDMARK.get(tanPlayer),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0="
        );

        String bagTexture;
        if (quantity == 0)
            bagTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjRjMTY0YmFjMjE4NGE3NmExZWU5NjkxMzI0MmUzMzVmMWQ0MTFjYWZmNTEyMDVlYTM5YjIwNWU2ZjhmMDU4YSJ9fX0=";
        else
            bagTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTliOTA2YjIxNTVmMTkzNzg3MDQyMzM4ZDA1Zjg0MDM5MWMwNWE2ZDNlODE2MjM5MDFiMjk2YmVlM2ZmZGQyIn19fQ==";

        ItemStack collectRessources = HeadUtils.makeSkullB64(
                Lang.GUI_COLLECT_LANDMARK.get(tanPlayer),
                bagTexture,
                Lang.GUI_COLLECT_LANDMARK_DESC1.get(tanPlayer),
                Lang.GUI_COLLECT_LANDMARK_DESC2.get(tanPlayer, Integer.toString(quantity))
        );


        GuiItem removeTownButton = ItemBuilder.from(removeTown).asGuiItem(event -> {
            event.setCancelled(true);
            landmark.removeOwnership();
            dispatchLandmarkGui(player, landmark);
        });

        GuiItem collectRessourcesButton = ItemBuilder.from(collectRessources).asGuiItem(event -> {
            event.setCancelled(true);
            landmark.giveToPlayer(player, quantity);
            TanChatUtils.message(player, Lang.GUI_LANDMARK_REWARD_COLLECTED.get(tanPlayer, Integer.toString(quantity)), GOOD);
            dispatchLandmarkGui(player, landmark);
        });


        ItemStack panel = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        GuiItem panelIcon = ItemBuilder.from(panel).asGuiItem(event -> event.setCancelled(true));


        GuiItem landmarkIcon = ItemBuilder.from(landmark.getIcon(tanPlayer.getLang())).asGuiItem(event -> event.setCancelled(true));

        gui.getFiller().fillTop(panelIcon);
        gui.getFiller().fillBottom(panelIcon);

        gui.setItem(1, 5, landmarkIcon);

        gui.setItem(2, 1, panelIcon);
        gui.setItem(2, 6, collectRessourcesButton);
        gui.setItem(2, 8, removeTownButton);
        gui.setItem(2, 9, panelIcon);

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, Player::closeInventory));
        gui.open(player);
    }

    public static void openConfirmMenu(Player player, String confirmLore, Consumer<Void> confirmAction, Consumer<Void> returnAction) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_CONFIRMATION.get(tanPlayer), 3);

        ItemStack confirm = HeadUtils.makeSkullB64(Lang.GENERIC_CONFIRM_ACTION.get(tanPlayer), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=",
                confirmLore);

        ItemStack cancel = HeadUtils.makeSkullB64(Lang.GENERIC_CANCEL_ACTION.get(tanPlayer), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==",
                Lang.GENERIC_CANCEL_ACTION_DESC1.get(tanPlayer));

        GuiItem confirmButton = ItemBuilder.from(confirm).asGuiItem(event -> {
            event.setCancelled(true);
            confirmAction.accept(null);
        });

        GuiItem cancelButton = ItemBuilder.from(cancel).asGuiItem(event -> {
            event.setCancelled(true);
            returnAction.accept(null);
        });

        gui.setItem(2, 4, confirmButton);
        gui.setItem(2, 6, cancelButton);

        gui.open(player);
    }

    public static void openHierarchyMenu(Player player, TerritoryData territoryData) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_HIERARCHY.get(tanPlayer), 3);

        GuiItem decorativeGlass = GuiUtil.getUnnamedItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE);

        GuiItem overlordInfo;
        if (territoryData.canHaveOverlord()) {
            GuiItem overlordButton;
            Optional<TerritoryData> overlordOptional = territoryData.getOverlord();
            if (overlordOptional.isPresent()) {
                TerritoryData overlord = overlordOptional.get();
                ItemStack overlordIcon = overlord.getIcon();
                ItemMeta meta = overlordIcon.getItemMeta();
                meta.setDisplayName(Lang.OVERLORD_GUI.get(tanPlayer));
                List<String> lore = new ArrayList<>();
                lore.add(Lang.GUI_OVERLORD_INFO.get(tanPlayer, overlord.getName()));
                meta.setLore(lore);
                overlordIcon.setItemMeta(meta);

                ItemStack declareIndependence = HeadUtils.createCustomItemStack(Material.SPRUCE_DOOR,
                        Lang.GUI_OVERLORD_DECLARE_INDEPENDENCE.get(tanPlayer),
                        Lang.GUI_OVERLORD_DECLARE_INDEPENDENCE_DESC1.get(tanPlayer)
                );
                ItemStack donateToOverlord = HeadUtils.createCustomItemStack(Material.DIAMOND,
                        Lang.GUI_OVERLORD_DONATE.get(tanPlayer),
                        Lang.GUI_OVERLORD_DONATE_DESC1.get(tanPlayer)
                );
                overlordInfo = ItemBuilder.from(overlordIcon).asGuiItem(event -> event.setCancelled(true));
                overlordButton = ItemBuilder.from(declareIndependence).asGuiItem(event -> {
                    event.setCancelled(true);
                    if (!territoryData.haveOverlord()) {
                        TanChatUtils.message(player, Lang.TERRITORY_NO_OVERLORD.get(tanPlayer));
                        openHierarchyMenu(player, territoryData); //This should trigger only if town have been kicked from region during the menu
                        return;
                    }

                    if (territoryData.isCapital()) {
                        TanChatUtils.message(player, Lang.CANNOT_DECLARE_INDEPENDENCE_BECAUSE_CAPITAL.get(tanPlayer, territoryData.getBaseColoredName()));
                        return;
                    }

                    openConfirmMenu(player, Lang.GUI_CONFIRM_DECLARE_INDEPENDENCE.get(tanPlayer, territoryData.getBaseColoredName(), overlord.getBaseColoredName()), confirm -> {
                        territoryData.removeOverlord();
                        territoryData.broadcastMessageWithSound(Lang.TOWN_BROADCAST_TOWN_LEFT_REGION.get(territoryData.getName(), overlord.getName()), BAD);
                        overlord.broadCastMessage(Lang.REGION_BROADCAST_TOWN_LEFT_REGION.get(territoryData.getName()));

                        player.closeInventory();
                    }, remove -> openHierarchyMenu(player, territoryData));
                });
                GuiItem donateToOverlordButton = ItemBuilder.from(donateToOverlord).asGuiItem(event -> {
                    event.setCancelled(true);
                    TanChatUtils.message(player, Lang.WRITE_IN_CHAT_AMOUNT_OF_MONEY_FOR_DONATION.get(tanPlayer));
                    PlayerChatListenerStorage.register(player, new DonateToTerritory(overlord));
                });
                gui.setItem(2, 3, donateToOverlordButton);
            } else {
                ItemStack noCurrentOverlord = HeadUtils.createCustomItemStack(Material.GOLDEN_HELMET, Lang.OVERLORD_GUI.get(tanPlayer),
                        Lang.NO_OVERLORD.get(tanPlayer));
                overlordInfo = ItemBuilder.from(noCurrentOverlord).asGuiItem(event -> event.setCancelled(true));

                ItemStack joinOverlord = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK, Lang.BROWSE_OVERLORD_INVITATION.get(tanPlayer),
                        Lang.BROWSE_OVERLORD_INVITATION_DESC1.get(tanPlayer, Integer.toString(territoryData.getNumberOfVassalisationProposals())));

                overlordButton = ItemBuilder.from(joinOverlord).asGuiItem(event -> {
                    event.setCancelled(true);
                    openChooseOverlordMenu(player, territoryData, 0);
                });
            }
            gui.setItem(2, 2, overlordButton);
        } else {
            ItemStack noOverlordItem = HeadUtils.createCustomItemStack(Material.IRON_BARS, Lang.OVERLORD_GUI.get(tanPlayer),
                    Lang.CANNOT_HAVE_OVERLORD.get(tanPlayer));
            overlordInfo = ItemBuilder.from(noOverlordItem).asGuiItem(event -> event.setCancelled(true));

            gui.setItem(2, 2, overlordInfo);
            gui.setItem(2, 3, overlordInfo);
            gui.setItem(2, 4, overlordInfo);

        }
        gui.setItem(1, 3, overlordInfo);

        GuiItem vassalInfo;
        if (territoryData.canHaveVassals()) {
            ItemStack vassalIcon = HeadUtils.createCustomItemStack(Material.GOLDEN_SWORD, Lang.VASSAL_GUI.get(tanPlayer),
                    Lang.VASSAL_GUI_DESC1.get(tanPlayer, territoryData.getBaseColoredName(), Integer.toString(territoryData.getVassalCount())));

            ItemStack vassals = HeadUtils.makeSkullB64(Lang.GUI_REGION_TOWN_LIST.get(tanPlayer), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
                    Lang.GUI_REGION_TOWN_LIST_DESC1.get(tanPlayer));
            GuiItem vassalsButton = ItemBuilder.from(vassals).asGuiItem(event -> {
                event.setCancelled(true);
                new VassalsMenu(player, territoryData);
            });
            vassalInfo = ItemBuilder.from(vassalIcon).asGuiItem(event -> event.setCancelled(true));
            gui.setItem(2, 6, vassalsButton);
        } else {
            ItemStack noVassalsIcon = HeadUtils.createCustomItemStack(Material.IRON_BARS, Lang.VASSAL_GUI.get(tanPlayer),
                    Lang.CANNOT_HAVE_VASSAL.get(tanPlayer));
            vassalInfo = ItemBuilder.from(noVassalsIcon).asGuiItem(event -> event.setCancelled(true));
            gui.setItem(2, 6, vassalInfo);
            gui.setItem(2, 7, vassalInfo);
            gui.setItem(2, 8, vassalInfo);
        }
        gui.setItem(1, 7, vassalInfo);


        gui.getFiller().fillTop(decorativeGlass);
        gui.getFiller().fillBottom(decorativeGlass);

        gui.setItem(2, 5, decorativeGlass);
        gui.setItem(2, 1, decorativeGlass);
        gui.setItem(2, 9, decorativeGlass);

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> territoryData.openMainMenu(player)));
        gui.open(player);
    }

    public static void openChooseOverlordMenu(Player player, TerritoryData territoryData, int page) {
        Gui gui = GuiUtil.createChestGui("Choose Overlord", 6);

        List<GuiItem> guiItems = territoryData.getAllSubjugationProposals(player, page);

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> openHierarchyMenu(player, territoryData),
                p -> openChooseOverlordMenu(player, territoryData, page + 1),
                p -> openChooseOverlordMenu(player, territoryData, page - 1));


        gui.open(player);
    }


}
