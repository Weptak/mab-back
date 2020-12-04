package be.bruxellesformation.mabback.rest;

import be.bruxellesformation.mabback.domain.Artefact;
import be.bruxellesformation.mabback.domain.Exposition;
import be.bruxellesformation.mabback.repositories.IArtefactsRepository;
import be.bruxellesformation.mabback.repositories.IExpositionsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/expo")
public class ExpositionRestController {

    // Linked repository
    private IExpositionsRepository expositionsRepository;
    private IArtefactsRepository artefactsRepository;

    // Constructor
    public ExpositionRestController(IExpositionsRepository expositionsRepository,
                                    IArtefactsRepository artefactsRepository) {
        this.expositionsRepository = expositionsRepository;
        this.artefactsRepository = artefactsRepository;
    }


    // Rest Endpoints

    /**
     * Responds to a GET request on "/expo" by returning all the current Expositions, ordered by number of visitors.
     * @param pageNumber the page number of the result set
     * @param itemsPerPage the number of result per page
     * @return a Page of Exposition.
     */
    @GetMapping
    public Page<Exposition> allActiveExpositions(@RequestParam String pageNumber, @RequestParam String itemsPerPage){
        Pageable pagination = PageRequest.of(Integer.parseInt(pageNumber), Integer.parseInt(itemsPerPage),
                Sort.by(Sort.Direction.DESC, "visitorCount"));
        LocalDate now = LocalDate.now();
        return expositionsRepository.findAllByEndDateAfter(pagination, now);
    }

    /**
     * Responds to a GET request on "/expo/old" by returning all the ended expositions, ordered from the most recent to
     * the oldest one.
     * @param pageNumber the page number of the result set
     * @param itemsPerPage the number of result per page
     * @return a Page of Exposition.
     */
    @GetMapping("/old")
    public Page<Exposition> oldExpositions(@RequestParam String pageNumber, @RequestParam String itemsPerPage) {
        Pageable pagination = PageRequest.of(Integer.parseInt(pageNumber), Integer.parseInt(itemsPerPage));
        LocalDate now = LocalDate.now();
        return expositionsRepository.findAllByEndDateBeforeOrderByStartDateDesc(pagination, now);
    }

    /**
     * Responds to a GET request on "/expo/{id}"
     * @param id the identifier of the exposition
     * @return a ResponseEntity with the exposition corresponding to the id, or a NO_CONTENT if nothing is found
     */
    @GetMapping(path = "/{id}")
    public ResponseEntity<Exposition> findById(@PathVariable String id){
        Long identifier = Long.parseLong(id);
        Optional<Exposition> exposition = expositionsRepository.findById(identifier);
        return exposition.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Responds to a GET request like "/expo/search?name=My super expo name"
     * @param name the name or part of name of the searched expo
     * @return a ResponseEntity containing the searched Exposition, or NO_CONTENT if nothing is found
     */
    @GetMapping("/search")
    public List<Exposition> findExpoByName(@RequestParam String name){
        return expositionsRepository.findAllByTitleIgnoreCaseContaining(name);
    }

    /**
     * Responds to a POST request on "/expo". The request must contain an Exposition in its body.
     * @param exposition the Exposition entity to be added in the database
     * @return a ResponseEntity containing the created Exposition and a CREATED status. If the Exposition id
     * was already in the database a NOT_ACCEPTABLE status is returned.
     */
    @PostMapping
    public ResponseEntity<Exposition> create(@RequestBody Exposition exposition){
        if (!expositionsRepository.existsById(exposition.getId())) {
            expositionsRepository.save(exposition);
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
            expositionsRepository.save(exposition);
            return new ResponseEntity<>(exposition,HttpStatus.ACCEPTED);
        } catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds to a DELETE request on "/expo/{id}". It will delete the Exposition from the database and put any
     * artefact attributed to it in the reserves.
     * @param id is the identification of the Exposition to delete from the database
     * @return a ResponseEntity with the ACCEPTED status if the Exposition was in the database and was deleted. Otherwise,
     * it returns a NOT_ACCEPTED status.
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Exposition> deleteById(@PathVariable String id){
        Long idLong = Long.parseLong(id);
        Optional<Exposition> exposition = expositionsRepository.findById(idLong);
        if (exposition.isPresent()) {
            exposition.get().endExposition();  // Putting all the artefacts back in the reserves
            expositionsRepository.deleteById(idLong);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Responds to a PATCH request on "/expo/{id}". It will update the number of visitors with the passed value in the
     * body of the request
     * @param id the Id of the exposition to witch add visitors
     * @param number the number of visitors to add to the exposition
     * @return a ResponseEntity containing the exposition with updated value of visitorCount and a Http status OK.
     * If the id is not found, a ResponseEntity with the status NOT_ACCEPTABLE is returned instead.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Exposition> addVisitor(@PathVariable String id, @RequestBody int number){

        Optional<Exposition> searchedExpo = expositionsRepository.findById(Long.parseLong(id));

        if (!searchedExpo.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

        Exposition exposition = searchedExpo.get();
        exposition.addVisitors(number);
        expositionsRepository.save(exposition);
        return new ResponseEntity<>(exposition, HttpStatus.OK);
    }

    /**
     * Responds to a Patch request on "/{id}/addArtefacts".
     * The method calls {@link Exposition#addArtefactToExposition(Artefact)} on each artefact referred to in the items array.
     * @param id The id of the Exposition where the Artefacts must be added
     * @param items an Array of Strings containing the identification of each Artefact to add to the expo
     * @return a ResponseEntity containing the Exposition and a Status OK if it was successful. If the id is not found
     * or if the Array is empty, a NOT_ACCEPTABLE status is returned.
     */
    @PatchMapping("/{id}/addArtefacts")
    public ResponseEntity<Exposition> addArtefacts(@PathVariable String id, @RequestBody String[] items){

        // Searching and check
        Optional<Exposition> searchedExpo = expositionsRepository.findById(Long.parseLong(id));

        if (!searchedExpo.isPresent() || items.length == 0)
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

        Exposition exposition = searchedExpo.get();

        // Adding the artefacts and persisting in the database
        Arrays.stream(items).forEach(
            identifier-> artefactsRepository
                    .findById(identifier)
                    .ifPresent(exposition::addArtefactToExposition)
        );

        expositionsRepository.save(exposition);
        return new ResponseEntity<>(exposition,HttpStatus.OK);
    }

    /**
     * Responds to a PATCH request on "/{id}/endexpo". The method calls {@link Exposition#endExposition()}.
     * @param id The id of the Exposition to be ended.
     * @return A ResponseEntity with the Exposition and OK status, or a NOT_FOUND status if the id is not found.
     */
    @PatchMapping("/{id}/endexpo")
    public ResponseEntity<Exposition> endingExposition(@PathVariable String id){
        Exposition exposition = expositionsRepository.findById(Long.parseLong(id)).orElse(null);
        if(exposition == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        exposition.endExposition();
        expositionsRepository.save(exposition);
        return new ResponseEntity<>(exposition, HttpStatus.OK);
    }
}
