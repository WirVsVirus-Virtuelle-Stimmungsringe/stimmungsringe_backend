package de.wirvsvirus.hack.model;

/**
 * Marker interface for objects persisted using microstream persistence framework https://microstream.one
 *
 * Rules for datamodel changes:
 * <ul>
 *   <li>new properties must be appended after last property</li>
 *   <li>NEVER change the order of properties</li>
 *   <li>NEVER change the type/name of a property</li>
 *   <li>migrate data by extending the class @link {@link de.wirvsvirus.hack.spring.DatabaseMigration}</li>
 * </ul>
 */
public interface MicrostreamObject {

}
