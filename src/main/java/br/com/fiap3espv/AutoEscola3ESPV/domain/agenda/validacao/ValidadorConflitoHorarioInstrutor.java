package br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.validacao;

import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.InstrucaoRepository;
import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidadorConflitoHorarioInstrutor implements ValidadorAgendamento {
    private final InstrucaoRepository repository;

    @Override
    public void validar(DadosAgendamento dados) {
        boolean instrutorOcupado = repository.existsByIdAndDataHora(dados.idInstrutor(), dados.dataHora());
        if (instrutorOcupado) {
            throw new ValidacaoException("Instrutor ocupado na data/hora informada!");
        }
    }
}