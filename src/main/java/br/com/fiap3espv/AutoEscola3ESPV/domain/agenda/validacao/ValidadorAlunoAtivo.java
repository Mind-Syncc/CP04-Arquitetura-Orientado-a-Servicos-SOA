package br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.validacao;

import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.ValidacaoException;
import br.com.fiap3espv.AutoEscola3ESPV.domain.aluno.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidadorAlunoAtivo implements ValidadorAgendamento {
    private final AlunoRepository alunoRepository;

    @Override
    public void validar(DadosAgendamento dados) {
        boolean alunoAtivo = alunoRepository.findAtivoById(dados.idAluno());

        if (!alunoAtivo) {
            throw new ValidacaoException("Instrução não pode ser agendada para aluno inativo!");
        }
    }
}