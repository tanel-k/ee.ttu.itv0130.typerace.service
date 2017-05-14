package ee.ttu.itv0130.typerace.service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import ee.ttu.itv0130.typerace.service.model.Word;

public interface WordRepository extends MongoRepository<Word, String> {

}
