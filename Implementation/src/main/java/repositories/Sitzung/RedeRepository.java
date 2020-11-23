package repositories.Sitzung;

import models.Sitzung.Rede;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedeRepository extends MongoRepository<Rede, String> {
}