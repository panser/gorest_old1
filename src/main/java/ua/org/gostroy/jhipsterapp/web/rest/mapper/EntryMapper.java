package ua.org.gostroy.jhipsterapp.web.rest.mapper;

import ua.org.gostroy.jhipsterapp.domain.*;
import ua.org.gostroy.jhipsterapp.web.rest.dto.EntryDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Entry and its DTO EntryDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EntryMapper {

    EntryDTO entryToEntryDTO(Entry entry);

    Entry entryDTOToEntry(EntryDTO entryDTO);
}
