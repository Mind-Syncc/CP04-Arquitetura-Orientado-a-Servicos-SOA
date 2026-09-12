package br.com.fiap3espv.AutoEscola3ESPV.domain.agenda;

import jakarta.validation.constraints.NotNull;

public record DadosRemocaoAgenda (@NotNull Long idInstrucao,
                                  @NotNull MotivoCancelamento motivoCancelamento) {
}
