package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class DatabaseAccessorObject implements DatabaseAccessor, AutoCloseable {
	private static final String URL = "jdbc:mysql://localhost:3306/sdvid?useSSL=false&useLegacyDatetimeCode=false&serverTimezone=US/Mountain";
	private static final String AUTH_USER = "student";
	private static final String AUTH_PASS = "student";
	private final PreparedStatement FIND_ACTORS_BY_FILM_ID;
	private final PreparedStatement FIND_FILM_BY_ID;
	private final PreparedStatement FIND_FILM_BY_SEARCH;
	private Connection conn;

	public DatabaseAccessorObject() throws ClassNotFoundException, SQLException {
		conn = DriverManager.getConnection(URL, AUTH_USER, AUTH_PASS);
		Class.forName("com.mysql.cj.jdbc.Driver");

		FIND_ACTORS_BY_FILM_ID = conn.prepareStatement(
				"SELECT actor.* FROM actor JOIN film_actor ON actor.id = film_actor.actor_id WHERE film_actor.film_id = ?");

		FIND_FILM_BY_ID = conn.prepareStatement(
				"SELECT f.*, l.name language FROM film f JOIN language l ON f.language_id = l.id WHERE f.id = ?");
		FIND_FILM_BY_SEARCH = conn.prepareStatement(
				"SELECT f.*, l.name language FROM film f JOIN language l ON f.language_id = l.id WHERE f.title LIKE ? OR f.description LIKE ?");

	}

	@Override
	public List<Film> searchFilmsByKeywords(String keyword, int limit) {
		List<Film> films = new ArrayList<>();
		keyword = "%" + keyword + "%";
		try {
			FIND_FILM_BY_SEARCH.setString(1, keyword);
			FIND_FILM_BY_SEARCH.setString(2, keyword);
			FIND_FILM_BY_SEARCH.setMaxRows(limit);
			ResultSet results = FIND_FILM_BY_SEARCH.executeQuery();
			while (results.next()) {
				limit--;
				Film film = mapFilm(results);
				films.add(film);
			}
		} catch (SQLException e) {
		}

		return films;
	}

	@Override
	public Film searchFilmById(int filmId) {
		Film film = null;
		try {
			FIND_FILM_BY_ID.setInt(1, filmId);
			ResultSet results = FIND_FILM_BY_ID.executeQuery();
			if (results.next()) {
				film = mapFilm(results);
			}

		} catch (SQLException e) {
		}
		return film;
	}

	@Override
	public List<Actor> searchActorsByFilmId(int filmId) {
		List<Actor> actorsInFilm = new ArrayList<>();
		try {
			FIND_ACTORS_BY_FILM_ID.setInt(1, filmId);
			ResultSet results = FIND_ACTORS_BY_FILM_ID.executeQuery();
			while (results.next()) {
				Actor actor = mapActor(results);
				actorsInFilm.add(actor);
			}
			results.close();
		} catch (SQLException e) {
		}
		return actorsInFilm;
	}

	@Override
	public void close() {
		List<AutoCloseable> closeableMembers = Arrays.asList(FIND_ACTORS_BY_FILM_ID, FIND_FILM_BY_SEARCH,
				FIND_FILM_BY_ID, conn);
		for (AutoCloseable member : closeableMembers) {
			try {
				if (member != null) {
					member.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public Film mapFilm(ResultSet results) throws SQLException { // need to get language.
		int filmId = results.getInt("id");
		String title = results.getString("title");
		String desc = results.getString("description");
		short releaseYear = results.getShort("release_year");
		int rentDur = results.getInt("rental_duration");
		double rate = results.getDouble("rental_rate");
		int length = results.getInt("length");
		double repCost = results.getDouble("replacement_cost");
		String rating = results.getString("rating");
		String features = results.getString("special_features");
		String language = results.getString("language");
		Film film = new Film(filmId, title, desc, releaseYear, rentDur, rate, length, repCost, rating, features,
				language);

		List<Actor> actorsInFilm = searchActorsByFilmId(film.getId());
		film.addActors(actorsInFilm);
		return film;
	}

	public Actor mapActor(ResultSet results) throws SQLException {
		int id = results.getInt("id");
		String firstName = results.getString("first_name");
		String lastName = results.getString("first_name");
		Actor actor = new Actor(id, firstName, lastName);
		return actor;
	}

}
