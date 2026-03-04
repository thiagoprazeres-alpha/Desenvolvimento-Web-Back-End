package com.example.aula03_3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Categoria representando categorias de produtos.
 * Utiliza Lombok para reduzir código boilerplate e extends BaseEntity para auditoria.
 */
@Entity
@Table(name = "categorias", uniqueConstraints = {
    @UniqueConstraint(columnNames = "slug")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Categoria extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String nome;
    
    @Column(length = 200)
    private String descricao;
    
    @Column(nullable = false, unique = true, length = 100)
    private String slug;
    
    @Column(length = 500)
    private String imageUrl;
    
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Produto> produtos = new ArrayList<>();
    
    /**
     * Verifica se a categoria possui produtos
     * @return true se houver produtos associados
     */
    public boolean temProdutos() {
        return produtos != null && !produtos.isEmpty();
    }
    
    /**
     * Retorna a quantidade de produtos na categoria
     * @return número de produtos
     */
    public int getQuantidadeProdutos() {
        return produtos != null ? produtos.size() : 0;
    }
}
