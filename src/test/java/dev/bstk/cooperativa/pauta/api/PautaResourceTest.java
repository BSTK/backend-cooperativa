package dev.bstk.cooperativa.pauta.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.bstk.cooperativa.pauta.api.request.PautaRequest;
import dev.bstk.cooperativa.pauta.api.request.PautaVotoRequest;
import dev.bstk.cooperativa.pauta.model.Pauta;
import dev.bstk.cooperativa.pauta.model.SessaoVotacao;
import dev.bstk.cooperativa.pauta.model.Status;
import dev.bstk.cooperativa.pauta.service.PautaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PautaResource.class)
@ExtendWith(SpringExtension.class)
class PautaResourceTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String ENDPOINT_CADASTRAR_NOVA_PAUTA = "/v1/api/pautas";
    private static final String ENDPOINT_VOTAR_PAUTA = "/v1/api/pautas/{pautaId}/votar";
    private static final String ENDPOINT_INICIAR_SESSAO_VOTACAO = "/v1/api/pautas/{pautaId}/iniciar-sessao-votacao/{tempoDuracao}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PautaService pautaService;


    @Test
    @DisplayName("Deve cadastrar uma nova pauta")
    void t1() throws Exception {
        final var request = PautaRequest.builder().titulo("Titulo AAAA").build();
        final var pautacadastrada = Pauta.builder().id(1L).titulo(request.getTitulo()).build();

        Mockito.when(pautaService.cadastrarNovaPauta(Mockito.any())).thenReturn(pautacadastrada);

        mockMvc.perform(
           post(ENDPOINT_CADASTRAR_NOVA_PAUTA)
              .contentType(MediaType.APPLICATION_JSON)
              .content(MAPPER.writeValueAsString(request))
           )
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.id").isNotEmpty())
           .andExpect(jsonPath("$.titulo").isNotEmpty())
           .andExpect(jsonPath("$.titulo").value(request.getTitulo()));
    }

    @Test
    @DisplayName("Deve iniciar uma sessão de votacao para uma pauta já cadastrada")
    void t2() throws Exception {
        final long pautaId = 1L;
        final long tempoDuracao = 2L;
        final var dataHoraInicio = LocalDateTime.now();
        final var dataHoraTermino = dataHoraInicio.plus(tempoDuracao, ChronoUnit.MINUTES);

        final var sessaoVotacao = SessaoVotacao.builder()
                .id(1L)
                .status(Status.SessaoVotacaoStatus.INICIADA)
                .dataHoraInicio(dataHoraInicio)
                .dataHoraTermino(dataHoraTermino)
                .build();

        Mockito.when(pautaService.iniciarSessaoVotacao(pautaId, tempoDuracao))
               .thenReturn(sessaoVotacao);

        mockMvc.perform(
          post(ENDPOINT_INICIAR_SESSAO_VOTACAO, pautaId, tempoDuracao)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.dataHoraInicio").isNotEmpty())
        .andExpect(jsonPath("$.dataHoraTermino").isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    @DisplayName("Deve incluir um voto em uma pauta")
    void t3(final Boolean voto) throws Exception {
        final var request = PautaVotoRequest
           .builder()
           .associadoId(1L)
           .voto(voto)
           .build();

        mockMvc.perform(
           post(ENDPOINT_VOTAR_PAUTA, UUID.randomUUID().toString())
             .contentType(MediaType.APPLICATION_JSON)
             .content(MAPPER.writeValueAsString(request))
        )
        .andExpect(status().isNoContent());
    }
}
