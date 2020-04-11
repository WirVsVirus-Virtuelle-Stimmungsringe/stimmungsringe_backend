package de.wirvsvirus.hack.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardResponse {

    private MyTileResponse myTile;

    private List<OtherTileResponse> otherTiles;

}
