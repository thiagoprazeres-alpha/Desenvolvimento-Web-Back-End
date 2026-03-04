package com.example.aula03_3.service;

import com.example.aula03_3.dto.CategoriaRequest;
import com.example.aula03_3.dto.CategoriaResponse;
import com.example.aula03_3.dto.ProdutoResponse;
import com.example.aula03_3.entity.Categoria;
import com.example.aula03_3.exception.RecursoNaoEncontradoException;
import com.example.aula03_3.mapper.CategoriaMapper;
import com.example.aula03_3.repository.CategoriaRepository;
import com.example.aula03_3.repository.ProdutoRepository;
import com.example.aula03_3.util.SlugUtil;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, ProdutoRepository produtoRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        if (categoriaRepository.existsByNome(request.nome())) {
            throw new IllegalArgumentException("Categoria com nome '" + request.nome() + "' ja existe");
        }

        Categoria categoria = new Categoria();
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());
        categoria.setSlug(SlugUtil.generateSlug(request.nome()));

        Categoria salva = categoriaRepository.save(categoria);
        return categoriaMapper.toResponse(salva);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(Long id) {
        Categoria categoria = buscarEntidadePorId(id);
        return categoriaMapper.toResponse(categoria);
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorSlug(String slug) {
        Categoria categoria = categoriaRepository.findBySlug(slug)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria com slug '" + slug + "' nao encontrada"));
        return categoriaMapper.toResponse(categoria);
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarEntidadePorId(id);

        if (!categoria.getNome().equals(request.nome()) && 
            categoriaRepository.existsByNome(request.nome())) {
            throw new IllegalArgumentException("Categoria com nome '" + request.nome() + "' ja existe");
        }

        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());
        
        // Gerar novo slug se o nome mudou
        if (!categoria.getNome().equals(request.nome())) {
            categoria.setSlug(SlugUtil.generateSlug(request.nome()));
        }

        Categoria atualizada = categoriaRepository.save(categoria);
        return categoriaMapper.toResponse(atualizada);
    }

    @Transactional
    public void deletar(Long id) {
        Categoria categoria = buscarEntidadePorId(id);
        
        if (!categoria.getProdutos().isEmpty()) {
            throw new IllegalArgumentException("Nao e possivel deletar categoria com produtos associados");
        }
        
        categoriaRepository.delete(categoria);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarProdutosPorCategoria(Long categoriaId) {
        Categoria categoria = buscarEntidadePorId(categoriaId);
        return categoriaMapper.toProdutoResponseList(categoria.getProdutos());
    }

    private Categoria buscarEntidadePorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria com id " + id + " nao encontrada"));
    }
}
