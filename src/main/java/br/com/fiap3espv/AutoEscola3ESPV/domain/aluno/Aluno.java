package br.com.fiap3espv.AutoEscola3ESPV.domain.aluno;

import br.com.fiap3espv.AutoEscola3ESPV.domain.endereco.Endereco;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Aluno")
@Table(name = "alunos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Aluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String cpf;

    @Embedded
    private Endereco endereco;
    private boolean ativo = true;

    public Aluno(DadosCadastroAluno dadosCadastroAluno) {
        this.nome = dadosCadastroAluno.nome();
        this.email = dadosCadastroAluno.email();
        this.cpf = dadosCadastroAluno.cpf();
        this.endereco = new Endereco(dadosCadastroAluno.dadosEndereco());
    }

    public void atualizarAluno(DadosAtualizacaoAluno dadosAtualizacaoAluno) {
        this.nome = dadosAtualizacaoAluno.nome();
        this.endereco = new Endereco(dadosAtualizacaoAluno.dadosEndereco());
    }

    public void excluir() {
        this.ativo = false;
    }
}