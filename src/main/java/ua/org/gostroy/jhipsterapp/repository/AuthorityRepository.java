package ua.org.gostroy.jhipsterapp.repository;

import ua.org.gostroy.jhipsterapp.domain.Authority;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Authority entity.
 */
public interface AuthorityRepository extends MongoRepository<Authority, String> {
}
