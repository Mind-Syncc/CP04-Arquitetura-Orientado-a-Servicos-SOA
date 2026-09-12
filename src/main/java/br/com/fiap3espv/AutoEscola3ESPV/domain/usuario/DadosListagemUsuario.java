package br.com.fiap3espv.AutoEscola3ESPV.domain.usuario;

public record DadosListagemUsuario(Long id,
                                   String login,
                                   String senha,
                                   Role role) {
    public DadosListagemUsuario(Usuario usuario) {
        this(usuario.getId(), usuario.getLogin(), usuario.getSenha(), usuario.getRole());
    }
}
