package dev.bstk.cooperativa.pauta.api.request;

import com.fasterxml.jackson.core.JsonParser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class VotoDeserializerTest {

    private final VotoDeserializer votoDeserializer = new VotoDeserializer();

    @Mock
    private JsonParser parser;

    @ParameterizedTest
    @DisplayName("Deve converter resposta válida Sim para True")
    @ValueSource(strings = {"Sim", "sim", "SIM", "sIm", "sIM",})
    void t1(final String valorInformado) throws IOException {
        Mockito.when(parser.getValueAsString()).thenReturn(valorInformado);

        final Boolean valorBooleanoConvertido = votoDeserializer.deserialize(parser, null);

        Assertions.assertThat(valorBooleanoConvertido).isNotNull().isTrue();
    }

    @ParameterizedTest
    @DisplayName("Deve converter resposta válida Não para False")
    @ValueSource(strings = {"Não", "não", "NÃO", "nÃo", "nÃO"})
    void t2(final String valorInformado) throws IOException {
        Mockito.when(parser.getValueAsString()).thenReturn(valorInformado);

        final Boolean valorBooleanoConvertido = votoDeserializer.deserialize(parser, null);

        Assertions.assertThat(valorBooleanoConvertido).isNotNull().isFalse();
    }

    @ParameterizedTest
    @DisplayName("Deve lançar exception para respostas inválidas")
    @NullAndEmptySource
    @ValueSource(strings = {"Nãoa", "ão","Cim", "Çim", "ssin", "ÃO", "N", "n", "S", "s"})
    void t3(final String valorInformado) throws IOException {
        Mockito.when(parser.getValueAsString()).thenReturn(valorInformado);

        Assertions
           .assertThatThrownBy(() -> votoDeserializer.deserialize(parser, null))
           .isInstanceOf(IllegalArgumentException.class)
           .hasMessage("Valor inválido! Apenas 'Sim' ou 'Não'");
    }
}
