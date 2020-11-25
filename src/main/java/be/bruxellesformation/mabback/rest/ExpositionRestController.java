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
     * Responds to a GET request like "/expo/search?name=My super expo name"
     * @param name the name or part of name of the searched expo
     * @return a ResponseEntity containing the searched Exposition, or NO_CONTENT if nothing is found
     */
    @GetMapping("/search")
    public List<Exposition> findExpoByName(@RequestParam String name){
        return repository.findAllByTitleIgnoreCaseContaining(name);
    }

    /**
     * Responds to a POST request on "/expo". The request must contain an Exposition in its body.
     * @param exposition the Exposition entity to be added in the database
     * @return a ResponseEntity containing the created Exposition and a CREATED status. If the Exposition id
     * was already in the database a NOT_ACCEPTABLE status is returned.
     */
    @PostMapping
    public ResponseEntity<Exposition> create(@RequestBody Exposition exposition){
        if (!repository.existsById(exposition.getId())) {
            repository.save(exposition);
            return new ResponseEntity<>(exposition, HttpStatus.CREATED);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Responds to a PUT request on "/expo". The request must contain an Exposition in its body.
     * @param exposition is the Exposition object containing the values to update in the database.
     * @return a ResponseEntity containing the Exposition with updated values and an ACCEPTED status. If the update is
     * unsuccessful, it will return a NOT_ACCEPTABLE status.
     */
    @PutMapping
    public ResponseEntity<Exposition> update(@RequestBody Exposition exposition){
        try {
            repository.save(exposition);
            return new ResponseEntity<>(exposition,HttpStatus.ACCEPTED);
        } catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /**
     * Responds to a DELETE request on "/expo/{id}"
     * @param id is the identification of the Exposition to delete from the database
     * @return a ResponseEntity with the ACCEPTED status if the Exposition was in the database and was deleted. Otherwise,
     * it returns a NOT_ACCEPTED status.
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Exposition> deleteById(@PathVariable String id){
        Long idLong = Long.parseLong(id);
        Optional<Exposition> culture = repository.findById(idLong);
        if (culture.isPresent()) {
            repository.deleteById(idLong);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
}
