package org.leralix.tan.upgrade.rewards.list;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.rewards.AggregatableStat;
import org.leralix.tan.upgrade.rewards.IndividualStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BiomeStat extends IndividualStat implements AggregatableStat<BiomeStat> {

    private final List<Biome> biomes;

    public BiomeStat(){
        this.biomes = Collections.emptyList();
    }

    public BiomeStat(List<Biome> biomeKey) {
        this.biomes = new ArrayList<>(biomeKey);
    }

    public static BiomeStat fromStrings(List<String> biomeKey){
        ArrayList<Biome> res = new ArrayList<>();

        for (String rawKey : biomeKey) {
            String key = normalizeKey(rawKey);

            if (isAllKeyword(key)) {
                addAllBiomes(res);
                break;
            }

            BiomeCategory category = findCategory(key);
            if (category != null) {
                res.addAll(category.getBiomes());
            } else {
                Biome biome = findBiome(key);
                if (biome != null) {
                    res.add(biome);
                }
            }
        }

        return new BiomeStat(res);
    }

    private static String normalizeKey(String key) {
        return key.toUpperCase().replace(" ", "_");
    }

    private static boolean isAllKeyword(String key) {
        return "ALL".equals(key);
    }

    private static void addAllBiomes(List<Biome> out) {
        try {
            out.addAll(Arrays.asList(Biome.values()));
        } catch (IncompatibleClassChangeError ignored) {
            out.add(Biome.PLAINS);
            // Error with MockBukkit. This allows for tests to run.
        }
    }

    private static BiomeCategory findCategory(String key) {
        for (BiomeCategory c : BiomeCategory.values()) {
            if (c.name().equals(key)) {
                return c;
            }
        }
        return null;
    }

    private static Biome findBiome(String key) {
        for (Biome b : Biome.values()) {
            if (b.name().equals(key)) {
                return b;
            }
        }
        return null;
    }

     private static List<Biome> biomesOf(String... names) {
         List<Biome> res = new ArrayList<>();
         for (String name : names) {
             try {
                 res.add(Biome.valueOf(name));
             } catch (IllegalArgumentException ignored) {
                 // Biome not present in this Minecraft version
             }
         }
         return res;
     }

    @Override
    public BiomeStat aggregate(List<BiomeStat> stats) {
        List<Biome> res = new ArrayList<>();
        for(BiomeStat stat : stats){
            res.addAll(stat.biomes);
        }
        return new BiomeStat(res);
    }

    @Override
    public BiomeStat scale(int factor) {
        if(factor > 0){
            return this;
        }
        else {
            return new BiomeStat(); // Level 0 => No biomes unlocked.
        }
    }

    @Override
    public FilledLang getStatReward(LangType langType, int level, int maxLevel) {
        String nbNewCommands = getMathSign(biomes.size());
        if(level == 0){
            return Lang.UPGRADE_LINE_INT.get(Lang.CHUNK_AUTHORIZED_BIOMES.get(langType),  "0", nbNewCommands);
        }
        else {
            return Lang.UPGRADE_LINE_INT_MAX.get(Lang.CHUNK_AUTHORIZED_BIOMES.get(langType),nbNewCommands);
        }
    }

    @Override
    public FilledLang getStatReward(LangType langType) {
        String nbNewCommands = getMathSign(biomes.size());
        return Lang.UPGRADE_LINE_INT_MAX.get(Lang.CHUNK_AUTHORIZED_BIOMES.get(langType), nbNewCommands);
    }

    @Override
    public Lang getStatName() {
        return Lang.CHUNK_AUTHORIZED_BIOMES;
    }

    /**
     * Defines if the chunk's biome can be claimed with this stat.
     * If at least one block in the chunk is in an authorized biome, then it can be claimed.
     * Check will only be done at the maximum height of the chunk.
     * @param chunk The chunk to check
     * @return True if the chunk can be claimed, false otherwise
     */
    public boolean canClaimBiome(Chunk chunk) {

        int middleX = (chunk.getX() << 4);
        int middleZ = (chunk.getZ() << 4);
        World world = chunk.getWorld();
        for(int x = middleX; x < middleX + 16; x++){
            for(int z = middleZ; z < middleZ + 16; z++){
                Biome biome = world.getBiome(x, world.getHighestBlockYAt(x, z), z);
                if(biomes.contains(biome)){
                    return true;
                }
            }
        }

        Biome biome = chunk.getWorld().getBiome(chunk.getX() << 4, 64, chunk.getZ() << 4);
        return biomes.contains(biome);
    }

    enum BiomeCategory {
        OCEAN(biomesOf(
                "OCEAN",
                "DEEP_OCEAN",
                "WARM_OCEAN",
                "LUKEWARM_OCEAN",
                "COLD_OCEAN",
                "DEEP_LUKEWARM_OCEAN",
                "DEEP_COLD_OCEAN",
                "FROZEN_OCEAN",
                "DEEP_FROZEN_OCEAN"
        )),
        PLAINS(biomesOf(
                "PLAINS",
                "SUNFLOWER_PLAINS",
                "MEADOW"
        )),
        DESERT(biomesOf(
                "DESERT",
                "BADLANDS",
                "WOODED_BADLANDS",
                "ERODED_BADLANDS"
        )),
        FOREST(biomesOf(
                "FOREST",
                "DARK_FOREST",
                "FLOWER_FOREST",
                "BIRCH_FOREST",
                "OLD_GROWTH_BIRCH_FOREST",
                "OLD_GROWTH_SPRUCE_TAIGA",
                "GROVE",
                "CHERRY_GROVE"
        )),
        MOUNTAIN(biomesOf(
                "WINDSWEPT_HILLS",
                "WINDSWEPT_GRAVELLY_HILLS",
                "ICE_SPIKES",
                "STONY_PEAKS",
                "FROZEN_PEAKS",
                "JAGGED_PEAKS",
                "SNOWY_SLOPES"
        )),
        NETHER(biomesOf(
                "NETHER_WASTES",
                "SOUL_SAND_VALLEY",
                "CRIMSON_FOREST",
                "WARPED_FOREST",
                "BASALT_DELTAS"
        )),
        END(biomesOf(
                "THE_END",
                "SMALL_END_ISLANDS",
                "END_MIDLANDS",
                "END_HIGHLANDS",
                "END_BARRENS"
        )),
        CUSTOM_BIOMES(List.of());

        private final List<Biome> biomes;

        BiomeCategory(List<Biome> biomes) {
            this.biomes = biomes;
        }

        public List<Biome> getBiomes() {
            return biomes;
        }

        public boolean contains(Biome biome) {
            return biomes.contains(biome);
        }
    }

}
