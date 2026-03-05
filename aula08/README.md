# CRUD de Produtos e Categorias com Spring Boot (Arquitetura em Camadas)

Projeto didático para primeira aula de backend com Spring Boot, implementando relacionamento 1:N entre Produto e Categoria.

## Camadas

- `controller`: recebe HTTP, valida entrada e devolve resposta.
- `service`: concentra regras de negócio e fluxo da aplicação.
- `repository`: acesso a dados com Spring Data JPA.
- `entity`: mapeamento da tabela no banco.
- `dto`: objetos de entrada/saída da API.
- `mapper`: conversão entre entidades e DTOs.
- `exception`: tratamento global de erros da API.

## Tecnologias

- Spring Boot
- Spring Web
- Spring Data JPA
- Bean Validation
- Lombok
- Java Records
- Java Enums
- PostgreSQL Database
- H2 Database (para testes)
- Docker & Docker Compose
- JPA Auditing (auditoria automática)
- Slugs (URLs amigáveis)
- Suporte a Imagens
- **Swagger/OpenAPI 3.0** (Documentação de API)

## Bean Validation

O Bean Validation (JSR-380) é uma especificação Java para validação de dados que permite definir regras de validação diretamente nos objetos através de anotações. No projeto, utilizamos a implementação Hibernate Validator.

### Como Funciona

1. **Anotações nos DTOs**: As validações são aplicadas nos objetos de requisição (DTOs) usando anotações como `@NotNull`, `@Size`, `@Positive`, etc.

2. **Validação Automática**: O Spring Boot automaticamente valida os parâmetros anotados com `@Valid` nos controllers.

3. **Tratamento de Erros**: Quando uma validação falha, o Spring lança `MethodArgumentNotValidException` que é capturada pelo `ApiExceptionHandler`.

### Anotações Utilizadas

#### ProdutoRequest.java
```java
public record ProdutoRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
    String nome,
    
    @Size(max = 300, message = "Descrição deve ter no máximo 300 caracteres")
    String descricao,
    
    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser positivo")
    BigDecimal preco,
    
    @NotNull(message = "Quantidade em estoque é obrigatória")
    @PositiveOrZero(message = "Quantidade em estoque não pode ser negativa")
    Integer quantidadeEstoque,
    
    @NotNull(message = "Categoria é obrigatória")
    Long categoriaId
) {}
```

#### CategoriaRequest.java
```java
public record CategoriaRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
    String nome,
    
    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    String descricao
) {}
```

## JPA Auditing (Auditoria Automática)

O JPA Auditing é uma funcionalidade do Spring Data JPA que permite o rastreamento automático de auditoria em entidades, como datas de criação e atualização dos registros.

### Como Funciona

1. **BaseEntity**: Classe base com campos de auditoria (`createdAt`, `updatedAt`)
2. **@CreatedDate**: Data de criação automática quando o registro é inserido
3. **@LastModifiedDate**: Data atualizada automaticamente quando o registro é modificado
4. **@EnableJpaAuditing**: Habilita a auditoria na aplicação

### Configuração

#### BaseEntity.java
```java
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

#### JpaAuditingConfig.java
```java
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
```

### Uso nas Entidades

Todas as entidades estendem `BaseEntity`:
```java
@Entity
public class Produto extends BaseEntity {
    // campos da entidade
}

