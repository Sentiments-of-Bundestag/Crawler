package repositories;

import models.Protokoll;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProtokollRepository extends MongoRepository<Protokoll, String> {
}