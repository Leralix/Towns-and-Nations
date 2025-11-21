package org.leralix.tan.war.info;

import org.leralix.tan.lang.FilledLang;

import java.util.List;

public class AttackResultCancelled extends AttackResult {

    private final long cancelledDate;

    public AttackResultCancelled(){
        this.cancelledDate = System.currentTimeMillis();
    }

    @Override
    public List<FilledLang> getResultLines() {
        return List.of(

        );
    }
}
