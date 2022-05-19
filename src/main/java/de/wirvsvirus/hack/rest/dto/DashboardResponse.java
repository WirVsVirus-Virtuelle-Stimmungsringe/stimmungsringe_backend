package de.wirvsvirus.hack.rest.dto;

import java.util.List;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class DashboardResponse {

  @NonNull
  MyTileResponse myTile;

  @NonNull
  List<OtherTileResponse> otherTiles;

  @Nullable
  GroupDataResponse groupData;

}
