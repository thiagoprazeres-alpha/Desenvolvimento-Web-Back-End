package com.example.aula03_3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 50, message = "Nome deve ter no maximo 50 caracteres")
        String nome,

        @Size(max = 200, message = "Descricao deve ter no maximo 200 caracteres")
        String descricao
) {
}
