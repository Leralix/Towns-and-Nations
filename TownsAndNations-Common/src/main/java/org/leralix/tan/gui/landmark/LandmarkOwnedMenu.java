package org.leralix.tan.gui.landmark;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.service.requirements.RankPermissionRequirement;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.GOOD;

public class LandmarkOwnedMenu extends BasicGui {

    private final TerritoryData territoryData;
    private final Landmark landmark;

    public LandmarkOwnedMenu(Player player, TownData townData, Landmark landmark){
        super(player, Lang.HEADER_LANDMARK_CLAIMED.get(townData.getName()), 3);
        this.territoryData = townData;
        this.landmark = landmark;
        open();
    }

    @Override
    public void open() {

        GuiItem landmarkIcon = iconManager.get(landmark.getIcon(langType)).asGuiItem(player, langType);
        gui.setItem(1, 5, landmarkIcon);

        gui.setItem(2, 4, getCollectButton());
        gui.setItem(2, 6, getAbandonButton());

        gui.setItem(3,1, GuiUtil.createBackArrow(player, HumanEntity::closeInventory));

        GuiItem panelIcon = GuiUtil.getUnnamedItem(Material.GRAY_STAINED_GLASS_PANE);
        gui.getFiller().fillTop(panelIcon);
        gui.getFiller().fillBottom(panelIcon);

        gui.open(player);
    }

    private @NotNull GuiItem getAbandonButton() {
        return iconManager.get(IconKey.GUI_LANDMARK_ABANDON_LANDMARK)
                .setName(Lang.GUI_REMOVE_LANDMARK.get(tanPlayer))
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_LANDMARK))
                .setAction(action -> {
                    landmark.removeOwnership();
                    SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
                    player.closeInventory();
                })
                .asGuiItem(player, langType);
    }

    private GuiItem getCollectButton() {
        int quantity = landmark.computeStoredReward(territoryData);
        return iconManager.get(quantity == 0 ? IconKey.GUI_LANDMARK_COLLECT_REWARDS_EMPTY : IconKey.GUI_LANDMARK_COLLECT_REWARDS)
                .setName(Lang.GUI_COLLECT_LANDMARK.get(langType))
                .setDescription(Lang.GUI_COLLECT_LANDMARK_DESC2.get(Integer.toString(quantity)))
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.COLLECT_LANDMARK))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(action -> {
                    if(quantity == 0){
                        SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                        return;
                    }

                    landmark.giveToPlayer(player, quantity);
                    TanChatUtils.message(player, Lang.GUI_LANDMARK_REWARD_COLLECTED.get(tanPlayer, Integer.toString(quantity)), GOOD);
                    open();
                })
                .asGuiItem(player, langType);
    }
}
