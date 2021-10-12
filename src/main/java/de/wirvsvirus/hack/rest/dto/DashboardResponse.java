package de.wirvsvirus.hack.rest.dto;

import java.util.List;
import javax.annotation.Nullable;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
@Builder
public class DashboardResponse {

    private MyTileResponse myTile;

    private List<OtherTileResponse> otherTiles;

    // nullable
    private GroupDataResponse groupData;

}
