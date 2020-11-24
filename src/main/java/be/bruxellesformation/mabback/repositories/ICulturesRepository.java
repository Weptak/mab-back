package be.bruxellesformation.mabback.repositories;

import be.bruxellesformation.mabback.domain.Culture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICulturesRepository extends JpaRepository<Culture, Long> {

    List<Culture> findAllByStartYearBetweenOrEndYearBetween(int beginStart, int beginEnd, int endingStart, int endingEnd);
    List<Culture> findByNameIgnoreCaseContaining(String name);
}
