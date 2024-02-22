package dev.bstk.cooperativa.pauta.handlerexception.exception;

public class VotoInvalidoException extends RuntimeException {

    public VotoInvalidoException(final String error) {
        super(error);
    }
}
