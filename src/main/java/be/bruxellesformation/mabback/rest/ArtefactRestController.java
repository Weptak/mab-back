package be.bruxellesformation.mabback.rest;

import be.bruxellesformation.mabback.domain.Artefact;
import be.bruxellesformation.mabback.repositories.IArtefactsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/collections")
public class ArtefactRestController {

    // Linked Repository
    private IArtefactsRepository repository;

    // Constructor
    public ArtefactRestController(IArtefactsRepository repository) {
        this.repository = repository;
    }

    // Rest Endpoints

    /**
     * Responds to a GET request on "/collections"
     * @return a List of all the Artefacts
     */
    @GetMapping
    public List<Artefact> allArtefacts(){
        return repository.findAll();
    }

    /**
     * Responds to a GET request like "/collections/EG1000"
     * @param id the identification of the Artefact
     * @return a ResponseEntity containing the Artefact, or NO_CONTENT if nothing is found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Artefact> findById(@PathVariable String id){
        Optional<Artefact> artefact = repository.findById(id);
        return artefact.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    /**
     * Responds to a GET request like "/collections/search?criteria=statue"
     * @param criteria the searched value
     * @return a List of Artefacts where either the name, the cultural phase, the type or the material contains
     * the searched value
     */
    @GetMapping("/search")
    public List<Artefact> complexSearch(@RequestParam String criteria){
        String name, culturalPhase, type, material;
        name = culturalPhase = type = material = criteria;
        return repository.
                findAllByNameContainingIgnoreCaseOrCulturalPhaseContainingIgnoreCaseOrTypeContainingIgnoreCaseOrMaterialContainingIgnoreCase(
                        name, culturalPhase, type, material);
    }

    /**
     * Responds to a POST request on "/collections". The request must contain an Artefact in its body.
     * @param artefact the Artefact entity to be added in the database
     * @return a ResponseEntity containing the created artefact and a CREATED status. If the Artefact identification
     * was already in the database a NOT_ACCEPTABLE status is returned.
     */

    @PostMapping
    public ResponseEntity<Artefact> create(@RequestBody Artefact artefact){
        if (!repository.existsById(artefact.getIdentification())) {
            repository.save(artefact);
            return new ResponseEntity<>(artefact, HttpStatus.CREATED);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Responds to a PUT request on "/collections". The request must contain an Artefact in its body.
     * @param artefact is the Artefact object containing the values to update in the database.
     * @return a ResponseEntity containing the Artefact with updated values and an ACCEPTED status. If the update is
     * unsuccessful, it will return a NOT_ACCEPTABLE status.
     */
    @PutMapping
    public ResponseEntity<Artefact> update(@RequestBody Artefact artefact){
        try {
            repository.save(artefact);
            return new ResponseEntity<>(artefact,HttpStatus.ACCEPTED);
        } catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /**
     * Responds to a DELETE request on "/collections/{id}"
     * @param id is the identification of the Artefact to delete from the database
     * @return a ResponseEntity with the ACCEPTED status if the Artefact was in the database and was deleted. Otherwise,
     * it returns a NOT_ACCEPTED status.
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Artefact> deleteById(@PathVariable String id){
        Optional<Artefact> artefact = repository.findById(id);
        if (artefact.isPresent()) {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
}
