package be.bruxellesformation.mabback.domain;


import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**  Project : Musée Archéologique de Brüsel
 *  File Name : Culture.java
 *  Date : 23-11-20
 *  @author : Yorick Weenen
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Culture {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String name;
	@Lob
	private String description;
	private String periodDescription;
	private String cultureMap;
	private int startYear;
	private int endYear;
	@OneToMany(mappedBy = "culture", cascade = CascadeType.ALL)
	@JsonIgnoreProperties("culture")
	private List<Artefact> artefacts;

	/*
	 * ----------------
	 * Constructor
	 * ----------------
	 */

	/** Creates a new instance of the Culture class.
	 *
	 * @param name is the name given to the culture
	 * @param description describes the culture
	 * @param periodDescription is a literary version of the timespan of the culture, like "first half of 2nd century BC"
	 * @param cultureMap is the link to the image that contains the map of the territory of that culture
	 * @param startYear is the starting date of that culture in years as an int, like "-199"
	 * @param endYear is the ending date of the culture in years as an int, like "-150"
	 */
	public Culture(String name, String description, String periodDescription, String cultureMap, int startYear, int endYear) {
		this.name = name;
		this.description = description;
		this.periodDescription = periodDescription;
		this.cultureMap = cultureMap;
		this.startYear = startYear;
		this.endYear = endYear;
	}
}
