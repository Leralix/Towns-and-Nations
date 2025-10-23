package org.leralix.tan.gui.user.territory.upgrade;

import dev.triumphteam.gui.components.util.GuiFiller;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.upgrade.TerritoryStats;
import org.leralix.tan.upgrade.Upgrade;
import org.leralix.tan.upgrade.rewards.IndividualStat;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UpgradeMenu extends BasicGui {

    private final TownData territoryData;
    private int verticalScrollIndex;
    private int horizontalScrollIndex;
    private final int maxLevel;

    public UpgradeMenu(Player player, TownData territoryData){
        super(player, Lang.HEADER_TOWN_UPGRADE, 6);
        this.territoryData = territoryData;
        this.verticalScrollIndex = 0;
        this.horizontalScrollIndex = 0;
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

        gui.setItem(6, 6, getTerritoryStats());


        TerritoryStats territoryStats = territoryData.getNewLevel();
        int townLevel = territoryStats.getMainLevel();
        //Fill the 5 rows of colored due to town level.
        for(int i = 0; i < 6; i++){
            int adaptedCursor = 5 - i + verticalScrollIndex;
            if(adaptedCursor > townLevel){
                gui.getFiller().fillBetweenPoints(i, 2, i, 9, GuiUtil.getUnnamedItem(Material.RED_STAINED_GLASS_PANE));
            }
            else if(adaptedCursor == townLevel){
                gui.getFiller().fillBetweenPoints(i, 2, i, 9, GuiUtil.getUnnamedItem(Material.YELLOW_STAINED_GLASS_PANE));
            }
            else {
                gui.getFiller().fillBetweenPoints(i, 2, i, 9, GuiUtil.getUnnamedItem(Material.GREEN_STAINED_GLASS_PANE));
            }
        }


        for(Upgrade upgrade : Constants.getUpgradeStorage().getUpgrades()){

            int row =  5 - upgrade.getRow() + verticalScrollIndex;
            int column = upgrade.getColumn() + 3 - horizontalScrollIndex;

            if(column > 9 || column < 2 || row > 5 || row < 1){
                continue;
            }
            int levelOfUpgrade = territoryStats.getLevel(upgrade) ;
            int maxLevelOfUpgrade = upgrade.getMaxLevel();

            List<String> desc = new ArrayList<>();
            desc.add(Lang.UPGRADE_CURRENT_LEVEL.get(langType, Integer.toString(levelOfUpgrade), Integer.toString(maxLevelOfUpgrade)));
            desc.add(" ");
            desc.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4.get(langType));
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
                            .asGuiItem(player)
            );

        }
    }

    private @NotNull GuiItem getTerritoryStats() {

        List<String> desc = new ArrayList<>();

        desc.add("Current stats :");
        desc.add("");



        Collection<IndividualStat> upgrades = territoryData.getNewLevel().getAllStats();

        for(IndividualStat stat : upgrades){
            desc.add(stat.getStatReward(langType));
        }



        return iconManager.get(territoryData.getIcon())
                .setName(territoryData.getName())
                .setDescription(desc)
                .asGuiItem(player);

    }

    private void generateMenuPart() {
        gui.getFiller().fillBottom(GuiUtil.getUnnamedItem(Material.GRAY_STAINED_GLASS_PANE));
        gui.getFiller().fillSide(GuiFiller.Side.LEFT, Collections.singletonList(GuiUtil.getUnnamedItem(Material.GRAY_STAINED_GLASS_PANE)));

        gui.setItem(3, 1, getUpButton());
        gui.setItem(4, 1, getDownButton());

        gui.setItem(6, 3, getUpgradeTownButton());

        gui.setItem(6, 1, GuiUtil.createBackArrow(player, territoryData::openMainMenu));
    }

    private @NotNull GuiItem getUpgradeTownButton() {

        TerritoryStats level = territoryData.getNewLevel();
        int currentLevel = level.getMainLevel();
        int nextLevelPrice = level.getMoneyRequiredForLevelUp();

        return iconManager.get(IconKey.LEVEL_UP_ICON)
                .setName(Lang.GUI_TOWN_LEVEL_UP.get(langType))
                .setDescription(
                        Lang.GUI_TOWN_LEVEL_UP_DESC1.get(langType, Integer.toString(currentLevel)),
                        Lang.GUI_TOWN_LEVEL_UP_DESC2.get(langType, Integer.toString(currentLevel + 1), Integer.toString(nextLevelPrice))
                )
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction( action -> {
                    territoryData.upgradeTown();
                    TanChatUtils.message(player, Lang.BASIC_LEVEL_UP.get(langType), SoundEnum.LEVEL_UP);
                    open();
                })
                .asGuiItem(player);
    }

    private @NotNull GuiItem getUpButton() {
        return iconManager.get(IconKey.UP_ARROW)
                .setName(Lang.GUI_GENERIC_UP.get(langType))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction( action -> {
                    verticalScrollIndex = Math.min(maxLevel, verticalScrollIndex + 1);
                    generateUpgrades();
                    gui.open(player);
                })
                .asGuiItem(player);
    }

    private @NotNull GuiItem getDownButton() {
        return iconManager.get(IconKey.DOWN_ARROW)
                .setName(Lang.GUI_GENERIC_DOWN.get(langType))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction( action -> {
                    verticalScrollIndex = Math.max(0, verticalScrollIndex - 1);
                    generateUpgrades();
                    gui.open(player);
                })
                .asGuiItem(player);
    }
}