@Entity
public class Categoria extends BaseEntity {
    // campos da entidade
}
```

### Benefícios

- **Rastreamento Automático**: Datas são preenchidas automaticamente
- **Auditoria Completa**: Histórico de criação e modificação
- **Transparência**: Sem necessidade de código manual para datas
- **Consistência**: Todas as entidades têm os mesmos campos de auditoria

### Campos na API

Os campos de auditoria são incluídos nos DTOs de resposta:
```json
{
  "id": 1,
  "nome": "Produto",
  "createdAt": "2026-02-26T16:17:27.15499",
  "updatedAt": "2026-02-26T16:17:27.15499"
}
```

### Principais Anotações

| Anotação | Descrição | Exemplo de Uso |
|----------|-----------|----------------|
| `@NotNull` | Campo não pode ser nulo | `@NotNull String nome` |
| `@NotBlank` | String não pode ser nula nem vazia | `@NotBlank String nome` |
| `@Size` | Tamanho máximo/mínimo | `@Size(max = 120) String nome` |
| `@Positive` | Número deve ser positivo | `@Positive BigDecimal preco` |
| `@PositiveOrZero` | Número deve ser positivo ou zero | `@PositiveOrZero Integer quantidade` |
| `@Email` | Formato de e-mail válido | `@Email String email` |
| `@Min` | Valor mínimo | `@Min(18) Integer idade` |
| `@Max` | Valor máximo | `@Max(120) Integer idade` |

### Fluxo de Validação

1. **Requisição HTTP** → Controller
2. **Controller** → Verifica anotação `@Valid`
3. **Spring Boot** → Aplica validações do Bean Validation
4. **Se válido** → Continua execução normal
5. **Se inválido** → Lança `MethodArgumentNotValidException`
6. **ApiExceptionHandler** → Captura e retorna erro 400

### Exemplo de Resposta de Erro

Quando uma validação falha, o sistema retorna:

```json
{
  "status": 400,
  "message": "Erro de validação",
  "timestamp": "2026-02-25T18:30:00",
  "errors": [
    "Nome é obrigatório",
    "Preço deve ser positivo",
    "Nome deve ter no máximo 120 caracteres"
  ]
}
```

### Vantagens do Bean Validation

- **Declarativo**: Validações definidas diretamente no código
- **Reutilizável**: Mesmas regras aplicadas em diferentes pontos
- **Padronizado**: Segue especificação JSR-380
- **Flexível**: Suporte a validações customizadas
- **Integração**: Funciona nativamente com Spring Boot

## Entidades

### Produto
- id: Long (PK)
- nome: String (120 chars, obrigatório)
- descricao: String (300 chars)
- preco: BigDecimal (obrigatório, > 0)
- quantidadeEstoque: Integer (obrigatório, >= 0)
- categoria: Categoria (FK, obrigatória)
- slug: String (150 chars, único, obrigatório)
- imageUrl: String (500 chars)

### Categoria
- id: Long (PK)
- nome: String (50 chars, único, obrigatório)
- descricao: String (200 chars)
- slug: String (100 chars, único, obrigatório)
- imageUrl: String (500 chars)
- produtos: List<Produto> (relacionamento 1:N)

## Relacionamento

**1 Categoria → N Produtos**

- Uma categoria pode ter muitos produtos
- Um produto pertence a exatamente uma categoria
- Implementado com `@ManyToOne` em Produto e `@OneToMany` em Categoria
- Chave estrangeira: `categoria_id` na tabela `produtos`

## Endpoints

### Categorias
Base: `/api/categorias`

- `POST /api/categorias` - Criar categoria
- `GET /api/categorias` - Listar todas categorias
- `GET /api/categorias/{id}` - Buscar categoria por ID
- `GET /api/categorias/slug/{slug}` - Buscar categoria por slug
- `PUT /api/categorias/{id}` - Atualizar categoria
- `DELETE /api/categorias/{id}` - Deletar categoria (não permite se tiver produtos)
- `GET /api/categorias/{id}/produtos` - Listar todos produtos de uma categoria específica
- `GET /api/categorias/{id}/produtos/paginados` - Listar produtos paginados de uma categoria específica

### Produtos
Base: `/api/produtos`

- `POST /api/produtos` - Criar produto
- `GET /api/produtos` - Listar todos produtos (com categoria)
- `GET /api/produtos/paginados` - Listar produtos paginados e ordenados
- `GET /api/produtos/filtro` - Filtrar produtos por nome e/ou preço (paginado)
- `GET /api/produtos/{id}` - Buscar produto por ID (com categoria)
- `GET /api/produtos/slug/{slug}` - Buscar produto por slug
- `PUT /api/produtos/{id}` - Atualizar produto
- `DELETE /api/produtos/{id}` - Remover produto

## Exemplos de Payload

### Criar Categoria
```json
{
  "nome": "Eletrônicos",
  "descricao": "Produtos eletrônicos em geral",
  "imageUrl": "https://example.com/images/eletronicos.jpg"
}
```

### Criar Produto
```json
{
  "nome": "Notebook Dell",
  "descricao": "Notebook Intel Core i5",
  "preco": 3499.90,
  "quantidadeEstoque": 8,
  "categoriaId": 1,
  "imageUrl": "https://example.com/images/notebook-dell.jpg"
}
```

### Resposta Produto (com Categoria)
```json
{
  "id": 1,
  "nome": "Notebook Dell",
  "descricao": "Notebook Intel Core i5",
  "preco": 3499.90,
  "quantidadeEstoque": 8,
  "slug": "notebook-dell",
  "imageUrl": "https://example.com/images/notebook-dell.jpg",
  "categoria": {
    "id": 1,
    "nome": "Eletrônicos",
    "descricao": "Produtos eletrônicos em geral",
    "slug": "eletronicos",
    "imageUrl": "https://example.com/images/eletronicos.jpg"
  },
  "createdAt": "2026-02-26T16:44:08.568671",
  "updatedAt": "2026-02-26T16:44:08.568671"
}
```

### Listar Produtos por Categoria
```bash
# Listar todos produtos da categoria ID 1
curl -X GET http://localhost:8080/api/categorias/1/produtos
```

### Resposta (Lista de Produtos)
```json
[
  {
    "id": 1,
    "nome": "Notebook Dell",
    "descricao": "Notebook Intel Core i5",
    "preco": 3499.90,
    "quantidadeEstoque": 8,
    "slug": "notebook-dell",
    "imageUrl": "https://example.com/images/notebook-dell.jpg",
    "categoria": {
      "id": 1,
      "nome": "Eletrônicos",
      "descricao": "Produtos eletrônicos em geral",
      "slug": "eletronicos",
      "imageUrl": "https://example.com/images/eletronicos.jpg"
    },
    "createdAt": "2026-02-26T16:44:08.568671",
    "updatedAt": "2026-02-26T16:44:08.568671"
  }
]
```

### Paginação e Sorting

#### Listar produtos paginados
```bash
# Primeira página com 2 itens, ordenados por nome crescente
curl "http://localhost:8080/api/produtos/paginados?page=0&size=2&sort=nome,asc"

