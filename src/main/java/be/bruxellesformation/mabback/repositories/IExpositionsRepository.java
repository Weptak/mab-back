package be.bruxellesformation.mabback.repositories;

import be.bruxellesformation.mabback.domain.Exposition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IExpositionsRepository extends JpaRepository<Exposition,Long> {
    List<Exposition> findAllByTitleIgnoreCaseContaining(String name);
}
