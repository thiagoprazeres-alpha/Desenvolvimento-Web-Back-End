package com.example.aula03_3.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum para representar o status de um produto no sistema.
 * Utiliza Lombok para gerar automaticamente getters e construtores.
 */
@Getter
@AllArgsConstructor
public enum StatusProduto {
    
    /**
     * Produto ativo e disponível para venda
     */
    ATIVO("Ativo", "Produto disponível para venda"),
    
    /**
     * Produto inativo e não disponível para venda
     */
    INATIVO("Inativo", "Produto não disponível para venda"),
    
    /**
     * Produto em falta no estoque
     */
    EM_FALTA("Em Falta", "Produto temporariamente indisponível"),
    
    /**
     * Produto descontinuado
     */
    DESCONTINUADO("Descontinuado", "Produto não será mais comercializado");
    
    private final String descricao;
    private final String detalhe;
    
    /**
     * Verifica se o status permite venda
     * @return true se o produto pode ser vendido
     */
    public boolean isPermiteVenda() {
        return this == ATIVO;
    }
    
    /**
     * Verifica se o status indica problema de estoque
     * @return true se há problema de estoque
     */
    public boolean isProblemaEstoque() {
        return this == EM_FALTA || this == DESCONTINUADO;
    }
}
