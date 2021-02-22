package de.wirvsvirus.hack.service;

import de.wirvsvirus.hack.model.Sentiment;
import org.springframework.stereotype.Service;

@Service
public class SentimentTextDefaultsService {

  public String getDefaultTextForSentiment(final Sentiment sentiment) {
    final String defaultText;
    switch (sentiment) {
      case thundery:
        defaultText = "stürmisch";
        break;
      case cloudyNight:
        defaultText = "finster";
        break;
      case windy:
        defaultText = "trüb";
        break;
      case cloudy:
        defaultText = "lau";
        break;
      case sunnyWithClouds:
        defaultText = "heiter";
        break;
      case sunny:
        defaultText = "heiss";
        break;
      default:
        throw new IllegalStateException();
    }
    return defaultText;
  }

  private SentimentTextDefaultsService() {
  }
}
