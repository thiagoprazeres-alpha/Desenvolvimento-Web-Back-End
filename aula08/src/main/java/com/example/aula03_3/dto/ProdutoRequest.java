package com.example.aula03_3.dto;

import com.example.aula03_3.enums.StatusProduto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProdutoRequest(
        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 120, message = "Nome deve ter no maximo 120 caracteres")
        String nome,

        @Size(max = 300, message = "Descricao deve ter no maximo 300 caracteres")
        String descricao,

        @NotNull(message = "Preco e obrigatorio")
        @DecimalMin(value = "0.01", message = "Preco deve ser maior que zero")
        BigDecimal preco,

        @NotNull(message = "Quantidade em estoque e obrigatoria")
        @Min(value = 0, message = "Quantidade em estoque nao pode ser negativa")
        Integer quantidadeEstoque,

        @NotNull(message = "Categoria e obrigatoria")
        Long categoriaId,

        StatusProduto status,

        @Size(max = 500, message = "URL da imagem deve ter no maximo 500 caracteres")
        String imageUrl
) {
}
