package org.leralix.tan.enums;

import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.Lang.Lang;
import org.leralix.tan.utils.DropChances;

public enum CustomVillagerProfession {

    GOLDSMITH(Lang.VILLAGER_GOLDSMITH.get(), Profession.TOOLSMITH, DropChances.getRareStone()),
    BOTANIST(Lang.VILLAGER_BOTANIST.get(), Profession.FARMER, DropChances.getRareWood()),
    COOK(Lang.VILLAGER_COOK.get(), Profession.BUTCHER, DropChances.getRareCrops()),
    WIZARD(Lang.VILLAGER_WIZARD.get(), Profession.CLERIC, DropChances.getRareSoul()),
    FISHERMAN(Lang.VILLAGER_FISHERMAN.get(),Profession.FISHERMAN ,DropChances.getRareFish() );

    private final String name;
    private final Profession profession;
    private final ItemStack buyingItem;

    CustomVillagerProfession(String name, Profession profession, ItemStack buyingItem){
        this.name = name;
        this.profession = profession;
        this.buyingItem = buyingItem;
    }
    public static CustomVillagerProfession getVillager(String tag){

        for(CustomVillagerProfession profession : CustomVillagerProfession.values()){
            if(profession.name().equalsIgnoreCase(tag))
                return profession;
        }
        return null;
    }
    public String getDisplayName(){
        return this.name;
    }
    public Profession getProfession(){
        return this.profession;
    }

    public ItemStack getBuyingItem(){
        return this.buyingItem;
    }
    public static CustomVillagerProfession fromString(String name) {
        try {
            return CustomVillagerProfession.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
