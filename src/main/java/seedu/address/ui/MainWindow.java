package seedu.address.ui;

import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.commons.core.Config;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.DeselectEventListCellEvent;
import seedu.address.commons.events.ui.DeselectTaskListCellEvent;
import seedu.address.commons.events.ui.ExitAppRequestEvent;
import seedu.address.commons.events.ui.ShowActivityRequestEvent;
import seedu.address.commons.events.ui.ShowEventOnlyRequestEvent;
import seedu.address.commons.events.ui.ShowHelpRequestEvent;
import seedu.address.commons.events.ui.ShowTaskOnlyRequestEvent;
import seedu.address.logic.Logic;
import seedu.address.model.UserPrefs;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private static String view = "mainView";

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private Config config;
    private MainView mainView;
    private TaskView taskView;
    private EventView eventView;
    private UserPrefs prefs;

    @FXML
    private StackPane centerStagePlaceholder;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane taskListPanelPlaceholder;

    @FXML
    private StackPane eventListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    public MainWindow(Stage primaryStage, Config config, UserPrefs prefs, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.config = config;
        this.prefs = prefs;

        // Configure the UI
        setTitle(config.getAppTitle());
        setWindowDefaultSize(prefs);

        setAccelerators();
        registerAsAnEventHandler(this);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });

        //@@author jasmoon
        getRoot().addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ESCAPE)  {
                int indexTask = mainView.getTaskListPanel().getSelectedIndex();
                int indexEvent = mainView.getEventListPanel().getSelectedIndex();
                if (indexTask != -1) {
                    if (view.equals("mainView")) {
                        raise(new DeselectTaskListCellEvent(mainView.getTaskListPanel().getTaskListView(), indexTask));
                    } else if (view.equals("taskView")) {
                        raise(new DeselectTaskListCellEvent(taskView.getTaskListPanel().getTaskListView(), indexTask));
                    }
                } else if (indexEvent != -1) {
                    logger.fine("Selection in event list panel with index '" + indexEvent
                            + "' has been deselected");
                    if (view.equals("mainView")) {
                        raise(new DeselectEventListCellEvent(mainView.getEventListPanel()
                                .getEventListView(), indexEvent));
                    } else if (view.equals("eventView")) {
                        raise(new DeselectEventListCellEvent(eventView.getEventListPanel()
                                .getEventListView(), indexEvent));
                    }
                }
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {

        mainView = new MainView(logic);
        centerStagePlaceholder.getChildren().add(mainView.getRoot());

        //@@author
        ResultDisplay resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(prefs.getDeskBoardFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(logic);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    void hide() {
        primaryStage.hide();
    }

    private void setTitle(String appTitle) {
        primaryStage.setTitle(appTitle);
    }

    /**
     * Sets the default size based on user preferences.
     */
    private void setWindowDefaultSize(UserPrefs prefs) {
        primaryStage.setHeight(prefs.getGuiSettings().getWindowHeight());
        primaryStage.setWidth(prefs.getGuiSettings().getWindowWidth());
        if (prefs.getGuiSettings().getWindowCoordinates() != null) {
            primaryStage.setX(prefs.getGuiSettings().getWindowCoordinates().getX());
            primaryStage.setY(prefs.getGuiSettings().getWindowCoordinates().getY());
        }
    }

    /**
     * Returns the current size and the position of the main Window.
     */
    GuiSettings getCurrentGuiSetting() {
        return new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
    }

    /**
     * Opens the help window.
     */
    @FXML
    public void handleHelp() {
        HelpWindow helpWindow = new HelpWindow();
        helpWindow.show();
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        raise(new ExitAppRequestEvent());
    }

    @Subscribe
    private void handleShowHelpEvent(ShowHelpRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        handleHelp();
    }

    //@@author jasmoon
    @Subscribe
    private void handleShowActivityRequestEvent(ShowActivityRequestEvent event)    {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        centerStagePlaceholder.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        view = "mainView";
        mainView = new MainView(logic);
        centerStagePlaceholder.getChildren().add(mainView.getRoot());
    }

    @Subscribe
    private void handleShowEventOnlyRequestEvent(ShowEventOnlyRequestEvent event)   {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        centerStagePlaceholder.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EventView.fxml"));
        view = "eventView";
        eventView = new EventView(logic);
        centerStagePlaceholder.getChildren().add(eventView.getRoot());
    }

    @Subscribe
    private void handleShowTaskOnlyRequestEvent(ShowTaskOnlyRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        centerStagePlaceholder.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskView.fxml"));
        view = "taskView";
        taskView = new TaskView(logic);
        centerStagePlaceholder.getChildren().add(taskView.getRoot());
    }
}
