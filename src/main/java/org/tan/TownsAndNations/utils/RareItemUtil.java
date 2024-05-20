package org.tan.TownsAndNations.utils;

public class RareItemUtil {
    /**
     * Get the price of a rare item
     * @param rareItemTag   The tag of the rare item
     * @return              The price of the rare item
     */
    public static int getPrice(String rareItemTag) {

        return switch (rareItemTag) {
            case "rareStone" -> ConfigUtil.getCustomConfig("config.yml").getInt("rareStoneValue");
            case "rareWood" -> ConfigUtil.getCustomConfig("config.yml").getInt("rareWoodValue");
            case "rareCrops" -> ConfigUtil.getCustomConfig("config.yml").getInt("rareCropsValue");
            case "rareSoul" -> ConfigUtil.getCustomConfig("config.yml").getInt("rareSoulValue");
            case "rareFish" -> ConfigUtil.getCustomConfig("config.yml").getInt("rareFishValue");
            default -> 0;
        };

    }
}
