package com.conti.elf_reader.gui.utils;

import com.conti.elf_reader.settings.Settings;
import com.utils.log.Logger;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.concurrent.FutureTask;

public class GuiUtils {

    public final static Image imageApp = new Image(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("gui/icon_app.png"));

    public final static Image imageTreeStructure = new Image(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("gui/icon_tree_structure.png"),
            25, 25, true, true);
    public final static Image imageTableStructure = new Image(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("gui/icon_table_structure.png"),
            25, 25, true, true);

    public final static Image imageIndirectCall = new Image(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("gui/icon_call_tree_indirect_call.png"));

    public final static Image imageWarning = new Image(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("gui/icon_call_tree_warning.png"));
    public final static Image imageError = new Image(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("gui/icon_call_tree_error.png"));
    public final static Image imageInfo = new Image(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("gui/icon_call_tree_info.png"));

    public final static Image imageRecursiveCall = new Image(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("gui/icon_call_tree_recursive_call.png"));
    public final static Image imageTask = new Image(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("gui/icon_call_tree_task.png"));
    public final static Image imageSelected = new Image(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("gui/icon_call_tree_selected.png"));

    public static void setAppIcon(Stage stage) {
        stage.getIcons().add(imageApp);
    }

    public static String getPath(TextInputControl txtElfFilePath) {

        String pathString = txtElfFilePath.getText().trim();
        if (pathString.startsWith("\"") && pathString.endsWith("\"")) {
            pathString = pathString.substring(1, pathString.length() - 1);
        }
        if (pathString.startsWith("\\") && !pathString.startsWith("\\\\")) {
            pathString = pathString.substring(1);
        }
        return pathString;
    }

    public static Label createLabel(String text) {

        Label label = new Label(text);
        label.setMinWidth(Region.USE_PREF_SIZE);
        return label;
    }

    public static CheckBox createCheckBox(String text){

        CheckBox checkBox = new CheckBox(text);
        checkBox.setMinWidth(Region.USE_PREF_SIZE);
        return checkBox;
    }

    public static Button createButton(String text){

        Button button = new Button(text);
        button.setMinWidth(Region.USE_PREF_SIZE);
        return button;
    }

    public static FileChooser createFileChooser() {

        FileChooser fileChooser = new FileChooser();
        File defaultRootWindows = new File("D:\\");
        File defaultRootLinux = new File("/");
        if (defaultRootWindows.exists()) {
            fileChooser.setInitialDirectory(defaultRootWindows);
        } else if (defaultRootLinux.exists()) {
            fileChooser.setInitialDirectory(defaultRootLinux);
        }
        return fileChooser;
    }

    public static void openResourceFile(String resourcePath) {

        try {
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(resourcePath);
            File tempFile = new File(Settings.dataFilesDirectoryPath + "\\" + resourcePath);
            FileUtils.copyInputStreamToFile(resourceAsStream, tempFile);
            Desktop.getDesktop().open(tempFile);

        } catch (Exception ignored) {
            Logger.printError("failed to open the resource file: " + resourcePath);
        }
    }

    public static void putStringInClipBoard(String string) {

        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(string);
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    public static void setupCustomTooltipBehavior(
            int openDelayInMillis, int visibleDurationInMillis, int closeDelayInMillis) {

        try {
            Class<?> ttBehaviourClass = null;
            Class<?>[] declaredClasses = Tooltip.class.getDeclaredClasses();
            for (Class<?> c : declaredClasses) {
                if (c.getCanonicalName().equals("javafx.scene.control.Tooltip.TooltipBehavior")) {
                    ttBehaviourClass = c;
                    break;
                }
            }
            if (ttBehaviourClass == null)
                return;

            Constructor<?> constructor = ttBehaviourClass.getDeclaredConstructor(
                    Duration.class, Duration.class, Duration.class, boolean.class);
            if (constructor == null)
                return;

            constructor.setAccessible(true);
            Object newTTBehaviour = constructor.newInstance(
                    new Duration(openDelayInMillis), new Duration(visibleDurationInMillis),
                    new Duration(closeDelayInMillis), false);

            Field ttBehaviourField = Tooltip.class.getDeclaredField("BEHAVIOR");
            if (ttBehaviourField == null)
                return;

            ttBehaviourField.setAccessible(true);

            ttBehaviourField.set(Tooltip.class, newTTBehaviour);

        } catch (Exception e) {
            System.out.println("Aborted setup due to error:" + e.getMessage());
        }
    }

    public static void runAndWait(final Runnable runnable) {

        try {
            final FutureTask<?> futureTask = new FutureTask<>(runnable, null);
            if (!Platform.isFxApplicationThread()) {
                try {
                    Platform.runLater(futureTask);
                    futureTask.get();

                } catch (final Exception exc) {
                    final Throwable cause = exc.getCause();
                    if (cause instanceof Exception) {
                        throw (Exception) cause;
                    } else {
                        throw exc;
                    }
                }

            } else {
                futureTask.run();
            }

        } catch (final Exception exc) {
            Logger.printError("failed to execute task in GUI thread!");
            Logger.printException(exc);
        }
    }
}
