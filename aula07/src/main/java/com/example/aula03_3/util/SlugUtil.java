package com.example.aula03_3.util;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Utilitário para geração de slugs (URLs amigáveis).
 * 
 * Slug é uma representação de texto em formato URL-friendly,
 * substituindo caracteres especiais, espaços e acentos.
 */
public class SlugUtil {

    /**
     * Gera um slug a partir de uma string.
     * 
     * Exemplos:
     * - "Livro de Java" -> "livro-de-java"
     * - "Notebook Dell Inspiron" -> "notebook-dell-inspiron"
     * - "Eletrônicos em Geral" -> "eletronicos-em-geral"
     * 
     * @param text Texto para converter em slug
     * @return Slug formatado para URL
     */
    public static String generateSlug(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        // 1. Converter para minúsculas
        String slug = text.toLowerCase(Locale.forLanguageTag("pt-BR"));

        // 2. Remover acentos e caracteres especiais
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        // 3. Substituir caracteres inválidos por hífen
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");

        // 4. Substituir espaços múltiplos por um único hífen
        slug = slug.replaceAll("\\s+", "-");

        // 5. Remover hífens múltiplos
        slug = slug.replaceAll("-+", "-");

        // 6. Remover hífens no início e fim
        slug = slug.replaceAll("^-|-$", "");

        return slug;
    }

    /**
     * Gera um slug único adicionando um sufixo numérico se necessário.
     * 
     * @param text Texto base para o slug
     * @param suffix Sufixo numérico para garantir unicidade
     * @return Slug único
     */
    public static String generateUniqueSlug(String text, Long suffix) {
        String baseSlug = generateSlug(text);
        if (suffix != null && suffix > 0) {
            return baseSlug + "-" + suffix;
        }
        return baseSlug;
    }
}
