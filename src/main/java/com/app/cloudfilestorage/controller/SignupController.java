package com.app.cloudfilestorage.controller;

import com.app.cloudfilestorage.dto.request.SignupRequest;
import com.app.cloudfilestorage.exception.SignupException;
import com.app.cloudfilestorage.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import static com.app.cloudfilestorage.utils.BindingResultResolver.getFirstMessage;

@Controller
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignupController {

    private final UserService userService;

    @GetMapping
    public String showLoginView(Model model) {
        SignupRequest signupRequest = new SignupRequest();
        model.addAttribute("signupRequest", signupRequest);

        return "signup-view";
    }

    @PostMapping
    public RedirectView signupUser(@Valid @ModelAttribute SignupRequest signupRequest,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) throws SignupException {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("validationErrorMessage", getFirstMessage(bindingResult));
            return new RedirectView("/signup");
        }

        userService.createUser(signupRequest);
        redirectAttributes.addFlashAttribute("successMessage", "You have been successfully registered");

        return new RedirectView("/login");
    }
}
