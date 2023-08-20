/*
    =====================================
    @author Bereznev Nikita @CreativeWex
    =====================================
 */

import com.bereznev.clients.ClientApplication;
import com.bereznev.clients.entity.Client;
import com.bereznev.clients.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ClientApplication.class)
@AutoConfigureMockMvc
@Log4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ClientControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    private List<Client> mockClientList;

    private static final String CONTROLLER_URL = "/api/v1/clients";

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        Client client1 = new Client(0L, "Client1", "First", "first@email.com");
        Client client2 = new Client(1L, "Client2", "Second", "second@email.com");
        Client client3 = new Client(2L, "Client3", "Third", "third@email.com");
        mockClientList = List.of(client1, client2, client3);
    }

    @Test
    @DisplayName("testGetAll - должен вернуть список clients")
    public void getAll() throws Exception {
        when(clientService.getAll()).thenReturn(mockClientList);
        mockMvc.perform(get(CONTROLLER_URL))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$[0].id").value(mockClientList.get(0).getId()))
                .andExpect(jsonPath("$[0].firstName").isNotEmpty()).andExpect(jsonPath("$[0].firstName").value(mockClientList.get(0).getFirstName()))
                .andExpect(jsonPath("$[0].lastName").isNotEmpty()).andExpect(jsonPath("$[0].lastName").value(mockClientList.get(0).getLastName()))
                .andExpect(jsonPath("$[0].email").isNotEmpty()).andExpect(jsonPath("$[0].email").value(mockClientList.get(0).getEmail()))

                .andExpect(jsonPath("$[1].id").value(mockClientList.get(1).getId()))
                .andExpect(jsonPath("$[1].firstName").isNotEmpty()).andExpect(jsonPath("$[1].firstName").value(mockClientList.get(1).getFirstName()))
                .andExpect(jsonPath("$[1].lastName").isNotEmpty()).andExpect(jsonPath("$[1].lastName").value(mockClientList.get(1).getLastName()))
                .andExpect(jsonPath("$[1].email").isNotEmpty()).andExpect(jsonPath("$[1].email").value(mockClientList.get(1).getEmail()))

                .andExpect(jsonPath("$[2].id").value(mockClientList.get(2).getId()))
                .andExpect(jsonPath("$[2].firstName").isNotEmpty()).andExpect(jsonPath("$[2].firstName").value(mockClientList.get(2).getFirstName()))
                .andExpect(jsonPath("$[2].lastName").isNotEmpty()).andExpect(jsonPath("$[2].lastName").value(mockClientList.get(2).getLastName()))
                .andExpect(jsonPath("$[2].email").isNotEmpty()).andExpect(jsonPath("$[2].email").value(mockClientList.get(2).getEmail()));
        log.debug("success");
    }

    @ParameterizedTest
    @DisplayName("testGetByIdCorrect - корректные Id")
    @ValueSource(ints = {0, 1, 2})
    public void getByIdCorrect(int id) throws Exception {
        when(clientService.findById(0L)).thenReturn(mockClientList.get(0));
        when(clientService.findById(1L)).thenReturn(mockClientList.get(1));
        when(clientService.findById(2L)).thenReturn(mockClientList.get(2));

        Client client = mockClientList.get(id);

        mockMvc.perform(get(CONTROLLER_URL + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(client.getId()))
                .andExpect(jsonPath("$.firstName").isNotEmpty()).andExpect(jsonPath("$.firstName").value(client.getFirstName()))
                .andExpect(jsonPath("$.lastName").isNotEmpty()).andExpect(jsonPath("$.lastName").value(client.getLastName()))
                .andExpect(jsonPath("$.email").isNotEmpty()).andExpect(jsonPath("$.email").value(client.getEmail()));
        log.debug("success");
    }

    @Test
    @DisplayName("testGetByInvalidId - несуществующий id, должен выбрасываться ResourceNotFoundException")
    public void getByInvalidId() throws Exception {
        mockMvc.perform(get(CONTROLLER_URL + "/404"))
                .andExpect(status().isNotFound());
        log.debug("success");
    }

    @Test
    @DisplayName("testSaveCorrectData - сохранение корректного пользователя")
    public void saveCorrectData() throws Exception {
        Client validClient = new Client(10L, "John", "Doe", "john@example.com");
        String jsonClient = objectMapper.writeValueAsString(validClient);

        when(clientService.save(any())).thenReturn(validClient);
        mockMvc.perform(post(CONTROLLER_URL + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonClient))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(validClient.getId()))
                .andExpect(jsonPath("$.firstName").isNotEmpty()).andExpect(jsonPath("$.firstName").value(validClient.getFirstName()))
                .andExpect(jsonPath("$.lastName").isNotEmpty()).andExpect(jsonPath("$.lastName").value(validClient.getLastName()))
                .andExpect(jsonPath("$.email").isNotEmpty()).andExpect(jsonPath("$.email").value(validClient.getEmail()));
        log.debug("success");
    }

    public static Stream<Arguments> generateAlreadyExistedClients() {
        return Stream.of(Arguments.of(
                new Client(2L, "Aaaaa", "Bbbbb", "b@gmail.com"), // Существует с данным id
                new Client(100L, "Aaaaa1111", "Bbbbb1111", "first@email.com") // Существует с данной почтой
        ));
    }

    @DisplayName("testSaveInvalidData - сохранение уже существующего клиента, должен выбрасываться AlreadyExistsException")
    @ParameterizedTest
    @MethodSource("generateAlreadyExistedClients")
    public void saveAlreadyExistedClient(Client alreadyExistedClient) throws Exception {
        String jsonClient = objectMapper.writeValueAsString(alreadyExistedClient);

        mockMvc.perform(post(CONTROLLER_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonClient))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Already exists"));
        log.debug("success");
    }

    public static Stream<Arguments> generateInvalidClients() {
        return Stream.of(Arguments.of(
                new Client(),
                new Client(200L, "", "", "first@email.com"),
                new Client(200L, "AAA", "aaaa", ""),
                new Client(200L, "AAA", "aaaa", "email")
        ));
    }

    @ParameterizedTest
    @MethodSource("generateInvalidClients")
    @DisplayName("saveInvalidClient - некорректыне данные, должен выбрасываться BadArgumentsException")
    public void saveInvalidClient() throws Exception {
        String jsonClient = objectMapper.writeValueAsString(new Client());

        mockMvc.perform(post(CONTROLLER_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonClient))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
        log.debug("success");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void deleteByCorrectId(int id) {
        when()

    }


}
