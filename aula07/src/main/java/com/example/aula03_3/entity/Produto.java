package com.example.aula03_3.entity;

import com.example.aula03_3.enums.StatusProduto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Entidade Produto representando produtos do sistema.
 * Utiliza Lombok para reduzir código boilerplate, Enum para status e extends BaseEntity para auditoria.
 */
@Entity
@Table(name = "produtos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Produto extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 300)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer quantidadeEstoque;

    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    @Column(length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusProduto status = StatusProduto.ATIVO;

    /**
     * Verifica se o produto está disponível para venda
     * @return true se o produto pode ser vendido
     */
    public boolean isDisponivelParaVenda() {
        return status != null && status.isPermiteVenda() && quantidadeEstoque != null && quantidadeEstoque > 0;
    }

    /**
     * Verifica se o produto tem problema de estoque
     * @return true se houver problema de estoque
     */
    public boolean temProblemaEstoque() {
        return status != null && status.isProblemaEstoque();
    }

    /**
     * Ativa o produto
     */
    public void ativar() {
        this.status = StatusProduto.ATIVO;
    }

    /**
     * Inativa o produto
     */
    public void inativar() {
        this.status = StatusProduto.INATIVO;
    }

    /**
     * Marca produto como em falta
     */
    public void marcarComoEmFalta() {
        this.status = StatusProduto.EM_FALTA;
    }

    /**
     * Descontinua o produto
     */
    public void descontinuar() {
        this.status = StatusProduto.DESCONTINUADO;
    }
}
