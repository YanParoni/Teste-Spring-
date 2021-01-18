package br.com.rest.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rest.forum.modelo.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {

	Curso findByNome(String nome);

}
