package br.com.fiap3espv.AutoEscola3ESPV.usuario.controller;

import br.com.fiap3espv.AutoEscola3ESPV.controller.UsuarioController;
import br.com.fiap3espv.AutoEscola3ESPV.domain.usuario.DadosCadastroUsuario;
import br.com.fiap3espv.AutoEscola3ESPV.domain.usuario.DadosDetalhamentoUsuario;
import br.com.fiap3espv.AutoEscola3ESPV.domain.usuario.Role;
import br.com.fiap3espv.AutoEscola3ESPV.service.UsuarioService;
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
class UsuarioControllerTest {

    @InjectMocks
    private UsuarioController controller;

    @Mock
    private UsuarioService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /usuarios com dados válidos deve retornar 201 Created")
    void cadastrarUsuario_dadosValidos_retorna201() throws Exception {
        DadosDetalhamentoUsuario resposta = new DadosDetalhamentoUsuario(1L, "fernanda.ribeiro", Role.ADMIN);

        when(service.cadastrarUsuario(any(DadosCadastroUsuario.class))).thenReturn(resposta);

        String json = """
                {
                  "login": "fernanda.ribeiro",
                  "senha": "Senha@123",
                  "role": "ADMIN"
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/usuarios/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("fernanda.ribeiro"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    @DisplayName("POST /usuarios com role inexistente deve retornar 400 Bad Request")
    void cadastrarUsuario_roleInexistente_retorna400() throws Exception {
        String json = """
                {
                  "login": "fernanda.ribeiro",
                  "senha": "Senha@123",
                  "role": "SUPERUSER"
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}