# Segunda página com 3 itens, ordenados por preço decrescente
curl "http://localhost:8080/api/produtos/paginados?page=1&size=3&sort=preco,desc"
```

#### Listar produtos por categoria paginados
```bash
# Produtos da categoria ID 4, primeira página com 2 itens
curl "http://localhost:8080/api/categorias/4/produtos/paginados?page=0&size=2&sort=preco,asc"
```

#### Filtrar produtos por nome e/ou preço
```bash
# Filtrar por nome (case insensitive)
curl "http://localhost:8080/api/produtos/filtro?nome=mouse&page=0&size=5"

# Filtrar por preço mínimo
curl "http://localhost:8080/api/produtos/filtro?precoMin=300&page=0&size=5&sort=preco,asc"

# Filtrar por faixa de preço
curl "http://localhost:8080/api/produtos/filtro?precoMin=100&precoMax=200&page=0&size=5"

# Filtrar combinado (nome + preço)
curl "http://localhost:8080/api/produtos/filtro?nome=Gamer&precoMin=100&page=0&size=5"
```

#### Resposta Paginada
```json
{
  "content": [
    {
      "id": 1,
      "nome": "Mouse Gamer",
      "descricao": "Mouse RGB",
      "preco": 149.90,
      "quantidadeEstoque": 25,
      "categoria": {
        "id": 4,
        "nome": "Informática",
        "descricao": "Produtos de informática"
      }
    }
  ],
  "pageNumber": 0,
  "pageSize": 2,
  "totalElements": 3,
  "totalPages": 2,
  "first": true,
  "last": false,
  "empty": false
}
```

## Slugs (URLs Amigáveis)

O sistema implementa slugs para criar URLs amigáveis e otimizadas para SEO, substituindo IDs numéricos por textos descritivos.

### O que é um Slug?

Slug é uma representação de texto em formato URL-friendly, convertendo caracteres especiais, espaços e acentos em um formato legível para URLs.

### Exemplos de Conversão

- "Notebook Dell Inspiron" → "notebook-dell-inspiron"
- "Eletrônicos em Geral" → "eletronicos-em-geral"
- "Livro de Java" → "livro-de-java"

### SlugUtil - Classe Utilitária

A classe `SlugUtil` é responsável por gerar slugs automaticamente:

```java
public class SlugUtil {
    public static String generateSlug(String text) {
        // 1. Converter para minúsculas
        // 2. Remover acentos e caracteres especiais
        // 3. Substituir caracteres inválidos por hífen
        // 4. Remover espaços e hífens múltiplos
        return slugFormatado;
    }
}
```

### Geração Automática

Os slugs são gerados automaticamente nos serviços:

#### CategoriaService
```java
@Transactional
public CategoriaResponse criar(CategoriaRequest request) {
    Categoria categoria = new Categoria();
    categoria.setNome(request.nome());
    categoria.setSlug(SlugUtil.generateSlug(request.nome())); // Automático!
    // ...
}
```

#### ProdutoService
```java
@Transactional
public ProdutoResponse criar(ProdutoRequest request) {
    Produto produto = new Produto();
    produto.setNome(request.nome());
    produto.setSlug(SlugUtil.generateSlug(request.nome())); // Automático!
    // ...
}
```

### Endpoints com Slug

O sistema oferece endpoints alternativos usando slugs:

#### Categorias
- `GET /api/categorias/slug/{slug}` - Buscar categoria por slug
- Exemplo: `/api/categorias/slug/eletronicos`

#### Produtos
- `GET /api/produtos/slug/{slug}` - Buscar produto por slug
- Exemplo: `/api/produtos/slug/notebook-dell-inspiron`

### Frontend com URLs Amigáveis

As páginas HTML foram atualizadas para usar slugs:

#### index.html
```html
<!-- Antes: categoria.html?id=1 -->
<!-- Agora: categoria.html?slug=eletronicos -->
<a href="categoria.html?slug=${categoria.slug}">${categoria.nome}</a>

