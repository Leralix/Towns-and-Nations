package org.leralix.tan.dataclass.territory.cosmetic;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * BannerBuilder converts a Spigot Banner into serializable attributes
 * and is able to rebuild it.
 */
public class BannerBuilder {

    private final Material bannerType;
    private final List<Pair<DyeColor, String>> patterns;

    public BannerBuilder() {
        this.patterns = new ArrayList<>();
        this.bannerType = Material.GREEN_BANNER;
    }

    /**
     * Extract serializable attributes from a Banner block.
     */
    public BannerBuilder(Material material, List<Pattern> patterns) {
        this.patterns = new ArrayList<>();
        this.bannerType = material;

        for (Pattern pattern : patterns) {
            this.patterns.add(new Pair<>(pattern.getColor(), pattern.getPattern().name()));
        }
    }

    /**
     * Build an ItemStack representing the banner.
     */
    public ItemStack buildItemStack() {
        ItemStack item = new ItemStack(bannerType);
        BannerMeta meta = (BannerMeta) item.getItemMeta();

        meta.setPatterns(getPatterns());

        item.setItemMeta(meta);
        return item;
    }

    public Material getBannerType() {
        return bannerType;
    }

    public List<Pattern> getPatterns(){
        List<Pattern> patternList = new ArrayList<>();
        for (Pair<DyeColor, String> patternData : patterns) {

            if (patternData == null || patternData.second() == null) {
                continue;
            }

            PatternType patternType = null;
            String wanted = patternData.second();
            for (PatternType pt : PatternType.values()) {
                if (pt.name().equals(wanted)) {
                    patternType = pt;
                    break;
                }
            }

            if (patternType == null) {
                continue;
            }

            patternList.add(new Pattern(patternData.first(), patternType));
        }
        return patternList;
    }
}
