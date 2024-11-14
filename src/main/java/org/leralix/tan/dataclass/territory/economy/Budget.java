package org.leralix.tan.dataclass.territory.economy;

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
        return lore;
    }
}
