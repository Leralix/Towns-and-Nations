package org.tan.TownsAndNations.utils;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.DataClass.TownLevel;
import org.tan.TownsAndNations.DataClass.TownUpgrade;
import org.tan.TownsAndNations.GUI.GuiManager2;
import org.tan.TownsAndNations.GUI.IGUI;
import org.tan.TownsAndNations.Lang.DynamicLang;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.tan.TownsAndNations.storage.DataStorage.TownDataStorage.getTownMap;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class GuiUtil {

    /**
     * Creates a {@link GuiItem} for upgrading a town's level based on the provided town upgrade and town data.
     * @param player the player who is viewing the GUI and attempting the upgrade
     * @param townUpgrade the town upgrade to be applied
     * @param townData the data of the town to be upgraded
     * @return a GUI item representing the upgrade action
     */
    public static GuiItem makeUpgradeGuiItem(final @NotNull Player player, final @NotNull  TownUpgrade townUpgrade, final @NotNull  TownData townData){

        TownLevel townLevelClass = townData.getTownLevel();
        int townUpgradeLevel = townLevelClass.getUpgradeLevel(townUpgrade.getName());

        List<String> lore = townUpgrade.getItemLore(townLevelClass, townUpgradeLevel);

        ItemStack upgradeItemStack = HeadUtils.createCustomItemStack(
                Material.getMaterial(townUpgrade.getMaterialCode()),
                DynamicLang.get(townUpgrade.getName()),
                lore);

        return ItemBuilder.from(upgradeItemStack).asGuiItem(event -> {
            event.setCancelled(true);
            if(townUpgrade.isPrerequisiteMet(townLevelClass)){
                townData.upgradeTown(player,townUpgrade,townUpgradeLevel);
            }
            else {
                player.sendMessage(getTANString() + Lang.GUI_TOWN_LEVEL_UP_UNI_REQ_NOT_MET.get());
                SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
            }
            GuiManager2.OpenTownLevel(player,0);
        });
    }

    /**
     * Create the town upgrade resume {@link GuiItem}. This gui is used to summarise
     * every upgrade rewards the town currently have.
     * @param townData  The town on which the upgrade should be shown
     * @return          The {@link GuiItem} displaying the town current benefices
     */
    public static GuiItem townUpgradeResume(TownData townData){

        ItemStack townIcon = HeadUtils.getTownIcon(townData.getID());
        List<String> lore = new ArrayList<>();
        lore.add(Lang.TOWN_LEVEL_BONUS_RECAP.get());

        Map<String,Integer> benefits = townData.getTownLevel().getTotalBenefits();

        for(Map.Entry<String,Integer> entry : benefits.entrySet()){
            String value_ID = entry.getKey();
            Integer value = entry.getValue();
            String line;
            if(value > 0){
                line = Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_1.get(DynamicLang.get(value_ID), value);
            }
            else {
                line = Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_2.get(DynamicLang.get(value_ID), value);
            }
            lore.add(line);
        }

        HeadUtils.setLore(townIcon, lore);

        return ItemBuilder.from(townIcon).asGuiItem(event -> event.setCancelled(true));
    }

    public static void createIterator(Gui gui, ArrayList<GuiItem> guItems, int page,
                                      Player player, Consumer<Player> backArrowAction,
                                      Consumer<Player> nextPageAction, Consumer<Player> previousPageAction) {

        createIterator(gui, guItems, page, player, backArrowAction, nextPageAction, previousPageAction, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
    }

    public static void createIterator(Gui gui, ArrayList<GuiItem> guItems, int page,
                                      Player player, Consumer<Player> backArrowAction,
                                      Consumer<Player> nextPageAction, Consumer<Player> previousPageAction,
                                      ItemStack decorativeGlassPane) {

        int pageSize = (gui.getRows() - 1) * 9;
        int startIndex = page * pageSize;
        boolean lastPage;
        int totalSize = guItems.size();

        int endIndex;
        if(startIndex + pageSize > totalSize){
            endIndex = totalSize;
            lastPage = true;
        }
        else {
            lastPage = false;
            endIndex = startIndex + pageSize;
        }


        int slot = 0;

        for (int i = startIndex; i < endIndex; i++) {
            gui.setItem(slot, guItems.get(i));
            slot++;
        }
        GuiItem panel = ItemBuilder.from(decorativeGlassPane).asGuiItem(event -> event.setCancelled(true));

        ItemStack previousPageButton = HeadUtils.makeSkull(
                Lang.GUI_PREVIOUS_PAGE.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19"
        );
        ItemStack nextPageButton = HeadUtils.makeSkull(
                Lang.GUI_NEXT_PAGE.get(),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0="
        );

        GuiItem _previous = ItemBuilder.from(previousPageButton).asGuiItem(event -> {
            event.setCancelled(true);
            if(page == 0){
                return;
            }
            previousPageAction.accept(player);
        });

        GuiItem _next = ItemBuilder.from(nextPageButton).asGuiItem(event -> {
            event.setCancelled(true);
            if(lastPage) {
                return;
            }
            nextPageAction.accept(player);
        });

        int lastRow = gui.getRows();

        gui.setItem(lastRow,1, IGUI.CreateBackArrow(player, backArrowAction));
        gui.setItem(lastRow,2, panel);
        gui.setItem(lastRow,3, panel);
        gui.setItem(lastRow,4, panel);
        gui.setItem(lastRow,5, panel);
        gui.setItem(lastRow,6, panel);
        gui.setItem(lastRow,7, _previous);
        gui.setItem(lastRow,8, _next);
        gui.setItem(lastRow,9, panel);





    }
}
