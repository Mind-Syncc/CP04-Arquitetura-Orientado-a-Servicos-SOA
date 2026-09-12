package br.com.fiap3espv.AutoEscola3ESPV.domain.usuario;

public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(String message) {
        super(message);
    }
}
