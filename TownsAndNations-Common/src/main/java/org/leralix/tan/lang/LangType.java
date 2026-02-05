package org.leralix.tan.lang;

import org.leralix.tan.gui.cosmetic.IconKey;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum LangType {

    AFRIKAANS(Locale.ENGLISH, "af", IconKey.FLAG_AF_ICON),
    ARABIC(Locale.ENGLISH, "ar", IconKey.FLAG_AR_ICON),
    CATALAN(Locale.ENGLISH, "ca", IconKey.FLAG_CA_ICON),
    CZECH(Locale.ENGLISH, "cs", IconKey.FLAG_CS_ICON),
    DANISH(Locale.ENGLISH, "da", IconKey.FLAG_DA_ICON),
    GERMAN(Locale.GERMAN, "de", IconKey.FLAG_DE_ICON),
    GREEK(Locale.ENGLISH, "el", IconKey.FLAG_EL_ICON),
    ENGLISH(Locale.ENGLISH, "en", IconKey.FLAG_EN_ICON),
    SPANISH(Locale.ENGLISH, "es-ES", IconKey.FLAG_ES_ES_ICON),
    FINNISH(Locale.ENGLISH, "fi", IconKey.FLAG_FI_ICON),
    FRENCH(Locale.FRENCH, "fr", IconKey.FLAG_FR_ICON),
    HEBREW(Locale.ENGLISH, "he", IconKey.FLAG_HE_ICON),
    HUNGARIAN(Locale.ENGLISH, "hu", IconKey.FLAG_HU_ICON),
    ITALIAN(Locale.ITALIAN, "it", IconKey.FLAG_IT_ICON),
    JAPANESE(Locale.JAPANESE, "ja", IconKey.FLAG_JA_ICON),
    KOREAN(Locale.KOREAN, "ko", IconKey.FLAG_KO_ICON),
    DUTCH(Locale.ENGLISH, "nl", IconKey.FLAG_NL_ICON),
    NORWEGIAN(Locale.ENGLISH, "no", IconKey.FLAG_NO_ICON),
    POLISH(Locale.ENGLISH, "pl", IconKey.FLAG_PL_ICON),
    PORTUGUESE(Locale.ENGLISH, "pt-PT", IconKey.FLAG_PT_PT_ICON),
    PORTUGUESE_BRAZIL(Locale.ENGLISH, "pt-BR", IconKey.FLAG_PT_BR_ICON),
    VIETNAMESE(Locale.ENGLISH, "vi", IconKey.FLAG_VI_ICON),
    ROMANIAN(Locale.ENGLISH, "ro", IconKey.FLAG_RO_ICON),
    RUSSIAN(Locale.ENGLISH, "ru", IconKey.FLAG_RU_ICON),
    SERBIAN(Locale.ENGLISH, "sr", IconKey.FLAG_SR_ICON),
    SWEDISH(Locale.ENGLISH, "sv-SE", IconKey.FLAG_SV_SE_ICON),
    THAI(Locale.ENGLISH, "th", IconKey.FLAG_TH_ICON),
    TURKISH(Locale.ENGLISH, "tr", IconKey.FLAG_TR_ICON),
    UKRAINIAN(Locale.ENGLISH, "uk", IconKey.FLAG_UK_ICON),
    CHINESE_SIMPLIFIED(Locale.PRC, "zh-CN", IconKey.FLAG_ZH_CN_ICON),
    CHINESE_TRADITIONAL(Locale.TAIWAN, "zh-TW", IconKey.FLAG_ZH_TW_ICON);


    private static final Map<String, LangType> CODE_MAP = new HashMap<>();

    static {
        for (LangType lang : values()) {
            CODE_MAP.put(lang.code, lang);
        }
    }

    private final Locale locale;
    private final String code;
    private final IconKey iconKey;

    LangType(Locale locale, String code, IconKey iconKey) {
        this.locale = locale;
        this.code = code;
        this.iconKey = iconKey;
    }

    public String getCode() {
        return code;
    }

    public static LangType fromCode(String code) {
        return CODE_MAP.get(code);
    }

    public String getName() {
        return name().toLowerCase();
    }

    public Locale getLocale() {
        return locale;
    }

    public IconKey getIconKey() {
        return iconKey;
    }
}
