package be.bruxellesformation.mabback.repositories;

import be.bruxellesformation.mabback.domain.Artefact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IArtefactsRepository extends JpaRepository<Artefact, String> {
    List<Artefact>
    findAllByNameContainingIgnoreCaseOrCulturalPhaseContainingIgnoreCaseOrTypeContainingIgnoreCaseOrMaterialContainingIgnoreCase(
            String name, String culturePhase, String type, String material);
}
