package br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.validacao;

import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.Instrucao;
import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.InstrucaoRepository;
import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ValidadorLimiteDiarioAluno implements ValidadorAgendamento {
    private final InstrucaoRepository repository;

    @Override
    public void validar(DadosAgendamento dados) {
        LocalDateTime inicioExpediente = dados.dataHora().withHour(6);
        LocalDateTime fimExpediente = dados.dataHora().withHour(21 - 1);
        boolean alunoReincidenciaDiaria = repository.existsByIdAndDataHoraBetween(
                dados.idAluno(),
                inicioExpediente,
                fimExpediente
        );
        if (alunoReincidenciaDiaria) {
            throw new ValidacaoException("Permitida apenas uma instrução diária por aluno!");
        }
    }
}