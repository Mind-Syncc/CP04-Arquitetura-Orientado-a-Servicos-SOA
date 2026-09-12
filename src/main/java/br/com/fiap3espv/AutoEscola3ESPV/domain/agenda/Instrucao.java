package br.com.fiap3espv.AutoEscola3ESPV.domain.agenda;

import br.com.fiap3espv.AutoEscola3ESPV.domain.aluno.Aluno;
import br.com.fiap3espv.AutoEscola3ESPV.domain.instrutor.Instrutor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "Instrucao")
@Table(name = "instrucoes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Instrucao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aluno")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instrutor")
    private Instrutor instrutor;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Column(name = "cancelada")
    private boolean cancelada = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_cancelamento")
    private MotivoCancelamento motivoCancelamento = null;

    public Instrucao(Aluno aluno, Instrutor instrutor, LocalDateTime dataHora) {
        this.aluno = aluno;
        this.instrutor = instrutor;
        this.dataHora = dataHora;
        this.cancelada = false;
        this.motivoCancelamento = null;
    }

    public void cancelar(MotivoCancelamento motivo) {
        this.cancelada = true;
        this.motivoCancelamento = motivo;
    }
}