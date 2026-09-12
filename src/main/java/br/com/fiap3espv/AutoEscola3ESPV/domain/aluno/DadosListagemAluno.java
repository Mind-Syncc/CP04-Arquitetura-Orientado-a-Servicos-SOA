package br.com.fiap3espv.AutoEscola3ESPV.domain.aluno;

public record DadosListagemAluno(
                                 String nome,
                                 String email,
                                 String cpf) {
    public DadosListagemAluno(Aluno aluno) {
        this(aluno.getNome(), aluno.getEmail(), aluno.getCpf());
    }
}
