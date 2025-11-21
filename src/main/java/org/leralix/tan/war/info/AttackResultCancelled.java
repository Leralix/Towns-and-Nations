package org.leralix.tan.war.info;

import org.leralix.tan.lang.FilledLang;

import java.util.List;

public class AttackResultCancelled extends AttackResult {


    @Override
    public List<FilledLang> getResultLines() {
        return List.of();
    }
}
