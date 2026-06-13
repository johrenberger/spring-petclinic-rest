/*
 * Tests for POST /api/pets-with-owner endpoint.
 * Verifies:
 *  - 201 Created on valid request
 *  - 400 Bad Request on missing required fields
 *  - 400 Bad Request on invalid date (future date)
 *  - 400 Bad Request on non-existent pet type id
 */

package org.springframework.samples.petclinic.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.rest.controller.v1.PetRestControllerV1;
import org.springframework.samples.petclinic.rest.dto.CreatePetWithOwnerRequest;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class CreatePetWithOwnerTests {

    @Autowired
    private PetRestControllerV1 petRestControllerV1;

    @MockitoBean
    private ClinicService clinicService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(petRestControllerV1)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private CreatePetWithOwnerRequest validRequest() {
        CreatePetWithOwnerRequest r = new CreatePetWithOwnerRequest();
        r.setOwnerFirstName("John");
        r.setOwnerLastName("Doe");
        r.setOwnerAddress("123 Main St");
        r.setOwnerCity("Springfield");
        r.setOwnerTelephone("5551234567");
        r.setPetName("Buddy");
        r.setPetBirthDate(LocalDate.of(2020, 1, 1));
        r.setPetTypeId(2);
        return r;
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreatePetWithOwner_valid_returns201() throws Exception {
        PetType petType = new PetType();
        petType.setId(2);
        petType.setName("dog");
        given(this.clinicService.findPetTypeById(2)).willReturn(petType);

        String json = this.objectMapper.writeValueAsString(validRequest());
        this.mockMvc.perform(post("/api/pets-with-owner")
                .content(json)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreatePetWithOwner_missingRequiredFields_returns400() throws Exception {
        CreatePetWithOwnerRequest r = new CreatePetWithOwnerRequest();
        // leave all required fields null

        String json = this.objectMapper.writeValueAsString(r);
        this.mockMvc.perform(post("/api/pets-with-owner")
                .content(json)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreatePetWithOwner_futureBirthDate_returns400() throws Exception {
        CreatePetWithOwnerRequest r = validRequest();
        r.setPetBirthDate(LocalDate.now().plusYears(1)); // future date

        String json = this.objectMapper.writeValueAsString(r);
        this.mockMvc.perform(post("/api/pets-with-owner")
                .content(json)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreatePetWithOwner_nonExistentPetType_returns400() throws Exception {
        given(this.clinicService.findPetTypeById(99)).willReturn(null);

        CreatePetWithOwnerRequest r = validRequest();
        r.setPetTypeId(99);

        String json = this.objectMapper.writeValueAsString(r);
        this.mockMvc.perform(post("/api/pets-with-owner")
                .content(json)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }
}
