package ua.org.gostroy.jhipsterapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import ua.org.gostroy.jhipsterapp.domain.Entry;
import ua.org.gostroy.jhipsterapp.service.EntryService;
import ua.org.gostroy.jhipsterapp.web.rest.util.HeaderUtil;
import ua.org.gostroy.jhipsterapp.web.rest.util.PaginationUtil;
import ua.org.gostroy.jhipsterapp.web.rest.dto.EntryDTO;
import ua.org.gostroy.jhipsterapp.web.rest.mapper.EntryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing Entry.
 */
@RestController
@RequestMapping("/api")
public class EntryResource {

    private final Logger log = LoggerFactory.getLogger(EntryResource.class);
        
    @Inject
    private EntryService entryService;
    
    @Inject
    private EntryMapper entryMapper;
    
    /**
     * POST  /entrys -> Create a new entry.
     */
    @RequestMapping(value = "/entrys",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EntryDTO> createEntry(@Valid @RequestBody EntryDTO entryDTO) throws URISyntaxException {
        log.debug("REST request to save Entry : {}", entryDTO);
        if (entryDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("entry", "idexists", "A new entry cannot already have an ID")).body(null);
        }
        EntryDTO result = entryService.save(entryDTO);
        return ResponseEntity.created(new URI("/api/entrys/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("entry", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /entrys -> Updates an existing entry.
     */
    @RequestMapping(value = "/entrys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EntryDTO> updateEntry(@Valid @RequestBody EntryDTO entryDTO) throws URISyntaxException {
        log.debug("REST request to update Entry : {}", entryDTO);
        if (entryDTO.getId() == null) {
            return createEntry(entryDTO);
        }
        EntryDTO result = entryService.save(entryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("entry", entryDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /entrys -> get all the entrys.
     */
    @RequestMapping(value = "/entrys",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<EntryDTO>> getAllEntrys(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Entrys");
        Page<Entry> page = entryService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/entrys");
        return new ResponseEntity<>(page.getContent().stream()
            .map(entryMapper::entryToEntryDTO)
            .collect(Collectors.toCollection(LinkedList::new)), headers, HttpStatus.OK);
    }

    /**
     * GET  /entrys/:id -> get the "id" entry.
     */
    @RequestMapping(value = "/entrys/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EntryDTO> getEntry(@PathVariable String id) {
        log.debug("REST request to get Entry : {}", id);
        EntryDTO entryDTO = entryService.findOne(id);
        return Optional.ofNullable(entryDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /entrys/:id -> delete the "id" entry.
     */
    @RequestMapping(value = "/entrys/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteEntry(@PathVariable String id) {
        log.debug("REST request to delete Entry : {}", id);
        entryService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("entry", id.toString())).build();
    }
}