<!-- Antes: produto.html?id=1 -->
<!-- Agora: produto.html?slug=notebook-dell -->
<a href="produto.html?slug=${produto.slug}">Ver detalhes</a>
```

### Vantagens dos Slugs

1. **SEO Otimizado**: URLs descritivas ajudam no ranking de busca
2. **Legibilidade**: URLs fáceis de ler e compartilhar
3. **Profissionalismo**: Aparência mais profissional que IDs numéricos
4. **Memorização**: URLs mais fáceis de memorizar
5. **Backward Compatibility**: Endpoints por ID continuam funcionando

### Exemplo de Uso Completo

```bash
# Criar categoria (slug gerado automaticamente)
curl -X POST "http://localhost:8080/api/categorias" \
  -H "Content-Type: application/json" \
  -d '{"nome": "Informática", "descricao": "Produtos de informática"}'

# Resposta inclui slug: "informatica"

# Buscar por slug
curl "http://localhost:8080/api/categorias/slug/informatica"

# Frontend usa URL amigável
# http://localhost:8080/categoria.html?slug=informatica
```

## Suporte a Imagens

O sistema suporta URLs de imagem para categorias e produtos, permitindo enriquecer a experiência visual.

### Campos de Imagem

#### Categoria
- `imageUrl`: String (500 caracteres) - URL da imagem da categoria

#### Produto  
- `imageUrl`: String (500 caracteres) - URL da imagem do produto

### Exibição no Frontend

#### produto.html
```html
<!-- Exibe imagem se disponível -->
${produto.imageUrl 
  ? `<img src="${produto.imageUrl}" alt="${produto.nome}" class="product-image">` 
  : ''}
```

### Exemplo de Payload com Imagem

```json
{
  "nome": "Notebook Dell",
  "descricao": "Notebook Intel Core i5",
  "preco": 3499.90,
  "quantidadeEstoque": 8,
  "categoriaId": 1,
  "imageUrl": "https://example.com/images/notebook-dell.jpg"
}
```

### Resposta com Imagem

```json
{
  "id": 1,
  "nome": "Notebook Dell",
  "imageUrl": "https://example.com/images/notebook-dell.jpg",
  "categoria": {
    "id": 1,
    "nome": "Informática",
    "imageUrl": "https://example.com/images/informatica.jpg"
  }
}
```

## Spring Data JPA e Queries Customizadas

O Spring Data JPA facilita muito o acesso a dados, mas às vezes precisamos de consultas mais complexas que os métodos padrão não conseguem atender. Para isso, usamos a anotação `@Query`.

### @Query Annotation

A anotação `@Query` permite definir consultas JPQL (Java Persistence Query Language) ou SQL nativas diretamente nos métodos do repository.

#### Exemplo no ProdutoRepository

```java
@Query("SELECT p FROM Produto p WHERE " +
       "(:nome IS NULL OR p.nome LIKE %:nome%) AND " +
       "(:precoMin IS NULL OR p.preco >= :precoMin) AND " +
       "(:precoMax IS NULL OR p.preco <= :precoMax)")
