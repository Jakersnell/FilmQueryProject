package com.skilldistillery.filmquery.app;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Film;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FilmQueryJavaFX extends Application {
	private static final int DISPLAY_LIMIT = 10;
	private RadioButton searchByIdButton, searchByKeywordButton;
	private Button searchActionButton;
	private TextField searchField;
	private TextArea resultArea;
	private ScrollPane scrollPane;
	private DatabaseAccessor dao;
	private Function<String, List<Film>> searchMethod;
	private Task<List<Film>> searchProcess;
	private Thread process;

	class GetFilmsTask extends Task<List<Film>> {
		@Override
		protected List<Film> call() throws Exception {
			return searchMethod.apply(searchField.getText());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			dao = new DatabaseAccessorObject();
			setupUI(primaryStage);
		} catch (Exception e) {
			abort("Initialization failed: " + e.getMessage());
		}
	}

	private void setupUI(Stage stage) {
		searchByIdButton = new RadioButton("Search by ID");
		searchByKeywordButton = new RadioButton("Search by Name");
		searchByKeywordButton.setSelected(true);
		searchActionButton = new Button("Search");
		searchField = new TextField();
		resultArea = new TextArea();
		resultArea.setEditable(false);
		scrollPane = new ScrollPane(resultArea);
		scrollPane.setFitToWidth(true);
		searchMethod = this::searchFilmsByKeywords;

		ToggleGroup searchToggleGroup = new ToggleGroup();
		searchByIdButton.setToggleGroup(searchToggleGroup);
		searchByKeywordButton.setToggleGroup(searchToggleGroup);

		searchByIdButton.setOnAction(this::setSearchByIdAsMethod);
		searchByKeywordButton.setOnAction(this::setSearchByKeywordAsMethod);
		searchActionButton.setOnAction(this::startSearchAction);

		HBox buttonBox = new HBox(10, searchByIdButton, searchByKeywordButton, searchActionButton);
		buttonBox.setAlignment(Pos.CENTER);

		VBox layout = new VBox(10, buttonBox, searchField, scrollPane);
		layout.setPrefSize(600, 450);

		Scene scene = new Scene(layout);
		scene.setOnKeyPressed(this::keyPressHandler);

		stage.setTitle("FilmQueryApp");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
	}

	private void setSearchByIdAsMethod(ActionEvent event) {
		searchMethod = this::searchFilmsById;
	}

	private void setSearchByKeywordAsMethod(ActionEvent event) {
		searchMethod = this::searchFilmsByKeywords;
	}

	private void keyPressHandler(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER)
			startSearchAction(null);
	}

	private void startSearchAction(ActionEvent event) {
		String searchTerms = searchField.getText().trim();
		scrollPane.setContent(null);

		if (searchTerms.isEmpty()) {
			resultArea.setText("Please enter a query.");
			return;
		}

		if (searchProcess != null) {
			searchProcess.cancel();
		}

		searchProcess = new GetFilmsTask();

		searchProcess.setOnSucceeded(this::searchSuccessHandler);

		searchProcess.setOnFailed(this::searchFailedHandler);

		process = new Thread(searchProcess);
		process.start();
	}

	private void searchFailedHandler(WorkerStateEvent event) {
		if (searchProcess != null) {
			Throwable e = searchProcess.getException();
			if (e instanceof IllegalArgumentException) {
				resultArea.setText("Invalid Input.");
			} else {
				resultArea.setText("An error occurred: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void searchSuccessHandler(WorkerStateEvent event) {
		if (searchProcess != null) {
			List<Film> films = searchProcess.getValue();
			displayResults(films);
		}
	}

	private List<Film> searchFilmsById(String id) {
		try {
			Integer intId = Integer.valueOf(id);
			return Arrays.asList(dao.searchFilmById(intId));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private List<Film> searchFilmsByKeywords(String keywords) {
		return dao.searchFilmsByKeywords(keywords, 10);
	}

	private void displayResults(List<Film> films) {
		if (films == null || films.isEmpty()) {
			scrollPane.setContent(new Text("Could not find any results."));
			return;
		}
		VBox container = new VBox(0);
		for (int i = 0; i < DISPLAY_LIMIT && i < films.size(); i++) {
			VBox filmBox = formatFilmBrief(films.get(i));
			filmBox.setMinHeight(100);
			container.getChildren().add(filmBox);
			container.getChildren().add(new Separator());
			filmBox.getStyleClass().add("hover-effect");
			filmBox.setStyle("-fx-background-color: #F0F0F0;");
			filmBox.setOnMouseEntered(e -> filmBox.setStyle("-fx-background-color: #FFFFFF;"));
			filmBox.setOnMouseExited(e -> filmBox.setStyle("-fx-background-color: #F0F0F0;"));

		}
		scrollPane.setContent(container);

	}

	private VBox formatFilmBrief(Film film) {
		Label title = new Label(" " + film.getTitle() + " - " + String.valueOf(film.getReleaseYear()));
		Label description = new Label(" " + film.getDescription());
		Label misc = new Label(" " + film.getRating() + " - " + film.getLanguage() + " - " + String.valueOf(film.getLength()) + " minutes");
	

		title.setWrapText(true);
		description.setWrapText(true);

		VBox filmBox = new VBox(15, title, description, misc);
		return filmBox;

	}

	private void abort(String message) {
		System.err.println(message);
		Platform.exit();
	}

	@Override
	public void stop() {
		if (dao != null)
			dao.close();
	}
}
