package be.bruxellesformation.mabback.rest;

import be.bruxellesformation.mabback.domain.Artefact;
import be.bruxellesformation.mabback.domain.Culture;
import be.bruxellesformation.mabback.repositories.IArtefactsRepository;
import be.bruxellesformation.mabback.repositories.ICulturesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/culture")
public class CultureRestController {

    //Linked Repositories
    private ICulturesRepository culturesRepository;
    private IArtefactsRepository artefactsRepository;

    // Constructor
    public CultureRestController(ICulturesRepository culturesRepository, IArtefactsRepository artefactsRepository) {
        this.culturesRepository = culturesRepository;
        this.artefactsRepository = artefactsRepository;
    }

    // Rest Endpoints

    /**
     * Responds to an Http GET request on "/culture"
     * @return the list of all the cultures in the repository
     */
    @GetMapping
    public List<Culture> allCultures(){
        return culturesRepository.findAllByOrderByStartYear();
    }

    /**
     * Responds to a GET request on "/culture/{id}"
     * @param id the id of the culture to return
     * @return a Response Entity containing the culture found, or a NO_CONTENT if nothing is found
     */
    @GetMapping(path = "/{id}")
    public ResponseEntity<Culture> findById(@PathVariable("id") String id){
        Optional<Culture> culture = culturesRepository.findById(Long.parseLong(id));
        return culture.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Responds to a GET request on "culture/{id}/artefacts" to retrieve all the artefacts from a culture.
     * @param id the id of the culture
     * @param pageNumber the page number of the result set
     * @param itemsPerPage the number of result per page
     * @return a Page of the artefacts related to the culture
     */
    @GetMapping(path = "/{id}/artefacts")
    public Page<Artefact> artefactsFromCulture(@PathVariable("id") String id ,@RequestParam String pageNumber, @RequestParam String itemsPerPage){
        Pageable pagination = PageRequest.of(Integer.parseInt(pageNumber), Integer.parseInt(itemsPerPage));
        Culture culture = culturesRepository.findById(Long.parseLong(id)).orElseThrow(NoSuchElementException::new);
        return artefactsRepository.findAllByCulture(culture,pagination);
    }

    /**
     * Responds to a GET request like "/culture/dates?startDate=-150&endDate=200"
     * @param startDate the earliest date for the search
     * @param endDate the latest date for the search
     * @return a List of Culture containing all the results of the search
     */
    @GetMapping("/dates")
    public List<Culture> culturesBetweenDates(@RequestParam int startDate, @RequestParam int endDate){
        int startEarlyLimit, endEarlyLimit;
        startEarlyLimit = endEarlyLimit = startDate;

        int startLateLimit, endLateLimit ;
        startLateLimit = endLateLimit= endDate;

        return culturesRepository.findAllByStartYearBetweenOrEndYearBetween(
                startEarlyLimit,startLateLimit,endEarlyLimit,endLateLimit);
    }

    /**
     * Responds to a GET request like "/culture/search?name=Romain"
     * @param name the name or part of name to be searched in the culture names
     * @return a List of Culture corresponding to the search
     */
    @GetMapping("/search")
    public List<Culture> findByName(@RequestParam String name){
        return culturesRepository.findByNameIgnoreCaseContaining(name);
    }

    /**
     * Responds to a POST request on "/culture". The request must contain a Culture in its body.
     * @param culture the Culture entity to be added in the database
     * @return a ResponseEntity containing the created culture and a CREATED status. If the Culture id
     * was already in the database a NOT_ACCEPTABLE status is returned.
     */
    @PostMapping
    public ResponseEntity<Culture> create(@RequestBody Culture culture){
        if (!culturesRepository.existsById(culture.getId())) {
            culturesRepository.save(culture);
            return new ResponseEntity<>(culture, HttpStatus.CREATED);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Responds to a PUT request on "/culture". The request must contain a Culture in its body.
     * @param culture is the Culture object containing the values to update in the database.
     * @return a ResponseEntity containing the Culture with updated values and an ACCEPTED status. If the update is
     * unsuccessful, it will return a NOT_ACCEPTABLE status.
     */
    @PutMapping
    public ResponseEntity<Culture> update(@RequestBody Culture culture){
        try {
            culturesRepository.save(culture);
            return new ResponseEntity<>(culture,HttpStatus.ACCEPTED);
        } catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds to a DELETE request on "/culture/{id}"
     * @param id is the identification of the Culture to delete from the database
     * @return a ResponseEntity with the ACCEPTED status if the Culture was in the database and was deleted. Otherwise,
     * it returns a NOT_ACCEPTED status.
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Culture> deleteById(@PathVariable String id){
        Long idLong = Long.parseLong(id);
        Optional<Culture> culture = culturesRepository.findById(idLong);
        if (culture.isPresent()) {
            culturesRepository.deleteById(idLong);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
}
