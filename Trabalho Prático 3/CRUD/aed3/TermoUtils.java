

package aed3;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TermoUtils {

    // Lista de Stop Words (Palavras irrelevantes para as buscas)
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "a", "o", "as", "os", "de", "do", "da", "dos", "das", "em", "um", "uma",
        "uns", "umas", "e", "é", "para", "por", "com", "sem", "no", "na", "nos", "nas",
        "oito", "um", "dois", "tres", "quatro", "cinco", "seis", "sete", "nove", "dez" // Inclusão de numerais
    ));

    /**
     * Processa uma string (nome do produto) para gerar um vetor de termos válidos.
     * Inclui tokenização, remoção de stop words e normalização.
     * @param texto A string a ser processada (ex: nome do produto).
     * @return Uma lista de termos válidos, em minúsculas e sem acentos.
     */
    public static List<String> processarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return List.of();
        }

        // 1. Normalização e Limpeza: Remove acentos e converte para minúsculas
        String normalizado = Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD)
                                       .replaceAll("[^\\p{ASCII}]", "") // Remove caracteres não-ASCII (que eram acentos)
                                       .replaceAll("[^a-z\\s]", " ")    // Substitui caracteres especiais/pontuação por espaço
                                       .replaceAll("\\s+", " ")         // Reduz múltiplos espaços para um único
                                       .trim();
        
        // 2. Tokenização: Divide em palavras
        String[] palavras = normalizado.split(" ");
        
        // 3. Filtragem: Remove stop words
        return Arrays.stream(palavras)
                     .filter(palavra -> !palavra.isEmpty() && !STOP_WORDS.contains(palavra))
                     .toList();
    }
}