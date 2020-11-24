package be.bruxellesformation.mabback.rest;

import be.bruxellesformation.mabback.domain.Exposition;
import be.bruxellesformation.mabback.repositories.IExpositionsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/expo")
public class ExpositionRestController {

    // Linked repository
    private IExpositionsRepository repository;

    // Constructor
    public ExpositionRestController(IExpositionsRepository repository) {
        this.repository = repository;
    }

    // Rest Endpoints

    /**
     * Responds to a GET request on "/expo" by returning all the Expositions
     * @return a List of Exposition
     */
    @GetMapping
    public List<Exposition> allExpositions(){
        return repository.findAll();
    }

    /**
     * Responds to a GET request on "/expo/{id}"
     * @param id the identifier of the exposition
     * @return a ResponseEntity with the exposition corresponding to the id, or a NO_CONTENT if nothing is found
     */
    @GetMapping(path = "/{id}")
    public ResponseEntity<Exposition> findById(@PathVariable String id){
        Long identifier = Long.parseLong(id);
        Optional<Exposition> exposition = repository.findById(identifier);
        return exposition.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    /**
     * Responds to a GET request like "/expo?name=My super expo name"
     * @param name the name or part of name of the searched expo
     * @return a ResponseEntity containing the searched Exposition, or NO_CONTENT if nothing is found
     */
    @GetMapping
    public List<Exposition> findExpoByName(@RequestParam String name){
        return repository.findAllByTitleIgnoreCaseContaining(name);
    }
}
