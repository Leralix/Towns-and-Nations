package org.leralix.tan.gui.service;

import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.gui.service.requirements.IndividualRequirementWithCost;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.Placeholder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Requirements {

    List<IndividualRequirement> individualRequirementList;

    public Requirements(){
        this.individualRequirementList = new ArrayList<>();
    }

    public void add(Collection<IndividualRequirement> requirements) {
        individualRequirementList.addAll(requirements);
    }

    public List<Placeholder> getRequirementsParagraph(){
        List<Placeholder> res = new ArrayList<>();

        res.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3);
        for(var requirement : individualRequirementList){
            Placeholder newLine = requirement.getLine();
            if(newLine != null){
                res.add(newLine);
            }
        }
        return res;
    }

    public boolean isInvalid(){
        for(var requirement : individualRequirementList){
            if(requirement.isInvalid()){
                return true;
            }
        }
        return false;
    }


    public boolean isEmpty() {
        return individualRequirementList.isEmpty();
    }

    public void actionConsume() {
        for(var requirement : individualRequirementList){
            if(requirement instanceof  IndividualRequirementWithCost individualRequirementWithCost){
                individualRequirementWithCost.actionDone();
            }
        }
    }
}
