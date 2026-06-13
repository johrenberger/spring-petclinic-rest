/*
 * Test for the new endpoint: GET /api/pets/{petId}/visits
 * with optional ?from=YYYY-MM-DD&to=YYYY-MM-DD
 */

package org.springframework.samples.petclinic.rest.controller;

import org.springframework.samples.petclinic.rest.controller.v1.VisitRestControllerV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.mapper.VisitMapper;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ContextConfiguration(classes=ApplicationTestConfig.class)
@WebAppConfiguration
class VisitRestControllerV1PetVisitsTests {

    @Autowired
    private VisitRestControllerV1 visitRestControllerV1;

    @MockitoBean
    private ClinicService clinicService;

    @Autowired
    private VisitMapper visitMapper;

    private MockMvc mockMvc;

    private List<Visit> visits;

    @BeforeEach
    void initVisits() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(visitRestControllerV1)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();

        visits = new ArrayList<>();

        Owner owner = new Owner();
        owner.setId(1);
        owner.setFirstName("Eduardo");
        owner.setLastName("Rodriquez");

        PetType petType = new PetType();
        petType.setId(2);
        petType.setName("dog");

        Pet pet = new Pet();
        pet.setId(8);
        pet.setName("Rosy");
        pet.setBirthDate(LocalDate.now().minusYears(2));
        pet.setOwner(owner);
        pet.setType(petType);

        Visit v1 = new Visit();
        v1.setId(10);
        v1.setPet(pet);
        v1.setDate(LocalDate.of(2026, 1, 15));
        v1.setDescription("annual checkup");
        visits.add(v1);

        Visit v2 = new Visit();
        v2.setId(11);
        v2.setPet(pet);
        v2.setDate(LocalDate.of(2026, 3, 20));
        v2.setDescription("rabies shot");
        visits.add(v2);

        Visit v3 = new Visit();
        v3.setId(12);
        v3.setPet(pet);
        v3.setDate(LocalDate.of(2026, 6, 1));
        v3.setDescription("follow-up");
        visits.add(v3);
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testListVisitsForPet_returnsAllSortedByDateDesc() throws Exception {
        given(this.clinicService.findPetById(8)).willReturn(visits.get(0).getPet());
        given(this.clinicService.findVisitsByPetId(8)).willReturn(visits);

        this.mockMvc.perform(get("/api/pets/8/visits")
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(3))
            // Sorted by date desc: 2026-06-01 first, 2026-01-15 last
            .andExpect(jsonPath("$[0].id").value(12))
            .andExpect(jsonPath("$[0].description").value("follow-up"))
            .andExpect(jsonPath("$[1].id").value(11))
            .andExpect(jsonPath("$[2].id").value(10));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testListVisitsForPet_filtersByDateRange() throws Exception {
        given(this.clinicService.findPetById(8)).willReturn(visits.get(0).getPet());
        Collection<Visit> filtered = new ArrayList<>();
        filtered.add(visits.get(1)); // 2026-03-20
        filtered.add(visits.get(2)); // 2026-06-01
        given(this.clinicService.findVisitsByPetIdAndDateBetween(
                8, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 12, 31)))
            .willReturn(filtered);

        this.mockMvc.perform(get("/api/pets/8/visits")
                .param("from", "2026-03-01")
                .param("to", "2026-12-31")
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(12))
            .andExpect(jsonPath("$[1].id").value(11));
    }
}
