package de.wirvsvirus.hack.repository.microstream;

import de.wirvsvirus.hack.model.AggregateRoot;
import lombok.Data;

@Data
public class MigrationMetadata implements AggregateRoot {

  boolean mockDataCreated;

}
