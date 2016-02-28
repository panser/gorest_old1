package ua.org.gostroy.jhipsterapp.service;

import ua.org.gostroy.jhipsterapp.domain.Entry;
import ua.org.gostroy.jhipsterapp.repository.EntryRepository;
import ua.org.gostroy.jhipsterapp.web.rest.dto.EntryDTO;
import ua.org.gostroy.jhipsterapp.web.rest.mapper.EntryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Entry.
 */
@Service
public class EntryService {

    private final Logger log = LoggerFactory.getLogger(EntryService.class);
    
    @Inject
    private EntryRepository entryRepository;
    
    @Inject
    private EntryMapper entryMapper;
    
    /**
     * Save a entry.
     * @return the persisted entity
     */
    public EntryDTO save(EntryDTO entryDTO) {
        log.debug("Request to save Entry : {}", entryDTO);
        Entry entry = entryMapper.entryDTOToEntry(entryDTO);
        entry = entryRepository.save(entry);
        EntryDTO result = entryMapper.entryToEntryDTO(entry);
        return result;
    }

    /**
     *  get all the entrys.
     *  @return the list of entities
     */
    public Page<Entry> findAll(Pageable pageable) {
        log.debug("Request to get all Entrys");
        Page<Entry> result = entryRepository.findAll(pageable); 
        return result;
    }

    /**
     *  get one entry by id.
     *  @return the entity
     */
    public EntryDTO findOne(String id) {
        log.debug("Request to get Entry : {}", id);
        Entry entry = entryRepository.findOne(id);
        EntryDTO entryDTO = entryMapper.entryToEntryDTO(entry);
        return entryDTO;
    }

    /**
     *  delete the  entry by id.
     */
    public void delete(String id) {
        log.debug("Request to delete Entry : {}", id);
        entryRepository.delete(id);
    }
}
