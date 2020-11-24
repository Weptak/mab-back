package be.bruxellesformation.mabback.repositories;

import be.bruxellesformation.mabback.domain.Culture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICulturesRepository extends JpaRepository<Culture, Long> {
}
