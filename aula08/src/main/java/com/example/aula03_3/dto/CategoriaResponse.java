package com.example.aula03_3.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record CategoriaResponse(
        Long id,
        String nome,
        String descricao,
        String slug,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
