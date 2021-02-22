package de.wirvsvirus.hack.service;

import de.wirvsvirus.hack.model.Sentiment;
import org.springframework.stereotype.Service;

@Service
public class SentimentTextDefaultsService {

  public String getDefaultTextForSentiment(final Sentiment sentiment) {
    final String defaultText;
    switch (sentiment) {
      case sunny:
        defaultText = "Bin gut drauf";
        break;
      case sunnyWithClouds:
        defaultText = "Bin heiter";
        break;
      case cloudy:
        defaultText = "Nichts Besonderes los";
        break;
      case windy:
        defaultText = "Ich halte durch";
        break;
      case cloudyNight:
        defaultText = "Bin müde";
        break;
      case thundery:
        defaultText = "Frag lieber nicht\u2026";
        break;
      default:
        throw new IllegalStateException();
    }
    return defaultText;
  }

  private SentimentTextDefaultsService() {
  }
}
