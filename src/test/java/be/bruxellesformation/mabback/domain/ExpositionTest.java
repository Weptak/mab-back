package be.bruxellesformation.mabback.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


class ExpositionTest {

    Exposition expositionTest = new Exposition(
            "My Test Expo",
            "This is a test expo",
            LocalDate.now(),
            LocalDate.of(2020,12, 25),
            "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cc/Ramses_II_at_Kadesh.jpg/260px-Ramses_II_at_Kadesh.jpg");

    Culture testCulture = new Culture("Ancienne Egypte", "Really old",
            "IIIe milléraire au Ier siècle ACN",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Ancient_Egypt_map-fr.svg/200px-Ancient_Egypt_map-fr.svg.png",
            -3125, -30);

    Artefact testArtefact = new Artefact("EG1000", "buste en calcaire",
            "This is a fake description", "statue", "pierre", testCulture,
            "Nouvel Empire", "New is always better", -1200, -1077,
            LocalDate.of(1986, 4, 24), "A3-27",
            "https://collectionapi.metmuseum.org/api/collection/v1/iiif/544454/1084271/main-image");

    @Test
    void addVisitors() {
        expositionTest.addVisitors(1);
        assertEquals(1, expositionTest.getVisitorCount());
    }

    @Test
    void addArtefactsToExposition() {
        expositionTest.addArtefactToExposition(testArtefact);
        assertEquals(1, expositionTest.getExposedArtefacts().size());
        assertEquals("EG1000",expositionTest.getExposedArtefacts().get(0).getIdentification() );
        assertTrue(testArtefact.isInExposition());
    }

    @Test
    void endExposition() {
        expositionTest.endExposition();
        assertFalse(testArtefact.isInExposition());
    }
}