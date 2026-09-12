package br.com.fiap3espv.AutoEscola3ESPV.controller;

import br.com.fiap3espv.AutoEscola3ESPV.domain.usuario.*;
import br.com.fiap3espv.AutoEscola3ESPV.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService service;

    @PostMapping
    public ResponseEntity<DadosDetalhamentoUsuario> cadastrarUsuario(@RequestBody @Valid DadosCadastroUsuario request,
                                                                     UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoUsuario response = service.cadastrarUsuario(request);
        var uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemUsuario>> listarUsuarios(@PageableDefault(size = 10, sort = "login") Pageable pageable) {
        return ResponseEntity.ok(service.listarUsuarios(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoUsuario> atualizarUsuario(@RequestBody @Valid DadosAtualizacaoUsuario request,
                                                                     @PathVariable Long id) {
        DadosDetalhamentoUsuario response = service.atualizarUsuario(request, id);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/redefinicao_senha/{id}")
    public ResponseEntity<Void> redefinirSenhaUsuario(@RequestBody @Valid DadosRedefinicaoSenha novaSenha,
                                                      @PathVariable Long id,
                                                      @AuthenticationPrincipal Usuario usuarioLogado) {
        service.redefinirSenhaUsuario(novaSenha, id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerUsuario(@PathVariable Long id) {
        service.removerUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
