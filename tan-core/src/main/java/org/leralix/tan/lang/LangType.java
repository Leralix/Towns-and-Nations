package org.leralix.tan.lang;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;

public enum LangType {
  AFRIKAANS(
      "af",
      "https://textures.minecraft.net/texture/961a1eacc10524d1f45f23b0e487bb2fc33948d9676b418b19a3da0b109d0e3c"),
  ARABIC(
      "ar",
      "https://textures.minecraft.net/texture/a4be759a9cf7f0a19a7e8e62f23789ad1d21cebae38af9d9541676a3db001572"),
  CATALAN(
      "ca",
      "https://textures.minecraft.net/texture/8dc57d3c83daf66d03677737d9769eae36b5b478d052759b0b4479fc16d55a02"),
  CZECH(
      "cs",
      "https://textures.minecraft.net/texture/79bbccc2102644d4e36dfabe735f0f9307adc5c84e20a0fdc5b3fa3de480d498"),
  DANISH(
      "da",
      "https://textures.minecraft.net/texture/10c23055c392606f7e531daa2676ebe2e348988810c15f15dc5b3733998232"),
  GERMAN(
      "de",
      "https://textures.minecraft.net/texture/21c23097ef1762d51df297f9c445708d9439ff91746d2f677b28dddfa27316"),
  GREEK(
      "el",
      "https://textures.minecraft.net/texture/1514de6dd2b7682b1d3ebcd10291ae1f021e3012b5c8beffeb75b1819eb4259d"),
  ENGLISH(
      "en",
      "https://textures.minecraft.net/texture/879d99d9c46474e2713a7e84a95e4ce7e8ff8ea4d164413a592e4435d2c6f9dc"),
  SPANISH(
      "es-ES",
      "https://textures.minecraft.net/texture/32bd4521983309e0ad76c1ee29874287957ec3d96f8d889324da8c887e485ea8"),
  FINNISH(
      "fi",
      "https://textures.minecraft.net/texture/59f2349729a7ec8d4b1478adfe5ca8af96479e983fbad238ccbd81409b4ed"),
  FRENCH(
      "fr",
      "https://textures.minecraft.net/texture/51269a067ee37e63635ca1e723b676f139dc2dbddff96bbfef99d8b35c996bc"),
  HEBREW(
      "he",
      "https://textures.minecraft.net/texture/5a1b4fa09b3a63273cb2a7cde2f5cd62c3218faca7525d9d4a44c2eee3a2963c"),
  HUNGARIAN(
      "hu",
      "https://textures.minecraft.net/texture/4a9c3c4b6c5031332dd2bfece5e31e999f8deff55474065cc86993d7bdcdbd0"),
  ITALIAN(
      "it",
      "https://textures.minecraft.net/texture/85ce89223fa42fe06ad65d8d44ca412ae899c831309d68924dfe0d142fdbeea4"),
  JAPANESE(
      "ja",
      "https://textures.minecraft.net/texture/d640ae466162a47d3ee33c4076df1cab96f11860f07edb1f0832c525a9e33323"),
  KOREAN(
      "ko",
      "https://textures.minecraft.net/texture/fc1be5f12f45e413eda56f3de94e08d90ede8e339c7b1e8f32797390e9a5f"),
  DUTCH(
      "nl",
      "https://textures.minecraft.net/texture/c23cf210edea396f2f5dfbced69848434f93404eefeabf54b23c073b090adf"),
  NORWEGIAN(
      "no",
      "https://textures.minecraft.net/texture/fda048bc153b38467e76a3347f38396860a8bc68603931e91f7af58bec57383d"),
  POLISH(
      "pl",
      "https://textures.minecraft.net/texture/7fa269b8a663f52d6323dcd92edcc83d5f91d508afa819882deda15375f03d"),
  PORTUGUESE(
      "pt-PT",
      "https://textures.minecraft.net/texture/ebd51f4693af174e6fe1979233d23a40bb987398e3891665fafd2ba567b5a53a"),
  PORTUGUESE_BRAZIL(
      "pt-BR",
      "https://textures.minecraft.net/texture/9a46475d5dcc815f6c5f2859edbb10611f3e861c0eb14f088161b3c0ccb2b0d9"),
  VIETNAMESE(
      "vi",
      "https://textures.minecraft.net/texture/8a57b9d7dd04169478cfdb8d0b6fd0b8c82b6566bb28371ee9a7c7c1671ad0bb"),
  ROMANIAN(
      "ro",
      "https://textures.minecraft.net/texture/dceb1708d5404ef326103e7b60559c9178f3dce729007ac9a0b498bdebe46107"),
  RUSSIAN(
      "ru",
      "https://textures.minecraft.net/texture/16eafef980d6117dabe8982ac4b4509887e2c4621f6a8fe5c9b735a83d775ad"),
  SERBIAN(
      "sr",
      "https://textures.minecraft.net/texture/76461165e48b86c56bb98f48b201aef05a30c914e90f4515f05219c6827e7e1d"),
  SWEDISH(
      "sv-SE",
      "https://textures.minecraft.net/texture/1b85f8114dea93cdee01ab8fe5cfca09c984c2459776bf626e349503702f21eb"),
  THAI(
      "th",
      "http://textures.minecraft.net/texture/b15be3cdc1af14ca15155517110ba326d9945007a889e6681999c37d07bd65f5"),
  TURKISH(
      "tr",
      "https://textures.minecraft.net/texture/9852b9aba3482348514c1034d0affe73545c9de679ae4647f99562b5e5f47d09"),
  UKRAINIAN(
      "uk",
      "https://textures.minecraft.net/texture/28b9f52e36aa5c7caaa1e7f26ea97e28f635e8eac9aef74cec97f465f5a6b51"),
  CHINESE_SIMPLIFIED(
      "zh-CN",
      "https://textures.minecraft.net/texture/7f9bc035cdc80f1ab5e1198f29f3ad3fdd2b42d9a69aeb64de990681800b98dc"),
  CHINESE_TRADITIONAL(
      "zh-TW",
      "https://textures.minecraft.net/texture/702a4afb2e1e2e3a1894a8b74272f95cfa994ce53907f9ac140bd3c932f9f");

  private static final Map<String, LangType> CODE_MAP = new HashMap<>();

  static {
    for (LangType lang : values()) {
      CODE_MAP.put(lang.code, lang);
    }
  }

  private final String code;
  private final String url;

  LangType(String code, String url) {
    this.code = code;
    this.url = url;
  }

  public static LangType of(Player player) {
    return PlayerDataStorage.getInstance().getSync(player).getLang();
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

  public ItemStack getIcon(LangType langType) {
    return HeadUtils.makeSkullURL(
        getName(),
        url,
        Lang.PERCENT_COMPLETED.get(langType, Integer.toString(Lang.getCompletionPercentage(this))));
  }
}
