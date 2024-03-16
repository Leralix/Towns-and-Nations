package org.tan.TownsAndNations.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.TownsAndNations.DataClass.RareItem;

import java.util.HashMap;
import java.util.Map;

public class RareItemUtil {

    public static int getPrice(String rareItemTag) {

        return switch (rareItemTag) {
            case "rareStone" -> ConfigUtil.getCustomConfig("config.yml").getInt("rareStoneValue");
            case "rareWood" -> ConfigUtil.getCustomConfig("config.yml").getInt("rareWoodValue");
            case "rareCrops" -> ConfigUtil.getCustomConfig("config.yml").getInt("rareCropsValue");
            case "rareSoul" -> ConfigUtil.getCustomConfig("config.yml").getInt("rareSoulValue");
            default -> 0;
        };

    }
}
