package com.example.aula03_3.service;

import com.example.aula03_3.dto.ProdutoFiltroRequest;
import com.example.aula03_3.dto.ProdutoPaginadoResponse;
import com.example.aula03_3.dto.ProdutoRequest;
import com.example.aula03_3.dto.ProdutoResponse;
import com.example.aula03_3.entity.Categoria;
import com.example.aula03_3.entity.Produto;
import com.example.aula03_3.enums.StatusProduto;
import com.example.aula03_3.exception.RecursoNaoEncontradoException;
import com.example.aula03_3.mapper.ProdutoMapper;
import com.example.aula03_3.repository.CategoriaRepository;
import com.example.aula03_3.repository.ProdutoRepository;
import com.example.aula03_3.util.SlugUtil;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProdutoMapper produtoMapper;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository, ProdutoMapper produtoMapper) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.produtoMapper = produtoMapper;
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        Produto produto = new Produto();
        aplicarDados(request, produto);
        Produto salvo = produtoRepository.save(produto);
        return produtoMapper.toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(produtoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoPaginadoResponse listarTodosPaginado(Pageable pageable) {
        Page<Produto> produtosPage = produtoRepository.findAll(pageable);
        Page<ProdutoResponse> responsePage = produtosPage.map(produtoMapper::toResponse);
        return ProdutoPaginadoResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public ProdutoPaginadoResponse listarProdutosPorCategoriaPaginado(Long categoriaId, Pageable pageable) {
        Page<Produto> produtosPage = produtoRepository.findByCategoriaId(categoriaId, pageable);
        Page<ProdutoResponse> responsePage = produtosPage.map(produtoMapper::toResponse);
        return ProdutoPaginadoResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public ProdutoPaginadoResponse listarProdutosPorFiltro(ProdutoFiltroRequest filtro, Pageable pageable) {
        String nome = filtro != null ? filtro.nome() : null;
        BigDecimal precoMin = filtro != null ? filtro.precoMin() : null;
        BigDecimal precoMax = filtro != null ? filtro.precoMax() : null;
        
        Page<Produto> produtosPage = produtoRepository.findByFiltros(nome, precoMin, precoMax, pageable);
        Page<ProdutoResponse> responsePage = produtosPage.map(produtoMapper::toResponse);
        return ProdutoPaginadoResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        Produto produto = buscarEntidadePorId(id);
        return produtoMapper.toResponse(produto);
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorSlug(String slug) {
        Produto produto = produtoRepository.findBySlug(slug)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto com slug '" + slug + "' nao encontrado"));
        return produtoMapper.toResponse(produto);
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarEntidadePorId(id);
        aplicarDados(request, produto);
        Produto atualizado = produtoRepository.save(produto);
        return produtoMapper.toResponse(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        Produto produto = buscarEntidadePorId(id);
        produtoRepository.delete(produto);
    }

    private Produto buscarEntidadePorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto com id " + id + " nao encontrado"));
    }

    private void aplicarDados(ProdutoRequest request, Produto produto) {
        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPreco(request.preco());
        produto.setQuantidadeEstoque(request.quantidadeEstoque());
        produto.setStatus(request.status() != null ? request.status() : StatusProduto.ATIVO);
        produto.setSlug(SlugUtil.generateSlug(request.nome()));
        
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria com id " + request.categoriaId() + " nao encontrada"));
        produto.setCategoria(categoria);
    }
}
