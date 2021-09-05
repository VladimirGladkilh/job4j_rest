package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.job4j.auth.AuthApplication;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;


import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuthApplication.class)
@AutoConfigureMockMvc
class PersonControllerTest {
    private MockMvc mockMvc;

    private Person item;

    private String itemJSON;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonController restController = new PersonController();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(restController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter());

        Person item = myTestItemPreInitiallized;

        itemJSON = new ObjectMapper().writeValueAsString(item);
    }

    @Test
    public void testQuerySuccess() throws Exception {

        List<Person> items = new ArrayList<>();
        items.add(item);

        Mockito.when(personRepository.findAll()).thenReturn(items);

        mockMvc.perform(get("/person/").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        Mockito.verify(personRepository).findAll();
    }


    @Test
    public void testInsertSuccess() throws Exception {

        Mockito.when(personRepository.save(Mockito.any(Person.class))).thenReturn(item);

        mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON).content(itemJSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

        Mockito.verify(personRepository).save(Mockito.any(Person.class));
    }
}