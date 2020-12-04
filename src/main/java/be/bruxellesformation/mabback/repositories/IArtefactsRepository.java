package be.bruxellesformation.mabback.repositories;

import be.bruxellesformation.mabback.domain.Artefact;
import be.bruxellesformation.mabback.domain.Culture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IArtefactsRepository extends JpaRepository<Artefact, String> {
    List<Artefact>
    findAllByNameContainingIgnoreCaseOrCulturalPhaseContainingIgnoreCaseOrTypeContainingIgnoreCaseOrMaterialContainingIgnoreCase(
            String name, String culturePhase, String type, String material);

    Page<Artefact> findAllByCulture(Culture culture, Pageable pageable);

    List<Artefact> findAllByStartYearBetweenOrEndYearBetween(
            int startEarlyLimit, int startLateLimit, int endEarlyLimit, int endLateLimit);
}
