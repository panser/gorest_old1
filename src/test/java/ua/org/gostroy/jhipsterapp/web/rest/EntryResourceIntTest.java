package ua.org.gostroy.jhipsterapp.web.rest;

import ua.org.gostroy.jhipsterapp.Application;
import ua.org.gostroy.jhipsterapp.domain.Entry;
import ua.org.gostroy.jhipsterapp.repository.EntryRepository;
import ua.org.gostroy.jhipsterapp.service.EntryService;
import ua.org.gostroy.jhipsterapp.web.rest.dto.EntryDTO;
import ua.org.gostroy.jhipsterapp.web.rest.mapper.EntryMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the EntryResource REST controller.
 *
 * @see EntryResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class EntryResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Z"));

    private static final String DEFAULT_TITLE = "AAAAA";
    private static final String UPDATED_TITLE = "BBBBB";
    
    private static final String DEFAULT_CONTENT = "AAAAA";
    private static final String UPDATED_CONTENT = "BBBBB";

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_STR = dateTimeFormatter.format(DEFAULT_DATE);

    @Inject
    private EntryRepository entryRepository;

    @Inject
    private EntryMapper entryMapper;

    @Inject
    private EntryService entryService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restEntryMockMvc;

    private Entry entry;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EntryResource entryResource = new EntryResource();
        ReflectionTestUtils.setField(entryResource, "entryService", entryService);
        ReflectionTestUtils.setField(entryResource, "entryMapper", entryMapper);
        this.restEntryMockMvc = MockMvcBuilders.standaloneSetup(entryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        entryRepository.deleteAll();
        entry = new Entry();
        entry.setTitle(DEFAULT_TITLE);
        entry.setContent(DEFAULT_CONTENT);
        entry.setDate(DEFAULT_DATE);
    }

    @Test
    public void createEntry() throws Exception {
        int databaseSizeBeforeCreate = entryRepository.findAll().size();

        // Create the Entry
        EntryDTO entryDTO = entryMapper.entryToEntryDTO(entry);

        restEntryMockMvc.perform(post("/api/entrys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(entryDTO)))
                .andExpect(status().isCreated());

        // Validate the Entry in the database
        List<Entry> entrys = entryRepository.findAll();
        assertThat(entrys).hasSize(databaseSizeBeforeCreate + 1);
        Entry testEntry = entrys.get(entrys.size() - 1);
        assertThat(testEntry.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testEntry.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testEntry.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = entryRepository.findAll().size();
        // set the field null
        entry.setDate(null);

        // Create the Entry, which fails.
        EntryDTO entryDTO = entryMapper.entryToEntryDTO(entry);

        restEntryMockMvc.perform(post("/api/entrys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(entryDTO)))
                .andExpect(status().isBadRequest());

        List<Entry> entrys = entryRepository.findAll();
        assertThat(entrys).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllEntrys() throws Exception {
        // Initialize the database
        entryRepository.save(entry);

        // Get all the entrys
        restEntryMockMvc.perform(get("/api/entrys?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(entry.getId())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE_STR)));
    }

    @Test
    public void getEntry() throws Exception {
        // Initialize the database
        entryRepository.save(entry);

        // Get the entry
        restEntryMockMvc.perform(get("/api/entrys/{id}", entry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(entry.getId()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE_STR));
    }

    @Test
    public void getNonExistingEntry() throws Exception {
        // Get the entry
        restEntryMockMvc.perform(get("/api/entrys/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateEntry() throws Exception {
        // Initialize the database
        entryRepository.save(entry);

		int databaseSizeBeforeUpdate = entryRepository.findAll().size();

        // Update the entry
        entry.setTitle(UPDATED_TITLE);
        entry.setContent(UPDATED_CONTENT);
        entry.setDate(UPDATED_DATE);
        EntryDTO entryDTO = entryMapper.entryToEntryDTO(entry);

        restEntryMockMvc.perform(put("/api/entrys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(entryDTO)))
                .andExpect(status().isOk());

        // Validate the Entry in the database
        List<Entry> entrys = entryRepository.findAll();
        assertThat(entrys).hasSize(databaseSizeBeforeUpdate);
        Entry testEntry = entrys.get(entrys.size() - 1);
        assertThat(testEntry.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testEntry.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testEntry.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    public void deleteEntry() throws Exception {
        // Initialize the database
        entryRepository.save(entry);

		int databaseSizeBeforeDelete = entryRepository.findAll().size();

        // Get the entry
        restEntryMockMvc.perform(delete("/api/entrys/{id}", entry.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Entry> entrys = entryRepository.findAll();
        assertThat(entrys).hasSize(databaseSizeBeforeDelete - 1);
    }
}
