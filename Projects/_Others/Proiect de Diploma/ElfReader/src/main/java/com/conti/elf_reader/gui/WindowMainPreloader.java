package com.conti.elf_reader.gui;

import org.tbee.javafx.scene.layout.MigPane;

import com.conti.elf_reader.gui.utils.GuiUtils;
import com.conti.elf_reader.gui.utils.ProgressIndicatorBar;

import javafx.application.Preloader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WindowMainPreloader extends Preloader {

	private static Stage primaryStage;

	@Override
	public void start(final Stage primaryStage) {

		WindowMainPreloader.primaryStage = primaryStage;

		GuiUtils.setAppIcon(primaryStage);
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setWidth(320);
		primaryStage.setHeight(320);

		final MigPane primaryPane = new MigPane();

		final Text text1 = new Text(".elf" + System.lineSeparator());
		text1.setStyle("-fx-font-family: \"Segoe UI\"; " +
				"-fx-font-weight: bold; -fx-font-style: italic; " +
				"-fx-font-size: 85pt; -fx-fill: #e8931d;");
		final Text text2 = new Text("reader");
		text2.setStyle("-fx-font-family: \"Segoe UI\"; " +
				"-fx-font-weight: bold; -fx-font-style: italic; " +
				"-fx-font-size: 54pt; -fx-fill: #e8931d;");
		final TextFlow textFlow = new TextFlow(text1, text2);
		textFlow.setTextAlignment(TextAlignment.CENTER);
		textFlow.setStyle("-fx-background-color: linear-gradient(#61a2b1, #2A5058); " +
				"-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1)");
		primaryPane.add(textFlow, "width 100%, height 100%, wrap");

		final ProgressIndicatorBar progressIndicatorBar = new ProgressIndicatorBar();
		primaryPane.add(progressIndicatorBar, "width 100%");

		final Scene scene = new Scene(primaryPane);
		primaryStage.setScene(scene);

		primaryStage.setOnShown(event -> centerOnScreen(primaryStage));

		primaryStage.show();
	}

	private void centerOnScreen(final Stage primaryStage) {

		final Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
		primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
		primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
	}

	static void hide() {
		GuiUtils.runAndWait(() -> primaryStage.hide());
	}
}
