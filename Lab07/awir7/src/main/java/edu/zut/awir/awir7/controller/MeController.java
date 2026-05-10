package edu.zut.awir.awir7.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MeController {

    @GetMapping("/me")
    public String me(@AuthenticationPrincipal Object principal, Model model) {
        if (principal instanceof OidcUser oidcUser) {
            model.addAttribute("name", oidcUser.getFullName());
            model.addAttribute("email", oidcUser.getEmail());
            model.addAttribute("picture", oidcUser.getPicture());
            return "me";
        }

        if (principal instanceof OAuth2User oauth2User) {
            model.addAttribute("name", oauth2User.getAttribute("name"));
            model.addAttribute("email", oauth2User.getAttribute("email"));
            model.addAttribute("picture", oauth2User.getAttribute("picture"));
        }

        return "me";
    }
}

