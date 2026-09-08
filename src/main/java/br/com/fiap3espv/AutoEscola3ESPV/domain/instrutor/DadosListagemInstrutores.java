package br.com.fiap3espv.AutoEscola3ESPV.domain.instrutor;

public record DadosListagemInstrutores(
        Long id,
        String nome,
        String email,
        Especialidade especialidade) {
    public DadosListagemInstrutores(Instrutor instrutor) {
        this(
                instrutor.getId(),
                instrutor.getNome(),
                instrutor.getEmail(),
                instrutor.getEspecialidade()
        );
    }
}