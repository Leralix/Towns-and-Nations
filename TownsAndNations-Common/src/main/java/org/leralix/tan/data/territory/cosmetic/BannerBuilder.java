package org.leralix.tan.data.territory.cosmetic;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
    public BannerBuilder(Material material, List<Pattern> bannerPattern) {
        this.patterns = new ArrayList<>();
        this.bannerType = material;

        var registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);

        for (Pattern pattern : bannerPattern) {
            PatternType type = pattern.getPattern();

            NamespacedKey key = registry.getKey(type);
            if (key != null) {
                patterns.add(
                        new Pair<>(
                                pattern.getColor(),
                                key.toString()
                        )
                );
            }
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

    public List<Pattern> getPatterns() {
        List<Pattern> patternList = new ArrayList<>();
        var registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);

        for (Pair<DyeColor, String> patternData : patterns) {
            if (patternData == null || patternData.second() == null) {
                continue;
            }

            NamespacedKey key = NamespacedKey.fromString(patternData.second());
            if (key == null) {
                continue;
            }

            PatternType bannerPattern = registry.get(key);
            if (bannerPattern == null) {
                continue;
            }

            patternList.add(new Pattern(patternData.first(), bannerPattern));
        }

        return patternList;
    }
}
