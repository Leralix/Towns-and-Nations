package org.leralix.tan.gui.landmark;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.data.upgrade.rewards.numeric.LandmarkCap;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.gui.common.PlayerGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.service.requirements.RankPermissionRequirement;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.LandmarkStorage;
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
        super(player, Lang.HEADER_LANDMARK_UNCLAIMED, 3);
        this.landmark = landmark;
        open();
    }


    @Override
    public void open() {


        gui.setItem(1, 5, getLandmarkIcon(landmark));

        gui.setItem(2, 5, getClaimButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, Player::closeInventory, langType));


        GuiItem panelGui = ItemBuilder.from(HeadUtils.createCustomItemStack(Material.GRAY_STAINED_GLASS_PANE, "")).asGuiItem(event -> event.setCancelled(true));
        gui.getFiller().fillBottom(panelGui);
        gui.open(player);
    }

    private @NotNull GuiItem getLandmarkIcon(Landmark landmark) {
        LangType lang = tanPlayer.getLang();
        return ItemBuilder.from(landmark.getIcon(lang)).asGuiItem(event -> event.setCancelled(true));
    }

    private @NotNull GuiItem getClaimButton() {
        TownData playerTown = TownDataStorage.getInstance().get(tanPlayer);
        double cost = Constants.getClaimLandmarkCost();
        List<FilledLang> description = new ArrayList<>();

        if (cost > 0.0) {
            description.add(Lang.GUI_LANDMARK_CLAIM_COST.get(String.valueOf(cost)));
        }

        boolean isRequirementsMet = true;

        LandmarkCap landmarkCap = playerTown.getNewLevel().getStat(LandmarkCap.class);
        int currentLandmarkCount = LandmarkStorage.getInstance().getLandmarkOf(playerTown).size();
        if (!landmarkCap.canDoAction(currentLandmarkCount)) {
            isRequirementsMet = false;
            description.add(Lang.GUI_LANDMARK_TOWN_FULL.get());
        }

        if (Constants.isLandmarkClaimRequiresEncirclement() && !landmark.isEncircledBy(playerTown)) {
            isRequirementsMet = false;
            description.add(Lang.GUI_LANDMARK_NOT_ENCIRCLED.get());
        }

        if (cost > playerTown.getBalance()) {
            isRequirementsMet = false;
            description.add(Lang.GUI_LANDMARK_NOT_ENOUGH_MONEY.get(Double.toString(Constants.getClaimLandmarkCost())));
        }


        IconKey iconKey = isRequirementsMet ? IconKey.GUI_CONFIRM_CLAIM_LANDMARK_REQUIREMENTS_MET_ICON : IconKey.GUI_CONFIRM_CLAIM_LANDMARK_REQUIREMENTS_UNMET_ICON;

        final boolean requirementMet = isRequirementsMet;

        return iconManager.get(iconKey)
                .setName(Lang.GUI_TOWN_RELATION_ADD_TOWN.get(tanPlayer))
                .setDescription(description)
                .setClickToAcceptMessage(Lang.GUI_LANDMARK_LEFT_CLICK_TO_CLAIM)
                .setRequirements(new RankPermissionRequirement(playerTown, tanPlayer, RolePermission.MANAGE_LANDMARK))
                .setAction(event -> {
                    if (!requirementMet) {
                        SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                        return;
                    }

                    double actualBalance = playerTown.getBalance();
                    double newBalance = actualBalance - cost;

                    new ConfirmMenu(
                            player,
                            Lang.GUI_GENERIC_NEW_BALANCE_MENU.get(Double.toString(actualBalance), Double.toString(newBalance)),
                            () -> {
                                playerTown.removeFromBalance(cost);
                                landmark.setOwner(playerTown);
                                playerTown.broadcastMessageWithSound(Lang.GUI_LANDMARK_CLAIMED.get(), GOOD);
                                PlayerGUI.dispatchLandmarkGui(player, tanPlayer, landmark);
                            },
                            this::open
                    );

                })
                .asGuiItem(player, langType);
    }

}
