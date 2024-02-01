package br.com.alura.logs.controller;

import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import br.com.alura.logs.CursoApplication;
import br.com.alura.logs.dto.CursoDto;
import br.com.alura.logs.exceptions.InternalErrorException;
import br.com.alura.logs.model.CursoModel;
import br.com.alura.logs.service.CursoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins="*", maxAge=3600)
@RequestMapping("/cursos")
public class CursoController {
	
	final CursoService cursoService;
	
	private static Logger logger = LoggerFactory.getLogger(br.com.alura.logs.controller.CursoController.class);
	
	public CursoController(CursoService cursoService) {
		this.cursoService = cursoService;
	}
	
	@PostMapping
	 public ResponseEntity<Object> saveCurso(@RequestBody @Valid CursoDto cursoDto){
        logger.info("Iniciando o processo de insercao de registro de novo curso...");
		logger.info("Chamando o cursoService para validar se o numero de matricula ja existe");
		if(cursoService.existsByNumeroMatricula(cursoDto.getNumeroMatricula())) {
            logger.warn("Novo registro nao inserido, o numero de matricula ja existe");
			return ResponseEntity.status(HttpStatus.CONFLICT).body("O número de matricula do curso já esta em uso!");
		}
		
        logger.info("Chamando o cursoService para validar se o numero do curso ja existe");
		if(cursoService.existsByNumeroCurso(cursoDto.getNumeroCurso())) {
            logger.warn("Novo registro nao inserido, o numero do curso ja existe");
			return ResponseEntity.status(HttpStatus.CONFLICT).body("O número do curso já esta em uso!");
		}

        logger.info("Validacoes o cursoService sobre cursoDTO executadas com sucesso");
        logger.info("Chamando cursoService.save para armazenar novo registro");
		var cursoModel = new CursoModel();
		BeanUtils.copyProperties(cursoDto, cursoModel);
		cursoModel.setDataInscricao(LocalDateTime.now(ZoneId.of("UTC")));
        logger.info("Novo registro salvo com sucesso");
		return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.save(cursoModel));
	}
	
	
	@GetMapping
	public ResponseEntity<Page<CursoModel>> getAllCursos(@PageableDefault(page = 0, size = 10, sort = "dataInscricao", direction = Sort.Direction.ASC) Pageable pageable) {
		try {
            logger.info("Buscando todos os registros via GET");
            return ResponseEntity.status(HttpStatus.OK).body(cursoService.findAll(pageable));
        } catch(CannotCreateTransactionException e) {
            logger.error("Deu cao grandao na conexao com o DB");
            throw new InternalErrorException("Erro momentaneo, por favor tente novamente mais tarde...");
        }
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Object> getOneCursos(@PathVariable(value="id") UUID id) {
        logger.info("chamando cursoService para buscar um registros por UUID");
		Optional<CursoModel> cursoModelOptional = cursoService.findById(id);
        if (!cursoModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(cursoModelOptional.get());
    }
	
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCursos(@PathVariable(value = "id") UUID id){
        Optional<CursoModel> cursoModelOptional = cursoService.findById(id);
        if (!cursoModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado!");
        }
        cursoService.delete(cursoModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Curso excluído com sucesso!");
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCursos(@PathVariable(value = "id") UUID id, @RequestBody @Valid CursoDto cursoDto) {
        Optional<CursoModel> cursoModelOptional = cursoService.findById(id);
        if (!cursoModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado!");
        }
        var cursoModel = new CursoModel();
        BeanUtils.copyProperties(cursoDto, cursoModel);
        cursoModel.setId(cursoModelOptional.get().getId());
        cursoModel.setDataInscricao(cursoModelOptional.get().getDataInscricao());
        return ResponseEntity.status(HttpStatus.OK).body(cursoService.save(cursoModel));
    }

}
