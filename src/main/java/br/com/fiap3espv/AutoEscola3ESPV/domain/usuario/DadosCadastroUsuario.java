package br.com.fiap3espv.AutoEscola3ESPV.domain.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroUsuario(@NotBlank String login,
                                   @NotBlank String senha,
                                   @NotNull Role role) {
}
