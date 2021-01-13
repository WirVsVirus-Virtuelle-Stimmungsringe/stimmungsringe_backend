package de.wirvsvirus.hack.spring;

import de.wirvsvirus.hack.repository.microstream.DataRoot;

public interface Database {

  DataRoot reloadRoot();

  void persist(Object... objects);

}
