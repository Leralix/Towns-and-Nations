package org.leralix.tan.data.building.landmark;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.LangType;
import org.tan.api.interfaces.buildings.TanLandmark;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.List;
import java.util.Optional;

public interface Landmark extends TanLandmark {
    String getID();

    String getName();

    void setName(String newName);

    void setOwner(Town newOwner);

    void setOwner(TanTerritory newOwner);

    void removeOwnership();

    String getOwnerID();

    Vector3D getPosition();

    void spawnChest();

    void dispawnChest();

    Optional<Block> getChest();

    Material getRessourceMaterial();

    ItemStack getResources();

    void generateResources();

    void setStoredLimit(int limit);

    boolean isOwned();

    TanTerritory getOwner();

    IconBuilder getIcon(LangType langType);

    List<FilledLang> getBaseDescription();

    void deleteLandmark();

    int computeStoredReward(Territory townData);

    void giveToPlayer(Player player, int number);

    void setReward(ItemStack itemOnCursor);

    Location getLocation();

    void setQuantity(int quantity);

    int getQuantity();

    void setItem(ItemStack item);

    ItemStack getItem();

    boolean isEncircledBy(TanTerritory territoryToCompare);

    boolean isOwnedBy(Territory territoryData);
}
