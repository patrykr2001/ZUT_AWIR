package edu.zut.awir.awir4.web.rest;

import edu.zut.awir.awir4.model.User;
import edu.zut.awir.awir4.service.UserService;
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
    public List<User> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        var u = service.findById(id);
        if (u == null) throw new NotFoundException("User",
                id);
        return u;
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody
                                       User payload) {
        payload.setId(null); // ID generowane przez bazę
        var saved = service.save(payload);
        return
                ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid
    @RequestBody User payload) {
        var existing = service.findById(id);
        if (existing == null) throw new
                NotFoundException("User", id);
        payload.setId(id);
        return service.save(payload);
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
