package dev.bstk.cooperativa.pauta.handlerexception;

import dev.bstk.cooperativa.pauta.handlerexception.exception.VotoInvalidoException;
import dev.bstk.cooperativa.pauta.handlerexception.exception.NaoEncontradoException;
import dev.bstk.cooperativa.pauta.handlerexception.exception.PautaInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<Map<Object, Object>> naoEncontrado(final Exception exception) {
        return execute(exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({PautaInvalidaException.class, VotoInvalidoException.class})
    public ResponseEntity<Map<Object, Object>> exceptionUrlToken(final Exception exception) {
        return execute(exception);
    }

    private ResponseEntity<Map<Object, Object>> execute(final Exception exception) {
        return ResponseEntity.ok(Map.of("error", new ErroResponse(exception.getMessage())));
    }
}