Page<Produto> findByFiltros(
    @Param("nome") String nome,
    @Param("precoMin") BigDecimal precoMin,
    @Param("precoMax") BigDecimal precoMax,
    Pageable pageable
);
```

### Análise Detalhada do Query

#### 1. Estrutura JPQL
```java
"SELECT p FROM Produto p WHERE ..."
```
- **SELECT p**: Seleciona a entidade Produto completa
- **FROM Produto p**: Define o alias "p" para a entidade Produto
- **WHERE**: Inicia as condições de filtragem

#### 2. Condições Dinâmicas com Parâmetros Opcionais
```java
"(:nome IS NULL OR p.nome LIKE %:nome%)"
```

**Como funciona:**
- `:nome IS NULL`: Verifica se o parâmetro nome foi fornecido
- `OR p.nome LIKE %:nome%`: Se nome fornecido, aplica filtro LIKE
- `%:nome%`: Operador LIKE curinga para busca parcial

**⚠️ Problema de Case Sensitive:**
O operador LIKE é case sensitive na maioria dos bancos PostgreSQL/MySQL por padrão.

**Solução: Usar LOWER() (Recomendado)**
```java
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
```

**Como funciona:**
- `LOWER(p.nome)`: Converte o campo do banco para minúsculas
- `LOWER(CONCAT('%', :nome, '%'))`: Converte o parâmetro para minúsculas
- Resultado: Comparação sempre em minúsculas (case insensitive)

**Exemplos de uso case insensitive:**
- `nome = "MOUSE"` → Encontra "mouse", "Mouse", "MOUSE"
- `nome = "gamer"` → Encontra "Gamer", "GAMER", "gamer"
- `nome = "Notebook"` → Encontra "notebook", "Notebook", "NOTEBOOK"

**Vantagens desta abordagem:**
- ✅ Universal (funciona em qualquer banco SQL)
- ✅ Performance boa com índices adequados
- ✅ Portabilidade máxima entre bancos
- ✅ Sintaxe JPQL padrão

#### 3. Filtros de Faixa de Preço
```java
"(:precoMin IS NULL OR p.preco >= :precoMin)"
"(:precoMax IS NULL OR p.preco <= :precoMax)"
```

**Lógica aplicada:**
- Se `precoMin` for nulo → ignora condição de preço mínimo
- Se `precoMin` fornecido → `WHERE p.preco >= precoMin`
- Se `precoMax` fornecido → `WHERE p.preco <= precoMax`

#### 4. Parâmetros Nomeados com @Param
```java
@Param("nome") String nome,
@Param("precoMin") BigDecimal precoMin,
@Param("precoMax") BigDecimal precoMax
```

**Vantagens dos @Param:**
- **Segurança**: Previne SQL Injection
- **Legibilidade**: Código mais claro e manutenível
- **Reuso**: Parâmetros podem ser repetidos na query
- **Type Safety**: Validação de tipos em tempo de compilação

#### 5. Paginação Integrada
```java
Page<Produto> findByFiltros(..., Pageable pageable)
```

**O Pageable inclui:**
- **page**: Número da página (0-based)
- **size**: Quantidade de itens por página
- **sort**: Ordenação (campo + direção)

### Exemplos de Uso na Prática

#### Busca por Nome Apenas
```java
// Chamada: findByFiltros("mouse", null, null, pageable)
// SQL gerado:
SELECT p.* FROM produtos p 
WHERE p.nome LIKE '%mouse%'
ORDER BY p.nome ASC 
LIMIT 10 OFFSET 0;
```

#### Busca por Faixa de Preço
```java
// Chamada: findByFiltros(null, new BigDecimal("100"), new BigDecimal("500"), pageable)
// SQL gerado:
SELECT p.* FROM produtos p 
WHERE p.preco >= 100 AND p.preco <= 500
ORDER BY p.preco DESC 
LIMIT 10 OFFSET 0;
```

#### Busca Combinada
```java
// Chamada: findByFiltros("Gamer", new BigDecimal("200"), null, pageable)
// SQL gerado:
SELECT p.* FROM produtos p 
WHERE p.nome LIKE '%Gamer%' AND p.preco >= 200
ORDER BY p.nome ASC 
LIMIT 10 OFFSET 0;
```

### Vantagens Desta Abordagem

#### 1. **Flexibilidade**
- Um único método atende múltiplos cenários de busca
- Parâmetros opcionais permitem combinações infinitas
- Sem necessidade de múltiplos métodos no repository

#### 2. **Performance**
- Query única no banco de dados
- Aproveitamento de índices existentes
- Paginação aplicada diretamente no SQL

#### 3. **Manutenibilidade**
- Lógica centralizada em um lugar
- Fácil de testar e modificar
- Código limpo e legível

#### 4. **Type Safety**
- Validação em tempo de compilação
- Autocompletação da IDE
- Refactoring seguro

### Alternativas Consideradas

#### Method Names do Spring Data
```java
// Opção 1: Múltiplos métodos
Page<Produto> findByNomeContaining(String nome, Pageable pageable);
Page<Produto> findByPrecoBetween(BigDecimal min, BigDecimal max, Pageable pageable);
Page<Produto> findByNomeContainingAndPrecoBetween(String nome, BigDecimal min, BigDecimal max, Pageable pageable);

