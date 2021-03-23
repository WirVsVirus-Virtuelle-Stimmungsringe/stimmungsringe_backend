package de.wirvsvirus.hack.service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
@Builder
public class KickVoteInfoDto {

  long kickVotes;

  long maxVoters;

}
