package com.apexpm.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectRequestDTO {
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    private String description;

}