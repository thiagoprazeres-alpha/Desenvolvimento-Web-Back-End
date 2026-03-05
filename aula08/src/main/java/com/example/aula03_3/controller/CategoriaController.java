package com.example.aula03_3.controller;

import com.example.aula03_3.dto.CategoriaRequest;
import com.example.aula03_3.dto.CategoriaResponse;
import com.example.aula03_3.dto.ProdutoPaginadoResponse;
import com.example.aula03_3.dto.ProdutoResponse;
import com.example.aula03_3.service.CategoriaService;
import com.example.aula03_3.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "API para gerenciamento de categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final ProdutoService produtoService;

    public CategoriaController(CategoriaService categoriaService, ProdutoService produtoService) {
        this.categoriaService = categoriaService;
        this.produtoService = produtoService;
    }

    @PostMapping
@Operation(summary = "Criar nova categoria", description = "Cria uma nova categoria no sistema com os dados fornecidos")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
    @ApiResponse(responseCode = "409", description = "Categoria com mesmo nome já existe")
})
public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CategoriaRequest request) {
    CategoriaResponse response = categoriaService.criar(request);
    URI location = URI.create("/api/categorias/" + response.id());
    return ResponseEntity.created(location).body(response);
}

@GetMapping
@Operation(summary = "Listar todas as categorias", description = "Retorna uma lista com todas as categorias cadastradas")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso")
})
public ResponseEntity<List<CategoriaResponse>> listarTodas() {
    return ResponseEntity.ok(categoriaService.listarTodas());
}

@GetMapping("/{id}")
@Operation(summary = "Buscar categoria por ID", description = "Retorna uma categoria específica pelo seu ID")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso"),
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
})
public ResponseEntity<CategoriaResponse> buscarPorId(@Parameter(description = "ID da categoria") @PathVariable Long id) {
    return ResponseEntity.ok(categoriaService.buscarPorId(id));
}

@GetMapping("/slug/{slug}")
@Operation(summary = "Buscar categoria por slug", description = "Retorna uma categoria específica pelo seu slug")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso"),
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
})
public ResponseEntity<CategoriaResponse> buscarPorSlug(@Parameter(description = "Slug da categoria") @PathVariable String slug) {
    return ResponseEntity.ok(categoriaService.buscarPorSlug(slug));
}

@PutMapping("/{id}")
@Operation(summary = "Atualizar categoria", description = "Atualiza os dados de uma categoria existente")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
    @ApiResponse(responseCode = "409", description = "Categoria com mesmo nome já existe")
})
public ResponseEntity<CategoriaResponse> atualizar(
        @Parameter(description = "ID da categoria") @PathVariable Long id, 
        @Valid @RequestBody CategoriaRequest request) {
    return ResponseEntity.ok(categoriaService.atualizar(id, request));
}

@DeleteMapping("/{id}")
@Operation(summary = "Deletar categoria", description = "Remove uma categoria do sistema (não permite se tiver produtos)")
@ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso"),
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
    @ApiResponse(responseCode = "400", description = "Categoria possui produtos vinculados")
})
public ResponseEntity<Void> deletar(@Parameter(description = "ID da categoria") @PathVariable Long id) {
    categoriaService.deletar(id);
    return ResponseEntity.noContent().build();
}

@GetMapping("/{id}/produtos")
@Operation(summary = "Listar produtos por categoria", description = "Retorna todos os produtos de uma categoria específica")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Produtos listados com sucesso"),
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
})
public ResponseEntity<List<ProdutoResponse>> listarProdutosPorCategoria(
        @Parameter(description = "ID da categoria") @PathVariable Long id) {
    return ResponseEntity.ok(categoriaService.listarProdutosPorCategoria(id));
}

@GetMapping("/{id}/produtos/paginados")
@Operation(summary = "Listar produtos por categoria paginados", description = "Retorna produtos de uma categoria de forma paginada")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Produtos paginados retornados com sucesso"),
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
})
public ResponseEntity<ProdutoPaginadoResponse> listarProdutosPorCategoriaPaginado(
        @Parameter(description = "ID da categoria") @PathVariable Long id, 
        Pageable pageable) {
    return ResponseEntity.ok(produtoService.listarProdutosPorCategoriaPaginado(id, pageable));
}
}
