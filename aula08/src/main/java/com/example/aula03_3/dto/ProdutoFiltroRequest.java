package com.example.aula03_3.dto;

import java.math.BigDecimal;

public record ProdutoFiltroRequest(
    String nome,
    BigDecimal precoMin,
    BigDecimal precoMax
) {
}
