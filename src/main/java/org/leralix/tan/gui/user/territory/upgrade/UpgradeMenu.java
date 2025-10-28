package org.leralix.tan.gui.user.territory.upgrade;

import dev.triumphteam.gui.components.util.GuiFiller;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.upgrade.TerritoryStats;
import org.leralix.tan.upgrade.Upgrade;
import org.leralix.tan.upgrade.rewards.IndividualStat;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpgradeMenu extends BasicGui {

    private final TerritoryData territoryData;
    private int scrollIndex;
    private final int maxLevel;

    public UpgradeMenu(Player player, TerritoryData territoryData){
        super(player, Lang.HEADER_TOWN_UPGRADE, 6);
        this.territoryData = territoryData;
        this.scrollIndex = 0;
        this.maxLevel = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TownMaxLevel", 10);
        open();
    }


    @Override
    public void open() {

        generateMenuPart();
        generateUpgrades();


        gui.open(player);
    }

    private void generateUpgrades() {

        gui.setItem(1, 1, getTerritoryStats(territoryData));


        TerritoryStats territoryStats = territoryData.getNewLevel();
        int townLevel = territoryStats.getMainLevel();
        //Fill the 5 rows of colored due to town level.
        for(int i = 2; i < 10; i++){
            int adaptedCursor = i - 1 + scrollIndex;
            if(adaptedCursor > townLevel){
                gui.getFiller().fillBetweenPoints(1, i, 4, i, GuiUtil.getUnnamedItem(Material.IRON_BARS));
                gui.setItem(5, i, GuiUtil.getUnnamedItem(Material.RED_STAINED_GLASS_PANE));
            }
            else if(adaptedCursor == townLevel){
                gui.getFiller().fillBetweenPoints(1, i, 4, i, GuiUtil.getUnnamedItem(Material.IRON_BARS));
                gui.setItem(5, i, getUpgradeTownButton());
            }
            else {
                gui.getFiller().fillBetweenPoints(1, i, 4, i, GuiUtil.getUnnamedItem(Material.LIME_STAINED_GLASS_PANE));
                gui.setItem(5, i, GuiUtil.getUnnamedItem(Material.GREEN_STAINED_GLASS_PANE));
            }
        }


        for(Upgrade upgrade : Constants.getUpgradeStorage().getUpgrades(territoryData)){

            int row =  upgrade.getRow() + 1;
            int column = upgrade.getColumn() + 2 - scrollIndex;

            if(column > 9 || column < 2 || row > 4 || row < 1){
                continue;
            }
            int levelOfUpgrade = territoryStats.getLevel(upgrade) ;
            int maxLevelOfUpgrade = upgrade.getMaxLevel();

            List<FilledLang> desc = new ArrayList<>();
            desc.add(Lang.UPGRADE_CURRENT_LEVEL.get(Integer.toString(levelOfUpgrade), Integer.toString(maxLevelOfUpgrade)));
            desc.add(Lang.EMPTY.get());
            desc.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4.get());
            for(IndividualStat individualStat : upgrade.getRewards()){
                desc.add(individualStat.getStatReward(langType, levelOfUpgrade, maxLevelOfUpgrade));
            }


            gui.setItem(row, column,
                    iconManager.get(upgrade.getIconMaterial())
                            .setName(upgrade.getName(langType))
                            .setDescription(desc)
                            .setRequirements(upgrade.getRequirements(territoryData, player))
                            .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_UPGRADE)
                            .setAction( action -> {
                                if(levelOfUpgrade >= maxLevelOfUpgrade){
                                    TanChatUtils.message(player, Lang.TOWN_UPGRADE_MAX_LEVEL.get(langType), SoundEnum.NOT_ALLOWED);
                                    return;
                                }
                                TanChatUtils.message(player, Lang.BASIC_LEVEL_UP.get(langType), SoundEnum.LEVEL_UP);
                                territoryData.upgradeTown(upgrade);
                                open();
                            })
                            .asGuiItem(player, langType)
            );
        }
    }

    private @NotNull GuiItem getTerritoryStats(TerritoryData territoryData) {

        List<FilledLang> desc = new ArrayList<>();
        desc.add(Lang.EMPTY.get());

        for(IndividualStat stat : territoryData.getNewLevel().getAllStats()){
            desc.add(stat.getStatReward(langType));
        }

        return iconManager.get(territoryData.getIcon())
                .setName(territoryData.getName())
                .setDescription(desc)
                .asGuiItem(player, langType);

    }

    private void generateMenuPart() {
        gui.getFiller().fillBottom(GuiUtil.getUnnamedItem(Material.GRAY_STAINED_GLASS_PANE));
        gui.getFiller().fillSide(GuiFiller.Side.LEFT, Collections.singletonList(GuiUtil.getUnnamedItem(Material.GRAY_STAINED_GLASS_PANE)));

        gui.setItem(6, 8, getRightButton());
        gui.setItem(6, 7, getLeftButton());

        gui.setItem(6, 1, GuiUtil.createBackArrow(player, territoryData::openMainMenu));
    }

    private @NotNull GuiItem getUpgradeTownButton() {

        TerritoryStats level = territoryData.getNewLevel();
        int currentLevel = level.getMainLevel();
        int nextLevelPrice = level.getMoneyRequiredForLevelUp();

        return iconManager.get(IconKey.LEVEL_UP_ICON)
                .setName(Lang.GUI_TOWN_LEVEL_UP.get(langType))
                .setDescription(
                        Lang.GUI_TOWN_LEVEL_UP_DESC1.get(Integer.toString(currentLevel)),
                        Lang.GUI_TOWN_LEVEL_UP_DESC2.get(Integer.toString(currentLevel + 1), Integer.toString(nextLevelPrice))
                )
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction( action -> {
                    territoryData.getNewLevel().levelUpMain();
                    TanChatUtils.message(player, Lang.BASIC_LEVEL_UP.get(langType), SoundEnum.LEVEL_UP);
                    open();
                })
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getRightButton() {
        return iconManager.get(IconKey.RIGHT_ARROW)
                .setName(Lang.GUI_GENERIC_UP.get(langType))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction( action -> {
                    scrollIndex = Math.min(maxLevel, scrollIndex + 1);
                    generateUpgrades();
                    gui.open(player);
                })
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getLeftButton() {
        return iconManager.get(IconKey.LEFT_ARROW)
                .setName(Lang.GUI_GENERIC_DOWN.get(langType))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction( action -> {
                    scrollIndex = Math.max(0, scrollIndex - 1);
                    generateUpgrades();
                    gui.open(player);
                })
                .asGuiItem(player, langType);
    }
}
