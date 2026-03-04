package com.example.aula03_3.controller;

import com.example.aula03_3.dto.ProdutoFiltroRequest;
import com.example.aula03_3.dto.ProdutoPaginadoResponse;
import com.example.aula03_3.dto.ProdutoRequest;
import com.example.aula03_3.dto.ProdutoResponse;
import com.example.aula03_3.service.ProdutoService;
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
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
        ProdutoResponse response = produtoService.criar(request);
        URI location = URI.create("/api/produtos/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @GetMapping("/paginados")
    public ResponseEntity<ProdutoPaginadoResponse> listarTodosPaginados(Pageable pageable) {
        return ResponseEntity.ok(produtoService.listarTodosPaginado(pageable));
    }

    @GetMapping("/filtro")
    public ResponseEntity<ProdutoPaginadoResponse> listarProdutosPorFiltro(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) java.math.BigDecimal precoMin,
            @RequestParam(required = false) java.math.BigDecimal precoMax,
            Pageable pageable) {
        
        ProdutoFiltroRequest filtro = new ProdutoFiltroRequest(nome, precoMin, precoMax);
        return ResponseEntity.ok(produtoService.listarProdutosPorFiltro(filtro, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProdutoResponse> buscarPorSlug(@PathVariable String slug) {
        return ResponseEntity.ok(produtoService.buscarPorSlug(slug));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
