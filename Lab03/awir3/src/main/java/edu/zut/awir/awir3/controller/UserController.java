package edu.zut.awir.awir3.controller;

import edu.zut.awir.awir3.model.User;
import edu.zut.awir.awir3.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("user", new User());
        return "add-user";
    }

    @PostMapping
    public String processForm(@Valid @ModelAttribute("user")
                              User user,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            return "add-user";
        }
        service.save(user);
        return "redirect:/users/list";
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("users", service.findAll());
        return "user-list";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", service.findById(id));
        return "edit-user";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("user") User
                                 user,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "edit-user";
        }
        user.setId(id);
        service.save(user);
        return "redirect:/users/list";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/users/list";
    }
}
