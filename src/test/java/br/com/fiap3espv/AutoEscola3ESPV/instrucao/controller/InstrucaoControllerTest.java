package br.com.fiap3espv.AutoEscola3ESPV.instrucao.controller;

import br.com.fiap3espv.AutoEscola3ESPV.controller.InstrucaoController;
import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.AgendaDeInstrucoes;
import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.DetalhamentoAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.domain.instrutor.Especialidade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InstrucaoAgendamentoControllerTest {

    private static final Especialidade ESPECIALIDADE = Especialidade.values()[0];

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");

    @InjectMocks
    private InstrucaoController controller;

    @Mock
    private AgendaDeInstrucoes agenda;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /instrucao com dados válidos deve retornar 200 OK")
    void agendarInstrucao_dadosValidos_retorna200() throws Exception {
        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);

        DetalhamentoAgendamento resposta = new DetalhamentoAgendamento(
                1L, "Mariana Souza Lima", "Ricardo Almeida Prado", ESPECIALIDADE, dataHora);

        when(agenda.agendar(any(DadosAgendamento.class))).thenReturn(resposta);

        mockMvc.perform(post("/instrucao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAgendamento(dataHora)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome_aluno").value("Mariana Souza Lima"))
                .andExpect(jsonPath("$.nome_instrutor").value("Ricardo Almeida Prado"))
                .andExpect(jsonPath("$.data_hora").value(FORMATO.format(dataHora)));
    }

    @Test
    @DisplayName("POST /instrucao com data no passado deve retornar 400 Bad Request")
    void agendarInstrucao_dataNoPassado_retorna400() throws Exception {
        LocalDateTime dataNoPassado = LocalDateTime.now().minusDays(1);

        mockMvc.perform(post("/instrucao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAgendamento(dataNoPassado)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(agenda);
    }

    private String jsonAgendamento(LocalDateTime dataHora) {
        return """
                {
                  "id_aluno": 1,
                  "id_instrutor": 2,
                  "data_hora": "%s"
                }
                """.formatted(FORMATO.format(dataHora));
    }
}