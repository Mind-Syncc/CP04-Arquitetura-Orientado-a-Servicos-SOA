package br.com.fiap3espv.AutoEscola3ESPV.domain.aluno;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    @Query("""
        select e.ativo
        from Aluno e
        where
        e.id = :id
    """)
    boolean findAtivoById(Long id);

    boolean existsByCpf(String cpf);

    Page<Aluno> findAllByAtivoTrue(Pageable pageable);
}