// Problemas:
// - Explosão de combinações
// - Código repetitivo
// - Difícil manutenção
```

#### Criteria API
```java
// Opção 2: Criteria API dinâmica
// Problemas:
// - Código verboso
// - Complexidade elevada
// - Difícil leitura
```

#### Query Nativa
```java
// Opção 3: SQL Nativo
@Query(value = "SELECT * FROM produtos WHERE ...", nativeQuery = true)
// Problemas:
// - Perde portabilidade de banco
// - Não aproveita otimizações JPQL
// - Maior acoplamento
```

### Melhorias Possíveis

#### 1. **Índices no Banco**
```sql
-- Recomendado para performance
CREATE INDEX idx_produtos_nome ON produtos(nome);
CREATE INDEX idx_produtos_preco ON produtos(preco);
CREATE INDEX idx_produtos_nome_preco ON produtos(nome, preco);
```

#### 2. **Full Text Search**
```java
// Para buscas mais avançadas
@Query("SELECT p FROM Produto p WHERE " +
       "FUNCTION('fts_match', p.nome, :termo) = true")
Page<Produto> buscaAvancada(@Param("termo") String termo, Pageable pageable);
```

#### 3. **Especificação JPA**
```java
// Para queries ainda mais dinâmicas
public class ProdutoSpecification {
    public static Specification<Produto> comFiltros(String nome, BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            // Lógica dinâmica complexa
        };
    }
}
```

## Swagger/OpenAPI 3.0

O projeto utiliza SpringDoc OpenAPI para gerar documentação automática da API RESTful, seguindo a especificação OpenAPI 3.0.

### O que é Swagger/OpenAPI?

OpenAPI é uma especificação padrão para descrever APIs REST. Swagger é um conjunto de ferramentas para implementar essa especificação, incluindo:

- **Documentação Automática**: Geração de documentação a partir do código
- **Interface Interativa**: UI para testar endpoints diretamente
- **Especificação Padronizada**: Formato JSON/YAML para descrever APIs
- **Client Generation**: Geração automática de clientes em diversas linguagens

### Configuração

#### Dependência
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

#### Configuração Personalizada
```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Produtos e Categorias")
                        .description("API RESTful para gerenciamento de produtos e categorias")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Desenvolvedor")
                                .email("dev@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento")
                ));
    }
}
```

### Anotações Utilizadas

#### @Tag
Organiza endpoints em grupos lógicos:
```java
@Tag(name = "Produtos", description = "API para gerenciamento de produtos")
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    // endpoints
}
```

#### @Operation
Descreve cada endpoint individualmente:
```java
@Operation(summary = "Criar novo produto", 
           description = "Cria um novo produto no sistema com os dados fornecidos")
public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
    // implementação
}
```

#### @ApiResponses
Documenta os possíveis códigos de resposta:
```java
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
})
```

#### @Parameter
Documenta parâmetros de entrada:
```java
@Parameter(description = "ID do produto") @PathVariable Long id
@Parameter(description = "Nome do produto para busca parcial (case insensitive)") @RequestParam(required = false) String nome
```

### Acesso à Documentação

#### Swagger UI (Interface Interativa)
- **URL**: `http://localhost:8080/swagger-ui.html`
- **Funcionalidades**: 
  - Testar endpoints diretamente na interface
  - Visualizar schemas de request/response
  - Download da especificação OpenAPI
  - Autenticação (se configurada)

#### OpenAPI JSON (Especificação)
- **URL**: `http://localhost:8080/v3/api-docs`
- **Formato**: JSON
- **Uso**: Para geração de clientes, validação, etc.

### Exemplo de Uso

#### 1. Acessar a Documentação
```bash
# Inicie a aplicação
./mvnw spring-boot:run

# Abra no navegador
open http://localhost:8080/swagger-ui.html
```

#### 2. Testar Endpoint via Swagger UI
1. Navegue até a seção "Produtos"
2. Expanda "POST /api/produtos"
3. Clique em "Try it out"
4. Preencha o corpo da requisição:
```json
{
  "nome": "Notebook Dell",
  "descricao": "Notebook Intel Core i5",
  "preco": 3499.90,
  "quantidadeEstoque": 8,
  "categoriaId": 1
}
```
5. Clique em "Execute"

