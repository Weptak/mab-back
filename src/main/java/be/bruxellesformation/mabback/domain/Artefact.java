package be.bruxellesformation.mabback.domain;

import be.bruxellesformation.mabback.exceptions.IsExposedException;
import be.bruxellesformation.mabback.exceptions.NotInExpositionException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

/** Project : Musée Archéologique de Brüsel
 * File Name : Artefact.java
 * Date : 23-11-20
 * @author : Yorick Weenen
 * The identification is the inventory identifier of the artefact
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Artefact {

	@Id
	private String identification;
	private String name;
	@Lob
	private String objectDescription;
	private String periodDescription;
	private String culturalPhase;
	private String type;
	private String material;
	private String localisation;
	private String imageURL;
	private boolean onPermanentDisplay = false;
	private boolean inExposition = false;
	private LocalDate dateOfEntry;
	private int startYear;
	private int endYear;
	@ManyToOne(cascade = CascadeType.ALL)
	@JsonIgnoreProperties("artefacts")
	private Culture culture;
	@ManyToOne (cascade = CascadeType.ALL)
	@JsonIgnore
	private Exposition exposition;

	/* ----------------
	 *  Constructor
	 *  ----------------
	 */

	/** Creates a new museum object instance.
	 *
	 * @param identification is the museum's identifier for the object, has to be unique.
	 * @param name is the name of the object, if it has one
	 * @param objectDescription describes the object
	 * @param type is the type of object, like "statue" or "amphora"
	 * @param material is the primary material used to make the object
	 * @param culture is the culture associated with the object
	 * @param culturalPhase is the cultural phase associated with the object.
	 * @param periodDescription is the literary value of the date of the object, like "second half of 2nd century AD"
	 * @param startYear the starting date of this object or object type date in Year, like "150"
	 * @param endYear the ending date of this object or object type date in Year, like "199"
	 * @param dateOfEntry when the museum object is added to the collection
	 * @param localisation the physical location of the object, can be a room identifier, an exposition name or a location in the reserves
	 * @param imageURL the link to the picture of the object
	 */
	public Artefact(String identification, String name, String objectDescription, String type, String material,
					Culture culture, String culturalPhase, String periodDescription, int startYear, int endYear,
					LocalDate dateOfEntry, String localisation, String imageURL) {
		this.identification = identification;
		this.name = name;
		this.periodDescription = periodDescription;
		this.culturalPhase = culturalPhase;
		this.type = type;
		this.material = material;
		this.localisation = localisation;
		this.imageURL = imageURL;
		this.dateOfEntry = dateOfEntry;
		this.startYear = startYear;
		this.endYear = endYear;
		this.culture = culture;
		this.objectDescription = objectDescription;
	}

	/* ----------------
	 *  Business Methods
	 *  ----------------
	 */

	/** Change the state of the museum object to be one in permanent display.
	 *  The method checks that the object is not in an exposition, and so unavailable.
	 *  The isOnPermanentDisplay is set to true.
	 * @param roomId is a String representing the code of the room the object is to be displayed in.
	 */
	public void displayArtefactInRoom(String roomId) {
		checkOnExpo();
		this.localisation = roomId;
		onPermanentDisplay =true;
	}


	/** Change the state of the museum object from one on permanent display to the reserves.
	 * @param reserveLocation the location in reserve to store the object
	 */
	public void sendArtefactToReserves(String reserveLocation){
		checkOnExpo();
		onPermanentDisplay = false;
		this.localisation=reserveLocation;
	}

	/** Change the state of the museum object to be one in an exposition.
	 * The method checks that the object is not already in an exposition, and so unavailable.
	 * The isInExposition is set to true.
	 * @param exposition the title of the exposition is used to update the localisation of the object.
	 */
	public void displayArtefactInExposition(Exposition exposition) {
		checkOnExpo();
		if (isOnPermanentDisplay())
			onPermanentDisplay=false;
		this.inExposition =true;
		this.localisation=exposition.getTitle();
		this.exposition=exposition;
	}

	/** The object is to be sent back to the museum's reserves.
	 *  The method checks if the object is currently in an exposition.
	 * @throws NotInExpositionException if the museum object is not in an exposition.
	 */
	public void getOutOfExpo(){
		if (inExposition) {
			inExposition = false;
			this.localisation = "In reserves";
			this.exposition=null;
		} else
			throw new NotInExpositionException("L'objet "+ this.identification +" n'est pas dans une exposition");
	}

	/** Check if the museum object is already in an exposition.
	 * @throws IsExposedException is thrown if the object is in an exposition.
	 */
	private void checkOnExpo() {
		if (inExposition)
			throw new IsExposedException("L'objet "+ this.identification+" est actuellement en exposition");
	}

}
