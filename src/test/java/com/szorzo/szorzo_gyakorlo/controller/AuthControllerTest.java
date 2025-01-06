package com.szorzo.szorzo_gyakorlo.controller;

import com.szorzo.szorzo_gyakorlo.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void startPractice_ShouldRedirectToHome() throws Exception {
        mockMvc.perform(post("/home/start")
                        .with(csrf())) // CSRF token hozzáadása
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home")); // Ellenőrizze az átirányítás helyességét
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void stopPractice_ShouldReturnHomeTemplate() throws Exception {
        mockMvc.perform(post("/home/stop")
                        .with(csrf())) // CSRF token hozzáadása
                .andExpect(status().isOk())
                .andExpect(view().name("home")) // Ellenőrizze a visszatérési sablont
                .andExpect(model().attributeExists("stats", "averageTime")); // Ellenőrizze a model attribútumokat
    }

    @Test
    void unauthorizedRequest_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/home/start"))
                .andExpect(status().isForbidden()); // Ellenőrizze, hogy a nem hitelesített kérés 403-as hibát ad vissza
    }
}
