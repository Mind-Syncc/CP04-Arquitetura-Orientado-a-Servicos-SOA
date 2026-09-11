package br.com.fiap3espv.AutoEscola3ESPV.domain.aluno;

import br.com.fiap3espv.AutoEscola3ESPV.domain.endereco.DadosEndereco;

public record DadosDetalhamentoAluno(Long id,
                                     String nome,
                                     String email,
                                     String cpf,
                                     DadosEndereco dadosEndereco,
                                     boolean ativo) {
    public DadosDetalhamentoAluno(Aluno aluno) {
        this(aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getCpf(),
                new DadosEndereco(aluno.getEndereco()),
                aluno.isAtivo());
    }
}
