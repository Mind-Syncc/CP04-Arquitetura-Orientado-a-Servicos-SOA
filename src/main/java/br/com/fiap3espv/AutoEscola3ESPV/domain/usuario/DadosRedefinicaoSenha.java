package br.com.fiap3espv.AutoEscola3ESPV.domain.usuario;

import jakarta.validation.constraints.NotBlank;

public record DadosRedefinicaoSenha(@NotBlank String senha) {
}
