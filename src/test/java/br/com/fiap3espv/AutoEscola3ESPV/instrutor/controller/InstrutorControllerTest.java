package br.com.fiap3espv.AutoEscola3ESPV.instrutor.controller;

import br.com.fiap3espv.AutoEscola3ESPV.controller.InstrutorController;
import br.com.fiap3espv.AutoEscola3ESPV.domain.endereco.DadosEndereco;
import br.com.fiap3espv.AutoEscola3ESPV.domain.instrutor.DadosCadastroInstrutor;
import br.com.fiap3espv.AutoEscola3ESPV.domain.instrutor.DadosDetalhamentoInstrutor;
import br.com.fiap3espv.AutoEscola3ESPV.domain.instrutor.Especialidade;
import br.com.fiap3espv.AutoEscola3ESPV.service.InstrutorService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class InstrutorControllerTest {

    private static final Especialidade ESPECIALIDADE = Especialidade.values()[0];

    @InjectMocks
    private InstrutorController controller;

    @Mock
    private InstrutorService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /instrutor com dados válidos deve retornar 201 Created")
    void cadastrarInstrutor_dadosValidos_retorna201() throws Exception {
        DadosEndereco endereco = new DadosEndereco(
                "Avenida Paulista", "1500", "Sala 10", "Bela Vista", "São Paulo", "SP", "01310200");
        DadosDetalhamentoInstrutor resposta = new DadosDetalhamentoInstrutor(
                1L, "Carlos dos Santos", "11976543210", "carlos.santos@autoescola.com",
                "12345678900", ESPECIALIDADE, endereco, true);

        when(service.cadastrarInstrutor(any(DadosCadastroInstrutor.class))).thenReturn(resposta);

        mockMvc.perform(post("/instrutor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCadastro("12345678900")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/instrutor/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Carlos dos Santos"))
                .andExpect(jsonPath("$.cnh").value("12345678900"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("POST /instrutor com CNH inválida deve retornar 400 Bad Request")
    void cadastrarInstrutor_cnhInvalida_retorna400() throws Exception {
        // CNH com 3 dígitos viola o @Pattern("\\d{9,11}") do DadosCadastroInstrutor
        mockMvc.perform(post("/instrutor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCadastro("123")))
                .andExpect(status().isBadRequest());

        // A validação barra a requisição antes de chegar ao service
        verifyNoInteractions(service);
    }

    // Atenção: no endereço, a cidade vai na chave "localidade" porque o DadosEndereco
    // mapeia esse campo com @JsonProperty("localidade").
    private String jsonCadastro(String cnh) {
        return """
                {
                  "nome": "Carlos dos Santos",
                  "telefone": "11976543210",
                  "email": "carlos.santos@autoescola.com",
                  "cnh": "%s",
                  "especialidade": "%s",
                  "endereco": {
                    "logradouro": "Avenida Paulista",
                    "numero": "1500",
                    "complemento": "Sala 10",
                    "bairro": "Bela Vista",
                    "localidade": "São Paulo",
                    "uf": "SP",
                    "cep": "01310200"
                  }
                }
                """.formatted(cnh, ESPECIALIDADE.name());
    }
}