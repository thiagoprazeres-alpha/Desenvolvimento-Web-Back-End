package com.example.aula03_3.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(
        int status,
        String mensagem,
        LocalDateTime timestamp,
        List<String> erros
) {
}
