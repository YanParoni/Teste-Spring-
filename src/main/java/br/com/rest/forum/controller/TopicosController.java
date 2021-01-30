package br.com.rest.forum.controller;

import java.net.URI;


import java.util.Optional;
import javax.transaction.Transactional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import br.com.rest.forum.controller.dto.DetailTopicoDto;
import br.com.rest.forum.controller.dto.TopicoDto;
import br.com.rest.forum.controller.form.AtualizarForm;
import br.com.rest.forum.controller.form.TopicoForm;
import br.com.rest.forum.modelo.Topico;
import br.com.rest.forum.repository.CursoRepository;
import br.com.rest.forum.repository.TopicoRepository;

@RestController
@RequestMapping("/topicos")
public class TopicosController {
	
	@Autowired
	private TopicoRepository topicoRepository;
	
	@Autowired
	private CursoRepository cursoRepository;
	
	@GetMapping
	@Cacheable(value="listaDeTopico")
	public Page<TopicoDto> lista(@RequestParam(required=false) String nomeCurso,@PageableDefault(sort="id",direction=Direction.DESC,page = 0,size=2)Pageable paginacao ) {
		
		if (nomeCurso == null) {
			Page<Topico> topicos = topicoRepository.findAll(paginacao);
			return TopicoDto.converter(topicos);
		} else {
			Page<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso,paginacao);
			return TopicoDto.converter(topicos);
		}
	}
	
	@PostMapping
	@Transactional
	@CacheEvict(value="listaDeTopico",allEntries = true)
	public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder) {
		Topico topico = form.converter(cursoRepository);
		topicoRepository.save(topico);
		
		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
		return ResponseEntity.created(uri).body(new TopicoDto(topico));
	}
	
	@GetMapping ("/{id}")
	public ResponseEntity<DetailTopicoDto> detalhar(@PathVariable Long id)
	{	
		Optional<Topico> topico = topicoRepository.findById(id);
		
		if(topico.isPresent()){
			return ResponseEntity.ok(new DetailTopicoDto(topico.get()));
		}
		return ResponseEntity.notFound().build();
	} 

	@PutMapping("/{id}")
	@Transactional
	@CacheEvict(value="listaDeTopico",allEntries = true)
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id,@RequestBody @Valid AtualizarForm form  ) {
		Optional<Topico> optional = topicoRepository.findById(id);
		
		if(optional.isPresent()){
			Topico topico=form.atualizar(id, topicoRepository);
			return ResponseEntity.ok(new TopicoDto(topico));
		}
		return ResponseEntity.notFound().build();
		
		
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	@CacheEvict(value="listaDeTopico",allEntries = true)
	public ResponseEntity<?> deletar(@PathVariable Long id){
		Optional<Topico> optional = topicoRepository.findById(id);
		if(optional.isPresent()){
		topicoRepository.deleteById(id);
		return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}
}