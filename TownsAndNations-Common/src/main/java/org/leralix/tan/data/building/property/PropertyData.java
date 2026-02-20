package org.leralix.tan.data.building.property;

import dev.triumphteam.gui.guis.GuiItem;
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
import org.leralix.tan.data.building.Building;
import org.leralix.tan.data.building.property.owner.AbstractOwner;
import org.leralix.tan.data.building.property.owner.PlayerOwned;
import org.leralix.tan.data.building.property.owner.TerritoryOwned;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.cosmetic.CustomIcon;
import org.leralix.tan.data.territory.cosmetic.ICustomIcon;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.data.territory.permission.PermissionGiven;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.property.PlayerPropertyManager;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.listeners.interact.events.property.CreatePropertyEvent;
import org.leralix.tan.storage.PermissionManager;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.storage.database.transactions.instance.RentingPropertyTransaction;
import org.leralix.tan.storage.database.transactions.instance.SellingPropertyTransaction;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TANCustomNBT;
import org.leralix.tan.utils.text.NumberUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.buildings.TanOwner;
import org.tan.api.interfaces.buildings.TanProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PropertyData extends Building implements TanProperty {

    private static final String PROPERTY_SIGN_METADATA = "propertySign";

    private final String ID;
    /**
     * Owner of the property
     */
    private AbstractOwner owner;
    /**
     * ID of the renter. Can be null if not rented.
     */
    private UUID rentingPlayerID;
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

        this.permissionManager = new PermissionManager(PermissionGiven.PROPERTY);
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
    
    @Override
    public String getID() {
        return getPropertyID();
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

    public TownData getTown() {
        return TownDataStorage.getInstance().get(getOwningStructureID());
    }

    private String getOwningStructureID() {
        String[] parts = ID.split("_");
        return parts[0];
    }

    public String getPropertyID() {
        String[] parts = ID.split("_");
        return parts[1];
    }

    public void setName(String name) {
        this.name = name;

        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), this::updateSign);
    }

    public void setDescription(String description) {
        this.description = description;
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), this::updateSign);
    }

    public void allocateRenter(Player renter) {
        rentingPlayerID = renter.getUniqueId();
        this.isForRent = false;
        if (Constants.shouldPayRentAtStart())
            payRent();
        this.updateSign();
        getPermissionManager().setAll(PermissionGiven.PROPERTY);
    }

    @Override
    public boolean isRented() {
        return rentingPlayerID != null;
    }

    public boolean isForRent() {
        return isForRent;
    }

    @Override
    public Optional<TanPlayer> getRenter() {
        if(rentingPlayerID == null){
            return Optional.empty();
        }
        else {
            return Optional.of(PlayerDataStorage.getInstance().get(rentingPlayerID));
        }
    }

    public UUID getRenterID() {
        return rentingPlayerID;
    }

    public Player getRenterPlayer() {
        UUID renterUuid = getRenterID();
        if (renterUuid == null) {
            return null;
        }
        return Bukkit.getPlayer(renterUuid);
    }

    public OfflinePlayer getOfflineRenter() {
        UUID renterUuid = getRenterID();
        if (renterUuid == null) {
            return Bukkit.getOfflinePlayer(new UUID(0L, 0L));
        }
        return Bukkit.getOfflinePlayer(renterUuid);
    }

    public String getDescription() {
        return description;
    }

    public void payRent() {

        UUID renterUuid = getRenterID();
        if (renterUuid == null) {
            expelRenter(true);
            return;
        }

        OfflinePlayer renter = Bukkit.getOfflinePlayer(renterUuid);
        TerritoryData town = getTown();

        double baseRent = getRentPrice();
        double rent = getRentPriceWithTax();
        double taxRent = rent - baseRent;

        if (EconomyUtil.getBalance(renter) < rent) {
            expelRenter(true);
            return;
        }

        EconomyUtil.removeFromBalance(renter, rent);
        getOwner().addToBalance(baseRent);
        town.addToBalance(taxRent);

        TransactionManager.getInstance().register(
                new RentingPropertyTransaction(
                        town.getID(),
                        getPropertyID(),
                        owner.getID(),
                        renter.getUniqueId().toString(),
                        baseRent,
                        town.getTaxOnRentingProperty()
                )
        );
    }

    @Override
    public TanOwner getOwner() {
        if (owner == null) {
            owner = new TerritoryOwned(getTown());
        }
        return owner;
    }

    public String getName() {
        return name;
    }

    public boolean isForSale() {
        return this.isForSale;
    }

    public double getRentPrice() {
        return this.rentPrice;
    }

    public double getRentPriceWithTax() {
        return NumberUtil.roundWithDigits(getRentPrice() * (1 + getTown().getTaxOnRentingProperty()));
    }

    public double getSalePrice() {
        return this.salePrice;
    }

    public double getPrice() {
        return this.salePrice;
    }

    public double getPriceWithTax() {
        return NumberUtil.roundWithDigits(getPrice() * (1 + getTown().getTaxOnBuyingProperty()));
    }

    public boolean containsLocation(Location location) {
        return Math.max(p1.getX(), p2.getX()) >= location.getX() && Math.min(p1.getX(), p2.getX()) <= location.getX() &&
                Math.max(p1.getY(), p2.getY()) >= location.getY() && Math.min(p1.getY(), p2.getY()) <= location.getY() &&
                Math.max(p1.getZ(), p2.getZ()) >= location.getZ() && Math.min(p1.getZ(), p2.getZ()) <= location.getZ();
    }

    public List<FilledLang> getBasicDescription(LangType langType) {
        List<FilledLang> lore = new ArrayList<>();

        lore.add(Lang.GUI_PROPERTY_DESCRIPTION.get(getDescription()));
        lore.add(Lang.GUI_PROPERTY_STRUCTURE_OWNER.get(getTown().getName()));

        lore.add(Lang.GUI_PROPERTY_OWNER.get(getOwner().getName()));
        if (isForSale())
            lore.add(Lang.GUI_PROPERTY_FOR_SALE.get(String.valueOf(salePrice)));
        else if (isRented())
            lore.add(Lang.GUI_PROPERTY_RENTED_BY.get(getRenterDisplayName(langType), String.valueOf(rentPrice)));
        else if (isForRent())
            lore.add(Lang.GUI_PROPERTY_FOR_RENT.get(String.valueOf(rentPrice)));
        else {
            lore.add(Lang.GUI_PROPERTY_NOT_FOR_SALE.get());
        }
        return lore;
    }

    public void swapIsForSale() {
        this.isForSale = !this.isForSale;
        if (this.isForSale)
            this.isForRent = false;
        updateSign();
    }

    public void swapIsRent() {
        this.isForRent = !this.isForRent;
        if (this.isForRent)
            this.isForSale = false;
        updateSign();
    }

    public void showBox(Player player) {
        ParticleUtils.drawBox(TownsAndNations.getPlugin(), player, this.getFirstCorner(), this.getSecondCorner(), 10, Constants.getPropertyBoundaryParticles());
    }

    public void updateSign() {
        World world = Bukkit.getWorld(signLocation.getWorldID());
        if (world == null)
            return;
        Block signBlock = world.getBlockAt(signLocation.getX(), signLocation.getY(), signLocation.getZ());

        Sign sign = (Sign) signBlock.getState();

        String[] lines = updateLines();

        SignSide signSide = sign.getSide(Side.FRONT);

        signSide.setLine(0, lines[0]);
        signSide.setLine(1, lines[1]);
        signSide.setLine(2, lines[2]);
        signSide.setLine(3, lines[3]);

        sign.update();
    }

    private String[] updateLines() {

        String[] lines = new String[4];

        LangType langType = Lang.getServerLang();

        lines[0] = Lang.SIGN_NAME.get(langType, this.getName());
        lines[1] = Lang.SIGN_PLAYER.get(langType, getOwner().getName());

        if (this.isForSale) {
            lines[2] = Lang.SIGN_FOR_SALE.get(langType);
            lines[3] = Lang.SIGN_SALE_PRICE.get(langType, Double.toString(this.getPriceWithTax()));
        } else if (this.isForRent) {
            lines[2] = Lang.SIGN_RENT.get(langType);
            lines[3] = Lang.SIGN_RENT_PRICE.get(langType, Double.toString(this.getRentPriceWithTax()));
        } else if (this.isRented()) {
            lines[2] = Lang.SIGN_RENTED_BY.get(langType);
            lines[3] = getRenterDisplayName(langType);
        } else {
            lines[2] = Lang.SIGN_NOT_FOR_SALE.get(langType);
            lines[3] = "";
        }

        return lines;
    }

    public void setRentPrice(double i) {
        this.rentPrice = i;
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), this::updateSign);
    }

    public void setSalePrice(double i) {
        this.salePrice = i;
        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), this::updateSign);
    }

    public Optional<Block> getSign() {

        World world = Bukkit.getWorld(signLocation.getWorldID());
        if (world == null) {
            return Optional.empty();
        }

        return Optional.of(world.getBlockAt(this.signLocation.getX(), this.signLocation.getY(), this.signLocation.getZ()));
    }

    public void delete() {
        TownData town = getTown();
        expelRenter(false);
        removeSign();

        town.removeProperty(this);

        if (getOwner() instanceof PlayerOwned playerOwnedClass) {
            UUID ownerID = UUID.fromString(playerOwnedClass.getID());
            ITanPlayer playerOwner = PlayerDataStorage.getInstance().get(ownerID);

            playerOwner.removeProperty(this);

            UUID ownerUuid;
            try {
                ownerUuid = UUID.fromString(playerOwnedClass.getID());
            } catch (IllegalArgumentException e) {
                return;
            }

            Player player = Bukkit.getPlayer(ownerUuid);
            if (player != null) {
                TanChatUtils.message(player, Lang.PROPERTY_DELETED.get(playerOwner.getLang()), SoundEnum.MINOR_GOOD);
            }
        }
    }

    public void removeSign() {
        World world = Bukkit.getWorld(signLocation.getWorldID());
        if (world == null) {
            return;
        }

        Block signBlock = signLocation.getLocation().getBlock();
        signBlock.setType(org.bukkit.Material.AIR);

        TANCustomNBT.removeBockMetaData(signBlock, PROPERTY_SIGN_METADATA);
        TANCustomNBT.removeBockMetaData(supportLocation.getLocation().getBlock(), PROPERTY_SIGN_METADATA);

        world.spawnParticle(Particle.BUBBLE_POP, signBlock.getLocation(), 5);
    }

    public void buyProperty(Player buyer, ITanPlayer buyerData) {
        LangType langType = buyerData.getLang();

        double playerBalance = EconomyUtil.getBalance(buyer);
        double cost = getPriceWithTax();
        if (playerBalance < cost) {
            TanChatUtils.message(buyer, Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(langType, Double.toString(cost - playerBalance)), SoundEnum.MINOR_BAD);
            return;
        }

        if (getOwner() instanceof PlayerOwned playerOwned) {
            UUID exOwnerID = UUID.fromString(playerOwned.getID());
            OfflinePlayer exOwnerOffline = Bukkit.getOfflinePlayer(exOwnerID);
            Player exOwner = exOwnerOffline.getPlayer();
            if (exOwner != null) {
                TanChatUtils.message(exOwner, Lang.PROPERTY_SOLD_EX_OWNER.get(langType, getName(), buyer.getName(), Double.toString(getPriceWithTax())), SoundEnum.GOOD);
            }

            ITanPlayer exOwnerData = PlayerDataStorage.getInstance().get(exOwnerID);
            exOwnerData.removeProperty(this);
        }

        TanChatUtils.message(buyer, Lang.PROPERTY_SOLD_NEW_OWNER.get(langType, getName(), Double.toString(getPriceWithTax())), SoundEnum.BAD);

        TownData town = getTown();
        double townCut = getPriceWithTax() - getPrice();

        EconomyUtil.removeFromBalance(buyer, getPriceWithTax());
        getOwner().addToBalance(getPrice());
        town.addToBalance(townCut);

        TransactionManager.getInstance().register(
                new SellingPropertyTransaction(
                        getTown().getID(),
                        getPropertyID(),
                        getOwner().getID(),
                        buyer.getUniqueId().toString(),
                        getPriceWithTax(),
                        getTown().getTaxOnBuyingProperty()
                )
        );

        ITanPlayer newOwnerData = PlayerDataStorage.getInstance().get(buyer.getUniqueId().toString());
        newOwnerData.addProperty(this);
        this.owner = new PlayerOwned(buyer.getUniqueId());

        this.isForSale = false;
        updateSign();
        getPermissionManager().setAll(PermissionGiven.PROPERTY);
    }

    public boolean isPlayerAllowed(ChunkPermissionType action, ITanPlayer tanPlayer) {

        var defaultPermission = Constants.getChunkPermissionConfig().getPropertiesPermission(action);
        if(defaultPermission.isLocked()){
            return defaultPermission.defaultRelation().isAllowed(getTown(), tanPlayer);
        }

        if (getPermissionManager().get(action).isAllowed(getTown(), tanPlayer)) {
            return true;
        }
        if (isRented())
            return tanPlayer.getID().equals(rentingPlayerID);
        return getOwner().canAccess(tanPlayer);
    }

    public String getDenyMessage(LangType langType) {
        if (isRented())
            return Lang.PROPERTY_RENTED_BY.get(langType, getRenterDisplayName(langType));
        else
            return Lang.PROPERTY_BELONGS_TO.get(langType, getOwner().getName());

    }

    public void expelRenter(boolean rentBack) {
        if (!isRented())
            return;
        ITanPlayer renter = PlayerDataStorage.getInstance().get(rentingPlayerID);
        if (renter != null) {
            renter.removeProperty(this);
        }
        this.rentingPlayerID = null;
        if (rentBack)
            isForRent = true;
        updateSign();
        getPermissionManager().setAll(PermissionGiven.PROPERTY);
    }

    public boolean isInChunk(ClaimedChunk chunk) {
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
            permissionManager = new PermissionManager(PermissionGiven.PROPERTY);
        }
        return permissionManager;
    }

    public void setSignData() {
        TANCustomNBT.setBockMetaData(signLocation.getLocation().getBlock(), PROPERTY_SIGN_METADATA, getTotalID());
        TANCustomNBT.setBockMetaData(supportLocation.getLocation().getBlock(), PROPERTY_SIGN_METADATA, getTotalID());
    }

    public void createPropertySign(Player player, Block block, BlockFace blockFace) {
        // Calcul de la position de la pancarte
        Location selectedSignLocation = block.getRelative(blockFace).getLocation();
        selectedSignLocation.getBlock().setType(blockFace == BlockFace.UP ? Material.OAK_SIGN : Material.OAK_WALL_SIGN);

        BlockState blockState = selectedSignLocation.getBlock().getState();
        Sign sign = (Sign) blockState;

        // Gestion de l'orientation pour les pancartes murales
        if (blockFace != BlockFace.UP) {
            BlockFace direction = CreatePropertyEvent.getTopDirection(block.getLocation(), player.getLocation());
            Directional directional = (Directional) sign.getBlockData();
            directional.setFacing(direction);
            sign.setBlockData(directional);
        } else {
            org.bukkit.block.data.type.Sign signData = (org.bukkit.block.data.type.Sign) sign.getBlockData();
            BlockFace direction = CreatePropertyEvent.getTopDirection(block.getLocation(), player.getLocation());
            signData.setRotation(direction);
            sign.setBlockData(signData);
        }

        sign.update();

        // Ajout des métadonnées aux blocs
        block.setMetadata(PROPERTY_SIGN_METADATA, new FixedMetadataValue(TownsAndNations.getPlugin(), getTotalID()));
        sign.getBlock().setMetadata(PROPERTY_SIGN_METADATA, new FixedMetadataValue(TownsAndNations.getPlugin(), getTotalID()));

        this.signLocation = new Vector3D(selectedSignLocation);
        this.supportLocation = new Vector3D(block.getLocation());
        setSignData();
        updateSign();
    }

    @Override
    public GuiItem getGuiItem(IconManager iconManager, Player player, BasicGui basicGui, LangType langType) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        boolean canInteract = getOwner().canAccess(tanPlayer);

        return iconManager.get(getIcon())
                .setName(getName())
                .setDescription(getBasicDescription(langType))
                .setAction(event -> {
                    if (!canInteract) {
                        SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                        return;
                    }
                    new PlayerPropertyManager(player, this, p -> basicGui.open());

                })
                .asGuiItem(player, langType);
    }

    @Override
    public Vector3D getPosition() {
        return signLocation;
    }

    public String getRenterDisplayName(LangType langType) {
        return getRenter()
                .map(TanPlayer::getNameStored)
                .orElse(Lang.GUI_UNKOWN_OWNER_OR_RENTER.get(langType));
    }

    public Vector3D getSignLocation() {
        return signLocation;
    }
}