package dev.bstk.cooperativa.pauta.api.request;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import dev.bstk.cooperativa.pauta.handlerexception.exception.VotoInvalidoException;

import java.io.IOException;
import java.util.Objects;

public class VotoDeserializer extends JsonDeserializer<Boolean> {

    private static final String SIM = "Sim";
    private static final String NAO = "Não";

    @Override
    public Boolean deserialize(final JsonParser parser, final DeserializationContext deserializationContext) throws IOException {
        final var valorInformado = parser.getValueAsString();
        final var valorInvalido = Objects.isNull(valorInformado)
                                || (!SIM.equalsIgnoreCase(valorInformado) && !NAO.equalsIgnoreCase(valorInformado));

        if (valorInvalido) {
            throw new VotoInvalidoException("Valor inválido! Apenas 'Sim' ou 'Não'");
        }

        return SIM.equalsIgnoreCase(valorInformado);
    }
}
