package edu.zut.awir.awir10.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.zut.awir.awir10.model.User;
import edu.zut.awir.awir10.repository.UserRepository;
import edu.zut.awir.awir10.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserIntegrationFlowTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;
    @Autowired
    UserRepository userRepository;

    @MockitoBean
    MailService mailService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void adminCanCreateUserViaIntegrationGateway() throws Exception {
        var payload = new User(null, "Integration User", "integration@example.com");
        mvc.perform(post("/si/users-sync")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Integration User"))
                .andExpect(jsonPath("$.email").value("integration@example.com"));

        assertTrue(userRepository.existsByEmailIgnoreCase("integration@example.com"));
    }

    @Test
    void regularUserIsForbidden() throws Exception {
        var payload = new User(null, "Integration User", "integration@example.com");
        mvc.perform(post("/si/users-sync")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth("user", "user123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    @Test
    void requestWithoutCredentialsIsUnauthorized() throws Exception {
        var payload = new User(null, "Integration User", "integration@example.com");
        mvc.perform(post("/si/users-sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isUnauthorized());
    }

    private String basicAuth(String username, String password) {
        String value = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
