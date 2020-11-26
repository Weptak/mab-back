package be.bruxellesformation.mabback.domain;

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
	private int visitorCount = 0;

	@OneToMany(mappedBy = "exposition")
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
	public Exposition(String title, String description, LocalDate startDate, LocalDate endDate) {
		this.title = title;
		Description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.exposedArtefacts = new ArrayList<>();
	}

   /* -------------
	* Business Methods
	* -------------
	*/

	/** Add visitors to the exposition
	 *
	 * @param numberOfVisitors is the amount of visitors to add. It has to be above 0.
	 */
	public void addVisitors(int numberOfVisitors) {
		if(numberOfVisitors>0)
			visitorCount += numberOfVisitors;
	}

	/** Add museum objects to the exposition. If no objects are sent in parameter, the method does nothing.
	 *
	 * @param items is the museum objects to be added in the exposition.
	 */
	public void addArtefactsToExposition(Artefact... items){
		if (items==null)
			return;
		for (Artefact item: items ) {
			item.displayArtefactInExposition(this);
			exposedArtefacts.add(item);
		}
	}

	/** This method calls the {@link Artefact#getOutOfExpo()} on each museum object of the exposition then removes all
	 * elements in the {@link #exposedArtefacts} collection in the Exposition instance.
	 */
	public void endExposition(){
		for ( Artefact item : exposedArtefacts ) {
			item.getOutOfExpo();
		}
		exposedArtefacts.clear();
	}
}
