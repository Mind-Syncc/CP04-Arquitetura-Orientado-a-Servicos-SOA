package br.com.fiap3espv.AutoEscola3ESPV.aluno.controller;

import br.com.fiap3espv.AutoEscola3ESPV.controller.AlunoController;
import br.com.fiap3espv.AutoEscola3ESPV.domain.aluno.Aluno;
import br.com.fiap3espv.AutoEscola3ESPV.domain.aluno.DadosCadastroAluno;
import br.com.fiap3espv.AutoEscola3ESPV.domain.aluno.DadosDetalhamentoAluno;
import br.com.fiap3espv.AutoEscola3ESPV.domain.endereco.DadosEndereco;
import br.com.fiap3espv.AutoEscola3ESPV.domain.endereco.Endereco;
import br.com.fiap3espv.AutoEscola3ESPV.service.AlunoService;
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
class AlunoControllerTest {

    @InjectMocks
    private AlunoController controller;

    @Mock
    private AlunoService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /alunos com dados válidos deve retornar 201 Created")
    void cadastrarAluno_dadosValidos_retorna201() throws Exception {
        DadosEndereco dadosEndereco = new DadosEndereco(
                "Avenida Paulista", "1500", null, "Bela Vista", "São Paulo", "SP", "01310100");
        Aluno aluno = new Aluno(1L, "João Pedro Silva", "joao.pedro@email.com",
                "12345678900", new Endereco(dadosEndereco), true);

        when(service.cadastrarAluno(any(DadosCadastroAluno.class)))
                .thenReturn(new DadosDetalhamentoAluno(aluno));

        String json = """
                {
                  "nome": "João Pedro Silva",
                  "email": "joao.pedro@email.com",
                  "cpf": "12345678900",
                  "cep": "01310100",
                  "numero": "1500"
                }
                """;

        mockMvc.perform(post("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/alunos/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Pedro Silva"))
                .andExpect(jsonPath("$.cpf").value("12345678900"));
    }

    @Test
    @DisplayName("POST /alunos com CEP inválido deve retornar 400 Bad Request")
    void cadastrarAluno_cepInvalido_retorna400() throws Exception {
        // CEP com 3 dígitos viola o @Pattern("[0-9]{8}") do DadosCadastroAluno
        String json = """
                {
                  "nome": "João Pedro Silva",
                  "email": "joao.pedro@email.com",
                  "cpf": "12345678900",
                  "cep": "123",
                  "numero": "1500"
                }
                """;

        mockMvc.perform(post("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}