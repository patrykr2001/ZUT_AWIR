package edu.zut.awir.awir3.controller;

import edu.zut.awir.awir3.model.User;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {
    @GetMapping("/new")
    public String showForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "add-user";
    }

    @PostMapping
    public String processForm(@Valid @ModelAttribute("user") User user,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user",
                    result);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/users/new";
        }
        redirectAttributes.addFlashAttribute("user", user);
        return "redirect:/users/info";
    }

    @GetMapping("/info")
    public String showInfo() {
        return "user-info";
    }
}
