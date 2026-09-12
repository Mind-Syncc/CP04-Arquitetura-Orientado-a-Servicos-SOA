package br.com.fiap3espv.AutoEscola3ESPV.controller;

import br.com.fiap3espv.AutoEscola3ESPV.domain.aluno.DadosAtualizacaoAluno;
import br.com.fiap3espv.AutoEscola3ESPV.domain.aluno.DadosCadastroAluno;
import br.com.fiap3espv.AutoEscola3ESPV.domain.aluno.DadosDetalhamentoAluno;
import br.com.fiap3espv.AutoEscola3ESPV.domain.aluno.DadosListagemAluno;
import br.com.fiap3espv.AutoEscola3ESPV.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
public class AlunoController {
    private final AlunoService service;

    @PostMapping
    public ResponseEntity<DadosDetalhamentoAluno> cadastrarAluno(@RequestBody @Valid DadosCadastroAluno request,
                                                                 UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoAluno response = service.cadastrarAluno(request);
        URI uri = uriBuilder.path("/alunos/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemAluno>> listarAlunos(@PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listarAlunos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoAluno> buscarAlunoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarAlunoPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoAluno> atualizarAluno(@RequestBody @Valid DadosAtualizacaoAluno request, @PathVariable Long id) {
        return ResponseEntity.ok(service.atualizarAluno(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerAluno(@PathVariable Long id) {
        service.removerAluno(id);
        return ResponseEntity.noContent().build();
    }
}
