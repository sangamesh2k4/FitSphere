package com.sangamesh.Fitsphere.dto.nutrition;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MacroCalculationRequestDto {

    @NotNull
    private Long fdcId;

    @NotNull
    @Min(1)
    private Double quantity;

    @NotNull
    private String unit;
}