#### 3. Visualizar Resposta
```json
{
  "id": 1,
  "nome": "Notebook Dell",
  "descricao": "Notebook Intel Core i5",
  "preco": 3499.90,
  "quantidadeEstoque": 8,
  "slug": "notebook-dell",
  "categoria": {
    "id": 1,
    "nome": "Eletrônicos",
    "slug": "eletronicos"
  },
  "createdAt": "2026-03-04T16:30:00.123",
  "updatedAt": "2026-03-04T16:30:00.123"
}
```

### Benefícios do Swagger/OpenAPI

#### 1. **Documentação Viva**
- Atualizada automaticamente com o código
- Sem necessidade de manutenção manual
- Sempre sincronizada com a API real

#### 2. **Testes Facilitados**
- Interface interativa para testes
- Geração automática de exemplos
- Validação de schemas em tempo real

#### 3. **Integração**
- Geração de clientes SDK
- Importação em ferramentas como Postman
- Documentação para times de frontend

#### 4. **Padronização**
- Especificação OpenAPI 3.0 padrão da indústria
- Compatível com diversas ferramentas
- Facilita contratos de API

### Configurações Avançadas

#### Customização da UI
```yaml
# application.yml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
    doc-expansion: none
    disable-swagger-default-url: true
    display-request-duration: true
    default-models-expand-depth: 1
    default-model-expand-depth: 1
```

#### Múltiplos Servidores
```java
.servers(List.of(
    new Server().url("http://localhost:8080").description("Desenvolvimento"),
    new Server().url("https://api.homolog.com").description("Homologação"),
    new Server().url("https://api.prod.com").description("Produção")
))
```

#### Autenticação (Exemplo Bearer)
```java
@Component
@OpenAPIDefinition
public class OpenApiSecurityConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }
}
```

### Boas Práticas

#### 1. **Descrições Claras**
- Use summaries concisos
- Descrições detalhadas para operações complexas
- Documente parâmetros obrigatórios vs opcionais

#### 2. **Códigos de Status**
- Documente todos os códigos possíveis
- Inclua exemplos de respostas de erro
- Use códigos HTTP padrão

#### 3. **Exemplos Úteis**
- Forneça exemplos realistas
- Inclua edge cases
- Use dados consistentes

#### 4. **Organização**
- Agrupe endpoints relacionados com @Tag
- Use nomes descritivos para parâmetros
- Mantenha a estrutura consistente

### Troubleshooting

#### Swagger UI não aparece
- Verifique se a dependência está no classpath
- Confirme se a aplicação está rodando
- Limpe o cache do navegador

#### Schemas não aparecem
- Verifique anotações @RequestBody/@ResponseBody
- Confirme se os DTOs têm getters públicos
- Verifique configuração do Jackson

#### Endpoints não documentados
- Verifique anotações @Operation
- Confirme se os controllers estão no pacote escaneado
- Verifique se @RestController está presente

## Validações

### Produto
- Nome: obrigatório, máximo 120 caracteres
- Descrição: máximo 300 caracteres
- Preço: obrigatório, maior que 0
- Quantidade: obrigatória, não negativa
- Categoria: obrigatória
- Status: opcional (default: ATIVO)

## Lombok

Lombok é uma biblioteca Java que reduz código boilerplate através de anotações. No projeto, utilizamos anotações específicas para evitar más práticas:

### Anotações Principais

#### @Getter e @Setter
Geram getters e setters apenas para os campos necessários, dando controle explícito sobre o que é exposto.

#### @NoArgsConstructor e @AllArgsConstructor
Geram construtores sem parâmetros e com todos os parâmetros respectivamente.

#### @Builder
Implementa o pattern Builder, permitindo construção fluída de objetos.

#### @ToString
Personaliza o método toString() com controle sobre quais campos incluir.

#### @EqualsAndHashCode
Gera métodos equals() e hashCode(), podendo configurar quais campos considerar.

### Por que evitar @Data em Entidades JPA?

O uso de `@Data` em entidades JPA é considerado uma má prática pelos seguintes motivos:

1. **Setters Públicos**: `@Data` gera setters para todos os campos, incluindo o ID, o que pode causar problemas de estado
2. **Equals/HashCode Problemáticos**: `@Data` inclui todos os campos em equals/hashCode, o que pode causar problemas com entidades gerenciadas pelo JPA
3. **Violação de Encapsulamento**: Exposição desnecessária de setters que deveriam ser controlados pela lógica de negócio

### Exemplo de Uso Correto

```java
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    // getters, setters controlados, toString(), equals(), hashCode() gerados
}
```

## Java Records

Records são classes imutáveis introduzidas no Java 14 para representar dados de forma concisa. No projeto, utilizamos para DTOs:

