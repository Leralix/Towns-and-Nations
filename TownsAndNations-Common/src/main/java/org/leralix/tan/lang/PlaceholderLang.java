package org.leralix.tan.lang;

public class PlaceholderLang implements Placeholder {

    private final String placeholder;
    public PlaceholderLang(String placeholder) {
        this.placeholder = placeholder;
    }

    public static PlaceholderLang of(String placeholder){
        return new PlaceholderLang(placeholder);
    }

    @Override
    public String translate(LangType langType) {
        return placeholder;
    }
}
