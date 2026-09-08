package br.com.fiap3espv.AutoEscola3ESPV.domain.agenda;

import br.com.fiap3espv.AutoEscola3ESPV.domain.instrutor.Especialidade;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DadosAgendamento(
        @NotNull
        @JsonAlias(value = "id_aluno")
        //@JsonProperty("id_aluno")
        Long idAluno,

        @JsonAlias(value = "id_instrutor")
        //@JsonProperty("id_instrutor")
        Long idInstrutor,
        Especialidade especialidade,

        @NotNull
        @Future
        @JsonFormat(pattern = "dd/MM/yyyy - HH:mm")
        @JsonAlias(value = "data_hora")
        //@JsonProperty("data_hora")
        LocalDateTime dataHora) {
}