package br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.validacao;

import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.ValidacaoException;
import br.com.fiap3espv.AutoEscola3ESPV.domain.instrutor.InstrutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidadorInstrutorAtivo implements ValidadorAgendamento {
    private InstrutorRepository instrutorRepository;

    @Override
    public void validar(DadosAgendamento dados) {
        boolean instrutorAtivo = instrutorRepository.findAtivoById(dados.idInstrutor());

        if (!instrutorAtivo) {
            throw new ValidacaoException("Instrução não pode ser agendada com instrutor inativo!");
        }
    }
}