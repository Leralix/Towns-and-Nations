package org.leralix.tan.gui.user.player;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class LangMenu extends IteratorGUI {


    public LangMenu(Player player){
        super(player, Lang.HEADER_SELECT_LANGUAGE, 3);
    }

    @Override
    public void open() {

        GuiUtil.createIterator(gui, getLangItems(), page, player,
                p -> new PlayerMenu(player),
                p -> nextPage(),
                p -> previousPage()
        );

        gui.setItem(3, 6, getPlayerGUI());

        gui.open(player);
    }

    private List<GuiItem> getLangItems() {
        List<GuiItem> guiItems = new ArrayList<>();
        for(LangType lang : LangType.values()){
            ItemStack langIcon = lang.getIcon();
            GuiItem langGui = ItemBuilder.from(langIcon).asGuiItem(event -> {
                tanPlayer.setLang(lang);
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_LANGUAGE_CHANGED.get(tanPlayer, lang.getName()));
                new PlayerMenu(player);
            });
            guiItems.add(langGui);
        }
        return guiItems;
    }



    private GuiItem getPlayerGUI() {
        return IconManager.getInstance().get(IconKey.HELP_TRANSLATION_ICON)
                .setName(Lang.HELP_US_TRANSLATE.get(tanPlayer))
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_OPEN.get(tanPlayer))
                .setAction(event -> {
                    TextComponent textComponent = new TextComponent(TanChatUtils.getTANString() + Lang.CLICK_HERE_TO_OPEN_BROWSER.get(tanPlayer));
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://crowdin.com/project/town-and-nation"));
                    player.spigot().sendMessage(textComponent);
                    player.closeInventory();
                })
                .asGuiItem(player);
    }


}
