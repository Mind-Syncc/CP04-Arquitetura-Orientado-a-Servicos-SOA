package br.com.fiap3espv.AutoEscola3ESPV.domain.agenda;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface InstrucaoRepository extends JpaRepository<Instrucao, Long> {
    boolean existsByIdAndDataHora(Long idInstrutor, LocalDateTime dataHora);

    boolean existsByIdAndDataHoraBetween(Long idAluno, LocalDateTime inicioExpediente, LocalDateTime fimExpediente);
}