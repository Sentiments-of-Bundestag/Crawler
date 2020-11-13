package repository;

import model.Config.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigRepository extends MongoRepository<Configuration, String> {
}