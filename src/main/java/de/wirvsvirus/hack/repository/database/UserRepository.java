package de.wirvsvirus.hack.repository.database;

import de.wirvsvirus.hack.model.User;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, UUID>,
    WithInsert<User> {

}
