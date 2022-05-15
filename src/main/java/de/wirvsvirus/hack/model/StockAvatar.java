package de.wirvsvirus.hack.model;

public enum StockAvatar {
  DANI,
  DIMI,
  GUDRUN,
  LISA,
  MIRIAM,
  STEFAN,
  DUPERMAN,
  CYBERPUNK,
  GUY_FAWKES,
  GIRL_HUGE_HAIR,
  AGED_HIPSTER,
  ANXIOUS_ROBOT,
  PANDA_HEART,
  CAT_HEART,
  BUILDER_HELMET,
  CORONAMASKED_MAN,
  COUCH_POTATO,
  GIRL_GOLDEN_GLASSES,
  NEAT_WOMAN,
  RAPPER_KID,
  YOUNG_TRUMP,
  HIPSTER_SUMO_HAIR(true),
  SKELETON_DEATH(true),
  PIG_SHAMROCK(true),
  LEPRECHAUN(true),
  ICEBEAR(true),
  GIRL_HAPPY_WITH_HAIR(true),
  MAN_SWEATING(true),
  GIRL_YELLOW(true),
  CUTE_REDHAIR(true),
  KANGAROO_KNOCKED_OUT(true);

  public final boolean isSvgImage;

  StockAvatar() {
    this(false);
  }

  StockAvatar(boolean isSvgImage) {
    this.isSvgImage = isSvgImage;
  }
}