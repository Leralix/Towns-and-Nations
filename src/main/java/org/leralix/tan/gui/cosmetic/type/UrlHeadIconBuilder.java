package org.leralix.tan.gui.cosmetic.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.leralix.tan.utils.HeadUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class UrlHeadIconBuilder extends IconBuilder{

    private final String headUrl;

    public UrlHeadIconBuilder(String headUrl) {
        this.headUrl = headUrl;
    }

    @Override
    protected ItemStack getItemStack(Player player) {
        PlayerProfile playerProfile = getProfile(getUrl());
        return createSkull(playerProfile);
    }



    private URL getUrl() {
        try {
            return new URL(headUrl);
        } catch (MalformedURLException e) {
            try {
                return new URL("http://textures.minecraft.net/texture/e7f9c6fef2ad96b3a5465642ba954671be1c4543e2e25e56aef0a47d5f1f");
            } catch (MalformedURLException e2) {
                throw new IllegalArgumentException("Invalid URL: " + headUrl);
            }
        }
    }

    private static PlayerProfile getProfile(URL url) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        textures.setSkin(url);
        profile.setTextures(textures);
        return profile;
    }

    private ItemStack createSkull(PlayerProfile playerProfile) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwnerProfile(playerProfile);
        skull.setItemMeta(meta);
        return skull;
    }
}
