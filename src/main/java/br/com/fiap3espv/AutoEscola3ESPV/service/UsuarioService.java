package br.com.fiap3espv.AutoEscola3ESPV.service;

import br.com.fiap3espv.AutoEscola3ESPV.domain.usuario.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DadosDetalhamentoUsuario cadastrarUsuario(DadosCadastroUsuario request) {
        if (repository.existsByLogin(request.login())) {
            throw new RuntimeException("Usuário com login já existente");
        }

        Usuario usuario = new Usuario(request);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        repository.save(usuario);
        return new DadosDetalhamentoUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public Page<DadosListagemUsuario> listarUsuarios(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable).map(DadosListagemUsuario::new);
    }

    @Transactional
    public DadosDetalhamentoUsuario atualizarUsuario(DadosAtualizacaoUsuario request, Long id) {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado"));
        usuario.atualizarDados(request);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        repository.save(usuario);
        return new DadosDetalhamentoUsuario(usuario);
    }

    @Transactional
    public void redefinirSenhaUsuario(DadosRedefinicaoSenha request, Long id, Usuario usuarioLogado) {
        if (!usuarioLogado.getId().equals(id)) {
            throw new AcessoNegadoException("Você só pode redefinir a própria senha!");
        }

        Usuario usuario = repository.findById(id).orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado"));

        if (!usuario.isAtivo()) {
            throw new RuntimeException("O usuário está desativado");
        } else if (passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new RuntimeException("A senha precisa ser diferente da original");
        }

        usuario.redefinirSenha(request.senha());
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
    }

    @Transactional
    public void removerUsuario(Long id) {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado"));
        usuario.removerUsuario();
    }
}
