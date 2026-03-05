package com.example.aula03_3.mapper;

import com.example.aula03_3.dto.CategoriaResponse;
import com.example.aula03_3.dto.ProdutoResponse;
import com.example.aula03_3.entity.Categoria;
import com.example.aula03_3.entity.Produto;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class CategoriaMapper {

    public CategoriaResponse toResponse(Categoria categoria) {
        if (categoria == null) {
            return null;
        }

        return new CategoriaResponse(
            categoria.getId(),
            categoria.getNome(),
            categoria.getDescricao(),
            categoria.getSlug(),
            categoria.getImageUrl(),
            categoria.getCreatedAt(),
            categoria.getUpdatedAt()
        );
    }

    public List<CategoriaResponse> toResponseList(List<Categoria> categorias) {
        if (categorias == null) {
            return null;
        }

        return categorias.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProdutoResponse> toProdutoResponseList(List<Produto> produtos) {
        if (produtos == null) {
            return null;
        }

        return produtos.stream()
                .map(produto -> {
                    CategoriaResponse categoriaResponse = null;
                    if (produto.getCategoria() != null) {
                        categoriaResponse = CategoriaResponse.builder()
                                .id(produto.getCategoria().getId())
                                .nome(produto.getCategoria().getNome())
                                .descricao(produto.getCategoria().getDescricao())
                                .slug(produto.getCategoria().getSlug())
                                .imageUrl(produto.getCategoria().getImageUrl())
                                .build();
                    }

                    return ProdutoResponse.builder()
                            .id(produto.getId())
                            .nome(produto.getNome())
                            .descricao(produto.getDescricao())
                            .preco(produto.getPreco())
                            .quantidadeEstoque(produto.getQuantidadeEstoque())
                            .status(produto.getStatus())
                            .categoria(categoriaResponse)
                            .createdAt(produto.getCreatedAt())
                            .updatedAt(produto.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
