package be.bruxellesformation.mabback.domain;


import javax.persistence.*;
import java.util.List;

/**  Project : Musée Archéologique de Brüsel
 *  File Name : Culture.java
 *  Date : 23-11-20
 *  @author : Yorick Weenen
 */

@Entity
public class Culture {
	@Id
	@GeneratedValue
	private long id;

	private String name;
	private String description;
	private String periodDescription;
	private String cultureMap;
	private int startYear;
	private int endYear;
	@OneToMany
	private List<Artefact> artefacts;

	/*
	 * ----------------
	 * Constructors
	 * ----------------
	 */

	public Culture() {
	}

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

	/*
	 * ----------------
	 * Getters & Setters
	 * ----------------
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPeriodDescription() {
		return periodDescription;
	}

	public void setPeriodDescription(String periodDescription) {
		this.periodDescription = periodDescription;
	}

	public String getCultureMap() {
		return cultureMap;
	}

	public void setCultureMap(String cultureMap) {
		this.cultureMap = cultureMap;
	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public List<Artefact> getArtefacts() {
		return artefacts;
	}

	public void setArtefacts(List<Artefact> artefacts) {
		this.artefacts = artefacts;
	}
}
