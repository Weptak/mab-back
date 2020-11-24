package be.bruxellesformation.mabback.repositories;

import be.bruxellesformation.mabback.domain.Exposition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IExpositionsRepository extends JpaRepository<Exposition,Long> {
}
