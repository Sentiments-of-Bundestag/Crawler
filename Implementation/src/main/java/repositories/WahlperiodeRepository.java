package repositories;

import models.Wahlperiode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WahlperiodeRepository extends MongoRepository<Wahlperiode, String> {
}