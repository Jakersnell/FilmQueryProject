package com.skilldistillery.filmquery.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

class DatabaseAccessTests {
	private DatabaseAccessor db;

	@BeforeEach
	void setUp() throws Exception {
		db = new DatabaseAccessorObject();
	}

	@AfterEach
	void tearDown() throws Exception {
		db = null;
	}

	@Test
	void test_getFilmById_returns_film_with_id() {
		Film f = db.searchFilmById(1);
		assertNotNull(f);
		assertEquals("ACADEMY DINOSAUR", f.getTitle());
	}

	@Test
	void test_getFilmById_with_invalid_id_returns_null() {
		Film f = db.searchFilmById(-42);
		assertNull(f);
	}
	
	@Test
	void test_findActorsByFilmId_returns_valid_list_of_actors() {
		List<Actor> actors = db.searchActorsByFilmId(1);
		List<Integer> actorIds = Arrays.asList(1, 10, 20, 30, 40, 53, 108, 162, 188, 198);
		
		assertEquals(actors.size(), actorIds.size());
		List<Integer> actorsToIds = actors.stream().map(Actor::getId).collect(Collectors.toList());
		assertTrue(actorIds.containsAll(actorsToIds));
	}

}
