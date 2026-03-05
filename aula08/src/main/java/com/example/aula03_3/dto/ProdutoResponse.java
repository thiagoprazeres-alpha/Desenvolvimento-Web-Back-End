package com.example.aula03_3.dto;

import com.example.aula03_3.enums.StatusProduto;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ProdutoResponse(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        Integer quantidadeEstoque,
        StatusProduto status,
        CategoriaResponse categoria,
        String slug,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
