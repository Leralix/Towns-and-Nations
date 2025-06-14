package org.leralix.tan.gui.legacy;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.newhistory.TransactionHistory;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.territory.cosmetic.PlayerHeadIcon;
import org.leralix.tan.dataclass.wars.CreateAttackData;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.dataclass.wars.WarRole;
import org.leralix.tan.dataclass.wars.wargoals.CaptureLandmarkWarGoal;
import org.leralix.tan.dataclass.wars.wargoals.ConquerWarGoal;
import org.leralix.tan.dataclass.wars.wargoals.LiberateWarGoal;
import org.leralix.tan.dataclass.wars.wargoals.SubjugateWarGoal;
import org.leralix.tan.enums.*;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;
import org.leralix.tan.gui.user.property.PlayerPropertyManager;
import org.leralix.tan.gui.user.territory.*;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.*;
import org.leralix.tan.storage.MobChunkSpawnStorage;
import org.leralix.tan.storage.legacy.UpgradeStorage;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.utils.*;

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

    //Property not to modify
    public static void openPlayerPropertyPlayerList(Player player, PropertyData propertyData, int page, Consumer<Player> onClose) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        int nRows = 4;
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_PLAYER_SPECIFIC_PROPERTY.get(playerData, propertyData.getName()), nRows);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        boolean canKick = propertyData.canPlayerManageInvites(playerData.getID());
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (String playerID : propertyData.getAllowedPlayersID()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));

            ItemStack playerHead = HeadUtils.getPlayerHead(offlinePlayer,
                    canKick ? Lang.GUI_TOWN_MEMBER_DESC3.get(playerData) : "");

            GuiItem headGui = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if (!canKick || event.getClick() != ClickType.RIGHT) {
                    return;
                }
                propertyData.removeAuthorizedPlayer(playerID);
                openPlayerPropertyPlayerList(player, propertyData, page, onClose);

                SoundUtil.playSound(player, MINOR_GOOD);
                player.sendMessage(Lang.PLAYER_REMOVED_FROM_PROPERTY.get(playerData, offlinePlayer.getName()));
            });
            guiItems.add(headGui);
        }
        GuiUtil.createIterator(gui, guiItems, page, player,
                onClose,
                p -> openPlayerPropertyPlayerList(player, propertyData, page + 1, onClose),
                p -> openPlayerPropertyPlayerList(player, propertyData, page - 1, onClose)
        );

        ItemStack addPlayer = HeadUtils.makeSkullB64(Lang.GUI_PROPERTY_AUTHORIZE_PLAYER.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        GuiItem addButton = ItemBuilder.from(addPlayer).asGuiItem(event -> {
            event.setCancelled(true);
            if (!propertyData.canPlayerManageInvites(playerData.getID())) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                return;
            }
            openPlayerPropertyAddPlayer(player, propertyData, 0);
        });
        gui.setItem(nRows, 4, addButton);

        gui.open(player);

    }

    private static void openPlayerPropertyAddPlayer(Player player, PropertyData propertyData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_PLAYER_SPECIFIC_PROPERTY.get(playerData, propertyData.getName()), 3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (Player playerIter : Bukkit.getOnlinePlayers()) {
            if (playerIter.getUniqueId().equals(player.getUniqueId()) || propertyData.isPlayerAuthorized(playerIter)) {
                continue;
            }

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIter);
            GuiItem headGui = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                propertyData.addAuthorizedPlayer(playerIter);
                openPlayerPropertyAddPlayer(player, propertyData, 0);
                SoundUtil.playSound(player, MINOR_GOOD);
                player.sendMessage(Lang.PLAYER_ADDED_TO_PROPERTY.get(playerData, playerIter.getName()));
            });
            guiItems.add(headGui);

        }

        GuiUtil.createIterator(gui, guiItems, 0, player,
                p -> new PlayerPropertyManager(player, propertyData, p1 -> player.closeInventory()),
                p -> openPlayerPropertyAddPlayer(player, propertyData, page + 1),
                p -> openPlayerPropertyAddPlayer(player, propertyData, page - 1)
        );

        gui.open(player);
    }

    public static void openSelectHeadTerritoryMenu(Player player, TerritoryData territoryData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_SELECT_ICON.get(playerData) + (page + 1), 6);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (String playerID : territoryData.getPlayerIDList()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));
            ItemStack playerHead = HeadUtils.getPlayerHead(offlinePlayer);
            GuiItem headGui = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                territoryData.setIcon(new PlayerHeadIcon(offlinePlayer.getUniqueId().toString()));
                SoundUtil.playSound(player, MINOR_GOOD);
                territoryData.openMainMenu(player);
            });
            guiItems.add(headGui);
        }
        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> territoryData.openMainMenu(player),
                p -> openSelectHeadTerritoryMenu(player, territoryData, page + 1),
                p -> openSelectHeadTerritoryMenu(player, territoryData, page - 1));

        gui.open(player);
    }

    //Wars that will probably be reworked
    public static void openSpecificPlannedAttackMenu(Player player, TerritoryData territory, PlannedAttack plannedAttack) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_WAR_MANAGER.get(playerData), 3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        GuiItem attackIcon = ItemBuilder.from(plannedAttack.getIcon(territory)).asGuiItem();
        gui.setItem(1, 5, attackIcon);

        ItemStack attackingSideInfo = plannedAttack.getAttackingIcon();
        GuiItem attackingSidePanel = ItemBuilder.from(attackingSideInfo).asGuiItem();
        gui.setItem(2, 2, attackingSidePanel);

        ItemStack defendingSideInfo = plannedAttack.getDefendingIcon();
        GuiItem defendingSidePanel = ItemBuilder.from(defendingSideInfo).asGuiItem();
        gui.setItem(2, 4, defendingSidePanel);


        WarRole territoryRole = plannedAttack.getTerritoryRole(territory);

        if (territoryRole == WarRole.MAIN_ATTACKER) {
            ItemStack cancelAttack = HeadUtils.createCustomItemStack(Material.BARRIER, Lang.GUI_CANCEL_ATTACK.get(playerData), Lang.GUI_GENERIC_CLICK_TO_DELETE.get(playerData));
            ItemStack renameAttack = HeadUtils.createCustomItemStack(Material.NAME_TAG, Lang.GUI_RENAME_ATTACK.get(playerData), Lang.GUI_GENERIC_CLICK_TO_RENAME.get(playerData));
            GuiItem cancelButton = ItemBuilder.from(cancelAttack).asGuiItem(event -> {
                plannedAttack.remove();
                territory.broadcastMessageWithSound(Lang.ATTACK_SUCCESSFULLY_CANCELLED.get(playerData, plannedAttack.getMainDefender().getName()), MINOR_GOOD);
                new WarMenu(player, territory);
            });

            GuiItem renameButton = ItemBuilder.from(renameAttack).asGuiItem(event -> {
                event.setCancelled(true);
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(playerData));
                PlayerChatListenerStorage.register(player, new ChangeAttackName(plannedAttack, p -> openSpecificPlannedAttackMenu(player, territory, plannedAttack)));
            });

            gui.setItem(2, 6, renameButton);
            gui.setItem(2, 8, cancelButton);
        } else if (territoryRole == WarRole.MAIN_DEFENDER) {
            ItemStack submitToRequests = HeadUtils.createCustomItemStack(Material.SOUL_LANTERN,
                    Lang.SUBMIT_TO_REQUESTS.get(playerData),
                    Lang.SUBMIT_TO_REQUEST_DESC1.get(playerData),
                    Lang.SUBMIT_TO_REQUEST_DESC2.get(playerData, plannedAttack.getWarGoal().getCurrentDesc()));

            GuiItem submitToRequestButton = ItemBuilder.from(submitToRequests).asGuiItem(event -> {
                plannedAttack.defenderSurrendered();
                new WarMenu(player, territory);
            });
            gui.setItem(2, 7, submitToRequestButton);

        } else if (territoryRole == WarRole.OTHER_ATTACKER || territoryRole == WarRole.OTHER_DEFENDER) {
            ItemStack quitWar = HeadUtils.createCustomItemStack(Material.DARK_OAK_DOOR, Lang.GUI_QUIT_WAR.get(playerData), Lang.GUI_QUIT_WAR_DESC1.get(playerData));

            GuiItem quitButton = ItemBuilder.from(quitWar).asGuiItem(event -> {
                plannedAttack.removeBelligerent(territory);
                territory.broadcastMessageWithSound(Lang.TERRITORY_NO_LONGER_INVOLVED_IN_WAR_MESSAGE.get(playerData, plannedAttack.getMainDefender().getName()), MINOR_GOOD);
                new WarMenu(player, territory);
            });
            gui.setItem(2, 7, quitButton);
        } else if (territoryRole == WarRole.NEUTRAL) {
            ItemStack joinAttacker = HeadUtils.createCustomItemStack(Material.IRON_SWORD,
                    Lang.GUI_JOIN_ATTACKING_SIDE.get(playerData),
                    Lang.GUI_JOIN_ATTACKING_SIDE_DESC1.get(playerData, territory.getBaseColoredName()),
                    Lang.GUI_WAR_GOAL_INFO.get(playerData, plannedAttack.getWarGoal().getDisplayName()));
            ItemStack joinDefender = HeadUtils.createCustomItemStack(Material.SHIELD,
                    Lang.GUI_JOIN_DEFENDING_SIDE.get(playerData),
                    Lang.GUI_JOIN_DEFENDING_SIDE_DESC1.get(playerData, territory.getBaseColoredName()));

            GuiItem joinAttackerButton = ItemBuilder.from(joinAttacker).asGuiItem(event -> {
                plannedAttack.addAttacker(territory);
                openSpecificPlannedAttackMenu(player, territory, plannedAttack);
            });

            GuiItem joinDefenderButton = ItemBuilder.from(joinDefender).asGuiItem(event -> {
                plannedAttack.addDefender(territory);
                openSpecificPlannedAttackMenu(player, territory, plannedAttack);
            });
            gui.setItem(2, 6, joinAttackerButton);
            gui.setItem(2, 8, joinDefenderButton);
        }

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new WarMenu(player, territory)));
        gui.open(player);

    }

    public static void openStartWarSettings(Player player, CreateAttackData createAttackData) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_CREATE_WAR_MANAGER.get(playerData, createAttackData.getMainDefender().getName()), 3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        TerritoryData mainAttacker = createAttackData.getMainAttacker();
        TerritoryData mainDefender = createAttackData.getMainDefender();

        ItemStack addTime = HeadUtils.makeSkullB64(Lang.GUI_ATTACK_ADD_TIME.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjMyZmZmMTYzZTIzNTYzMmY0MDQ3ZjQ4NDE1OTJkNDZmODVjYmJmZGU4OWZjM2RmNjg3NzFiZmY2OWE2NjIifX19",
                Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(playerData),
                Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get(playerData));
        ItemStack removeTIme = HeadUtils.makeSkullB64(Lang.GUI_ATTACK_REMOVE_TIME.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE1NmRhYjUzZDRlYTFhNzlhOGU1ZWQ2MzIyYzJkNTZjYjcxNGRkMzVlZGY0Nzg3NjNhZDFhODRhODMxMCJ9fX0=",
                Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(playerData),
                Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get(playerData));
        ItemStack time = HeadUtils.makeSkullB64(Lang.GUI_ATTACK_SET_TO_START_IN.get(playerData, DateUtil.getStringDeltaDateTime(createAttackData.getDeltaDateTime())), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWU5OThmM2ExNjFhNmM5ODlhNWQwYTFkYzk2OTMxYTM5OTI0OWMwODBiNjYzNjQ1ODFhYjk0NzBkZWE1ZTcyMCJ9fX0=",
                Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(playerData),
                Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get(playerData));
        ItemStack confirm = HeadUtils.makeSkullB64(Lang.GUI_CONFIRM_ATTACK.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=",
                Lang.GUI_CONFIRM_ATTACK_DESC1.get(playerData, mainDefender.getBaseColoredName()));

        if (!createAttackData.getWargoal().isCompleted()) {
            HeadUtils.addLore(confirm, Lang.GUI_WARGOAL_NOT_COMPLETED.get(playerData));
        }

        ItemStack wargoal = createAttackData.getWargoal().getIcon();


        GuiItem addTimeButton = ItemBuilder.from(addTime).asGuiItem(event -> {
            event.setCancelled(true);
            SoundUtil.playSound(player, ADD);
            if (event.isShiftClick()) {
                createAttackData.addDeltaDateTime(60 * 1200L);
            } else if (event.isLeftClick()) {
                createAttackData.addDeltaDateTime(1200);
            }
            openStartWarSettings(player, createAttackData);
        });

        GuiItem removeTimeButton = ItemBuilder.from(removeTIme).asGuiItem(event -> {
            event.setCancelled(true);
            SoundUtil.playSound(player, REMOVE);

            if (event.isShiftClick()) {
                createAttackData.addDeltaDateTime(-60 * 1200L);
            } else if (event.isLeftClick()) {
                createAttackData.addDeltaDateTime(-1200);
            }

            if (createAttackData.getDeltaDateTime() < 0)
                createAttackData.setDeltaDateTime(0);
            openStartWarSettings(player, createAttackData);
        });

        GuiItem timeInfo = ItemBuilder.from(time).asGuiItem(event -> event.setCancelled(true));

        GuiItem wargoalButton = ItemBuilder.from(wargoal).asGuiItem(event -> {
            openSelectWarGoalMenu(player, createAttackData);
            event.setCancelled(true);
        });

        GuiItem confirmButton = ItemBuilder.from(confirm).asGuiItem(event -> {
            event.setCancelled(true);

            if (!createAttackData.getWargoal().isCompleted()) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_WARGOAL_NOT_COMPLETED.get(playerData));
                return;
            }

            PlannedAttackStorage.newWar(createAttackData);
            new WarMenu(player, mainAttacker);

            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_ATTACK_TOWN_EXECUTED.get(playerData, mainDefender.getName()));
            mainAttacker.broadcastMessageWithSound(Lang.GUI_TOWN_ATTACK_TOWN_INFO.get(playerData, mainAttacker.getName(), mainDefender.getName()), WAR);
            mainDefender.broadcastMessageWithSound(Lang.GUI_TOWN_ATTACK_TOWN_INFO.get(playerData, mainAttacker.getName(), mainDefender.getName()), WAR);
        });


        gui.setItem(2, 2, removeTimeButton);
        gui.setItem(2, 3, timeInfo);
        gui.setItem(2, 4, addTimeButton);

        gui.setItem(2, 6, wargoalButton);

        gui.setItem(2, 8, confirmButton);
        gui.setItem(3, 1, GuiUtil.createBackArrow(player, e -> openSingleRelation(player, mainAttacker, TownRelation.WAR, 0)));

        createAttackData.getWargoal().addExtraOptions(gui, player, createAttackData);

        gui.open(player);

    }

    public static void openSelecteTerritoryToLiberate(Player player, CreateAttackData createAttackData, LiberateWarGoal liberateWarGoal) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_CREATE_WAR_MANAGER.get(playerData, createAttackData.getMainDefender().getName()), 6);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        TerritoryData territoryToAttack = createAttackData.getMainDefender();
        for (TerritoryData territoryData : territoryToAttack.getVassals()) {
            if (territoryData.isCapital()) {
                continue;
            }
            ItemStack territoryIcon = territoryData.getIconWithInformations(playerData.getLang());
            HeadUtils.addLore(territoryIcon, "", Lang.LEFT_CLICK_TO_SELECT.get(playerData));

            GuiItem territoryButton = ItemBuilder.from(territoryIcon).asGuiItem(event -> {
                event.setCancelled(true);
                liberateWarGoal.setTerritoryToLiberate(territoryData);
                openStartWarSettings(player, createAttackData);
            });

            gui.addItem(territoryButton);
        }

        gui.setItem(6, 1, GuiUtil.createBackArrow(player, e -> openStartWarSettings(player, createAttackData)));
        gui.open(player);
    }

    public static void openSelecteLandmarkToCapture(Player player, CreateAttackData createAttackData, CaptureLandmarkWarGoal captureLandmarkWarGoal, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_CREATE_WAR_MANAGER.get(playerData, createAttackData.getMainDefender().getName()), 6);

        TownData defendingTerritory = (TownData) createAttackData.getMainDefender();

        List<GuiItem> landmarkButtons = new ArrayList<>();
        for (Landmark ownedLandmark : defendingTerritory.getOwnedLandmarks()) {
            ItemStack landmarkIcon = ownedLandmark.getIcon();
            HeadUtils.addLore(landmarkIcon, "", Lang.LEFT_CLICK_TO_SELECT.get(playerData));

            GuiItem landmarkButton = ItemBuilder.from(landmarkIcon).asGuiItem(event -> {
                event.setCancelled(true);
                captureLandmarkWarGoal.setLandmarkToCapture(ownedLandmark);
                openStartWarSettings(player, createAttackData);
            });
            landmarkButtons.add(landmarkButton);
        }

        GuiUtil.createIterator(gui, landmarkButtons, page, player,
                p -> openStartWarSettings(player, createAttackData),
                p -> openSelecteLandmarkToCapture(player, createAttackData, captureLandmarkWarGoal, page + 1),
                p -> openSelecteLandmarkToCapture(player, createAttackData, captureLandmarkWarGoal, page - 1));

        gui.open(player);
    }

    private static void openSelectWarGoalMenu(Player player, CreateAttackData createAttackData) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_SELECT_WARGOAL.get(playerData), 3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        boolean canBeSubjugated = createAttackData.canBeSubjugated();
        boolean canBeLiberated = !(createAttackData.getMainDefender() instanceof TownData);
        boolean canCaptureLandmark = createAttackData.getMainAttacker() instanceof TownData && createAttackData.getMainDefender() instanceof TownData;

        ItemStack conquer = HeadUtils.createCustomItemStack(Material.IRON_SWORD, Lang.CONQUER_WAR_GOAL.get(playerData),
                Lang.CONQUER_WAR_GOAL_DESC.get(playerData),
                Lang.LEFT_CLICK_TO_SELECT.get(playerData));

        ItemStack captureLandmark = HeadUtils.createCustomItemStack(Material.DIAMOND,
                Lang.CAPTURE_LANDMARK_WAR_GOAL.get(playerData),
                Lang.CAPTURE_LANDMARK_WAR_GOAL_DESC.get(playerData));

        ItemStack subjugate = HeadUtils.createCustomItemStack(Material.CHAIN,
                Lang.SUBJUGATE_WAR_GOAL.get(playerData),
                Lang.GUI_WARGOAL_SUBJUGATE_WAR_GOAL_RESULT.get(playerData, createAttackData.getMainDefender().getName(), createAttackData.getMainAttacker().getName()));

        if (!canBeSubjugated)
            HeadUtils.addLore(subjugate, Lang.GUI_WARGOAL_SUBJUGATE_CANNOT_BE_USED.get(playerData));
        else
            HeadUtils.addLore(subjugate, Lang.LEFT_CLICK_TO_SELECT.get(playerData));

        ItemStack liberate = HeadUtils.createCustomItemStack(Material.LANTERN, Lang.LIBERATE_SUBJECT_WAR_GOAL.get(playerData),
                Lang.LIBERATE_SUBJECT_WAR_GOAL_DESC.get(playerData));

        if (!canBeLiberated)
            HeadUtils.addLore(liberate, Lang.GUI_WARGOAL_LIBERATE_CANNOT_BE_USED.get(playerData));
        else
            HeadUtils.addLore(liberate, Lang.LEFT_CLICK_TO_SELECT.get(playerData));


        GuiItem conquerButton = ItemBuilder.from(conquer).asGuiItem(event -> {
            event.setCancelled(true);
            createAttackData.setWarGoal(new ConquerWarGoal(createAttackData.getMainAttacker(), createAttackData.getMainDefender()));
            openStartWarSettings(player, createAttackData);
        });

        GuiItem captureLandmarkButton = ItemBuilder.from(captureLandmark).asGuiItem(event -> {
            event.setCancelled(true);
            if (!canCaptureLandmark) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_WARGOAL_CAPTURE_LANDMARK_CANNOT_BE_USED.get(playerData));
                return;
            }
            createAttackData.setWarGoal(new CaptureLandmarkWarGoal(createAttackData.getMainAttacker().getID(), createAttackData.getMainDefender().getID()));
            openStartWarSettings(player, createAttackData);
        });

        GuiItem subjugateButton = ItemBuilder.from(subjugate).asGuiItem(event -> {
            event.setCancelled(true);
            if (!canBeSubjugated) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_WARGOAL_SUBJUGATE_CANNOT_BE_USED.get(playerData));
                return;
            }
            createAttackData.setWarGoal(new SubjugateWarGoal(createAttackData));
            openStartWarSettings(player, createAttackData);
        });

        GuiItem liberateButton = ItemBuilder.from(liberate).asGuiItem(event -> {
            event.setCancelled(true);

            if (!canBeLiberated) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_WARGOAL_LIBERATE_CANNOT_BE_USED.get(playerData));
                return;
            }
            createAttackData.setWarGoal(new LiberateWarGoal());
            openStartWarSettings(player, createAttackData);
        });

        gui.setItem(2, 2, conquerButton);
        gui.setItem(2, 4, captureLandmarkButton);
        gui.setItem(2, 6, subjugateButton);
        gui.setItem(2, 8, liberateButton);

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, e -> openStartWarSettings(player, createAttackData)));

        gui.open(player);
    }


    //Landmarks, to update
    public static void openOwnedLandmark(Player player, TownData townData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_TOWN_OWNED_LANDMARK.get(playerData, page + 1), 6);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        ArrayList<GuiItem> landmarkGui = new ArrayList<>();

        for (String landmarkID : townData.getOwnedLandmarksID()) {
            Landmark landmarkData = LandmarkStorage.getInstance().get(landmarkID);

            GuiItem landmarkButton = ItemBuilder.from(landmarkData.getIcon()).asGuiItem(event -> event.setCancelled(true));
            landmarkGui.add(landmarkButton);
        }
        GuiUtil.createIterator(gui, landmarkGui, page, player,
                p -> new TownMenu(player, townData),
                p -> openOwnedLandmark(player, townData, page + 1),
                p -> openOwnedLandmark(player, townData, page - 1)
        );

        gui.open(player);

    }

    //Old history files
    public static void openTownEconomicsHistory(Player player, TerritoryData territoryData, TransactionHistoryEnum transactionHistoryEnum) {
        openTownEconomicsHistory(player, territoryData, transactionHistoryEnum, 0);
    }

    public static void openTownEconomicsHistory(Player player, TerritoryData territoryData, TransactionHistoryEnum transactionHistoryEnum, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_HISTORY.get(playerData), 6);
        List<GuiItem> guiItems = new ArrayList<>();

        for (List<TransactionHistory> transactionHistory : TownsAndNations.getPlugin().getDatabaseHandler().getTransactionHistory(territoryData, transactionHistoryEnum)) {
            ItemStack transactionIcon = HeadUtils.createCustomItemStack(Material.PAPER, ChatColor.GREEN + transactionHistory.get(0).getDate());

            for (TransactionHistory transaction : transactionHistory) {
                HeadUtils.addLore(transactionIcon, transaction.addLoreLine());
            }
            guiItems.add(ItemBuilder.from(transactionIcon).asGuiItem(event -> event.setCancelled(true)));
        }

        Collections.reverse(guiItems);//newer first

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> new TreasuryMenu(player, territoryData),
                p -> openTownEconomicsHistory(player, territoryData, transactionHistoryEnum, page + 1),
                p -> openTownEconomicsHistory(player, territoryData, transactionHistoryEnum, page - 1));

        gui.open(player);
    }

    //Town level to rework
    public static void openTownLevel(Player player, int level) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_TOWN_UPGRADE.get(playerData, level + 1), 6);

        TownData townData = TownDataStorage.getInstance().get(player);
        Level townLevel = townData.getLevel();

        ItemStack whitePanel = HeadUtils.createCustomItemStack(Material.WHITE_STAINED_GLASS_PANE, "");
        ItemStack ironBars = HeadUtils.createCustomItemStack(Material.IRON_BARS, Lang.LEVEL_LOCKED.get(playerData));

        GuiItem townIcon = GuiUtil.townUpgradeResume(townData);

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
                        Lang.GUI_TOWN_LEVEL_UP.get(playerData),
                        Lang.GUI_TOWN_LEVEL_UP_DESC1.get(playerData, townLevel.getTownLevel()),
                        Lang.GUI_TOWN_LEVEL_UP_DESC2.get(playerData, townLevel.getTownLevel() + 1, townLevel.getMoneyRequiredForLevelUp()));

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
                Lang.GUI_NEXT_PAGE.get(playerData),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0="
        );

        ItemStack previousPageButton = HeadUtils.makeSkullB64(
                Lang.GUI_PREVIOUS_PAGE.get(playerData),
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


    public static void openTownChangeOwnershipPlayerSelect(Player player, TownData townData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_CHANGE_OWNERSHIP.get(playerData), 3);

        List<GuiItem> guiItems = new ArrayList<>();
        for (String playerUUID : townData.getPlayerIDList()) {
            OfflinePlayer townPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));

            ItemStack playerHead = HeadUtils.getPlayerHead(townPlayer.getName(), townPlayer,
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.get(playerData, player.getName()),
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC2.get(playerData));


            GuiItem playerHeadIcon = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);

                openConfirmMenu(player, Lang.GUI_CONFIRM_CHANGE_TOWN_LEADER.get(playerData, townPlayer.getName()), confirm -> {

                    townData.setLeaderID(townPlayer.getUniqueId().toString());
                    player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_SUCCESS.get(playerData, townPlayer.getName()));
                    dispatchPlayerTown(player);

                    player.closeInventory();

                }, remove -> new TownSettingsMenu(player, townData));

            });
            guiItems.add(playerHeadIcon);
        }
        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> new TownSettingsMenu(player, townData),
                p -> openTownChangeOwnershipPlayerSelect(player, townData, page + 1),
                p -> openTownChangeOwnershipPlayerSelect(player, townData, page - 1));

        gui.open(player);
    }

    public static void openRelations(Player player, TerritoryData territory) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_RELATIONS.get(playerData, territory.getName()), 3);

        ItemStack war = HeadUtils.createCustomItemStack(Material.IRON_SWORD,
                Lang.GUI_TOWN_RELATION_HOSTILE.get(playerData),
                Lang.GUI_TOWN_RELATION_HOSTILE_DESC1.get(playerData));
        ItemStack embargo = HeadUtils.createCustomItemStack(Material.BARRIER,
                Lang.GUI_TOWN_RELATION_EMBARGO.get(playerData),
                Lang.GUI_TOWN_RELATION_EMBARGO_DESC1.get(playerData));
        ItemStack nap = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK,
                Lang.GUI_TOWN_RELATION_NAP.get(playerData),
                Lang.GUI_TOWN_RELATION_NAP_DESC1.get(playerData));
        ItemStack alliance = HeadUtils.createCustomItemStack(Material.CAMPFIRE,
                Lang.GUI_TOWN_RELATION_ALLIANCE.get(playerData),
                Lang.GUI_TOWN_RELATION_ALLIANCE_DESC1.get(playerData));
        ItemStack diplomacyProposal = HeadUtils.createCustomItemStack(Material.PAPER,
                Lang.GUI_TOWN_RELATION_DIPLOMACY_PROPOSAL.get(playerData),
                Lang.GUI_TOWN_RELATION_DIPLOMACY_PROPOSAL_DESC1.get(playerData),
                Lang.GUI_TOWN_RELATION_DIPLOMACY_PROPOSAL_DESC2.get(playerData, territory.getAllDiplomacyProposal().size()));

        GuiItem warButton = ItemBuilder.from(war).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player, territory, TownRelation.WAR, 0);
        });
        GuiItem embargoButton = ItemBuilder.from(embargo).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player, territory, TownRelation.EMBARGO, 0);

        });
        GuiItem napButton = ItemBuilder.from(nap).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player, territory, TownRelation.NON_AGGRESSION, 0);

        });
        GuiItem allianceButton = ItemBuilder.from(alliance).asGuiItem(event -> {
            event.setCancelled(true);
            openSingleRelation(player, territory, TownRelation.ALLIANCE, 0);
        });
        GuiItem proposalsButton = ItemBuilder.from(diplomacyProposal).asGuiItem(event -> {
            event.setCancelled(true);
            if (!territory.doesPlayerHavePermission(playerData, RolePermission.MANAGE_TOWN_RELATION)) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                return;
            }
            openProposalMenu(player, territory, 0);
        });

        GuiItem panel = ItemBuilder.from(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true));

        gui.getFiller().fillTop(panel);
        gui.getFiller().fillBottom(panel);

        gui.setItem(9, warButton);
        gui.setItem(11, embargoButton);
        gui.setItem(13, napButton);
        gui.setItem(15, allianceButton);
        gui.setItem(17, proposalsButton);

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> territory.openMainMenu(player)));
        gui.open(player);
    }

    public static void openProposalMenu(Player player, TerritoryData territoryData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_RELATIONS.get(playerData, territoryData.getName()), 6);

        ArrayList<GuiItem> guiItems = new ArrayList<>();

        for (DiplomacyProposal diplomacyProposal : territoryData.getAllDiplomacyProposal()) {
            guiItems.add(diplomacyProposal.createGuiItem(player, territoryData, page));
        }

        GuiUtil.createIterator(gui, guiItems, page, player, p -> openRelations(player, territoryData),
                p -> openProposalMenu(player, territoryData, page - 1),
                p -> openProposalMenu(player, territoryData, page + 1));

        gui.open(player);
    }

    public static void openSingleRelation(Player player, TerritoryData mainTerritory, TownRelation relation, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_RELATION_WITH.get(playerData, relation.getName(), page + 1), 6);

        PlayerData playerStat = PlayerDataStorage.getInstance().get(player);

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (String territoryID : mainTerritory.getRelations().getTerritoriesIDWithRelation(relation)) {

            TerritoryData territoryData = TerritoryUtil.getTerritory(territoryID);
            ItemStack icon = territoryData.getIconWithInformationAndRelation(mainTerritory, playerData.getLang());

            if (relation == TownRelation.WAR) {
                ItemMeta meta = icon.getItemMeta();
                assert meta != null;
                List<String> lore = meta.getLore();
                assert lore != null;
                lore.add(Lang.GUI_TOWN_ATTACK_TOWN_DESC1.get(playerData));
                meta.setLore(lore);
                icon.setItemMeta(meta);
            }

            GuiItem townButton = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);

                if (relation == TownRelation.WAR) {
                    if (territoryData.getNumberOfClaimedChunk() < 1) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_ATTACK_NO_CLAIMED_CHUNK.get(playerData));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    if (mainTerritory.atWarWith(territoryID)) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_ATTACK_ALREADY_ATTACKING.get(playerData));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    openStartWarSettings(player, new CreateAttackData(mainTerritory, territoryData));
                }
            });
            guiItems.add(townButton);
        }

        ItemStack addTownButton = HeadUtils.makeSkullB64(
                Lang.GUI_TOWN_RELATION_ADD_TOWN.get(playerData),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19"
        );
        ItemStack removeTownButton = HeadUtils.makeSkullB64(
                Lang.GUI_TOWN_RELATION_REMOVE_TOWN.get(playerData),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0="
        );

        GuiItem addRelation = ItemBuilder.from(addTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            if (!mainTerritory.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_TOWN_RELATION)) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                return;
            }
            openTownRelationAdd(player, mainTerritory, relation, 0);
        });
        GuiItem removeRelation = ItemBuilder.from(removeTownButton).asGuiItem(event -> {
            event.setCancelled(true);
            if (!mainTerritory.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_TOWN_RELATION)) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                return;
            }
            openTownRelationRemove(player, mainTerritory, relation, 0);
        });

        GuiUtil.createIterator(gui, guiItems, page, player, p -> openRelations(player, mainTerritory),
                p -> openSingleRelation(player, mainTerritory, relation, page - 1),
                p -> openSingleRelation(player, mainTerritory, relation, page - 1));

        gui.setItem(6, 4, addRelation);
        gui.setItem(6, 5, removeRelation);


        gui.open(player);
    }

    public static void openTownRelationAdd(Player player, TerritoryData territory, TownRelation wantedRelation, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_SELECT_ADD_TERRITORY_RELATION.get(playerData, wantedRelation.getName()), 6);

        List<String> relationListID = territory.getRelations().getTerritoriesIDWithRelation(wantedRelation);
        ItemStack decorativeGlass = HeadUtils.createCustomItemStack(Material.GREEN_STAINED_GLASS_PANE, "");
        List<GuiItem> guiItems = new ArrayList<>();

        List<String> territories = new ArrayList<>();
        territories.addAll(TownDataStorage.getInstance().getTownMap().keySet());
        territories.addAll(RegionDataStorage.getInstance().getRegionStorage().keySet());

        territories.removeAll(relationListID); //Territory already have this relation
        territories.remove(territory.getID()); //Remove itself

        for (String otherTownUUID : territories) {
            TerritoryData otherTerritory = TerritoryUtil.getTerritory(otherTownUUID);
            ItemStack icon = otherTerritory.getIconWithInformationAndRelation(territory, playerData.getLang());

            TownRelation actualRelation = territory.getRelationWith(otherTerritory);

            if (!actualRelation.canBeChanged()) {
                continue;
            }

            GuiItem iconGui = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);

                if (otherTerritory.haveNoLeader()) {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_NO_LEADER.get(playerData));
                    return;
                }

                if (wantedRelation.isSuperiorTo(actualRelation)) {
                    otherTerritory.receiveDiplomaticProposal(territory, wantedRelation);
                    player.sendMessage(TanChatUtils.getTANString() + Lang.DIPLOMATIC_INVITATION_SENT_SUCCESS.get(playerData, otherTerritory.getName()));
                } else {
                    territory.setRelation(otherTerritory, wantedRelation);
                }
                openSingleRelation(player, territory, wantedRelation, 0);

            });
            guiItems.add(iconGui);
        }


        GuiUtil.createIterator(gui, guiItems, 0, player, p -> openSingleRelation(player, territory, wantedRelation, 0),
                p -> openTownRelationAdd(player, territory, wantedRelation, page - 1),
                p -> openTownRelationAdd(player, territory, wantedRelation, page + 1),
                decorativeGlass);


        gui.open(player);
    }

    public static void openTownRelationRemove(Player player, TerritoryData territory, TownRelation wantedRelation, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_SELECT_REMOVE_TERRITORY_RELATION.get(playerData, wantedRelation.getName()), 6);

        List<String> relationListID = territory.getRelations().getTerritoriesIDWithRelation(wantedRelation);
        ItemStack decorativeGlass = HeadUtils.createCustomItemStack(Material.RED_STAINED_GLASS_PANE, "");
        List<GuiItem> guiItems = new ArrayList<>();


        for (String otherTownUUID : relationListID) {
            TerritoryData otherTerritory = TerritoryUtil.getTerritory(otherTownUUID);
            ItemStack townIcon = otherTerritory.getIconWithInformationAndRelation(territory, playerData.getLang());

            GuiItem townGui = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                if (wantedRelation.isSuperiorTo(TownRelation.NEUTRAL)) {
                    territory.setRelation(otherTerritory, TownRelation.NEUTRAL);
                } else {
                    otherTerritory.receiveDiplomaticProposal(territory, TownRelation.NEUTRAL);
                    player.sendMessage(TanChatUtils.getTANString() + Lang.DIPLOMATIC_INVITATION_SENT_SUCCESS.get(playerData, otherTerritory.getName()));
                    SoundUtil.playSound(player, MINOR_GOOD);
                }
                openSingleRelation(player, territory, wantedRelation, 0);
            });
            guiItems.add(townGui);
        }

        GuiUtil.createIterator(gui, guiItems, 0, player, p -> openSingleRelation(player, territory, wantedRelation, 0),
                p -> openTownRelationRemove(player, territory, wantedRelation, page - 1),
                p -> openTownRelationRemove(player, territory, wantedRelation, page + 1),
                decorativeGlass);


        gui.open(player);
    }


    public static void openChunkGeneralSettings(Player player, TerritoryData territoryData) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_CHUNK_GENERAL_SETTINGS.get(playerData), 3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));
        Map<GeneralChunkSetting, Boolean> generalSettings = territoryData.getChunkSettings().getChunkSetting();

        for (GeneralChunkSetting generalChunkSetting : GeneralChunkSetting.values()) {


            GuiItem guiItem = ItemBuilder.from(generalChunkSetting.getIcon(generalSettings.get(generalChunkSetting))).asGuiItem(event -> {
                event.setCancelled(true);
                if (!territoryData.doesPlayerHavePermission(player, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                    SoundUtil.playSound(player, NOT_ALLOWED);
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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_MOB_SETTINGS.get(playerData, page + 1), 6);

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
                    status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_ACTIVATED.get(playerData));
                } else {
                    status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_DEACTIVATED.get(playerData));
                }
            } else {
                status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_LOCKED.get(playerData));
                status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_LOCKED2.get(playerData, cost));
            }
            ItemStack mobIcon = HeadUtils.makeSkullB64(mobEnum.name(), mobEnum.getTexture(), status);

            GuiItem mobItem = new GuiItem(mobIcon, event -> {
                event.setCancelled(true);
                if (!townData.doesPlayerHavePermission(playerData, RolePermission.MANAGE_MOB_SPAWN)) {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                    return;
                }
                if (upgradeStatus.isUnlocked()) {
                    upgradeStatus.setActivated(!upgradeStatus.canSpawn());
                    SoundUtil.playSound(player, ADD);
                } else {
                    if (townData.getBalance() < cost) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get(playerData));
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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(type.getLabel(playerData.getLang()), 6);

        PlayerData playerStat = PlayerDataStorage.getInstance().get(player.getUniqueId().toString());
        List<GuiItem> guiItems = new ArrayList<>();

        for (String authorizedPlayerID : territoryData.getPermission(type).getAuthorizedPlayers()) {
            OfflinePlayer authorizedPlayer = Bukkit.getOfflinePlayer(UUID.fromString(authorizedPlayerID));
            ItemStack icon = HeadUtils.getPlayerHead(authorizedPlayer.getName(), authorizedPlayer,
                    Lang.GUI_TOWN_MEMBER_DESC3.get(playerData));

            GuiItem guiItem = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);
                if (!territoryData.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
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
                p -> new PlayerChunkSettingsMenu(player, territoryData),
                p -> openPlayerListForChunkPermission(player, territoryData, type, page + 1),
                p -> openPlayerListForChunkPermission(player, territoryData, type, page + 1));


        ItemStack addIcon = HeadUtils.makeSkullB64(Lang.GUI_GENERIC_ADD_BUTTON.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");

        GuiItem addButton = ItemBuilder.from(addIcon).asGuiItem(event -> {
            event.setCancelled(true);
            if (!territoryData.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                SoundUtil.playSound(player, NOT_ALLOWED);
                return;
            }
            openAddPlayerForChunkPermission(player, territoryData, type, 0);
        });

        gui.setItem(6, 3, addButton);

        gui.open(player);
    }

    private static void openAddPlayerForChunkPermission(Player player, TerritoryData territoryData, ChunkPermissionType type, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_AUTHORIZE_PLAYER.get(playerData), 6);

        PlayerData playerStat = PlayerDataStorage.getInstance().get(player.getUniqueId().toString());

        List<GuiItem> guiItems = new ArrayList<>();

        for (Player playerToAdd : Bukkit.getOnlinePlayers()) {

            PlayerData playerToAddData = PlayerDataStorage.getInstance().get(playerToAdd);
            if (territoryData.getPermission(type).isAllowed(territoryData, playerToAddData))
                continue;

            ItemStack icon = HeadUtils.getPlayerHead(playerToAdd.getName(), playerToAdd,
                    Lang.GUI_GENERIC_ADD_BUTTON.get(playerData));

            GuiItem guiItem = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);
                if (!territoryData.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
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
                p -> openAddPlayerForChunkPermission(player, territoryData, type, page + 1));

        gui.open(player);
    }

    private static void openVassalsList(Player player, TerritoryData territoryData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_VASSALS.get(playerData, page + 1), 6);

        List<GuiItem> guiList = new ArrayList<>();

        for (TerritoryData townData : territoryData.getVassals()) {
            ItemStack townIcon = townData.getIconWithInformations(playerData.getLang());
            GuiItem townInfo = ItemBuilder.from(townIcon).asGuiItem(event -> event.setCancelled(true));
            guiList.add(townInfo);
        }

        ItemStack addTown = HeadUtils.makeSkullB64(Lang.GUI_INVITE_TOWN_TO_REGION.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
        ItemStack removeTown = HeadUtils.makeSkullB64(Lang.GUI_KICK_TOWN_TO_REGION.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=");


        GuiItem addButton = ItemBuilder.from(addTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (!territoryData.doesPlayerHavePermission(playerData, RolePermission.TOWN_ADMINISTRATOR)) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get(playerData));
                return;
            }
            openAddVassal(player, territoryData, page);
        });
        GuiItem removeButton = ItemBuilder.from(removeTown).asGuiItem(event -> {
            event.setCancelled(true);
            if (!territoryData.doesPlayerHavePermission(playerData, RolePermission.TOWN_ADMINISTRATOR)) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get(playerData));
                return;
            }
            openRemoveVassal(player, territoryData, page);
        });

        GuiUtil.createIterator(gui, guiList, 0, player, p -> openHierarchyMenu(player, territoryData),
                p -> openVassalsList(player, territoryData, page - 1),
                p -> openVassalsList(player, territoryData, page + 1));


        gui.setItem(6, 3, addButton);
        gui.setItem(6, 4, removeButton);
        gui.open(player);
    }

    private static void openAddVassal(Player player, TerritoryData territoryData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_VASSALS.get(playerData, page + 1), 6);

        List<GuiItem> guiItems = new ArrayList<>();

        for (TerritoryData potentialVassal : territoryData.getPotentialVassals()) {
            if (territoryData.isVassal(potentialVassal) || potentialVassal.containsVassalisationProposal(territoryData))
                continue;

            ItemStack territoryIcon = potentialVassal.getIconWithInformationAndRelation(territoryData, playerData.getLang());
            HeadUtils.addLore(territoryIcon, Lang.GUI_REGION_INVITE_TOWN_DESC1.get(playerData));

            GuiItem townButton = ItemBuilder.from(territoryIcon).asGuiItem(event -> {
                event.setCancelled(true);
                potentialVassal.addVassalisationProposal(territoryData);
                openAddVassal(player, territoryData, page);
            });
            guiItems.add(townButton);
        }

        GuiUtil.createIterator(gui, guiItems, page, player, p -> openVassalsList(player, territoryData, page),
                p -> openAddVassal(player, territoryData, page - 1),
                p -> openAddVassal(player, territoryData, page + 1));

        gui.open(player);
    }

    private static void openRemoveVassal(Player player, TerritoryData territoryData, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_VASSALS.get(playerData, page + 1), 6);

        List<GuiItem> guiItems = new ArrayList<>();
        for (TerritoryData territoryVassal : territoryData.getVassals()) {
            ItemStack townIcon = territoryVassal.getIconWithInformationAndRelation(territoryData, playerData.getLang());
            HeadUtils.addLore(townIcon, Lang.GUI_REGION_INVITE_TOWN_DESC1.get(playerData));

            GuiItem townButton = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                if (territoryVassal.isCapitalOf(territoryData)) {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.CANT_KICK_REGIONAL_CAPITAL.get(playerData, territoryVassal.getName()));
                    return;
                }
                territoryData.broadcastMessageWithSound(Lang.GUI_REGION_KICK_TOWN_BROADCAST.get(playerData, territoryVassal.getName()), BAD);
                territoryVassal.removeOverlord();
                player.closeInventory();
            });
            guiItems.add(townButton);
        }

        GuiUtil.createIterator(gui, guiItems, page, player, p -> openVassalsList(player, territoryData, page),
                p -> openRemoveVassal(player, territoryData, page - 1),
                p -> openRemoveVassal(player, territoryData, page + 1));

        gui.open(player);
    }

    public static void openRegionChangeOwnership(Player player, int page) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_CHANGE_OWNERSHIP.get(playerData), 6);
        RegionData regionData = playerData.getRegion();

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (String playerID : regionData.getPlayerIDList()) {

            PlayerData iteratePlayerData = PlayerDataStorage.getInstance().get(playerID);
            ItemStack switchPlayerIcon = HeadUtils.getPlayerHead(Bukkit.getOfflinePlayer(UUID.fromString(playerID)));

            GuiItem switchPlayerButton = ItemBuilder.from(switchPlayerIcon).asGuiItem(event -> {
                event.setCancelled(true);

                openConfirmMenu(player, Lang.GUI_CONFIRM_CHANGE_LEADER.get(playerData, iteratePlayerData.getNameStored()), confirm -> {
                    FileUtil.addLineToHistory(Lang.HISTORY_REGION_CAPITAL_CHANGED.get(playerData, player.getName(), regionData.getCapital().getName(), playerData.getTown().getName()));
                    regionData.setLeaderID(iteratePlayerData.getID());

                    regionData.broadcastMessageWithSound(Lang.GUI_REGION_SETTINGS_REGION_CHANGE_LEADER_BROADCAST.get(playerData, iteratePlayerData.getNameStored()), GOOD);

                    if (!regionData.getCapital().getID().equals(iteratePlayerData.getTown().getID())) {
                        regionData.broadCastMessage(TanChatUtils.getTANString() + Lang.GUI_REGION_SETTINGS_REGION_CHANGE_CAPITAL_BROADCAST.get(playerData, iteratePlayerData.getTown().getName()));
                        regionData.setCapital(iteratePlayerData.getTownId());
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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        if (!landmark.isOwned()) {
            openLandmarkNoOwner(player, landmark);
            return;
        }
        if (townData.ownLandmark(landmark)) {
            openPlayerOwnLandmark(player, landmark);
            return;
        }
        TownData owner = TownDataStorage.getInstance().get(landmark.getOwnerID());
        player.sendMessage(TanChatUtils.getTANString() + Lang.LANDMARK_ALREADY_CLAIMED.get(playerData, owner.getName()));
        SoundUtil.playSound(player, MINOR_BAD);

    }

    private static void openLandmarkNoOwner(Player player, Landmark landmark) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_LANDMARK_UNCLAIMED.get(playerData), 3);

        GuiItem landmarkIcon = ItemBuilder.from(landmark.getIcon()).asGuiItem(event -> event.setCancelled(true));

        TownData playerTown = TownDataStorage.getInstance().get(player);

        ItemStack claimLandmark = HeadUtils.makeSkullB64(
                Lang.GUI_TOWN_RELATION_ADD_TOWN.get(playerData),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                playerTown.canClaimMoreLandmarks() ? Lang.GUI_LANDMARK_LEFT_CLICK_TO_CLAIM.get(playerData) : Lang.GUI_LANDMARK_TOWN_FULL.get(playerData)
        );

        GuiItem claimLandmarkGui = ItemBuilder.from(claimLandmark).asGuiItem(event -> {
            event.setCancelled(true);
            if (!playerTown.canClaimMoreLandmarks()) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_LANDMARK_TOWN_FULL.get(playerData));
                SoundUtil.playSound(player, MINOR_BAD);
                return;
            }

            playerTown.addLandmark(landmark);
            playerTown.broadcastMessageWithSound(Lang.GUI_LANDMARK_CLAIMED.get(playerData), GOOD);
            dispatchLandmarkGui(player, landmark);
        });

        ItemStack panel = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        GuiItem panelGui = ItemBuilder.from(panel).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(1, 5, landmarkIcon);
        gui.setItem(2, 5, claimLandmarkGui);

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, Player::closeInventory));
        gui.setItem(3, 2, panelGui);
        gui.setItem(3, 3, panelGui);
        gui.setItem(3, 4, panelGui);
        gui.setItem(3, 5, panelGui);
        gui.setItem(3, 6, panelGui);
        gui.setItem(3, 7, panelGui);
        gui.setItem(3, 8, panelGui);
        gui.setItem(3, 9, panelGui);

        gui.open(player);
    }

    private static void openPlayerOwnLandmark(Player player, Landmark landmark) {
        TownData townData = TownDataStorage.getInstance().get(landmark.getOwnerID());
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_LANDMARK_CLAIMED.get(playerData, townData.getName()), 3);
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        int quantity = landmark.computeStoredReward(townData);

        ItemStack removeTown = HeadUtils.makeSkullB64(
                Lang.GUI_REMOVE_LANDMARK.get(playerData),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0="
        );

        String bagTexture;
        if (quantity == 0)
            bagTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjRjMTY0YmFjMjE4NGE3NmExZWU5NjkxMzI0MmUzMzVmMWQ0MTFjYWZmNTEyMDVlYTM5YjIwNWU2ZjhmMDU4YSJ9fX0=";
        else
            bagTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTliOTA2YjIxNTVmMTkzNzg3MDQyMzM4ZDA1Zjg0MDM5MWMwNWE2ZDNlODE2MjM5MDFiMjk2YmVlM2ZmZGQyIn19fQ==";

        ItemStack collectRessources = HeadUtils.makeSkullB64(
                Lang.GUI_COLLECT_LANDMARK.get(playerData),
                bagTexture,
                Lang.GUI_COLLECT_LANDMARK_DESC1.get(playerData),
                Lang.GUI_COLLECT_LANDMARK_DESC2.get(playerData, quantity)
        );


        GuiItem removeTownButton = ItemBuilder.from(removeTown).asGuiItem(event -> {
            event.setCancelled(true);
            townData.removeLandmark(landmark);
            TownData playerTown = TownDataStorage.getInstance().get(player);
            playerTown.broadcastMessageWithSound(Lang.GUI_LANDMARK_REMOVED.get(playerData), BAD);
            dispatchLandmarkGui(player, landmark);
        });

        GuiItem collectRessourcesButton = ItemBuilder.from(collectRessources).asGuiItem(event -> {
            event.setCancelled(true);
            landmark.giveToPlayer(player, quantity);
            player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_LANDMARK_REWARD_COLLECTED.get(playerData, quantity));
            SoundUtil.playSound(player, GOOD);
            dispatchLandmarkGui(player, landmark);
        });


        ItemStack panel = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        GuiItem panelIcon = ItemBuilder.from(panel).asGuiItem(event -> event.setCancelled(true));


        GuiItem landmarkIcon = ItemBuilder.from(landmark.getIcon()).asGuiItem(event -> event.setCancelled(true));

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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_CONFIRMATION.get(playerData), 3);

        ItemStack confirm = HeadUtils.makeSkullB64(Lang.GENERIC_CONFIRM_ACTION.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=",
                confirmLore);

        ItemStack cancel = HeadUtils.makeSkullB64(Lang.GENERIC_CANCEL_ACTION.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==",
                Lang.GENERIC_CANCEL_ACTION_DESC1.get(playerData));

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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_HIERARCHY.get(playerData), 3);

        GuiItem decorativeGlass = GuiUtil.getUnnamedItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE);

        GuiItem overlordInfo;
        if (territoryData.canHaveOverlord()) {
            GuiItem overlordButton;
            if (territoryData.haveOverlord()) {
                TerritoryData overlord = territoryData.getOverlord();
                ItemStack overlordIcon = overlord.getIcon();
                ItemMeta meta = overlordIcon.getItemMeta();
                meta.setDisplayName(Lang.OVERLORD_GUI.get(playerData));
                List<String> lore = new ArrayList<>();
                lore.add(Lang.GUI_OVERLORD_INFO.get(playerData, overlord.getName()));
                meta.setLore(lore);
                overlordIcon.setItemMeta(meta);

                ItemStack declareIndependence = HeadUtils.createCustomItemStack(Material.SPRUCE_DOOR,
                        Lang.GUI_OVERLORD_DECLARE_INDEPENDENCE.get(playerData),
                        Lang.GUI_OVERLORD_DECLARE_INDEPENDENCE_DESC1.get(playerData)
                );
                ItemStack donateToOverlord = HeadUtils.createCustomItemStack(Material.DIAMOND,
                        Lang.GUI_OVERLORD_DONATE.get(playerData),
                        Lang.GUI_OVERLORD_DONATE_DESC1.get(playerData)
                );
                overlordInfo = ItemBuilder.from(overlordIcon).asGuiItem(event -> event.setCancelled(true));
                overlordButton = ItemBuilder.from(declareIndependence).asGuiItem(event -> {
                    event.setCancelled(true);
                    if (!territoryData.haveOverlord()) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.TERRITORY_NO_OVERLORD.get(playerData));
                        openHierarchyMenu(player, territoryData); //This should trigger only if town have been kicked from region during the menu
                        return;
                    }
                    TerritoryData overlordData = territoryData.getOverlord();

                    if (territoryData.isCapital()) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.CANNOT_DECLARE_INDEPENDENCE_BECAUSE_CAPITAL.get(playerData, territoryData.getBaseColoredName()));
                        return;
                    }

                    openConfirmMenu(player, Lang.GUI_CONFIRM_DECLARE_INDEPENDENCE.get(playerData, territoryData.getBaseColoredName(), overlord.getBaseColoredName()), confirm -> {
                        territoryData.removeOverlord();
                        territoryData.broadcastMessageWithSound(Lang.TOWN_BROADCAST_TOWN_LEFT_REGION.get(playerData, territoryData.getName(), overlordData.getName()), BAD);
                        overlordData.broadCastMessage(Lang.REGION_BROADCAST_TOWN_LEFT_REGION.get(playerData, territoryData.getName()));

                        player.closeInventory();
                    }, remove -> openHierarchyMenu(player, territoryData));
                });
                GuiItem donateToOverlordButton = ItemBuilder.from(donateToOverlord).asGuiItem(event -> {
                    event.setCancelled(true);
                    player.sendMessage(TanChatUtils.getTANString() + Lang.WRITE_IN_CHAT_AMOUNT_OF_MONEY_FOR_DONATION.get(playerData));
                    PlayerChatListenerStorage.register(player, new DonateToTerritory(overlord));
                });
                gui.setItem(2, 3, donateToOverlordButton);
            } else {
                ItemStack noCurrentOverlord = HeadUtils.createCustomItemStack(Material.GOLDEN_HELMET, Lang.OVERLORD_GUI.get(playerData),
                        Lang.NO_OVERLORD.get(playerData));
                overlordInfo = ItemBuilder.from(noCurrentOverlord).asGuiItem(event -> event.setCancelled(true));

                ItemStack joinOverlord = HeadUtils.createCustomItemStack(Material.WRITABLE_BOOK, Lang.BROWSE_OVERLORD_INVITATION.get(playerData),
                        Lang.BROWSE_OVERLORD_INVITATION_DESC1.get(playerData, territoryData.getNumberOfVassalisationProposals()));

                overlordButton = ItemBuilder.from(joinOverlord).asGuiItem(event -> {
                    event.setCancelled(true);
                    openChooseOverlordMenu(player, territoryData, 0);
                });
            }
            gui.setItem(2, 2, overlordButton);
        } else {
            ItemStack noOverlordItem = HeadUtils.createCustomItemStack(Material.IRON_BARS, Lang.OVERLORD_GUI.get(playerData),
                    Lang.CANNOT_HAVE_OVERLORD.get(playerData));
            overlordInfo = ItemBuilder.from(noOverlordItem).asGuiItem(event -> event.setCancelled(true));

            gui.setItem(2, 2, overlordInfo);
            gui.setItem(2, 3, overlordInfo);
            gui.setItem(2, 4, overlordInfo);

        }
        gui.setItem(1, 3, overlordInfo);

        GuiItem vassalInfo;
        if (territoryData.canHaveVassals()) {
            ItemStack vassalIcon = HeadUtils.createCustomItemStack(Material.GOLDEN_SWORD, Lang.VASSAL_GUI.get(playerData),
                    Lang.VASSAL_GUI_DESC1.get(playerData, territoryData.getBaseColoredName(), territoryData.getVassalCount()));

            ItemStack vassals = HeadUtils.makeSkullB64(Lang.GUI_REGION_TOWN_LIST.get(playerData), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
                    Lang.GUI_REGION_TOWN_LIST_DESC1.get(playerData));
            GuiItem vassalsButton = ItemBuilder.from(vassals).asGuiItem(event -> {
                event.setCancelled(true);
                openVassalsList(player, territoryData, 0);
            });
            vassalInfo = ItemBuilder.from(vassalIcon).asGuiItem(event -> event.setCancelled(true));
            gui.setItem(2, 6, vassalsButton);
        } else {
            ItemStack noVassalsIcon = HeadUtils.createCustomItemStack(Material.IRON_BARS, Lang.VASSAL_GUI.get(playerData),
                    Lang.CANNOT_HAVE_VASSAL.get(playerData));
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
