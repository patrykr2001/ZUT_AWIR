package edu.zut.awir.awir7.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.zut.awir.awir7.service.DuplicateEmailException;
import edu.zut.awir.awir7.service.UserService;
import edu.zut.awir.awir7.web.rest.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserRestControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockitoBean
    UserService userService;

    @Test
    void rejectsInvalidPayload() throws Exception {
        var u = new UserDto(null, "", "not-an-email");
        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(u)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsConflictWhenEmailAlreadyExists() throws Exception {
        var u = new UserDto(null, "Jan", "jan@example.com");
        when(userService.save(any())).thenThrow(new DuplicateEmailException(u.getEmail()));

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(u)))
                .andExpect(status().isConflict());
    }
}

