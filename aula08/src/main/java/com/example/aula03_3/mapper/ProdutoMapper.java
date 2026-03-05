package com.example.aula03_3.mapper;

import com.example.aula03_3.dto.CategoriaResponse;
import com.example.aula03_3.dto.ProdutoResponse;
import com.example.aula03_3.entity.Categoria;
import com.example.aula03_3.entity.Produto;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ProdutoMapper {

    public ProdutoResponse toResponse(Produto produto) {
        if (produto == null) {
            return null;
        }

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
                .slug(produto.getSlug())
                .imageUrl(produto.getImageUrl())
                .createdAt(produto.getCreatedAt())
                .updatedAt(produto.getUpdatedAt())
                .build();
    }
}
