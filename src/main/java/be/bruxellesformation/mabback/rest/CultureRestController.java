package be.bruxellesformation.mabback.rest;

import be.bruxellesformation.mabback.domain.Culture;
import be.bruxellesformation.mabback.repositories.ICulturesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/culture")
public class CultureRestController {

    //Linked Repository
    private ICulturesRepository repository;

    // Constructor
    public CultureRestController(ICulturesRepository repository) {
        this.repository = repository;
    }

    // Rest Endpoints

    /**
     * Responds to an Http GET request on "/culture"
     * @return the list of all the cultures in the repository
     */
    @GetMapping
    public List<Culture> allCultures(){
        return repository.findAll();
    }

    /**
     * Responds to a GET request on "/culture/{id}"
     * @param id the id of the culture to return
     * @return a Response Entity containing the culture found, or a NO_CONTENT if nothing is found
     */
    @GetMapping(path = "/{id}")
    public ResponseEntity<Culture> findById(@PathVariable("id") String id){
        Optional<Culture> culture = repository.findById(Long.parseLong(id));
        return culture.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    /**
     * Responds to a GET request like "/culture?startDate=-150&endDate=200"
     * @param startDate the earliest date for the search
     * @param endDate the latest date for the search
     * @return a List of Culture containing all the results of the search
     */
    @GetMapping
    public List<Culture> culturesBetweenDates(@RequestParam String startDate, @RequestParam String endDate){
        int beginStart, endingStart;
        beginStart = endingStart = Integer.parseInt(startDate);
        int beginEnd, endingEnd ;
        beginEnd = endingEnd= Integer.parseInt(endDate);
        return repository.findAllByStartYearBetweenOrEndYearBetween(beginStart,beginEnd,endingStart,endingEnd);
    }

    /**
     * Responds to a GET request like "/culture?name=Romain"
     * @param name the name or part of name to be searched in the culture names
     * @return a List of Culture corresponding to the search
     */
    @GetMapping
    public List<Culture> findByName(@RequestParam String name){
        return repository.findByNameIgnoreCaseContaining(name);
    }
}
