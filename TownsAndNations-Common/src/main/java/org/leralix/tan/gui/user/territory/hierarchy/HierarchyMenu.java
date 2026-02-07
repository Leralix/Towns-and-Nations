package org.leralix.tan.gui.user.territory.hierarchy;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.NationData;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.DonateToTerritory;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Optional;

public class HierarchyMenu extends BasicGui {

    private final TerritoryData territoryData;

    public HierarchyMenu(Player player, TerritoryData territoryData) {
        super(player, Lang.HEADER_HIERARCHY, 3);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {
        Gui gui = GuiUtil.createChestGui(Lang.HEADER_HIERARCHY.get(tanPlayer), 3);


        gui.setItem(1, 3, setupOverlordSection());
        gui.setItem(1, 7, setupVassalSection());

        fillDecorations(gui, Material.LIGHT_BLUE_STAINED_GLASS_PANE);

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> territoryData.openMainMenu(player, tanPlayer), tanPlayer.getLang()));
        gui.open(player);
    }

    private GuiItem setupOverlordSection() {
        if (!territoryData.canHaveOverlord()) {
            GuiItem info = createNoOverlordPossibleInfo();
            gui.setItem(2, 2, info);
            gui.setItem(2, 3, info);
            gui.setItem(2, 4, info);
            return info;
        }

        Optional<TerritoryData> overlordOptional = territoryData.getOverlordInternal();
        if (overlordOptional.isPresent()) {
            TerritoryData overlord = overlordOptional.get();
            GuiItem info = createOverlordInfo(overlord);
            GuiItem button = createDeclareIndependenceButton(player, territoryData, tanPlayer, overlord);
            gui.setItem(2, 2, button);
            gui.setItem(2, 3, createDonateToOverlordButton(player, tanPlayer, overlord));
            return info;
        }

        GuiItem info = createNoCurrentOverlordInfo(tanPlayer);

        gui.setItem(2, 2, getJoinOverlordButton());
        return info;
    }

    private @NotNull GuiItem getJoinOverlordButton() {

        return iconManager.get(Material.WRITABLE_BOOK)
                .setName(Lang.BROWSE_OVERLORD_INVITATION.get(langType))
                .setDescription(
                        Lang.BROWSE_OVERLORD_INVITATION_DESC1.get(Integer.toString(territoryData.getNumberOfVassalisationProposals()))
                )
                .setAction(action -> {
                    action.setCancelled(true);
                    new TerritoryChooseOverlordMenu(player, territoryData, p -> setupOverlordSection());
                })
                .asGuiItem(player, langType);
    }

    private GuiItem createOverlordInfo(TerritoryData overlord) {
        return iconManager.get(overlord.getIcon())
                .setName(Lang.OVERLORD_GUI.get(langType))
                .setDescription(Lang.GUI_OVERLORD_INFO.get(overlord.getName()))
                .asGuiItem(player, langType);
    }

    private GuiItem createNoCurrentOverlordInfo(ITanPlayer tanPlayer) {
        return iconManager.get(Material.GOLDEN_HELMET)
                .setName(Lang.OVERLORD_GUI.get(tanPlayer))
                .setDescription(Lang.NO_OVERLORD.get())
                .asGuiItem(player, langType);
    }

    private GuiItem createNoOverlordPossibleInfo() {

        return iconManager.get(Material.IRON_BARS)
                .setName(Lang.OVERLORD_GUI.get(langType))
                .setDescription(Lang.CANNOT_HAVE_OVERLORD.get())
                .asGuiItem(player, langType);
    }

    private GuiItem createDeclareIndependenceButton(Player player, TerritoryData territoryData, ITanPlayer tanPlayer, TerritoryData overlord) {

        return iconManager.get(Material.SPRUCE_DOOR)
                .setName(Lang.GUI_OVERLORD_DECLARE_INDEPENDENCE.get(langType))
                .setDescription(Lang.GUI_OVERLORD_DECLARE_INDEPENDENCE_DESC1.get())
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(action -> {
                    action.setCancelled(true);
                    if (!territoryData.haveOverlord()) {
                        TanChatUtils.message(player, Lang.TERRITORY_NO_OVERLORD.get(tanPlayer));
                        open();
                        return;
                    }

                    if (territoryData.isCapital()) {
                        if (overlord instanceof NationData) {
                            TanChatUtils.message(player, Lang.CANNOT_DECLARE_INDEPENDENCE_BECAUSE_NATION_CAPITAL.get(tanPlayer, territoryData.getColoredName()));
                        } else {
                            TanChatUtils.message(player, Lang.CANNOT_DECLARE_INDEPENDENCE_BECAUSE_CAPITAL.get(tanPlayer, territoryData.getColoredName()));
                        }
                        return;
                    }

                    new ConfirmMenu(
                            player,
                            Lang.GUI_CONFIRM_DECLARE_INDEPENDENCE.get(territoryData.getColoredName(), overlord.getColoredName()),
                            () -> {
                                territoryData.removeOverlord();
                                if (overlord instanceof NationData) {
                                    territoryData.broadcastMessageWithSound(Lang.REGION_BROADCAST_REGION_LEFT_NATION.get(territoryData.getName(), overlord.getName()), SoundEnum.BAD);
                                    overlord.broadCastMessage(Lang.NATION_BROADCAST_REGION_LEFT_NATION.get(territoryData.getName()));
                                } else {
                                    territoryData.broadcastMessageWithSound(Lang.TOWN_BROADCAST_TOWN_LEFT_REGION.get(territoryData.getName(), overlord.getName()), SoundEnum.BAD);
                                    overlord.broadCastMessage(Lang.REGION_BROADCAST_TOWN_LEFT_REGION.get(territoryData.getName()));
                                }

                                player.closeInventory();
                            },
                            this::open
                    );
                })
                .asGuiItem(player, langType);
    }

    private GuiItem createDonateToOverlordButton(Player player, ITanPlayer tanPlayer, TerritoryData overlord) {
        return iconManager.get(Material.DIAMOND)
                .setName(Lang.GUI_OVERLORD_DONATE.get(langType))
                .setDescription(Lang.GUI_OVERLORD_DONATE_DESC1.get())
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(action -> {
                    action.setCancelled(true);
                    TanChatUtils.message(player, Lang.WRITE_IN_CHAT_AMOUNT_OF_MONEY_FOR_DONATION.get(tanPlayer));
                    PlayerChatListenerStorage.register(player, tanPlayer.getLang(), new DonateToTerritory(overlord));
                })
                .asGuiItem(player, langType);
    }

    private GuiItem setupVassalSection() {
        if (territoryData.canHaveVassals()) {

            IconKey iconKey = (territoryData instanceof NationData) ? IconKey.REGION_BASE_ICON : IconKey.TOWN_BASE_ICON;
            String name = (territoryData instanceof NationData) ? Lang.GUI_NATION_REGION_LIST.get(tanPlayer) : Lang.GUI_REGION_TOWN_LIST.get(tanPlayer);
            FilledLang desc = (territoryData instanceof NationData) ? Lang.GUI_NATION_REGION_LIST_DESC1.get() : Lang.GUI_REGION_TOWN_LIST_DESC1.get();

            gui.setItem(2, 6, iconManager.get(iconKey)
                    .setName(name)
                    .setDescription(desc)
                    .setAction(event -> new VassalsMenu(player, territoryData))
                    .asGuiItem(player, tanPlayer.getLang()));

            return iconManager.get(Material.GOLDEN_SWORD)
                    .setName(Lang.VASSAL_GUI.get(tanPlayer))
                    .setDescription(Lang.VASSAL_GUI_DESC1.get(territoryData.getColoredName(), Integer.toString(territoryData.getVassalCount())))
                    .asGuiItem(player, langType);
        }

        var info = iconManager.get(Material.IRON_BARS)
                .setName(Lang.VASSAL_GUI.get(tanPlayer))
                .setDescription(Lang.CANNOT_HAVE_VASSAL.get())
                .asGuiItem(player, langType);

        gui.setItem(2, 6, info);
        gui.setItem(2, 7, info);
        gui.setItem(2, 8, info);
        return info;
    }

    private void fillDecorations(Gui gui, Material material) {
        GuiItem decorativeItem = GuiUtil.getUnnamedItem(material);
        gui.getFiller().fillTop(decorativeItem);
        gui.getFiller().fillBottom(decorativeItem);

        gui.setItem(2, 5, decorativeItem);
        gui.setItem(2, 1, decorativeItem);
        gui.setItem(2, 9, decorativeItem);
    }

}
