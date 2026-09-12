package br.com.fiap3espv.AutoEscola3ESPV.controller;

import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.*;
import br.com.fiap3espv.AutoEscola3ESPV.domain.aluno.Aluno;
import br.com.fiap3espv.AutoEscola3ESPV.domain.aluno.AlunoRepository;
import br.com.fiap3espv.AutoEscola3ESPV.domain.instrutor.Instrutor;
import br.com.fiap3espv.AutoEscola3ESPV.domain.instrutor.InstrutorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/instrucao")
@RequiredArgsConstructor
public class InstrucaoController {
    private final AgendaDeInstrucoes agenda;

    @PostMapping
    public ResponseEntity<DetalhamentoAgendamento> agendarInstrucao(@RequestBody @Valid DadosAgendamento dados) {
        return ResponseEntity.ok(agenda.agendar(dados));
    }

    @DeleteMapping
    public ResponseEntity<Void> cancelarInstrucao(@RequestBody @Valid DadosRemocaoAgenda request) {
        agenda.cancelar(request);
        return ResponseEntity.noContent().build();
    }
}