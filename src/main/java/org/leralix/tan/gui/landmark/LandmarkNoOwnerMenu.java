package org.leralix.tan.gui.landmark;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.deprecated.HeadUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.GOOD;

public class LandmarkNoOwnerMenu extends BasicGui {

    private final Landmark landmark;

    public LandmarkNoOwnerMenu(Player player, Landmark landmark) {
        super(player, Lang.HEADER_LANDMARK_UNCLAIMED.get(player), 3);
        this.landmark = landmark;
        open();
    }


    @Override
    public void open() {


        gui.setItem(1, 5, getLandmarkIcon(landmark));

        gui.setItem(2, 5, getClaimButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, Player::closeInventory));


        GuiItem panelGui = ItemBuilder.from(HeadUtils.createCustomItemStack(Material.GRAY_STAINED_GLASS_PANE, "")).asGuiItem(event -> event.setCancelled(true));
        gui.getFiller().fillBottom(panelGui);
        gui.open(player);
    }

    private @NotNull GuiItem getLandmarkIcon(Landmark landmark) {
        LangType lang = tanPlayer.getLang();
        return ItemBuilder.from(landmark.getIcon(lang)).asGuiItem(event -> event.setCancelled(true));
    }

    private @NotNull GuiItem getClaimButton() {
        TownData playerTown = TownDataStorage.getInstance().get(player);
        double cost = Constants.getClaimLandmarkCost();
        List<String> description = new ArrayList<>();

        if(cost > 0.0){
            description.add(Lang.GUI_LANDMARK_CLAIM_COST.get(player, cost));
        }

        boolean isRequirementsMet = true;

        if (!playerTown.canClaimMoreLandmarks()) {
            isRequirementsMet = false;
            description.add(Lang.GUI_LANDMARK_TOWN_FULL.get(tanPlayer));
        }

        if (Constants.isLandmarkClaimRequiresEncirclement() && !landmark.isEncircledBy(playerTown)) {
            isRequirementsMet = false;
            description.add(Lang.GUI_LANDMARK_NOT_ENCIRCLED.get(tanPlayer));
        }

        if (cost > playerTown.getBalance()) {
            isRequirementsMet = false;
            description.add(Lang.GUI_LANDMARK_NOT_ENOUGH_MONEY.get(tanPlayer, Constants.getClaimLandmarkCost()));
        }

        if (isRequirementsMet) {
            description.add(Lang.GUI_LANDMARK_LEFT_CLICK_TO_CLAIM.get(tanPlayer));
        }


        IconKey iconKey = isRequirementsMet ? IconKey.GUI_CONFIRM_CLAIM_LANDMARK_REQUIREMENTS_MET_ICON : IconKey.GUI_CONFIRM_CLAIM_LANDMARK_REQUIREMENTS_UNMET_ICON;

        final boolean requirementMet = isRequirementsMet;

        return iconManager.get(iconKey)
                .setName(Lang.GUI_TOWN_RELATION_ADD_TOWN.get(tanPlayer))
                .setDescription(description)
                .setAction(event -> {
                    if (!requirementMet) {
                        SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                        return;
                    }

                    double actualBalance = playerTown.getBalance();
                    double newBalance = actualBalance - cost;

                    PlayerGUI.openConfirmMenu(
                            player,
                            Lang.GUI_GENERIC_NEW_BALANCE_MENU.get(tanPlayer, actualBalance, newBalance),
                            confirm -> {
                                playerTown.removeFromBalance(cost);
                                landmark.setOwner(playerTown);
                                playerTown.broadcastMessageWithSound(Lang.GUI_LANDMARK_CLAIMED.get(), GOOD);
                                PlayerGUI.dispatchLandmarkGui(player, landmark);
                            },
                            cancel -> open()
                    );

                })
                .asGuiItem(player);
    }

}
