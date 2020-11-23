package be.bruxellesformation.mabback.domain;

import be.bruxellesformation.mabback.exceptions.IsExposedException;
import be.bruxellesformation.mabback.exceptions.NotInExpositionException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ArtefactTest {

    Exposition testExposition = new Exposition(
            "My Test Expo",
            "This is a test expo",
            LocalDate.now(),
            LocalDate.of(2020,12, 25));

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
    void displayArtefactAndThenSendToReserves() {
        testArtefact.displayArtefactInRoom("Room 27");
        assertEquals("Room 27", testArtefact.getLocalisation());
        assertTrue(testArtefact.isOnPermanentDisplay());

        testArtefact.sendArtefactToReserves("A2-35");
        assertFalse(testArtefact.isOnPermanentDisplay());
        assertEquals("A2-35", testArtefact.getLocalisation());
    }

    @Test
    void displayArtefactInExpositionThenGetItOut() {
        testArtefact.displayArtefactInExposition(testExposition);
        assertTrue(testArtefact.isInExposition());
        String expected = "My Test Expo";
        assertEquals(expected, testArtefact.getLocalisation());
        assertEquals(testExposition, testArtefact.getExposition());

        testArtefact.getOutOfExpo();
        assertFalse(testArtefact.isInExposition());
        assertNull(testArtefact.getExposition());
        expected = "In reserves";
        assertEquals(expected, testArtefact.getLocalisation());
    }

    @Test
    void shouldGetExceptions(){
        testArtefact.displayArtefactInExposition(testExposition);
        assertThrows(IsExposedException.class, () -> testArtefact.displayArtefactInRoom("Room 27"));
        assertThrows(IsExposedException.class, () -> testArtefact.displayArtefactInExposition(testExposition));

        testArtefact.getOutOfExpo();
        assertThrows(NotInExpositionException.class, ()-> testArtefact.getOutOfExpo());

    }
}