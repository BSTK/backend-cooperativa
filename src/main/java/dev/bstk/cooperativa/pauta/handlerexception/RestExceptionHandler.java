package dev.bstk.cooperativa.pauta.handlerexception;

import dev.bstk.cooperativa.pauta.handlerexception.exception.NaoEncontradoException;
import dev.bstk.cooperativa.pauta.handlerexception.exception.PautaInvalidaException;
import dev.bstk.cooperativa.pauta.handlerexception.exception.VotoInvalidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<Map<Object, Object>> naoEncontrado(final Exception exception) {
        return execute(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({PautaInvalidaException.class, VotoInvalidoException.class})
    public ResponseEntity<Map<Object, Object>> exceptionUrlToken(final Exception exception) {
        return execute(exception, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Map<Object, Object>> execute(final Exception exception, final HttpStatus status) {
        return ResponseEntity.status(status).body(Map.of("error", new ErroResponse(exception.getMessage())));
    }
}
