package com.skilldistillery.filmquery.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Film implements Cloneable {
	private int id;
	private String title;
	private String description;
	private short releaseYear;
	private int rentalDur;
	private double rentalRate;
	private int length;
	private double replacementCost;
	private String rating;
	private String features;
	private List<Actor> actorsInFilm;
	String language;

	public Film() {
	}

	public Film(int id, String title, String description, short releaseYear, int rentalDur, double rentalRate,
			int length, double replacementCost, String rating, String features, String language) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.releaseYear = releaseYear;
		this.rentalDur = rentalDur;
		this.rentalRate = rentalRate;
		this.length = length;
		this.replacementCost = replacementCost;
		this.rating = rating;
		this.features = features;
		this.language = language;
	}

	public Film(int id, String title, String description, short releaseYear, int rentalDur, double rentalRate,
			int length, double replacementCost, String rating, String features, String language,
			List<Actor> actorsInFilm) {
		this(id, title, description, releaseYear, rentalDur, rentalRate, length, replacementCost, rating, features,
				language);
		addActors(actorsInFilm);
		;
	}

	public void addActors(List<Actor> actorsInFilm) {
		this.actorsInFilm = actorsInFilm;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<Actor> getActors() {
		List<Actor> actors = new ArrayList<>();
		actorsInFilm.forEach(a->actors.add(a));
		return actors;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public short getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(short releaseYear) {
		this.releaseYear = releaseYear;
	}

	public int getRentalDur() {
		return rentalDur;
	}

	public void setRentalDur(int rentalDur) {
		this.rentalDur = rentalDur;
	}

	public double getRentalRate() {
		return rentalRate;
	}

	public void setRentalRate(double rentalRate) {
		this.rentalRate = rentalRate;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public double getReplacementCost() {
		return replacementCost;
	}

	public void setReplacementCost(double replacementCost) {
		this.replacementCost = replacementCost;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}

	@Override
	public int hashCode() {
		return Objects.hash(actorsInFilm, description, features, id, language, length, rating, releaseYear, rentalDur,
				rentalRate, replacementCost, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Film other = (Film) obj;
		return Objects.equals(actorsInFilm, other.actorsInFilm) && Objects.equals(description, other.description)
				&& Objects.equals(features, other.features) && id == other.id
				&& Objects.equals(language, other.language) && length == other.length
				&& Objects.equals(rating, other.rating) && releaseYear == other.releaseYear
				&& rentalDur == other.rentalDur
				&& Double.doubleToLongBits(rentalRate) == Double.doubleToLongBits(other.rentalRate)
				&& Double.doubleToLongBits(replacementCost) == Double.doubleToLongBits(other.replacementCost)
				&& Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return "Film [id=" + id + ", title=" + title + ", description=" + description + ", releaseYear=" + releaseYear
				+ ", rentalDur=" + rentalDur + ", rentalRate=" + rentalRate + ", length=" + length
				+ ", replacementCost=" + replacementCost + ", rating=" + rating + ", features=" + features
				+ ", actorsInFilm=" + actorsInFilm + ", language=" + language + "]";
	}

}
