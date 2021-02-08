package de.wirvsvirus.hack.push;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationData {

  @JsonProperty("click_action")
  String clickAction;
  String id;
  String status;

}
