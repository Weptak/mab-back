package be.bruxellesformation.mabback.repositories;

import be.bruxellesformation.mabback.domain.Exposition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IExpositionsRepository extends JpaRepository<Exposition,Long> {

    Page<Exposition> findAllByEndDateAfter(Pageable pageable, LocalDate date);
    Page<Exposition> findAllByEndDateBeforeOrderByStartDateDesc(Pageable pageable, LocalDate date);

    List<Exposition> findAllByTitleIgnoreCaseContaining(String name);
}
