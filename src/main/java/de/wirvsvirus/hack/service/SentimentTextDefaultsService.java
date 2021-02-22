package de.wirvsvirus.hack.service;

import de.wirvsvirus.hack.model.Sentiment;
import org.springframework.stereotype.Service;

@Service
public class SentimentTextDefaultsService {

  public String getDefaultTextForSentiment(final Sentiment sentiment) {
    final String defaultText;
    switch (sentiment) {
      case sunny:
        defaultText = "ich bin gut drauf";
        break;
      case sunnyWithClouds:
        defaultText = "ich bin heiter";
        break;
      case cloudy:
        defaultText = "nichts besonderes";
        break;
      case windy:
        defaultText = "ich halte durch";
        break;
      case cloudyNight:
        defaultText = "ich bin müde";
        break;
      case thundery:
        defaultText = "frag nicht...";
        break;
      default:
        throw new IllegalStateException();
    }
    return defaultText;
  }

  private SentimentTextDefaultsService() {
  }
}
