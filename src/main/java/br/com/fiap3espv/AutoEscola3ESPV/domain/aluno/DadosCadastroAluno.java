package br.com.fiap3espv.AutoEscola3ESPV.domain.aluno;

import br.com.fiap3espv.AutoEscola3ESPV.domain.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroAluno(@NotBlank
                                 String nome,

                                 @NotBlank
                                 @Email
                                 String email,

                                 @NotBlank
                                 String cpf,

                                 @NotNull
                                 @Valid
                                 DadosEndereco dadosEndereco) {
}
