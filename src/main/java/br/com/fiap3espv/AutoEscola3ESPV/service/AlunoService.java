package br.com.fiap3espv.AutoEscola3ESPV.service;

import br.com.fiap3espv.AutoEscola3ESPV.domain.aluno.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AlunoService {
    private final AlunoRepository repository;

    @Transactional
    public DadosDetalhamentoAluno cadastrarAluno(DadosCadastroAluno request) {

        if (repository.existsByCpf(request.cpf())) {
            throw new RuntimeException("CPF já cadastrado no sistema");
        }

        Aluno aluno = new Aluno(request);
        repository.save(aluno);
        return new DadosDetalhamentoAluno(aluno);
    }

    @Transactional(readOnly = true)
    public Page<DadosListagemAluno> listarAlunos(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable).map(DadosListagemAluno::new);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoAluno buscarAlunoPorId(Long id) {
        Aluno aluno = repository.findById(id).orElseThrow(() -> new AlunoNotFoundException("Aluno não encontrado"));
        return new DadosDetalhamentoAluno(aluno);
    }

    @Transactional
    public DadosDetalhamentoAluno atualizarAluno(DadosAtualizacaoAluno dadosAtualizacaoAluno, Long id) {
        Aluno aluno = repository.findById(id).orElseThrow(() -> new AlunoNotFoundException("Aluno não encontrado"));
        aluno.atualizarAluno(dadosAtualizacaoAluno);
        return new DadosDetalhamentoAluno(aluno);
    }

    @Transactional
    public void removerAluno(Long id) {
        Aluno aluno = repository.findById(id).orElseThrow(() -> new AlunoNotFoundException("Aluno não encontrado"));
        aluno.excluir();
        repository.save(aluno);
    }
}
