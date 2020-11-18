package repositories.Sitzung;

import models.Sitzung.Sitzungsverlauf;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SitzungsverlaufRepository extends MongoRepository<Sitzungsverlauf, String> {
}