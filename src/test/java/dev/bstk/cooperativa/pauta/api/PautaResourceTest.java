package dev.bstk.cooperativa.pauta.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.bstk.cooperativa.pauta.api.request.PautaRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

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
    private static final String ENDPOINT_INICIAR_SESSAO_VOTACAO = "/v1/api/pautas/{pautaId}/iniciar-sessao-votacao/{tempoDuracao}";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("[ POST ] - Deve cadastrar uma nova pauta")
    void t1() throws Exception {
        final var request = PautaRequest
                .builder()
                .titulo("Titulo AAAA")
                .descricao("Descricao AAAA")
                .build();

        mockMvc.perform(
           post(ENDPOINT_CADASTRAR_NOVA_PAUTA)
              .contentType(MediaType.APPLICATION_JSON)
              .content(MAPPER.writeValueAsString(request))
           )
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.uuid").isNotEmpty())
           .andExpect(jsonPath("$.titulo").isNotEmpty())
           .andExpect(jsonPath("$.titulo").value(request.getTitulo()))
           .andExpect(jsonPath("$.descricao").isNotEmpty())
           .andExpect(jsonPath("$.descricao").value(request.getDescricao()));
    }

    @Test
    @DisplayName("[ POST ] - Deve iniciar uma sessão de votacao para uma pauta já cadastrada")
    void t2() throws Exception {
        mockMvc.perform(
          post(ENDPOINT_INICIAR_SESSAO_VOTACAO, UUID.randomUUID().toString(), "3")
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.uuid").isNotEmpty())
        .andExpect(jsonPath("$.pauta.uuid").isNotEmpty())
        .andExpect(jsonPath("$.dataHoraInicio").isNotEmpty())
        .andExpect(jsonPath("$.dataHoraTermino").isNotEmpty());
    }
}
