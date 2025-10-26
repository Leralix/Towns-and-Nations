package org.leralix.tan.upgrade.rewards.list;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.rewards.AggregatableStat;
import org.leralix.tan.upgrade.rewards.IndividualStat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A stat representing a list of strings, such as permissions or commands.
 * For now, it is only used to represent permissions unlocked by an upgrade.
 */
public class PermissionList extends IndividualStat implements AggregatableStat<PermissionList> {

    private final List<String> values;

    public PermissionList(){
        values = Collections.emptyList();
    }

    public PermissionList(Collection<String> values){
        this.values = List.copyOf(values);
    }

    public Collection<String> getValues(){
        return values;
    }

    @Override
    public PermissionList aggregate(List<PermissionList> stats) {
        List<String> res = new ArrayList<>();
        for(PermissionList stat : stats){
            res.addAll(stat.values);
        }
        return new PermissionList(res);
    }

    @Override
    public PermissionList scale(int factor) {
        if(factor > 0){
            return this;
        }
        else {
            return new PermissionList(); // Level 0 => No values unlocked.
        }
    }

    @Override
    public String getStatReward(LangType langType, int level, int maxLevel) {
        String nbNewCommands = getMathSign(values.size());
        if(level == 0){
            return Lang.UPGRADE_LINE_INT.get(langType, Lang.UNLOCK_PERMISSION.get(langType),  "0", nbNewCommands);
        }
        else {
            return Lang.UPGRADE_LINE_INT_MAX.get(langType, Lang.UNLOCK_PERMISSION.get(langType),nbNewCommands);
        }
    }

    @Override
    public String getStatReward(LangType langType) {
        if(values.isEmpty()){
            return Lang.UPGRADE_LINE_INT_MAX.get(langType, Lang.UNLOCK_PERMISSION.get(langType), "0");
        }
        else {
            String nbNewCommands = getMathSign(values.size());
            return Lang.UPGRADE_LINE_INT_MAX.get(langType, Lang.UNLOCK_PERMISSION.get(langType), nbNewCommands);
        }
    }
}
