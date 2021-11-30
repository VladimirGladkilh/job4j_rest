package ru.job4j.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.job4j.auth.AuthApplication;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuthApplication.class)
@AutoConfigureMockMvc
class PersonControllerTest {

    private Person item;

    private String itemJSON;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonRepository personRepository;

    @Before
    public void setUp() throws JsonProcessingException {
        item = new Person(1, "UserName", "pass");
        itemJSON = new ObjectMapper().writeValueAsString(item);
    }

    @Test
    public void testQuerySuccess() throws Exception {

        List<Person> items = new ArrayList<>();
        items.add(item);

        Mockito.when(personRepository.findAll()).thenReturn(items);

        mockMvc.perform(get("/person/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(personRepository).findAll();
    }


    @Test
    public void testInsertSuccess() throws Exception {
        item = new Person(1, "UserName", "pass");
        Mockito.when(personRepository.save(item)).thenReturn(item);
        itemJSON = new ObjectMapper().writeValueAsString(item);
        mockMvc.perform(post("/person/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemJSON)
                .accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isCreated());

        Mockito.verify(personRepository).save(Mockito.any(Person.class));
    }

    @Test
    public void testFindAll() throws Exception {
        List<Person> list = List.of(
                new Person(1, "Ivan", "123"),
                new Person(2, "Admin", "000"),
                new Person(3, "User1", "abc"));
        Mockito.when(personRepository.findAll()).thenReturn(list);
        mockMvc.perform(get("/person/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].password", is("123")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].login", is("User1")));
    }

    @Test
    public void testFindByID() throws Exception {
        Person person = new Person(1, "Ivan", "123");
        Mockito.when(personRepository.findById(1)).thenReturn(Optional.of(person));
        mockMvc.perform(get("/person/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.login", is("Ivan")));
    }

    @Test
    public void testCreate() throws Exception {
        Person person = new Person(1, "Ivan", "123");
        Mockito.when(personRepository.save(person)).thenReturn(person);
        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(post("/person/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdate() throws Exception {
        Person person = new Person(1, "User1", "123");
        ObjectMapper mapper = new ObjectMapper();
        Mockito.when(personRepository.save(person)).thenReturn(person);
        mockMvc.perform(put("/person/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        verify(personRepository).save(captor.capture());
        assertEquals(captor.getValue().getLogin(), "User1");
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/person/1")
                .param("id", "1"))
                .andExpect(status().isOk());

    }
}