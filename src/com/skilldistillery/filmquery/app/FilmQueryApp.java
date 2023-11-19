package com.skilldistillery.filmquery.app;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {
	private DatabaseAccessor dao;
	private Scanner sc;
	private Deque<Event> events;
	private List<Film> results;

	private FilmQueryApp() {
		sc = new Scanner(System.in);
		events = new ArrayDeque<Event>();
		results = new ArrayList<Film>();
	}

	public static void main(String[] args) {
		FilmQueryApp app = new FilmQueryApp();
		app.launch();
	}

	private void launch() {
		boolean running = false;
		try {
			dao = new DatabaseAccessorObject();
			running = true;
		} catch (Exception e) {
			System.out.println("A fatal error occured.");
		}
		if (running) {
			clearScreen();
			events.push(Event.MAIN_MENU);
			run();
		}
		exitMessage();
	}

	private void run() {
		while (true) {
			switch (events.pop()) {
			case QUIT:
				dao.close();
				return;
			case MAIN_MENU:
				results.clear();
				mainMenuLoop();
				break;
			case SEARCH_ID:
				searchById();
				break;
			case SEARCH_KEYWORD:
				searchByKeywords();
				break;
			case DISPLAY:
				display();
				break;
			}
		}
	}

	private void searchByKeywords() {
		String input = getUserInput("Please enter your query", s -> s, s -> true);
		if (input != null) {
			results.addAll(dao.searchFilmsByKeywords(input, 10));
			events.push(Event.DISPLAY);
		}
	}

	private void display() {
		clearScreen();
		if (!results.isEmpty()) {
			for (Film film : results) {
				printBlue(filmView(film), "");
			}
			printRed("\nwarning, continuing will clear the screen,\nand go back to the main menu.", "\n");
		} else {
			printRed("no results were found!", "\n");
		}
		enterToContinue();
		events.push(Event.MAIN_MENU);
	}

	private void mainMenuLoop() {
		Integer selection = userSelectFrom(Arrays.asList("Search By ID", "Search By Keywords"));
		if (selection != null) {
			switch (selection) {
			case 0:
				events.push(Event.SEARCH_ID);
				break;
			case 1:
				events.push(Event.SEARCH_KEYWORD);
				break;
			}
		}
	}

	private void searchById() {
		Integer id = getUserInt("Please enter an ID to search for");
		if (id != null) {
			Film film = dao.searchFilmById(id);
			if (film != null) {
				results.add(film);
			}
			events.push(Event.DISPLAY);
		}
	}

	private String filmView(Film film) {
		String divider = "\n--------------------------------------------------------------\n";
		StringBuilder view = new StringBuilder();

		String cast = String.join(", ", film.getActors().stream().map(Actor::getFullName).collect(Collectors.toList()));
		Arrays.asList(divider, film.getTitle(), " - ", film.getReleaseYear(), " - ", film.getRating(), " - ",
				film.getLanguage(), divider, wrapText(film.getDescription(), " "), divider, wrapText(cast, ", "), divider)
				.forEach(view::append);

		return view.toString();
	}

	private String wrapText(String text, String on) {
		final int WRAP_LEN = 60;
		StringBuilder wrappedText = new StringBuilder();
		int start = 0;

		while (start < text.length()) {
			if (start + WRAP_LEN < text.length()) {
				int end = text.lastIndexOf(on, start + WRAP_LEN);
				if (end <= start)
					end = start + WRAP_LEN;
				wrappedText.append(text.substring(start, end)).append("\n");
				start = end + 1;
			} else {
				wrappedText.append(text.substring(start));
				break;
			}
		}
		return wrappedText.toString();
	}

	private <T> Integer userSelectFrom(List<T> options) {
		StringBuilder builder = new StringBuilder("Please select an option by number.\n");

		for (int i = 0; i < options.size(); i++) {
			builder.append("" + (i + 1) + " -- " + options.get(i) + "\n");
		}

		Integer userSelection = getUserInput(builder.toString(), Integer::parseInt,
				i -> (1 <= i && i <= options.size()));

		return (userSelection == null) ? null : userSelection - 1;
	}

	public <T> T getUserInput(String message, Function<String, T> parseMethod, Function<T, Boolean> isValidChecker)
			throws IllegalArgumentException {
		while (true) {
			try {
				printGreen(message + "\nType 'quit' to quit.", "\n");

				String input = sc.nextLine();
				if (checkQuit(input)) {
					return null;
				}

				T parsed = parseMethod.apply(input);

				if (isValidChecker.apply(parsed)) {
					System.out.println();
					clearScreen();
					return parsed;
				} else {
					throw new IllegalArgumentException();
				}
			} catch (IllegalArgumentException e) {
				clearScreen();
				printRed("Invalid input", "\n");
			}
			enterToContinue();
		}
	}

	public void clearScreen() {
		for (int i = 0; i < 1000; i++) {
			System.out.println();
		}
	}

	public Integer getUserInt(String message) {
		return getUserInput(message, Integer::parseInt, a -> true);
	}

	private void enterToContinue() {
		printGreen("Please press enter to continue...", "\n");
		sc.nextLine();
		clearScreen();
	}

	private boolean checkQuit(String input) {
		boolean isQuit = input.equalsIgnoreCase("quit");
		if (isQuit) {
			events.push(Event.QUIT);
		}
		return isQuit;
	}

	public void exitMessage() {
		clearScreen();
		printGreen("goodbye!", "\n");
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			;
		} finally {
			clearScreen();
		}

	}

	public void printGreen(String message, String append) {
		print(message, PrintColor.GREEN, append);
	}

	public void printBlue(String message, String append) {
		print(message, PrintColor.BLUE, append);
	}

	public void printRed(String message, String append) {
		print(message, PrintColor.RED, append);
	}

	private void print(String message, PrintColor color, String append) {
		System.out.println(color.format(message) + append);
	}
}

enum PrintColor {
	GREEN("32"), RED("31"), BLUE("36");

	String code;

	PrintColor(String code) {
		this.code = code;
	}

	String format(String str) {
		return "\033[" + code + "m" + str + "\033[0m";
	}
}
