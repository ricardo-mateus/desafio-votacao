package com.alelo.desafio.votacao.exception;

import com.alelo.desafio.votacao.service.CpfValidationClient;
import lombok.extern.java.Log;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
@Log
public class RestExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusiness(BusinessException ex) {
        log.info("....# Regra de regócio: " + ex.getMessage() +  " #....");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CpfValidationClient.CpfNotFoundException.class)
    public ResponseEntity<String> handleCpfNotFound(CpfValidationClient.CpfNotFoundException ex) {
        log.info("....# CPF não pode votar (externo) : " + ex.getMessage() +  "  #....");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> fallback(Exception ex) {
        log.info("....# Erro de servidor : " + ex.getMessage() +  "  #....");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
