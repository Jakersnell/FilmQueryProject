package com.skilldistillery.filmquery.database;

import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public interface DatabaseAccessor {
	List<Actor> searchActorsByFilmId(int id);
	Film searchFilmById(int id);
	List<Film> searchFilmsByKeywords(String keywords, int limit);
	void close();
}
