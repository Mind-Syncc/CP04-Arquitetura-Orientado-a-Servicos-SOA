package br.com.fiap3espv.AutoEscola3ESPV.domain.usuario;

public record DadosDetalhamentoUsuario(
        Long id,
        String login,
        Role role) {
    public DadosDetalhamentoUsuario(Usuario usuario) {
        this(usuario.getId(), usuario.getLogin(), usuario.getRole());
    }
}
