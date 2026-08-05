package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.upgrade.TerritoryStats;
import org.leralix.tan.data.upgrade.Upgrade;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.user.territory.upgrade.UpgradeMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

public class AdminUpgradeMenu extends UpgradeMenu {

    public AdminUpgradeMenu(Player player, Territory territoryData, BasicGui returnMenu) {
        super(player, territoryData, returnMenu);
    }

    @Override
    protected @NotNull GuiItem getUpgradeItem(Upgrade upgrade, int levelOfUpgrade, int maxLevelOfUpgrade) {
        return iconManager.get(upgrade.getIconMaterial())
                .setName(upgrade.getName(langType))
                .setDescription(buildUpgradeDescription(upgrade, levelOfUpgrade, maxLevelOfUpgrade))
                .setClickToAcceptMessage(
                        Lang.GUI_GENERIC_CLICK_TO_UPGRADE,
                        Lang.GUI_GENERIC_RIGHT_CLICK_TO_REMOVE
                )
                .setAction(action -> {
                    if (action.isLeftClick()) {
                        if (levelOfUpgrade >= maxLevelOfUpgrade) {
                            action.setCancelled(true);
                            SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                            return;
                        }
                        territoryData.getNewLevel().levelUp(upgrade);
                        TanChatUtils.message(player, Lang.BASIC_LEVEL_UP.get(langType), SoundEnum.LEVEL_UP);
                        SoundUtil.playSound(player, SoundEnum.ADD);
                        open();
                    } else if (action.isRightClick()) {
                        if (!territoryData.getNewLevel().levelDown(upgrade)) {
                            action.setCancelled(true);
                            SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                            return;
                        }
                        SoundUtil.playSound(player, SoundEnum.REMOVE);
                        open();
                    }
                })
                .asGuiItem(player, langType);
    }

    @Override
    protected @NotNull GuiItem getUpgradeTownButton() {
        TerritoryStats level = territoryData.getNewLevel();
        int currentLevel = level.getMainLevel();

        return iconManager.get(org.leralix.tan.gui.cosmetic.IconKey.LEVEL_UP_ICON)
                .setName(Lang.GUI_TERRITORY_LEVEL_UP.get(langType))
                .setDescription(
                        Lang.GUI_TOWN_LEVEL_UP_DESC1.get(Integer.toString(currentLevel))
                )
                .setClickToAcceptMessage(
                        Lang.GUI_GENERIC_LEFT_CLICK_TO_ACCEPT,
                        Lang.GUI_GENERIC_RIGHT_CLICK_TO_REMOVE
                )
                .setAction(action -> {
                    if (action.isLeftClick()) {
                        territoryData.getNewLevel().levelUpMain();
                        TanChatUtils.message(player, Lang.BASIC_LEVEL_UP.get(langType), SoundEnum.LEVEL_UP);
                        SoundUtil.playSound(player, SoundEnum.ADD);
                        open();
                    } else if (action.isRightClick()) {
                        if (!territoryData.getNewLevel().levelDownMain()) {
                            action.setCancelled(true);
                            SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                            return;
                        }
                        SoundUtil.playSound(player, SoundEnum.REMOVE);
                        open();
                    }
                })
                .asGuiItem(player, langType);
    }
}
