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
  CUTE_REDHAIR,
  SUNNY_HIPSTER(false),
  SUNNY_GIRL_WITH_FLOWER(false),
  SUNNY_GIRL_WITH_HAT(false),
  SUNNY_SPORTSMAN_SWEATING(false),
  SUNNY_KANGAROO_KNOCKED_OUT(false),
  SKELETON_DEATH(false),
  LEPRECHAUN(false),
  PIG_SHAMROCK(false),
  ICEBEAR(false);

  private final boolean isSelectableInProfile;

  StockAvatar() {
    this(true);
  }

  StockAvatar(boolean isSelectableInProfile) {
    this.isSelectableInProfile = isSelectableInProfile;
  }

  public boolean isSelectableInProfile() {
    return isSelectableInProfile;
  }
}