package be.bruxellesformation.mabback.rest;

import be.bruxellesformation.mabback.domain.Artefact;
import be.bruxellesformation.mabback.repositories.IArtefactsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin
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
     * Responds to a GET request on "/collections".
     * The body must contain a Map with the keys: pageNumber and itemsPerPage. The values should be numbers.
     * @param pageNumber the page number of the result set
     * @param itemsPerPage the number of result per page
     * @return a Page of all the Artefacts
     */
    @GetMapping
    public Page<Artefact> allArtefacts(@RequestParam String pageNumber, @RequestParam String itemsPerPage){
        Pageable pagination = PageRequest.of(Integer.parseInt(pageNumber), Integer.parseInt(itemsPerPage));
        return repository.findAll(pagination);
    }

    /**
     * Responds to a GET request like "/collections/EG1000"
     * @param id the identification of the Artefact in the path of the request
     * @return a ResponseEntity containing the Artefact, or NO_CONTENT if nothing is found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Artefact> findById(@PathVariable String id){
        Optional<Artefact> artefact = repository.findById(id);
        return artefact.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    /**
     * Responds to a GET request like "/collections/dates?startDate=-150&endDate=200"
     * @param startDate the earliest date for the search
     * @param endDate the latest date for the search
     * @return a List of Artefact containing all the results of the search
     */
    @GetMapping("/dates")
    public List<Artefact> artefactsBetweenDates(@RequestParam int startDate, @RequestParam int endDate){
        int startEarlyLimit, endEarlyLimit;
        startEarlyLimit = endEarlyLimit = startDate;

        int startLateLimit, endLateLimit ;
        startLateLimit = endLateLimit= endDate;

        return repository.findAllByStartYearBetweenOrEndYearBetween(
                startEarlyLimit,startLateLimit,endEarlyLimit,endLateLimit);
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
            return new ResponseEntity<>(artefact,HttpStatus.OK);
        } catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
            return new ResponseEntity<>(HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Responds to a PATCH request on "/collections/{id}" followed by optional request parameters. Used to move an
     * Artefact to a new room, to the reserves or remove it from an exposition to the reserves.
     * @param id The ID of the Artefact that has to be moved.
     * @param room The name of the room into which the artefact is to be moved.
     *             If the value is "reserves", the artefact will be moved to the reserves by calling the
     *             {@link Artefact#sendArtefactToReserves(String)} method.
     *             If the value is "off expo", the artefact will be moved out of the exposition by calling the
     *             {@link Artefact#sendOutOfExpo()} method.
     * @return A ResponseEntity with the updated Artefact and an OK status. A NOT_FOUND status will be returned if
     * the ID is not found in the database. An INTERNAL_SERVER_ERROR is returned if the room value is "off expo" and
     * the Artefact was not in an exposition.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Artefact> changeLocation(@PathVariable String id,
                                                   @RequestParam(required = false) String room){

        // Retrieve the Artefact
        Artefact artefact;
        Optional<Artefact> searchedArtefact = repository.findById(id);
        if(!searchedArtefact.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            artefact = searchedArtefact.get();

        // Change the location depending on the parameters of the request
        switch (room){
            case "reserves" :
                artefact.sendArtefactToReserves("In Reserves");
                break;
            case "off expo" :
                if(!artefact.sendOutOfExpo()){
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;
            default: artefact.changeLocalisation(room);
        }

        repository.save(artefact);
        return new ResponseEntity<>(artefact,HttpStatus.OK);
    }
}
