package org.leralix.tan.lang;

public class DynamicPlaceholder implements Placeholder {

    private final String dynamicKey;

    public DynamicPlaceholder(String dynamicKey){
        this.dynamicKey = dynamicKey;
    }


    @Override
    public String translate(LangType langType) {
        return DynamicLang.get(langType, dynamicKey);
    }
}
