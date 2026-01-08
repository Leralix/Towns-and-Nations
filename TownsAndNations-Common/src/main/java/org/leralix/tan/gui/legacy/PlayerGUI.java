package org.leralix.tan.gui.legacy;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.landmark.LandmarkNoOwnerMenu;
import org.leralix.tan.gui.landmark.LandmarkOwnedMenu;
import org.leralix.tan.gui.user.territory.KingdomMenu;
import org.leralix.tan.gui.user.territory.NoKingdomMenu;
import org.leralix.tan.gui.user.territory.NoRegionMenu;
import org.leralix.tan.gui.user.territory.NoTownMenu;
import org.leralix.tan.gui.user.territory.RegionMenu;
import org.leralix.tan.gui.user.territory.TownMenu;
import org.leralix.tan.gui.user.territory.hierarchy.VassalsMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.DonateToTerritory;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.leralix.lib.data.SoundEnum.BAD;
import static org.leralix.lib.data.SoundEnum.MINOR_BAD;

public class PlayerGUI {

    private PlayerGUI() {
        throw new IllegalStateException("Utility class");
    }

    public static void dispatchPlayerKingdom(Player player) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        KingdomData kingdomData = tanPlayer.getKingdom();
        if (kingdomData != null) {
            new KingdomMenu(player, kingdomData);
        } else {
            new NoKingdomMenu(player);
        }
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

    public static void dispatchLandmarkGui(Player player, Landmark landmark) {

        TownData townData = TownDataStorage.getInstance().get(player);
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        if (!landmark.isOwned()) {
            new LandmarkNoOwnerMenu(player, landmark);
            return;
        }
        if (landmark.isOwnedBy(townData)) {
            new LandmarkOwnedMenu(player, townData, landmark);
            return;
        }
        TownData owner = TownDataStorage.getInstance().get(landmark.getOwnerID());
        TanChatUtils.message(player, Lang.LANDMARK_ALREADY_CLAIMED.get(tanPlayer, owner.getName()), MINOR_BAD);
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
                        if (overlord instanceof KingdomData) {
                            TanChatUtils.message(player, Lang.CANNOT_DECLARE_INDEPENDENCE_BECAUSE_KINGDOM_CAPITAL.get(tanPlayer, territoryData.getBaseColoredName()));
                        } else {
                            TanChatUtils.message(player, Lang.CANNOT_DECLARE_INDEPENDENCE_BECAUSE_CAPITAL.get(tanPlayer, territoryData.getBaseColoredName()));
                        }
                        return;
                    }

                    new ConfirmMenu(
                            player,
                            Lang.GUI_CONFIRM_DECLARE_INDEPENDENCE.get(territoryData.getBaseColoredName(), overlord.getBaseColoredName()),
                            () -> {
                                territoryData.removeOverlord();
                                if (overlord instanceof KingdomData) {
                                    territoryData.broadcastMessageWithSound(Lang.REGION_BROADCAST_REGION_LEFT_KINGDOM.get(territoryData.getName(), overlord.getName()), BAD);
                                    overlord.broadCastMessage(Lang.KINGDOM_BROADCAST_REGION_LEFT_KINGDOM.get(territoryData.getName()));
                                } else {
                                    territoryData.broadcastMessageWithSound(Lang.TOWN_BROADCAST_TOWN_LEFT_REGION.get(territoryData.getName(), overlord.getName()), BAD);
                                    overlord.broadCastMessage(Lang.REGION_BROADCAST_TOWN_LEFT_REGION.get(territoryData.getName()));
                                }

                                player.closeInventory();
                            },
                            () -> openHierarchyMenu(player, territoryData)
                    );
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
            ItemStack vassalIcon = HeadUtils.createCustomItemStack(
                    Material.GOLDEN_SWORD,
                    Lang.VASSAL_GUI.get(tanPlayer),
                    Lang.VASSAL_GUI_DESC1.get(tanPlayer, territoryData.getBaseColoredName(), Integer.toString(territoryData.getVassalCount()))
            );

            gui.setItem(2, 6, IconManager.getInstance().get((territoryData instanceof KingdomData) ? IconKey.REGION_BASE_ICON : IconKey.TOWN_BASE_ICON)
                    .setName((territoryData instanceof KingdomData) ? Lang.GUI_KINGDOM_REGION_LIST.get(tanPlayer) : Lang.GUI_REGION_TOWN_LIST.get(tanPlayer))
                    .setDescription((territoryData instanceof KingdomData) ? Lang.GUI_KINGDOM_REGION_LIST_DESC1.get() : Lang.GUI_REGION_TOWN_LIST_DESC1.get())
                    .setAction(event -> new VassalsMenu(player, territoryData))
                    .asGuiItem(player, tanPlayer.getLang()));

            vassalInfo = ItemBuilder.from(vassalIcon).asGuiItem(event -> event.setCancelled(true));
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
