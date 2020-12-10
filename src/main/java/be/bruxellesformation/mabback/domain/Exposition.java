package be.bruxellesformation.mabback.domain;

import be.bruxellesformation.mabback.exceptions.ExpositionException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**  Project : Musée Archéologique de Brüsel
 *  File Name : Exposition.java
 *  Date : 23-11-20
 *  @author : Yorick Weenen
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Exposition {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String title;
	@Lob
	private String Description;
	private LocalDate startDate;
	private LocalDate endDate;
	private String imageUrl;
	private int visitorCount = 0;

	@OneToMany(mappedBy = "exposition", cascade = CascadeType.PERSIST)
	private List<Artefact> exposedArtefacts;

	/*
	 * -------------
	 * Constructor
	 * -------------
 	 */

	/**
	 * Create a new instance of the Exposition class.
	 * @param title is the name of the exposition
	 * @param description is the description of the exposition
	 * @param startDate the starting date of the exposition
	 * @param endDate the date of the ending of the exposition
	 */
	public Exposition(String title, String description, LocalDate startDate, LocalDate endDate, String imageUrl) {
		this.title = title;
		this.Description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.imageUrl = imageUrl;
		this.exposedArtefacts = new ArrayList<>();
	}

   /* -------------
	* Methods
	* -------------
	*/

	/** Add visitors to the exposition
	 *
	 * @param numberOfVisitors is the amount of visitors to add. It has to be above 0.
	 */
	public void addVisitors(int numberOfVisitors) {
		if(numberOfVisitors>0) {
			visitorCount += numberOfVisitors;
		}
	}

	/** Add a museum object to the exposition.
	 * Calls the {@link Artefact#displayArtefactInExposition(Exposition)} on the artefact.
	 * @param artefact is the museum object to be added in the exposition.
	 */
	public void addArtefactToExposition(Artefact artefact){
		artefact.displayArtefactInExposition(this);
		exposedArtefacts.add(artefact);
	}

	/** This method calls the {@link Artefact#sendOutOfExpo()} on each museum object of the exposition then removes all
	 * elements in the {@link #exposedArtefacts} collection in the Exposition instance.
	 * @throws ExpositionException with the identification of each Artefact that was not in an exposition in the message.
	 */
	public void endExposition(){
		if(exposedArtefacts.isEmpty()) {
			return;
		}
		List<String> problems = new ArrayList<>();
		for ( Artefact item : exposedArtefacts ) {
			boolean check = item.sendOutOfExpo();
			if(!check){
				problems.add(item.getIdentification());
			}
		}
		if(!problems.isEmpty()){
			throw new ExpositionException("Ces artefacts n'étaient pas dans une exposition : " + problems.toString());
		}
		exposedArtefacts.clear();
	}
}
