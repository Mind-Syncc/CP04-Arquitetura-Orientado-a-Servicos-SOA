package br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.validacao;

import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.Instrucao;
import br.com.fiap3espv.AutoEscola3ESPV.domain.agenda.ValidacaoException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ValidadorAntecedenciaCancelamento implements ValidadorCancelamento {
    @Override
    public void validar(Instrucao instrucao) {
        LocalDateTime limiteMinimoParaCancelar = LocalDateTime.now().plusHours(24);

        if (instrucao.getDataHora().isBefore(limiteMinimoParaCancelar)) {
            throw new ValidacaoException(
                    "O cancelamento só pode ser feito com antecedência mínima de 24 horas!");
        }
    }
}
