package org.leralix.tan.gui.cosmetic.type;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.gui.service.Requirements;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class IconBuilder {

    private String name;
    private final List<FilledLang> description;
    private final Requirements requirements;
    private Consumer<InventoryClickEvent> action;
    private boolean hideItemFlags;
    private final IconType menuIcon;
    private boolean hidePrerequisites;
    private final List<Lang> clickForActionMessage;

    public IconBuilder(IconType menuIcon) {
        this.description = new ArrayList<>();
        this.hideItemFlags = false;
        this.requirements = new Requirements();
        this.clickForActionMessage = new ArrayList<>();
        this.clickForActionMessage.add(Lang.GUI_GENERIC_CLICK_TO_OPEN);
        this.hidePrerequisites = false;
        if (menuIcon == null) {
            this.menuIcon = new ItemIconBuilder(Material.BARRIER);
        } else {
            this.menuIcon = menuIcon;
        }
    }


    public IconBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public IconBuilder setDescription(FilledLang... descriptions) {
        return setDescription(List.of(descriptions));
    }

    public IconBuilder setDescription(Collection<FilledLang> description) {
        this.description.clear();
        this.description.addAll(description);
        return this;
    }

    public IconBuilder setRequirements(IndividualRequirement... requirements) {
        return setRequirements(List.of(requirements));
    }

    public IconBuilder setRequirements(Collection<IndividualRequirement> requirements) {
        this.requirements.add(requirements);
        return this;
    }
    public IconBuilder setClickToAcceptMessage(Lang... messages){
        return setClickToAcceptMessage(List.of(messages));
    }
    public IconBuilder setClickToAcceptMessage(Collection<Lang> messages){
        this.clickForActionMessage.clear();
        this.clickForActionMessage.addAll(messages);
        return this;
    }

    public IconBuilder setAction(Consumer<InventoryClickEvent> action) {
        this.action = action;
        return this;
    }

    public IconBuilder setHideItemFlags(boolean hideItemFlags) {
        this.hideItemFlags = hideItemFlags;
        return this;
    }

    public IconBuilder hidePrerequisite(boolean hidePrerequisites){
        this.hidePrerequisites = hidePrerequisites;
        return this;
    }

    public GuiItem asGuiItem(Player player, LangType langType) {
        ItemStack item = menuIcon.getItemStack(player);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(generateDescription(langType));
            if (hideItemFlags) {
                meta.getItemFlags().add(ItemFlag.HIDE_ATTRIBUTES);
                meta.getItemFlags().add(ItemFlag.HIDE_ENCHANTS);
                meta.getItemFlags().add(ItemFlag.HIDE_UNBREAKABLE);
                meta.getItemFlags().add(ItemFlag.HIDE_PLACED_ON);
                meta.getItemFlags().add(ItemFlag.HIDE_DYE);
            }
            item.setItemMeta(meta);
        }
        if (action == null) {
            return ItemBuilder.from(item).asGuiItem(event -> event.setCancelled(true));
        } else {
            return ItemBuilder.from(item).asGuiItem(event -> {
                if (requirements.isInvalid()) {
                    TanChatUtils.message(player, Lang.GUI_TOWN_LEVEL_UP_UNI_REQ_NOT_MET.get(langType), SoundEnum.NOT_ALLOWED);
                    return;
                }
                requirements.actionConsume();
                action.accept(event);
            });
        }
    }

    private List<String> generateDescription(LangType langType) {
        List<String> res = new ArrayList<>();

        for(FilledLang filledLang : description){
            res.add(filledLang.get(langType));
        }

        if (!hidePrerequisites && action != null && !requirements.isEmpty()) {
            res.add("");
            res.addAll(requirements.getRequirementsParagraph(langType));
        }
        res.add("");
        for(Lang messages : clickForActionMessage){
            res.add(messages.get(langType));
        }
        return res;
    }

}
