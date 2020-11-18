package repositories.Person;

import models.Person.Fraktion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraktionRepository extends MongoRepository<Fraktion, String> {
}