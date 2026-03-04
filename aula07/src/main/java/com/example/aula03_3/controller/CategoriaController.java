package com.example.aula03_3.controller;

import com.example.aula03_3.dto.CategoriaRequest;
import com.example.aula03_3.dto.CategoriaResponse;
import com.example.aula03_3.dto.ProdutoPaginadoResponse;
import com.example.aula03_3.dto.ProdutoResponse;
import com.example.aula03_3.service.CategoriaService;
import com.example.aula03_3.service.ProdutoService;
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
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final ProdutoService produtoService;

    public CategoriaController(CategoriaService categoriaService, ProdutoService produtoService) {
        this.categoriaService = categoriaService;
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse response = categoriaService.criar(request);
        URI location = URI.create("/api/categorias/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listarTodas() {
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoriaResponse> buscarPorSlug(@PathVariable String slug) {
        return ResponseEntity.ok(categoriaService.buscarPorSlug(slug));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/produtos")
    public ResponseEntity<List<ProdutoResponse>> listarProdutosPorCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.listarProdutosPorCategoria(id));
    }

    @GetMapping("/{id}/produtos/paginados")
    public ResponseEntity<ProdutoPaginadoResponse> listarProdutosPorCategoriaPaginado(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(produtoService.listarProdutosPorCategoriaPaginado(id, pageable));
    }
}
