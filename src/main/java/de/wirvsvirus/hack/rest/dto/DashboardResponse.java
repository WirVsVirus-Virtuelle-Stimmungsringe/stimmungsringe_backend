package de.wirvsvirus.hack.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardResponse {

    private MyTileResponse myTile;

    private List<OtherTileResponse> otherTiles;

    private GroupDataResponse groupData;
}
