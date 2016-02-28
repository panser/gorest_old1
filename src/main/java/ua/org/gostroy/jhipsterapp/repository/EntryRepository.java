package ua.org.gostroy.jhipsterapp.repository;

import ua.org.gostroy.jhipsterapp.domain.Entry;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Entry entity.
 */
public interface EntryRepository extends MongoRepository<Entry,String> {

}
