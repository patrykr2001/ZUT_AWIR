package edu.zut.awir.awir6.web.rest;

import edu.zut.awir.awir6.service.UserService;
import edu.zut.awir.awir6.web.rest.dto.UserDto;
import edu.zut.awir.awir6.web.rest.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService service;

    @GetMapping
    public List<UserDto> list() {
        return service.findAll().stream().map(UserMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        var u = service.findById(id);
        if (u == null) throw new NotFoundException("User",
                id);
        return UserMapper.toDto(u);
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody
                                          UserDto payload) {
        var user = UserMapper.toEntity(payload);
        user.setId(null); // ID generowane przez bazę
        var saved = service.save(user);
        return
                ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @Valid
    @RequestBody UserDto payload) {
        var existing = service.findById(id);
        if (existing == null) throw new
                NotFoundException("User", id);
        var user = UserMapper.toEntity(payload);
        user.setId(id);
        return UserMapper.toDto(service.save(user));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        var existing = service.findById(id);
        if (existing == null) throw new
                NotFoundException("User", id);
        service.delete(id);
    }

    public static class NotFoundException extends
            RuntimeException {
        public NotFoundException(String what, Object id) {
            super(what + " " + id + " not found");
        }
    }
}
