package com.lefpap.taskscli.model.dto;

import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;

public record CliTaskSearchParams(
    @NotEmpty String title,
    LocalDate fromDate,
    LocalDate toDate
) { }
