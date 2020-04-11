package de.wirvsvirus.hack.repository;

import de.wirvsvirus.hack.rest.dto.MyTileResponse;
import de.wirvsvirus.hack.rest.dto.OtherTileResponse;
import lombok.Data;

import java.util.List;

@Data
public class DashboardResponse {

    private MyTileResponse myTile;

    private List<OtherTileResponse> otherTiles;

}
