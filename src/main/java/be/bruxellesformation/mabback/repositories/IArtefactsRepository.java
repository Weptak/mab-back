package be.bruxellesformation.mabback.repositories;

import be.bruxellesformation.mabback.domain.Artefact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IArtefactsRepository extends JpaRepository<Artefact, String> {
}
