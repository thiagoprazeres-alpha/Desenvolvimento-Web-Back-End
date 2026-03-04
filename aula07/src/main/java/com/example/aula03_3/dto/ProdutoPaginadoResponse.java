package com.example.aula03_3.dto;

import java.util.List;

public record ProdutoPaginadoResponse(
    List<ProdutoResponse> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last,
    boolean empty
) {
    public static ProdutoPaginadoResponse from(org.springframework.data.domain.Page<ProdutoResponse> page) {
        return new ProdutoPaginadoResponse(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast(),
            page.isEmpty()
        );
    }
}
