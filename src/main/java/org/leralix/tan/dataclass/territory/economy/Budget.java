package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Budget {
    
    
    private final List<ProfitLine> profitList;
    
    public Budget(){
        profitList = new LinkedList<>();
    }

    public void addProfitLine(ProfitLine profitLine){
        profitList.add(profitLine);
    }


    public List<String> createLore() {
        List<String> lore = new LinkedList<>();
        for(ProfitLine profitLine : profitList){
            lore.add(profitLine.getLine());
        }
        Collections.sort(lore);
        return lore;
    }

    public void createGui(Gui gui, Player player) {
        for(ProfitLine profitLine : profitList){
            profitLine.addItems(gui, player);
        }
    }
}
