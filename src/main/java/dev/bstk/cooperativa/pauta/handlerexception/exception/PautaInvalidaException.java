package dev.bstk.cooperativa.pauta.handlerexception.exception;

public class PautaInvalidaException extends RuntimeException {

    public PautaInvalidaException(final String error) {
        super(error);
    }
}
