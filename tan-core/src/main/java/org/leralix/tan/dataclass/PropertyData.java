package org.leralix.tan.dataclass;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector3D;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.lib.utils.particles.ParticleUtils;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.building.Building;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.newhistory.PropertyBuyTaxTransaction;
import org.leralix.tan.dataclass.property.AbstractOwner;
import org.leralix.tan.dataclass.property.PlayerOwned;
import org.leralix.tan.dataclass.property.TerritoryOwned;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.territory.cosmetic.CustomIcon;
import org.leralix.tan.dataclass.territory.cosmetic.ICustomIcon;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.property.PlayerPropertyManager;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.listeners.interact.events.property.CreatePropertyEvent;
import org.leralix.tan.storage.PermissionManager;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TANCustomNBT;
import org.leralix.tan.utils.text.NumberUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class PropertyData extends Building {
  private final String ID;

  /**
   * Old storage method for the owner of the property. Replaced by owner field. Keep until 0.17.0 to
   * ensure retro-compatibility. Migration handled by PropertyDataDeserializer.
   */
  @Deprecated(since = "0.15.6")
  private String owningPlayerID;

  /** */
  private AbstractOwner owner;

  /** ID of the renter. Can be null if not rented. */
  private String rentingPlayerID;

  private PermissionManager permissionManager;

  private ICustomIcon icon;
  private String name;
  private String description;
  private boolean isForSale;
  private double salePrice;
  private boolean isForRent;
  private double rentPrice;
  private final Vector3D p1;
  private final Vector3D p2;
  private Vector3D signLocation;
  private Vector3D supportLocation;

  public PropertyData(String id, Vector3D p1, Vector3D p2, TerritoryData owner) {
    this(id, p1, p2, new TerritoryOwned(owner));
  }

  public PropertyData(String id, Vector3D p1, Vector3D p2, ITanPlayer owner) {
    this(id, p1, p2, new PlayerOwned(owner));
  }

  public PropertyData(String id, Vector3D p1, Vector3D p2, AbstractOwner owner) {
    this.ID = id;
    this.owner = owner;
    this.p1 = p1;
    this.p2 = p2;

    ItemStack itemStack = new ItemStack(Material.OAK_SIGN);
    this.icon = new CustomIcon(itemStack);
    this.name = "Unnamed Zone";
    this.description = "No description";
    this.isForSale = false;
    this.salePrice = 0;
    this.isForRent = false;
    this.rentingPlayerID = null;
    this.rentPrice = 0;

    this.permissionManager = new PermissionManager(RelationPermission.SELECTED_ONLY);
  }

  public Vector3D getFirstCorner() {
    return this.p1;
  }

  public Vector3D getSecondCorner() {
    return this.p2;
  }

  public String getTotalID() {
    return ID;
  }

  public void setIcon(CustomIcon icon) {
    this.icon = icon;
  }

  public ItemStack getIcon() {
    if (icon == null) {
      icon = new CustomIcon(new ItemStack(Material.OAK_SIGN));
    }
    return icon.getIcon();
  }

  private String getOwningStructureID() {
    String[] parts = ID.split("_");
    return parts[0];
  }

  public TownData getTown() {
    return TownDataStorage.getInstance().getSync(getOwningStructureID());
  }

  public String getPropertyID() {
    String[] parts = ID.split("_");
    return parts[1];
  }

  public void setName(String name) {
    this.name = name;

    org.leralix.tan.utils.FoliaScheduler.runTask(TownsAndNations.getPlugin(), this::updateSign);
  }

  public void setDescription(String description) {
    this.description = description;
    org.leralix.tan.utils.FoliaScheduler.runTask(TownsAndNations.getPlugin(), this::updateSign);
  }

  public void allocateRenter(Player renter) {
    rentingPlayerID = renter.getUniqueId().toString();
    this.isForRent = false;
    if (Constants.shouldPayRentAtStart()) payRent();
    org.leralix.tan.utils.FoliaScheduler.runTask(TownsAndNations.getPlugin(), this::updateSign);
    getPermissionManager().setAll(RelationPermission.SELECTED_ONLY);
  }

  public boolean isRented() {
    return rentingPlayerID != null;
  }

  public boolean isForRent() {
    return isForRent;
  }

  public ITanPlayer getRenter() {
    return PlayerDataStorage.getInstance().getSync(rentingPlayerID);
  }

  public String getRenterID() {
    return rentingPlayerID;
  }

  public Player getRenterPlayer() {
    if (rentingPlayerID == null) {
      return null;
    }
    try {
      return Bukkit.getPlayer(UUID.fromString(rentingPlayerID));
    } catch (IllegalArgumentException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Invalid renting player UUID for property " + name + ": " + rentingPlayerID);
      return null;
    }
  }

  public OfflinePlayer getOfflineRenter() {
    if (rentingPlayerID == null) {
      return null;
    }
    try {
      return Bukkit.getOfflinePlayer(UUID.fromString(rentingPlayerID));
    } catch (IllegalArgumentException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Invalid renting player UUID for property " + name + ": " + rentingPlayerID);
      return null;
    }
  }

  public String getDescription() {
    return description;
  }

  public void payRent() {
    if (rentingPlayerID == null) {
      return;
    }

    OfflinePlayer renter;
    try {
      renter = Bukkit.getOfflinePlayer(UUID.fromString(rentingPlayerID));
    } catch (IllegalArgumentException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Invalid renting player UUID for property " + name + ", expelling renter");
      expelRenter(true);
      return;
    }

    TerritoryData town = getTown();

    if (town == null) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Property " + name + " has no valid town, cannot collect rent");
      return;
    }

    double baseRent = getBaseRentPrice();
    double rent = getRentPrice();
    double taxRent = rent - baseRent;

    if (EconomyUtil.getBalance(renter) < rent) {
      expelRenter(true);
      return;
    }

    EconomyUtil.removeFromBalance(renter, rent);
    getOwner().addToBalance(baseRent);
    town.addToBalance(taxRent);
  }

  public AbstractOwner getOwner() {
    if (owner == null) {
      owner = new PlayerOwned(owningPlayerID);
    }
    return owner;
  }

  public String getName() {
    return name;
  }

  public boolean isForSale() {
    return this.isForSale;
  }

  public double getBaseRentPrice() {
    return this.rentPrice;
  }

  public double getRentPrice() {
    TownData town = getTown();
    if (town == null) {
      return getBaseRentPrice();
    }
    return NumberUtil.roundWithDigits(getBaseRentPrice() * (1 + town.getTaxOnRentingProperty()));
  }

  public double getBaseSalePrice() {
    return this.salePrice;
  }

  public double getSalePrice() {
    TownData town = getTown();
    if (town == null) {
      return getBaseSalePrice();
    }
    return NumberUtil.roundWithDigits(getBaseSalePrice() * (1 + town.getTaxOnBuyingProperty()));
  }

  public boolean containsLocation(Location location) {
    return Math.max(p1.getX(), p2.getX()) >= location.getX()
        && Math.min(p1.getX(), p2.getX()) <= location.getX()
        && Math.max(p1.getY(), p2.getY()) >= location.getY()
        && Math.min(p1.getY(), p2.getY()) <= location.getY()
        && Math.max(p1.getZ(), p2.getZ()) >= location.getZ()
        && Math.min(p1.getZ(), p2.getZ()) <= location.getZ();
  }

  public List<FilledLang> getBasicDescription() {
    List<FilledLang> lore = new ArrayList<>();

    lore.add(Lang.GUI_PROPERTY_DESCRIPTION.get(getDescription()));

    TownData town = getTown();
    if (town != null) {
      lore.add(Lang.GUI_PROPERTY_STRUCTURE_OWNER.get(town.getName()));
    }

    lore.add(Lang.GUI_PROPERTY_OWNER.get(getOwner().getName()));
    if (isForSale()) lore.add(Lang.GUI_PROPERTY_FOR_SALE.get(String.valueOf(salePrice)));
    else if (isRented()) {
      ITanPlayer renter = getRenter();
      if (renter != null) {
        lore.add(
            Lang.GUI_PROPERTY_RENTED_BY.get(renter.getNameStored(), String.valueOf(rentPrice)));
      }
    } else if (isForRent()) lore.add(Lang.GUI_PROPERTY_FOR_RENT.get(String.valueOf(rentPrice)));
    else {
      lore.add(Lang.GUI_PROPERTY_NOT_FOR_SALE.get());
    }
    return lore;
  }

  public void swapIsForSale() {
    this.isForSale = !this.isForSale;
    if (this.isForSale) this.isForRent = false;
    org.leralix.tan.utils.FoliaScheduler.runTask(TownsAndNations.getPlugin(), this::updateSign);
  }

  public void swapIsRent() {
    this.isForRent = !this.isForRent;
    if (this.isForRent) this.isForSale = false;
    org.leralix.tan.utils.FoliaScheduler.runTask(TownsAndNations.getPlugin(), this::updateSign);
  }

  public void showBox(Player player) {
    ParticleUtils.drawBox(
        TownsAndNations.getPlugin(),
        player,
        this.getFirstCorner(),
        this.getSecondCorner(),
        10,
        Constants.getPropertyBoundaryParticles());
  }

  public void updateSign() {
    if (signLocation == null) {
      return;
    }

    try {
      World world = Bukkit.getWorld(signLocation.getWorldID());
      if (world == null) {
        return;
      }

      Block signBlock =
          world.getBlockAt(signLocation.getX(), signLocation.getY(), signLocation.getZ());

      if (!(signBlock.getState() instanceof Sign)) {
        TownsAndNations.getPlugin()
            .getLogger()
            .warning("Property " + name + " sign location is not a sign: " + signBlock.getType());
        return;
      }

      Sign sign = (Sign) signBlock.getState();
      String[] lines = updateLines();
      SignSide signSide = sign.getSide(Side.FRONT);

      signSide.line(0, org.leralix.tan.utils.text.ComponentUtil.fromLegacy(lines[0]));
      signSide.line(1, org.leralix.tan.utils.text.ComponentUtil.fromLegacy(lines[1]));
      signSide.line(2, org.leralix.tan.utils.text.ComponentUtil.fromLegacy(lines[2]));
      signSide.line(3, org.leralix.tan.utils.text.ComponentUtil.fromLegacy(lines[3]));

      sign.update();
    } catch (Exception e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .warning("Error updating sign for property " + name + ": " + e.getMessage());
    }
  }

  private String[] updateLines() {

    String[] lines = new String[4];

    LangType langType = Lang.getServerLang();

    lines[0] = Lang.SIGN_NAME.get(langType, this.getName());
    lines[1] = Lang.SIGN_PLAYER.get(langType, getOwner().getName());

    if (this.isForSale) {
      lines[2] = Lang.SIGN_FOR_SALE.get(langType);
      lines[3] = Lang.SIGN_SALE_PRICE.get(langType, Double.toString(this.getSalePrice()));
    } else if (this.isForRent) {
      lines[2] = Lang.SIGN_RENT.get(langType);
      lines[3] = Lang.SIGN_RENT_PRICE.get(langType, Double.toString(this.getRentPrice()));
    } else if (this.isRented()) {
      lines[2] = Lang.SIGN_RENTED_BY.get(langType);
      ITanPlayer renter = this.getRenter();
      lines[3] = renter != null ? renter.getNameStored() : "Unknown";
    } else {
      lines[2] = Lang.SIGN_NOT_FOR_SALE.get(langType);
      lines[3] = "";
    }

    return lines;
  }

  public void setRentPrice(double i) {
    this.rentPrice = i;
    org.leralix.tan.utils.FoliaScheduler.runTask(TownsAndNations.getPlugin(), this::updateSign);
  }

  public void setSalePrice(double i) {
    this.salePrice = i;
    org.leralix.tan.utils.FoliaScheduler.runTask(TownsAndNations.getPlugin(), this::updateSign);
  }

  public Optional<Block> getSign() {

    World world = Bukkit.getWorld(signLocation.getWorldID());
    if (world == null) {
      return Optional.empty();
    }

    return Optional.of(
        world.getBlockAt(
            this.signLocation.getX(), this.signLocation.getY(), this.signLocation.getZ()));
  }

  public void delete() {
    TownData town = getTown();
    expelRenter(false);
    removeSign();

    town.removeProperty(this);

    if (getOwner() instanceof PlayerOwned playerOwnedClass) {
      ITanPlayer playerOwner =
          PlayerDataStorage.getInstance().getSync(playerOwnedClass.getPlayerID());
      playerOwner.removeProperty(this);

      Player player = Bukkit.getPlayer(UUID.fromString(playerOwnedClass.getPlayerID()));
      if (player != null) {
        TanChatUtils.message(
            player, Lang.PROPERTY_DELETED.get(playerOwner.getLang()), SoundEnum.MINOR_GOOD);
      }
    }
  }

  private void removeSign() {
    World world = Bukkit.getWorld(signLocation.getWorldID());
    if (world == null) {
      return;
    }

    Block signBlock = signLocation.getLocation().getBlock();
    signBlock.setType(org.bukkit.Material.AIR);

    TANCustomNBT.removeBockMetaData(signBlock, "propertySign");
    TANCustomNBT.removeBockMetaData(supportLocation.getLocation().getBlock(), "propertySign");

    world.spawnParticle(Particle.BUBBLE_POP, signBlock.getLocation(), 5);
  }

  public void buyProperty(Player buyer) {
    LangType langType = PlayerDataStorage.getInstance().getSync(buyer).getLang();

    double playerBalance = EconomyUtil.getBalance(buyer);
    double cost = getSalePrice();
    if (playerBalance < cost) {
      TanChatUtils.message(
          buyer,
          Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(
              langType, Double.toString(cost - playerBalance)),
          SoundEnum.MINOR_BAD);
      return;
    }

    if (getOwner() instanceof PlayerOwned playerOwned) {
      UUID exOwnerID = UUID.fromString(playerOwned.getPlayerID());
      OfflinePlayer exOwnerOffline = Bukkit.getOfflinePlayer(exOwnerID);
      Player exOwner = exOwnerOffline.getPlayer();

      // Only send message if player is online
      if (exOwner != null) {
        TanChatUtils.message(
            exOwner,
            Lang.PROPERTY_SOLD_EX_OWNER.get(
                langType, getName(), buyer.getName(), Double.toString(getSalePrice())),
            SoundEnum.GOOD);
      }

      ITanPlayer exOwnerData = PlayerDataStorage.getInstance().getSync(exOwnerID);
      if (exOwnerData != null) {
        exOwnerData.removeProperty(this);
      }
    }

    TanChatUtils.message(
        buyer,
        Lang.PROPERTY_SOLD_NEW_OWNER.get(langType, getName(), Double.toString(getSalePrice())),
        SoundEnum.BAD);

    TownData town = getTown();
    double townCut = getSalePrice() - getBaseSalePrice();

    TownsAndNations.getPlugin()
        .getDatabaseHandler()
        .addTransactionHistory(new PropertyBuyTaxTransaction(town, this, townCut));
    EconomyUtil.removeFromBalance(buyer, getSalePrice());
    getOwner().addToBalance(getBaseSalePrice());
    town.addToBalance(townCut);

    ITanPlayer newOwnerData =
        PlayerDataStorage.getInstance().getSync(buyer.getUniqueId().toString());
    newOwnerData.addProperty(this);
    this.owner = new PlayerOwned(buyer.getUniqueId().toString());

    this.isForSale = false;
    org.leralix.tan.utils.FoliaScheduler.runTask(TownsAndNations.getPlugin(), this::updateSign);
    getPermissionManager().setAll(RelationPermission.SELECTED_ONLY);
  }

  public boolean isPlayerAllowed(ChunkPermissionType action, ITanPlayer tanPlayer) {

    if (getPermissionManager().canPlayerDo(getTown(), action, tanPlayer)) {
      return true;
    }
    if (isRented()) return tanPlayer.getID().equals(rentingPlayerID);
    return getOwner().canAccess(tanPlayer);
  }

  public String getDenyMessage(LangType langType) {
    if (isRented()) return Lang.PROPERTY_RENTED_BY.get(langType, getRenter().getNameStored());
    else return Lang.PROPERTY_BELONGS_TO.get(langType, getOwner().getName());
  }

  public void expelRenter(boolean rentBack) {
    if (!isRented()) return;
    ITanPlayer renter = PlayerDataStorage.getInstance().getSync(rentingPlayerID);
    renter.removeProperty(this);
    this.rentingPlayerID = null;
    if (rentBack) isForRent = true;
    org.leralix.tan.utils.FoliaScheduler.runTask(TownsAndNations.getPlugin(), this::updateSign);
    getPermissionManager().setAll(RelationPermission.SELECTED_ONLY);
  }

  public boolean isInChunk(ClaimedChunk2 chunk) {
    int minX = Math.min(p1.getX() >> 4, p2.getX() >> 4);
    int maxX = Math.max(p1.getX() >> 4, p2.getX() >> 4);
    int minZ = Math.min(p1.getZ() >> 4, p2.getZ() >> 4);
    int maxZ = Math.max(p1.getZ() >> 4, p2.getZ() >> 4);
    int chunkX = chunk.getX();
    int chunkZ = chunk.getZ();

    return (chunkX >= minX && chunkX <= maxX && chunkZ >= minZ && chunkZ <= maxZ);
  }

  public PermissionManager getPermissionManager() {
    if (permissionManager == null) {
      permissionManager = new PermissionManager(RelationPermission.SELECTED_ONLY);
    }
    return permissionManager;
  }

  public void setSignData() {
    TANCustomNBT.setBockMetaData(
        signLocation.getLocation().getBlock(), "propertySign", getTotalID());
    TANCustomNBT.setBockMetaData(
        supportLocation.getLocation().getBlock(), "propertySign", getTotalID());
  }

  public void createPropertySign(Player player, Block block, BlockFace blockFace) {
    // Calcul de la position de la pancarte
    Location selectedSignLocation = block.getRelative(blockFace).getLocation();
    selectedSignLocation
        .getBlock()
        .setType(blockFace == BlockFace.UP ? Material.OAK_SIGN : Material.OAK_WALL_SIGN);

    BlockState blockState = selectedSignLocation.getBlock().getState();
    Sign sign = (Sign) blockState;

    // Gestion de l'orientation pour les pancartes murales
    if (blockFace != BlockFace.UP) {
      BlockFace direction =
          CreatePropertyEvent.getTopDirection(block.getLocation(), player.getLocation());
      Directional directional = (Directional) sign.getBlockData();
      directional.setFacing(direction);
      sign.setBlockData(directional);
    } else {
      org.bukkit.block.data.type.Sign signData =
          (org.bukkit.block.data.type.Sign) sign.getBlockData();
      BlockFace direction =
          CreatePropertyEvent.getTopDirection(block.getLocation(), player.getLocation());
      signData.setRotation(direction);
      sign.setBlockData(signData);
    }

    sign.update();

    // Ajout des métadonnées aux blocs
    block.setMetadata(
        "propertySign", new FixedMetadataValue(TownsAndNations.getPlugin(), getTotalID()));
    sign.getBlock()
        .setMetadata(
            "propertySign", new FixedMetadataValue(TownsAndNations.getPlugin(), getTotalID()));

    this.signLocation = new Vector3D(selectedSignLocation);
    this.supportLocation = new Vector3D(block.getLocation());
    setSignData();
    updateSign();
  }

  @Override
  public GuiItem getGuiItem(
      IconManager iconManager, Player player, BasicGui basicGui, LangType langType) {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    boolean canInteract = getOwner().canAccess(tanPlayer);

    return iconManager
        .get(getIcon())
        .setName(getName())
        .setDescription(getBasicDescription())
        .setAction(
            event -> {
              if (!canInteract) {
                SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                return;
              }
              PlayerPropertyManager.open(player, this, p -> basicGui.open());
            })
        .asGuiItem(player, langType);
  }

  @Override
  public Vector3D getPosition() {
    return signLocation;
  }
}
