package com.example.aula03_3.controller;

import com.example.aula03_3.dto.ProdutoFiltroRequest;
import com.example.aula03_3.dto.ProdutoPaginadoResponse;
import com.example.aula03_3.dto.ProdutoRequest;
import com.example.aula03_3.dto.ProdutoResponse;
import com.example.aula03_3.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "API para gerenciamento de produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
@Operation(summary = "Criar novo produto", description = "Cria um novo produto no sistema com os dados fornecidos")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
})
public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
    ProdutoResponse response = produtoService.criar(request);
    URI location = URI.create("/api/produtos/" + response.id());
    return ResponseEntity.created(location).body(response);
}

@GetMapping
@Operation(summary = "Listar todos os produtos", description = "Retorna uma lista com todos os produtos cadastrados")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
})
public ResponseEntity<List<ProdutoResponse>> listarTodos() {
    return ResponseEntity.ok(produtoService.listarTodos());
}

@GetMapping("/paginados")
@Operation(summary = "Listar produtos paginados", description = "Retorna produtos de forma paginada e ordenada")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Produtos paginados retornados com sucesso")
})
public ResponseEntity<ProdutoPaginadoResponse> listarTodosPaginados(Pageable pageable) {
    return ResponseEntity.ok(produtoService.listarTodosPaginado(pageable));
}

@GetMapping("/filtro")
@Operation(summary = "Filtrar produtos", description = "Filtra produtos por nome e/ou faixa de preço com paginação")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Produtos filtrados retornados com sucesso")
})
public ResponseEntity<ProdutoPaginadoResponse> listarProdutosPorFiltro(
        @Parameter(description = "Nome do produto para busca parcial (case insensitive)") @RequestParam(required = false) String nome,
        @Parameter(description = "Preço mínimo do produto") @RequestParam(required = false) java.math.BigDecimal precoMin,
        @Parameter(description = "Preço máximo do produto") @RequestParam(required = false) java.math.BigDecimal precoMax,
        Pageable pageable) {
    
    ProdutoFiltroRequest filtro = new ProdutoFiltroRequest(nome, precoMin, precoMax);
    return ResponseEntity.ok(produtoService.listarProdutosPorFiltro(filtro, pageable));
}

@GetMapping("/{id}")
@Operation(summary = "Buscar produto por ID", description = "Retorna um produto específico pelo seu ID")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso"),
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
})
public ResponseEntity<ProdutoResponse> buscarPorId(@Parameter(description = "ID do produto") @PathVariable Long id) {
    return ResponseEntity.ok(produtoService.buscarPorId(id));
}

@GetMapping("/slug/{slug}")
@Operation(summary = "Buscar produto por slug", description = "Retorna um produto específico pelo seu slug")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso"),
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
})
public ResponseEntity<ProdutoResponse> buscarPorSlug(@Parameter(description = "Slug do produto") @PathVariable String slug) {
    return ResponseEntity.ok(produtoService.buscarPorSlug(slug));
}

@PutMapping("/{id}")
@Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
})
public ResponseEntity<ProdutoResponse> atualizar(
        @Parameter(description = "ID do produto") @PathVariable Long id, 
        @Valid @RequestBody ProdutoRequest request) {
    return ResponseEntity.ok(produtoService.atualizar(id, request));
}

@DeleteMapping("/{id}")
@Operation(summary = "Deletar produto", description = "Remove um produto do sistema")
@ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
})
public ResponseEntity<Void> deletar(@Parameter(description = "ID do produto") @PathVariable Long id) {
    produtoService.deletar(id);
    return ResponseEntity.noContent().build();
}
}
