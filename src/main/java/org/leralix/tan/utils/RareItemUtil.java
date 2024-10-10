package org.leralix.tan.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

public class RareItemUtil {
    private RareItemUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Get the price of a rare item
     * @param rareItemTag   The tag of the rare item
     * @return              The price of the rare item
     */
    public static int getPrice(String rareItemTag) {
        FileConfiguration config = ConfigUtil.getCustomConfig(ConfigTag.MAIN);
        return switch (rareItemTag) {
            case "rareStone" -> config.getInt("rareStoneValue");
            case "rareWood" -> config.getInt("rareWoodValue");
            case "rareCrops" -> config.getInt("rareCropsValue");
            case "rareSoul" -> config.getInt("rareSoulValue");
            case "rareFish" -> config.getInt("rareFishValue");
            default -> 0;
        };

    }
}
