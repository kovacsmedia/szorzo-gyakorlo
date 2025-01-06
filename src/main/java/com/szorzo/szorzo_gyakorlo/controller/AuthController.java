package com.szorzo.szorzo_gyakorlo.controller;

import com.szorzo.szorzo_gyakorlo.model.User;
import com.szorzo.szorzo_gyakorlo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    private Map<String, Integer> stats = new HashMap<>();
    private List<Long> reactionTimes = new ArrayList<>();
    private Instant startTime;
    private boolean isActive = false;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // A "login.html" sablon
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // A "register.html" sablon
    }

    @PostMapping("/register")
    public String registerUser(User user, Model model) {
        try {
            userService.registerUser(user.getUsername(), user.getPassword());
            return "redirect:/login"; // Sikeres regisztráció esetén a login oldalra irányít
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register"; // Hibás regisztráció esetén újra a register oldalt tölti be
        }
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        model.addAttribute("stats", stats);
        if (isActive) {
            int number1 = (int) (Math.random() * 10) + 1;
            int number2 = (int) (Math.random() * 10) + 1;
            model.addAttribute("number1", number1);
            model.addAttribute("number2", number2);
            startTime = Instant.now();
        }
        return "home"; // A "home.html" sablon
    }

    @PostMapping("/home")
    @PreAuthorize("hasRole('USER')")
    public String checkAnswer(int number1, int number2, int answer, Model model) {
        if (!isActive) {
            return "redirect:/home";
        }
        int correctAnswer = number1 * number2;
        long reactionTime = Instant.now().toEpochMilli() - startTime.toEpochMilli();
        reactionTimes.add(reactionTime);

        if (answer == correctAnswer) {
            stats.put("correct", stats.getOrDefault("correct", 0) + 1);
            model.addAttribute("result", "Helyes! Ügyes vagy!");
        } else {
            stats.put("wrong", stats.getOrDefault("wrong", 0) + 1);
            model.addAttribute("result", "Helytelen! A helyes válasz: " + correctAnswer + ".");
        }

        model.addAttribute("reactionTime", reactionTime / 1000.0); // Reakcióidő másodpercben
        return showHomePage(model); // Új feladat generálása
    }


    @PostMapping("/home/start")
    public String startPractice() {
        isActive = true;
        stats.put("correct", 0);
        stats.put("wrong", 0);
        reactionTimes.clear();
        return "redirect:/home";
    }


    @PostMapping("/home/stop")
    public String stopPractice(Model model) {
        isActive = false;
        double averageTime = reactionTimes.stream().mapToLong(Long::longValue).average().orElse(0) / 1000.0;
        model.addAttribute("averageTime", averageTime);
        model.addAttribute("stats", stats);
        return "home"; // A "home.html" sablon
    }
}
