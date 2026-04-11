package edu.zut.awir.awir4.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.zut.awir.awir4.model.User;
import edu.zut.awir.awir4.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
class UserRestControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockitoBean
    UserService userService;

    @Test
    void rejectsInvalidPayload() throws Exception {
        var u = new User(null, "", "not-an-email");
        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(u)))
                .andExpect(status().isBadRequest());
    }
}
