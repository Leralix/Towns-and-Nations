package org.leralix.tan.lang;

import java.util.Collections;
import java.util.List;

public class FilledLang implements Placeholder{

    private final Lang lang;
    private final List<Placeholder> placeholders;

    public FilledLang(Lang lang) {
        this.lang = lang;
        this.placeholders = Collections.emptyList();
    }

    public FilledLang(Lang lang, Placeholder... placeholders) {
        this(lang, List.of(placeholders));
    }

    public FilledLang(Lang lang, List<Placeholder> placeholders) {
        this.lang = lang;
        this.placeholders = placeholders;
    }

    @Override
    public String translate(LangType langType) {
        return lang.translate(langType, placeholders);
    }
}
