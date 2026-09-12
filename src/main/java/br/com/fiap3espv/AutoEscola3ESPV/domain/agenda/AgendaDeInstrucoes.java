package br.com.fiap3espv.AutoEscola3ESPV.domain.agenda;

import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.validacao.ValidadorAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.validacao.ValidadorCancelamento;
import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.validacao.ValidadorConflitoHorarioInstrutor;
import br.com.fiap3espv.AutoEscola3ESPV.domain.aluno.Aluno;
import br.com.fiap3espv.AutoEscola3ESPV.domain.aluno.AlunoNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.domain.aluno.AlunoRepository;
import br.com.fiap3espv.AutoEscola3ESPV.domain.instrutor.Instrutor;
import br.com.fiap3espv.AutoEscola3ESPV.domain.instrutor.InstrutorNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.domain.instrutor.InstrutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaDeInstrucoes {
    private final InstrucaoRepository repository;
    private final AlunoRepository alunoRepository;
    private final InstrutorRepository instrutorRepository;
    private final List<ValidadorAgendamento> validadoresAgendamento;
    private final List<ValidadorCancelamento> validadoresCancelamento;

    @Transactional
    public DetalhamentoAgendamento agendar(DadosAgendamento dados) {
        if (!alunoRepository.existsById(dados.idAluno())) {
            throw new AlunoNotFoundException("ID do aluno informado não existe!");
        }
        if (dados.idInstrutor() != null && !instrutorRepository.existsById(dados.idInstrutor())) {
            throw new InstrutorNotFoundException("ID do instrutor informado não existe");
        }
        //Validações
        validadoresAgendamento.forEach(v -> v.validar(dados));

        Aluno aluno = alunoRepository.getReferenceById(dados.idAluno());
        Instrutor instrutor = escolherInstrutor(dados);
        if (instrutor == null) {
            throw new ValidacaoException("Nenhum instrutor disponível para a data/hora informada!");
        }

        Instrucao instrucao = new Instrucao(aluno, instrutor, dados.dataHora());
        Instrucao salvo = repository.save(instrucao);
        return new DetalhamentoAgendamento(salvo);
    }

    private Instrutor escolherInstrutor(DadosAgendamento dados) {
        if (dados.idInstrutor() != null) {
            return instrutorRepository.getReferenceById(dados.idInstrutor());
        }
        if (dados.especialidade() == null) {
            throw new ValidacaoException("Especialidade é obrigatória, caso o instrutor não seja informado!");
        }
        return instrutorRepository.escolherInstrutorAleatorioDisponivel(dados.especialidade(), dados.dataHora());
    }

    @Transactional
    public void cancelar(DadosRemocaoAgenda dados) {
        Instrucao instrucao = repository.findById(dados.idInstrucao())
                .orElseThrow(() -> new InstrucaoNotFoundException("ID da instrução informado não existe!"));

        validadoresCancelamento.forEach(v -> v.validar(instrucao));

        instrucao.cancelar(dados.motivoCancelamento());
    }


}