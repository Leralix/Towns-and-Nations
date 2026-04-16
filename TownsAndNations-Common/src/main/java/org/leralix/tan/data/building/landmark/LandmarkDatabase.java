package org.leralix.tan.data.building.landmark;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.database.DatabaseData;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class LandmarkDatabase implements Landmark, DatabaseData<Landmark> {

    private final DbManager<Landmark> manager;

    private Landmark data;

    public LandmarkDatabase(Landmark data, DbManager<Landmark> manager) {
        this.data = data;
        this.manager = manager;
    }

    @Override
    public void setData(Landmark data) {
        this.data = data;
    }

    @Override
    public String getID() {
        return data.getID();
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public void setName(String newName) {
        mutate(p -> p.setName(newName));
    }

    @Override
    public void setOwner(Town newOwner) {
        mutate(p -> p.setOwner(newOwner));
    }

    @Override
    public void setOwner(TanTerritory newOwner) {
        mutate(p -> p.setOwner(newOwner));
    }

    @Override
    public void removeOwnership() {
        mutate(Landmark::removeOwnership);
    }

    @Override
    public String getOwnerID() {
        return data.getOwnerID();
    }

    @Override
    public Vector3D getPosition() {
        return data.getPosition();
    }

    @Override
    public void spawnChest() {
        data.spawnChest();
    }

    @Override
    public void dispawnChest() {
        data.dispawnChest();
    }

    @Override
    public Optional<Block> getChest() {
        return data.getChest();
    }

    @Override
    public Material getRessourceMaterial() {
        return data.getRessourceMaterial();
    }

    @Override
    public ItemStack getResources() {
        return data.getResources();
    }

    @Override
    public void generateResources() {
        mutate(Landmark::generateResources);
    }

    @Override
    public void setStoredLimit(int limit) {
        mutate(p -> p.setStoredLimit(limit));
    }

    @Override
    public boolean isOwned() {
        return data.isOwned();
    }

    @Override
    public TanTerritory getOwner() {
        return data.getOwner();
    }

    @Override
    public IconBuilder getIcon(LangType langType) {
        return data.getIcon(langType);
    }

    @Override
    public List<FilledLang> getBaseDescription() {
        return data.getBaseDescription();
    }

    @Override
    public void deleteLandmark() {
        mutate(Landmark::deleteLandmark);
    }

    @Override
    public int computeStoredReward(Territory townData) {
        int val = data.computeStoredReward(townData);
        manager.save(this);
        return val;
    }

    @Override
    public void giveToPlayer(Player player, int number) {
        mutate(p -> p.giveToPlayer(player, number));
    }

    @Override
    public void setReward(ItemStack itemOnCursor) {
        mutate(p -> p.setReward(itemOnCursor));
    }

    @Override
    public Location getLocation() {
        return data.getLocation();
    }

    @Override
    public void setQuantity(int quantity) {
        mutate(p -> p.setQuantity(quantity));
    }

    @Override
    public int getQuantity() {
        return data.getQuantity();
    }

    @Override
    public void setItem(ItemStack item) {
        mutate(p -> p.setItem(item));
    }

    @Override
    public ItemStack getItem() {
        return data.getItem();
    }

    @Override
    public boolean isEncircledBy(TanTerritory territoryToCompare) {
        return data.isEncircledBy(territoryToCompare);
    }

    @Override
    public boolean isOwnedBy(Territory territoryData) {
        return data.isOwnedBy(territoryData);
    }

    private void mutate(Consumer<Landmark> mutator) {
        mutator.accept(data);
        manager.save(this);
    }

}
