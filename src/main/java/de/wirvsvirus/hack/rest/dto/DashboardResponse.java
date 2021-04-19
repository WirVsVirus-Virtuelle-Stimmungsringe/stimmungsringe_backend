package de.wirvsvirus.hack.rest.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {

    private MyTileResponse myTile;

    private List<OtherTileResponse> otherTiles;

    private GroupDataResponse groupData;

}