### Características

- **Imutabilidade**: Campos são final e gerados automaticamente
- **Construtor compacto**: Gerado automaticamente com todos os campos
- **Métodos de acesso**: Getters automáticos (nomeDoCampo())
- **toString(), equals(), hashCode()**: Implementados automaticamente

### Exemplo de Uso

```java
public record ProdutoRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
    String nome,
    
    @Size(max = 300, message = "Descrição deve ter no máximo 300 caracteres")
    String descricao,
    
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    BigDecimal preco,
    
    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "Quantidade em estoque não pode ser negativa")
    Integer quantidadeEstoque,
    
    @NotNull(message = "Categoria é obrigatória")
    Long categoriaId,
    
    StatusProduto status
) {
}
```

## Java Enums

Enums são usados para representar conjuntos fixos de constantes. No projeto, criamos o StatusProduto:

### StatusProduto

```java
@Getter
@AllArgsConstructor
public enum StatusProduto {
    ATIVO("Ativo", "Produto disponível para venda"),
    INATIVO("Inativo", "Produto não disponível para venda"),
    EM_FALTA("Em Falta", "Produto temporariamente indisponível"),
    DESCONTINUADO("Descontinuado", "Produto não será mais comercializado");

    private final String descricao;
    private final String detalhe;

    public boolean isPermiteVenda() {
        return this == ATIVO;
    }

    public boolean isProblemaEstoque() {
        return this == EM_FALTA || this == DESCONTINUADO;
    }
}
```

### Vantagens

- **Type Safety**: Evita strings mágicas no código
- **Documentação**: Auto-documentado através dos valores
- **Métodos de negócio**: Lógica relacionada ao status encapsulada
- **Validação**: Valores pré-definidos evitam erros

### Uso na Entidade

```java
@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 20)
@Builder.Default
private StatusProduto status = StatusProduto.ATIVO;
```

### Categoria
- Nome: obrigatório, máximo 50 caracteres, único
- Descrição: máximo 200 caracteres

## Rodando o projeto

### Com Docker (Recomendado)

1. **Iniciar o PostgreSQL:**
```bash
docker-compose up -d
```

2. **Executar a aplicação:**
```bash
./mvnw spring-boot:run
```

### Sem Docker

```bash
./mvnw spring-boot:run
```

### Para Testes (com H2)

Para rodar a aplicação com banco H2 em memória (ideal para testes rápidos):

```bash
SPRING_PROFILES_ACTIVE=test ./mvnw spring-boot:run
```

## Configurações de Ambiente (Profiles)

O projeto suporta múltiplos ambientes através de profiles do Spring:

### Profile Default (Produção)
- **Arquivo**: `application.properties`
- **Banco**: PostgreSQL
- **Uso**: Ambiente de produção/desenvolvimento

### Profile Test
- **Arquivo**: `application-test.properties`
- **Banco**: H2 (em memória)
- **DDL**: `create-drop` (recria tabelas a cada execução)
- **Dados Iniciais**: `data-test.sql`
- **Console H2**: Disponível em `http://localhost:8080/h2-console`
- **Uso**: Testes rápidos, CI/CD, desenvolvimento sem Docker

### Como Alternar entre Profiles

```bash
# Profile default (PostgreSQL)
./mvnw spring-boot:run

# Profile test (H2)
SPRING_PROFILES_ACTIVE=test ./mvnw spring-boot:run

# Profile custom
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

## Acesso ao Banco

### PostgreSQL (com Docker)
- **Host**: localhost
- **Porta**: 5432
- **Database**: produtosdb
- **User**: postgres
- **Password**: postgres123

### H2 (Profile Test)
- **URL**: `jdbc:h2:mem:testdb`
- **Console**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **User**: sa
- **Password**: password
- **Driver**: org.h2.Driver

### Parar containers Docker
```bash
docker-compose down
```

## Estrutura do Banco

```sql
CREATE TABLE categorias (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(200),
    slug VARCHAR(100) NOT NULL UNIQUE,
    image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE produtos (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    descricao VARCHAR(300),
    preco NUMERIC(10,2) NOT NULL,
    quantidade_estoque INTEGER NOT NULL,
    slug VARCHAR(150) NOT NULL UNIQUE,
    image_url VARCHAR(500),
    categoria_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- Constraints únicas para slugs
ALTER TABLE categorias ADD CONSTRAINT uk_categorias_slug UNIQUE (slug);
ALTER TABLE produtos ADD CONSTRAINT uk_produtos_slug UNIQUE (slug);
```
