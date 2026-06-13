/*
 * Request body for POST /api/pets-with-owner.
 * Creates both a new Owner and a new Pet in a single
 * request. Used by the backend-implementation exercise
 * (use case #4).
 */

package org.springframework.samples.petclinic.rest.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CreatePetWithOwnerRequest {

    @NotBlank(message = "ownerFirstName is required")
    @Size(max = 30, message = "ownerFirstName must be at most 30 characters")
    private String ownerFirstName;

    @NotBlank(message = "ownerLastName is required")
    @Size(max = 30, message = "ownerLastName must be at most 30 characters")
    private String ownerLastName;

    @NotBlank(message = "ownerAddress is required")
    @Size(max = 255, message = "ownerAddress must be at most 255 characters")
    private String ownerAddress;

    @NotBlank(message = "ownerCity is required")
    @Size(max = 80, message = "ownerCity must be at most 80 characters")
    private String ownerCity;

    @NotBlank(message = "ownerTelephone is required")
    @Size(min = 7, max = 20, message = "ownerTelephone must be 7-20 characters")
    private String ownerTelephone;

    @NotBlank(message = "petName is required")
    @Size(max = 80, message = "petName must be at most 80 characters")
    private String petName;

    @NotNull(message = "petBirthDate is required")
    @PastOrPresent(message = "petBirthDate must be in the past or today")
    private LocalDate petBirthDate;

    @NotNull(message = "petTypeId is required")
    @Positive(message = "petTypeId must be positive")
    private Integer petTypeId;

    public String getOwnerFirstName() { return ownerFirstName; }
    public void setOwnerFirstName(String v) { this.ownerFirstName = v; }
    public String getOwnerLastName() { return ownerLastName; }
    public void setOwnerLastName(String v) { this.ownerLastName = v; }
    public String getOwnerAddress() { return ownerAddress; }
    public void setOwnerAddress(String v) { this.ownerAddress = v; }
    public String getOwnerCity() { return ownerCity; }
    public void setOwnerCity(String v) { this.ownerCity = v; }
    public String getOwnerTelephone() { return ownerTelephone; }
    public void setOwnerTelephone(String v) { this.ownerTelephone = v; }
    public String getPetName() { return petName; }
    public void setPetName(String v) { this.petName = v; }
    public LocalDate getPetBirthDate() { return petBirthDate; }
    public void setPetBirthDate(LocalDate v) { this.petBirthDate = v; }
    public Integer getPetTypeId() { return petTypeId; }
    public void setPetTypeId(Integer v) { this.petTypeId = v; }
}
