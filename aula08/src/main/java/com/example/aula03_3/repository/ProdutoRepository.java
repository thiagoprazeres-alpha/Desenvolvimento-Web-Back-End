package com.example.aula03_3.repository;

import com.example.aula03_3.entity.Produto;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    Page<Produto> findAll(Pageable pageable);
    
    Page<Produto> findByCategoriaId(Long categoriaId, Pageable pageable);
    
    Optional<Produto> findBySlug(String slug);
    
    @Query("SELECT p FROM Produto p WHERE " +
           "(:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:precoMin IS NULL OR p.preco >= :precoMin) AND " +
           "(:precoMax IS NULL OR p.preco <= :precoMax)")
    Page<Produto> findByFiltros(
        @Param("nome") String nome,
        @Param("precoMin") BigDecimal precoMin,
        @Param("precoMax") BigDecimal precoMax,
        Pageable pageable
    );
}
