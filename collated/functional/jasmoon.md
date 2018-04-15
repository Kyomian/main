# jasmoon
###### \java\seedu\address\commons\events\ui\PanelSelectionChangedEvent.java
``` java
package seedu.address.commons.events.ui;

import seedu.address.commons.events.BaseEvent;

/**
 * Represents a selection change in the Panel
 */
public class PanelSelectionChangedEvent extends BaseEvent {

    private final Object newSelection;
    private final String activityType;

    public PanelSelectionChangedEvent(Object newSelection, String activityType) {
        this.newSelection = newSelection;
        this.activityType = activityType;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public Object getNewSelection() {
        return newSelection;
    }

    public String getActivityType() {
        return activityType;
    }
}
```
###### \java\seedu\address\commons\events\ui\ShowTaskOnlyRequestEvent.java
``` java
/**
 * An event requesting to view only tasks.
 */
public class ShowTaskOnlyRequestEvent extends BaseEvent {

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }


}
```
###### \java\seedu\address\logic\commands\HelpCommand.java
``` java
    public HelpCommand()    {
    }

    public HelpCommand(String args) {
        commandRequest = args.trim();
    }

    @Override
    public CommandResult execute() throws CommandException {
        if (commandRequest == null)   {
            EventsCenter.getInstance().post(new ShowHelpRequestEvent());
            return new CommandResult(SHOWN_HELP_MESSAGE);
        } else   {
            switch(commandRequest) {

            case TaskCommand.COMMAND_WORD:
                return new CommandResult(TaskCommand.MESSAGE_USAGE);

            case EventCommand.COMMAND_WORD:
                return new CommandResult(EventCommand.MESSAGE_USAGE);

            case CompleteCommand.COMMAND_WORD:
                return new CommandResult(CompleteCommand.MESSAGE_USAGE);

            //case EditCommand.COMMAND_WORD:
                //return new CommandResult(EditCommand.MESSAGE_USAGE);

            case RemoveCommand.COMMAND_WORD:
                return new CommandResult(RemoveCommand.MESSAGE_USAGE);

            case RemoveCommand.COMMAND_ALIAS:
                return new CommandResult(RemoveCommand.MESSAGE_USAGE);

            case HelpCommand.COMMAND_WORD:
                return new CommandResult(HelpCommand.MESSAGE_USAGE);

            case HelpCommand.COMMAND_ALIAS:
                return new CommandResult(HelpCommand.MESSAGE_USAGE);

            case ListCommand.COMMAND_WORD:
                return new CommandResult(ListCommand.MESSAGE_USAGE);

            case ListCommand.COMMAND_ALIAS:
                return new CommandResult(ListCommand.MESSAGE_USAGE);

            case FindCommand.COMMAND_WORD:
                return new CommandResult(FindCommand.MESSAGE_USAGE);

            case OverdueCommand.COMMAND_WORD:
                return new CommandResult(OverdueCommand.MESSAGE_USAGE);

            case ImportCommand.COMMAND_WORD:
                return new CommandResult(ImportCommand.MESSAGE_USAGE);

            case ExportCommand.COMMAND_WORD:
                return new CommandResult(ExportCommand.MESSAGE_USAGE);

            case UndoCommand.COMMAND_WORD:
                return new CommandResult(UndoCommand.MESSAGE_USAGE);

            case UndoCommand.COMMAND_ALIAS:
                return new CommandResult(UndoCommand.MESSAGE_USAGE);

            case RedoCommand.COMMAND_WORD:
                return new CommandResult(RedoCommand.MESSAGE_USAGE);

            case RedoCommand.COMMAND_ALIAS:
                return new CommandResult(RedoCommand.MESSAGE_USAGE);

            case ClearCommand.COMMAND_WORD:
                return new CommandResult(ClearCommand.MESSAGE_USAGE);

            case ClearCommand.COMMAND_ALIAS:
                return new CommandResult(ClearCommand.MESSAGE_USAGE);

            default:
                throw new CommandException(MESSAGE_USAGE);
            }
        }
    }


}
```
###### \java\seedu\address\logic\Logic.java
``` java
    /** Returns an unmodifiable view of the filtered list of tasks */
    ObservableList<Activity> getFilteredTaskList();

    /** Returns an unmodifiable view of the filtered list of events*/
    ObservableList<Activity> getFilteredEventList();

```
###### \java\seedu\address\logic\LogicManager.java
``` java
    @Override
    public ObservableList<Activity> getFilteredTaskList() {
        return model.getFilteredTaskList();
    }

    @Override
    public ObservableList<Activity> getFilteredEventList() {
        return model.getFilteredEventList();
    }

    @Override
    public ListElementPointer getHistorySnapshot() {
        return new ListElementPointer(history.getHistory());
    }
}
```
###### \java\seedu\address\logic\parser\HelpCommandParser.java
``` java
/**
 * Parses input arguments and create a new HelpCommand object.
 */
public class HelpCommandParser implements Parser<HelpCommand> {

    public final ArrayList<String> availableCommands;

    /**
     * HelpCommandParser constructor - creates an ArrayList which contains all commands open to the help function.
     */
    public HelpCommandParser()  {
        availableCommands = new ArrayList<>();
        availableCommands.add(TaskCommand.COMMAND_WORD);
        availableCommands.add(EventCommand.COMMAND_WORD);
        availableCommands.add(RemoveCommand.COMMAND_WORD);
        availableCommands.add(RemoveCommand.COMMAND_ALIAS);
        availableCommands.add(EditCommand.COMMAND_WORD);
        availableCommands.add(FindCommand.COMMAND_WORD);
        availableCommands.add(CompleteCommand.COMMAND_WORD);
        availableCommands.add(HelpCommand.COMMAND_WORD);
        availableCommands.add(HelpCommand.COMMAND_ALIAS);
        availableCommands.add(ListCommand.COMMAND_WORD);
        availableCommands.add(ListCommand.COMMAND_ALIAS);
        availableCommands.add(UndoCommand.COMMAND_WORD);
        availableCommands.add(UndoCommand.COMMAND_ALIAS);
        availableCommands.add(RedoCommand.COMMAND_WORD);
        availableCommands.add(RedoCommand.COMMAND_ALIAS);
        availableCommands.add(ClearCommand.COMMAND_WORD);
        availableCommands.add(ClearCommand.COMMAND_ALIAS);
        availableCommands.add(ImportCommand.COMMAND_WORD);
        availableCommands.add(ExportCommand.COMMAND_WORD);
    }

    /**
     * Parses the given {@code String} of arguments in the context of the HelpCommand
     * and returns an HelpCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */

    public HelpCommand parse(String args) throws ParseException {

        String commandRequest = args.trim();
        if (commandRequest.length() == 0) {
            return new HelpCommand();
        } else {
            if (availableCommands.contains(commandRequest)) {
                return new HelpCommand(args);
            } else {
                throw new ParseException(String.format(Messages.MESSAGE_INVALID_HELP_REQUEST, commandRequest));
            }
        }
    }
}
```
###### \java\seedu\address\logic\parser\ListCommandParser.java
``` java
/**
 * Parses input arguments and create a new ListCommand object.
 */
public class ListCommandParser implements Parser<ListCommand> {

    public final ArrayList<String> availableCommands;

    public ListCommandParser()  {
        availableCommands = new ArrayList<String>();
        availableCommands.add("task");
        availableCommands.add("event");
    }

    /**
     * Parses the given {@code String} of arguments in the context of the ListCommand
     * and returns an ListCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ListCommand parse(String args) throws ParseException {
        String commandRequest = args.trim();

        if (availableCommands.contains(commandRequest) || commandRequest.length() == 0)   {
            return new ListCommand(commandRequest);
        } else  {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }
    }

}
```
###### \java\seedu\address\model\Model.java
``` java
    /** Returns an unmodifiable view of the filtered task list */
    ObservableList<Activity> getFilteredTaskList();

    /** Returns an unmodifiable view of the filtered event list */
    ObservableList<Activity> getFilteredEventList();

    /**
     * Updates the filter of the filtered activity list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredActivityList(Predicate<Activity> predicate);

}
```
###### \java\seedu\address\model\ModelManager.java
``` java
    /**
     * Returns an unmodifiable view of the list of {@code Task} backed by the task list of
     * {@code deskBoard}
     */
    @Override
    public ObservableList<Activity> getFilteredTaskList()   {
        FilteredList<Activity> taskList =  new FilteredList<>(filteredActivities, new TaskOnlyPredicate());
        ObservableList<Activity> result = FXCollections.unmodifiableObservableList(taskList);
        return result;
    }

```
###### \java\seedu\address\ui\CalendarPanel.java
``` java
/**
 * Panel containing the calendar.
 * NOTE: Calendar getter methods in API returns unmodifiableObservableList.
 */
public class CalendarPanel extends UiPart<Region> {

    private static final String FXML = "CalendarPanel.fxml";

    private CalendarView calendarView;

    public CalendarPanel(ObservableList<Activity> activityList) {
        super(FXML);

        calendarView = new CalendarView();
        registerAsAnEventHandler(this);
        configureCalendar();
        syncCalendarWithActivities(activityList);
    }

    /**
     * Configures the contents to display of the calendar.
     */
    private void configureCalendar()   {
        calendarView.setRequestedTime(LocalTime.now());
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowSearchField(false);
        calendarView.setShowSearchResultsTray(false);
        calendarView.setShowPrintButton(false);
        calendarView.showMonthPage();
        calendarView.prefWidth(calendarView.computeAreaInScreen());
        calendarView.prefHeight(calendarView.computeAreaInScreen());
    }

    /**
     * Syncs the calender with UniqueActivityList at start.
     * @param activityList
     */
    private void syncCalendarWithActivities(ObservableList<Activity> activityList)  {
        resetCalendar();
        CalendarSource activityCalendarSource = new CalendarSource("Activity Calendar");
        Calendar taskCalendar = new Calendar("Task Calendar");
        taskCalendar.setLookAheadDuration(Duration.ofDays(365));
        taskCalendar.setStyle(Calendar.Style.getStyle(1));
        activityCalendarSource.getCalendars().add(taskCalendar);

        for (Activity activity: activityList)    {
            if (activity.getActivityType().equals("TASK")) {
                Task task = (Task) activity;
                LocalDateTime dueDateTime = task.getDueDateTime().getLocalDateTime();
                Entry entry = new Entry(task.getName().fullName);
                entry.setInterval(dueDateTime);
                taskCalendar.addEntry(entry);
            }
        }

        Calendar eventCalendar = new Calendar("Event Calendar");
        eventCalendar.setStyle(Calendar.Style.getStyle(4));
        eventCalendar.setLookAheadDuration(Duration.ofDays(365));

        activityCalendarSource.getCalendars().add(eventCalendar);

        for (Activity activity: activityList)    {
            if (activity.getActivityType().equals("EVENT")) {
                Event event = (Event) activity;
                LocalDateTime startDateTime = event.getStartDateTime().getLocalDateTime();
                LocalDateTime endDateTime = event.getEndDateTime().getLocalDateTime();
                Entry entry = new Entry(event.getName().fullName, new Interval(startDateTime, endDateTime));
                entry.setLocation(event.getLocation() == null ? null : event.getLocation().value);
                eventCalendar.addEntry(entry);
            }
        }
        calendarView.getCalendarSources().add(activityCalendarSource);
    }

    @Subscribe
    private void handleDeskBoardChangedEvent(DeskBoardChangedEvent event) {
        syncCalendarWithActivities(event.data.getActivityList());
    }

    private void resetCalendar()    {
        calendarView.setToday(LocalDate.now());
        calendarView.setTime(LocalTime.now());
        calendarView.getCalendarSources().clear();
    }

    public CalendarView getRoot()   {
        return this.calendarView;
    }
}
```
###### \java\seedu\address\ui\EventListPanel.java
``` java
package seedu.address.ui;

import java.util.logging.Logger;

import org.fxmisc.easybind.EasyBind;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.DeselectEventListCellEvent;
import seedu.address.commons.events.ui.JumpToEventListRequestEvent;
import seedu.address.commons.events.ui.JumpToTaskListRequestEvent;
import seedu.address.commons.events.ui.PanelSelectionChangedEvent;
import seedu.address.model.activity.Activity;

/**
 * Panel containing the list of events.
 */
public class EventListPanel extends UiPart<Region> {

    private static final String FXML = "EventListPanel.fxml";
    private static TaskListPanel taskListPanel;
    private static int selectedIndex = -1;
    private final Logger logger = LogsCenter.getLogger(EventListPanel.class);

    @FXML
    private ListView<EventCard> eventListView;

    private Label emptyLabel = new Label("Event List is empty!");

    public EventListPanel(ObservableList<Activity> eventList) {
        super(FXML);
        setConnections(eventList);
        registerAsAnEventHandler(this);
        setUpPlaceHolder();
        //maybe do not need this
        //eventListView.managedProperty().bind(eventListView.visibleProperty());
    }

    private void setConnections(ObservableList<Activity> eventList) {
        ObservableList<EventCard> mappedList = EasyBind.map(
                eventList, (event) -> new EventCard(event, eventList.indexOf(event) + 1));
        eventListView.setItems(mappedList);
        linkCell();
        setEventHandlerForSelectionChangeEvent();
    }

    private void setUpPlaceHolder()   {
        eventListView.setPlaceholder(emptyLabel);
        emptyLabel.setStyle("-fx-font-family: \"Open Sans\"; -fx-font-size: 25px; ");
    }

    /**
     * Links eventListView to eventListViewCell as its custom ListCell
     */
    private void linkCell() {
        eventListView.setCellFactory(listView -> {
            EventListViewCell cell = new EventListViewCell();
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                eventListView.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (eventListView.getSelectionModel().getSelectedIndices().contains(index))  {
                        logger.fine("Selection in event list panel with index '" + index
                                + "' has been deselected");

                        raise(new DeselectEventListCellEvent(eventListView, index));
                    } else {
                        selectedIndex = index;
                        eventListView.getSelectionModel().select(index);
                    }
                    event.consume();
                }
            });
            return cell;
        });
    }

    private void setEventHandlerForSelectionChangeEvent() {
        eventListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        logger.fine("Selection in event list panel changed to : '" + newValue + "'");
                        raise(new PanelSelectionChangedEvent(newValue, "EventCard"));
                    }
                });
    }

    /**
     * Scrolls to the {@code EventCard} at the {@code index} and selects it.
     */
    private void scrollTo(int index) {
        Platform.runLater(() -> {
            eventListView.scrollTo(index);
            eventListView.getSelectionModel().clearAndSelect(index);
        });
    }

```
###### \java\seedu\address\ui\EventView.java
``` java

/**
 * The event view for center stage.
 */
public class EventView extends UiPart<Region> {

    private static final String FXML = "EventView.fxml";

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    private EventListPanel eventListPanel;
    private CalendarPanel calendarPanel;
    private BrowserPanel browserPanel;

    @FXML
    private SplitPane eventView;

    @FXML
    private StackPane eventListPanelPlaceholder;

    @FXML
    private StackPane browserPlaceholder;

    @FXML
    private StackPane calendarPanelPlaceholder;

    public EventView(Logic logic) {
        super(FXML);
        configureDividers();

        eventListPanel = new EventListPanel(logic.getFilteredEventList());
        eventListPanelPlaceholder.getChildren().add(eventListPanel.getRoot());

        browserPanel = new BrowserPanel();
        browserPlaceholder.getChildren().add(browserPanel.getRoot());

        calendarPanel = new CalendarPanel(logic.getFilteredActivitiesList());
        ZoomPane zoomPane = new ZoomPane();
        Parent calendarPane = zoomPane.createZoomPane(new Group(calendarPanel.getRoot()));
        calendarPanelPlaceholder.getChildren().setAll(calendarPane);
    }

    /**
     * Make divider not resizable.
     */
    private void configureDividers()    {

        SplitPane.Divider divider = eventView.getDividers().get(0);
        divider.positionProperty().addListener((ObservableValue<? extends Number>
                                                        observable, Number oldValue, Number newValue) -> {
            divider.setPosition(0.50);
        });

    }

    public SplitPane getRoot()   {
        return this.eventView;
    }

    public EventListPanel getEventListPanel() {
        return eventListPanel;
    }
}
```
###### \java\seedu\address\ui\MainView.java
``` java
/**
 * The default view for center stage.
 */
public class MainView extends UiPart<Region> {

    private static final String FXML = "MainView.fxml";

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    private TaskListPanel taskListPanel;
    private EventListPanel eventListPanel;
    private CalendarPanel calendarPanel;
    private BrowserPanel browserPanel;

    @FXML
    private SplitPane mainView;

    @FXML
    private SplitPane mainViewSplitPane2;

    @FXML
    private StackPane taskListPanelPlaceholder;

    @FXML
    private StackPane eventListPanelPlaceholder;

    @FXML
    private StackPane browserPlaceholder;

    @FXML
    private StackPane calendarPanelPlaceholder;

    public MainView(Logic logic) {
        super(FXML);
        configureDividers();

        taskListPanel = new TaskListPanel(logic.getFilteredTaskList());
        taskListPanelPlaceholder.getChildren().add(taskListPanel.getRoot());

        eventListPanel = new EventListPanel(logic.getFilteredEventList());
        eventListPanelPlaceholder.getChildren().add(eventListPanel.getRoot());

        browserPanel = new BrowserPanel();
        browserPlaceholder.getChildren().add(browserPanel.getRoot());

        calendarPanel = new CalendarPanel(logic.getFilteredActivitiesList());
        ZoomPane zoomPane = new ZoomPane();
        Parent calendarPane = zoomPane.createZoomPane(new Group(calendarPanel.getRoot()));
        calendarPanelPlaceholder.getChildren().setAll(calendarPane);
    }

    /**
     * Make dividers not resizable.
     */
    private void configureDividers()    {

        SplitPane.Divider divider = mainView.getDividers().get(0);
        divider.positionProperty().addListener((ObservableValue<? extends Number>
                                                        observable, Number oldValue, Number newValue) -> {
            divider.setPosition(0.25);
        });

        SplitPane.Divider divider2 = mainViewSplitPane2.getDividers().get(0);
        divider2.positionProperty().addListener((ObservableValue<? extends Number>
                                                         observable, Number oldValue, Number newValue) -> {
            divider2.setPosition(0.34);
        });

    }

    public SplitPane getRoot()   {
        return this.mainView;
    }

    public TaskListPanel getTaskListPanel() {
        return taskListPanel;
    }

    public EventListPanel getEventListPanel() {
        return eventListPanel;
    }
}
```
###### \java\seedu\address\ui\MainWindow.java
``` java
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

```
###### \java\seedu\address\ui\MainWindow.java
``` java
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
```
###### \java\seedu\address\ui\TaskCard.java
``` java
package seedu.address.ui;

import static seedu.address.ui.util.DateTimeUtil.getDisplayedDateTime;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.activity.Activity;
import seedu.address.model.activity.Task;

/**
 * An UI component that displays information of a {@code Task}.
 */
public class TaskCard extends UiPart<Region> {

    private static final String FXML = "TaskListCard.fxml";

    public final Task task;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label dateTime;
    @FXML
    private FlowPane status;

    public TaskCard(Activity task, int displayedIndex) {
        super(FXML);
        this.task = (Task) task;
        id.setText(displayedIndex + ". ");
        name.setText(this.task.getName().fullName);
        dateTime.setText(getDisplayedDateTime(this.task));
        if (task.isCompleted()) {
            status.getChildren().add(new Label("Completed"));
        } else {
            status.getChildren().add(new Label("Uncompleted"));
        }
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TaskCard)) {
            return false;
        }

        // state check
        TaskCard card = (TaskCard) other;
        return id.getText().equals(card.id.getText())
                && task.equals(card.task);
    }

    public Task getTask() {
        return task;
    }

}
```
###### \java\seedu\address\ui\TaskListPanel.java
``` java
package seedu.address.ui;

import java.util.logging.Logger;

import org.fxmisc.easybind.EasyBind;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.DeselectTaskListCellEvent;
import seedu.address.commons.events.ui.JumpToEventListRequestEvent;
import seedu.address.commons.events.ui.JumpToTaskListRequestEvent;
import seedu.address.commons.events.ui.PanelSelectionChangedEvent;
import seedu.address.model.activity.Activity;

/**
 * Panel containing the list of activities.
 */
public class TaskListPanel extends UiPart<Region> {
    private static final String FXML = "TaskListPanel.fxml";
    private static EventListPanel eventListPanel;
    private static int selectedIndex = -1;
    private final Logger logger = LogsCenter.getLogger(TaskListPanel.class);

    @FXML
    private ListView<TaskCard> taskListView;

    private Label emptyLabel = new Label("Task List is empty!");

    public TaskListPanel(ObservableList<Activity> taskList) {
        super(FXML);
        setConnections(taskList);
        registerAsAnEventHandler(this);
        setUpPlaceholder();
    }

    private void setUpPlaceholder() {
        taskListView.setPlaceholder(emptyLabel);
        emptyLabel.setStyle("-fx-font-family: \"Open Sans\"; -fx-font-size: 25px; ");
    }

    private void setConnections(ObservableList<Activity> taskList) {
        ObservableList<TaskCard> mappedList = EasyBind.map(
                taskList, (activity) -> new TaskCard(activity, taskList.indexOf(activity) + 1));
        taskListView.setItems(mappedList);
        linkCell();
        setEventHandlerForSelectionChangeEvent();
    }

    /**
     * Links taskListView to taskListViewCell as its custom ListCell
     * Add deselection for mouse and ESC key.
     */
    private void linkCell() {
        taskListView.setCellFactory(listView -> {
            TaskListViewCell cell = new TaskListViewCell();
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                taskListView.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (taskListView.getSelectionModel().getSelectedIndices().contains(index))  {
                        logger.fine("Selection in task list panel with index '" + index
                                + "' has been deselected");
                        raise(new DeselectTaskListCellEvent(taskListView, index));
                    } else {
                        selectedIndex = index;
                        taskListView.getSelectionModel().select(index);
                    }
                    event.consume();
                }
            });

            return cell;
        });
    }

    public void setEventHandlerForSelectionChangeEvent() {
        taskListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        logger.fine("Selection in task list panel changed to : '" + newValue + "'");
                        raise(new PanelSelectionChangedEvent(newValue, "TaskCard"));
                    }
                });
    }

    /**
     * Scrolls to the {@code TaskCard} at the {@code index} and selects it.
     */
    private void scrollTo(int index) {
        Platform.runLater(() -> {
            taskListView.scrollTo(index);
            taskListView.getSelectionModel().clearAndSelect(index);
        });
    }

```
###### \java\seedu\address\ui\TaskListPanel.java
``` java
    public ListView<TaskCard> getTaskListView()   {
        return taskListView;
    }

    public void setData(EventListPanel eventListPanel) {
        this.eventListPanel = eventListPanel;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

}
```
###### \java\seedu\address\ui\TaskView.java
``` java
/**
 * The task view for center stage
 */
public class TaskView extends UiPart<Region> {

    private static final String FXML = "TaskView.fxml";

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    private TaskListPanel taskListPanel;
    private CalendarPanel calendarPanel;
    private BrowserPanel browserPanel;

    @FXML
    private SplitPane taskView;

    @FXML
    private StackPane taskListPanelPlaceholder;

    @FXML
    private StackPane browserPlaceholder;

    @FXML
    private StackPane calendarPanelPlaceholder;

    public TaskView(Logic logic) {
        super(FXML);
        configureDividers();

        taskListPanel = new TaskListPanel(logic.getFilteredTaskList());
        taskListPanelPlaceholder.getChildren().add(taskListPanel.getRoot());

        browserPanel = new BrowserPanel();
        browserPlaceholder.getChildren().add(browserPanel.getRoot());

        calendarPanel = new CalendarPanel(logic.getFilteredActivitiesList());
        ZoomPane zoomPane = new ZoomPane();
        Parent calendarPane = zoomPane.createZoomPane(new Group(calendarPanel.getRoot()));
        calendarPanelPlaceholder.getChildren().setAll(calendarPane);

    }

    /**
     * Make divider not resizable.
     */
    private void configureDividers()    {

        SplitPane.Divider divider = taskView.getDividers().get(0);
        divider.positionProperty().addListener((ObservableValue<? extends Number>
                                                        observable, Number oldValue, Number newValue) -> {
            divider.setPosition(0.50);
        });

    }

    public SplitPane getRoot()   {
        return this.taskView;
    }

    public TaskListPanel getTaskListPanel() {
        return taskListPanel;
    }
}
```
###### \resources\view\BrowserPanel.fxml
``` fxml

<HBox id="browserPanel" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
</HBox>
```
###### \resources\view\CalendarPanel.fxml
``` fxml
<StackPane id="calendarPanel" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1" prefWidth="350.0" prefHeight="500.0">
</StackPane>
```
###### \resources\view\ClindarStyler.css
``` css
@font-face  {
    font-family: 'Open Sans Bold';
    src: url('../fonts/open-sans/OpenSans-Bold.ttf');
}

@font-face  {
    font-family: 'Open Sans Semibold';
    src: url('../fonts/open-sans/OpenSans-Semibold.ttf');
}

@font-face  {
    font-family: 'Open Sans Italic';
    src: url('../fonts/open-sans/OpenSans-Italic.ttf');
}

@font-face  {
    font-family: 'Open Sans';
    src: url('../fonts/open-sans/OpenSans-Regular.ttf');
}

@font-face  {
    font-family: 'Arvo';
    src: url('../fonts/arvo/Arvo-Regular.ttf');
}

@font-face  {
    font-family: 'Arvo Italic';
    src: url('../fonts/arvo/Arvo-Italic.ttf');
}

@font-face  {
    font-family: 'Arvo Bold';
    src: url('../fonts/arvo/Arvo-Bold.ttf');
}

/* menu bar*/
.menu-bar {
    -fx-background-color: #f7e399;
    -fx-padding: 0 2 0 2;
    -fx-border-width: 0 0 1 0;
    -fx-border-color: #434547;
}

.menu-bar .label {
    -fx-font-family: "Calibri Light";
    -fx-font-size: 12pt;
    -fx-text-fill: black;
}

.context-menu {
    -fx-background-color: #ffd177;
    -fx-border-width: 1 0 0 0;
    -fx-border-color: #434547;
}

/* browser panel */

#browser #name {
    -fx-font-family: "Arvo Bold";
    -fx-font-size: 28;
}

#browser .label {
    -fx-font-family: "Arvo";
    -fx-font-size: 13;
    -fx-padding: 2 4 2 4;
}

#browser #emphasizeText {
    -fx-font-family: "Arvo";
    -fx-font-size: 18;
}

#browser #text {
    -fx-font-family: "Arvo";
    -fx-font-size: 13;
}

/* calendar panel */

#calendarPanelPlaceholder   {
    -fx-border-color: #434547;
    -fx-border-width: 2 1 1 1;
}

#calendarPanel  {
    -fx-border-width: 1 1 1 1;
    -fx-border-color: white;
}

.scroll-pane {
    -fx-focus-color: transparent;
    -fx-faint-focus-color: transparent;
}

.scroll-pane .scroll-bar:horizontal .thumb {
    -fx-background-color: linear-gradient(to bottom, #f4f4f4, #bcbcbc);
    -fx-background-insets: 2;
}

/* taskList and eventList */

.list-view   {
    -fx-background-color: transparent;
    -fx-border-insets: 2 2 2 2;
    -fx-background-insets: 2 2 2 2;
    -fx-focus-color: transparent;
    -fx-faint-focus-color: transparent;
}

.list-cell:empty    {
    -fx-background-color: transparent;
    -fx-border-color: transparent;
}

.list-cell:filled:hover {
    -fx-border-width: 1 1 1 1;
    -fx-border-color: #2dc6c6;
    -fx-border-insets: 0 0 0 1;
    -fx-background-insets: 0 0 0 1;
}

.list-cell:filled:selected {
    -fx-effect: innershadow(gaussian, rgba(98,100,104,0.8), 50, 0, -5.0, 5.0);
}

#taskListView .list-cell:filled:even {
    -fx-background: linear-gradient(to right, #e8e8e8 0%, #e8e8e8 96%, #b2eaff 96%, #b2eaff 100%);
}

#taskListView .list-cell:filled:odd   {
    -fx-background: linear-gradient(to right, #d1d1d1 0%, #d1d1d1 96%, #8ccef2 96%, #8ccef2 100%);
}

#eventListView .list-cell:filled:even {
    -fx-background: linear-gradient(to right, #e8e8e8 0%, #e8e8e8 96%, #ffd6d8 96%, #ffd6d8 100%);
}

#eventListView .list-cell:filled:odd {
    -fx-background: linear-gradient(to right, #d1d1d1 0%, #d1d1d1 96%, #ffb2b5 96%, #ffb2b5 100%);
}

.list-cell .label {
    -fx-text-fill: black;
}

.cell_big_label {
    -fx-font-family: "Open Sans Semibold";
    -fx-font-size: 14pt;
    -fx-text-fill: #010504;
}

.cell_small_label {
    -fx-font-family: "Open Sans";
    -fx-font-size: 8pt;
    -fx-text-fill: #010504;
}

#tags {
    -fx-hgap: 7;
    -fx-vgap: 3;
}

#tags .label {
    -fx-font-family: "Open Sans";
    -fx-font-size: 11;
    -fx-padding: 1 3 1 3;
}

#tags .label, #browser .label {
    -fx-text-fill: white;
    -fx-background-color: #854c99;
    -fx-border-radius: 2;
    -fx-background-radius: 2;
}

.scroll-bar .thumb  {
    -fx-background-color: linear-gradient(to right, #f4f4f4, #bcbcbc);
    -fx-background-insets: 2;
}

.scroll-bar .increment-button, .list-view .scroll-bar .decrement-button {
    -fx-background-color: transparent;
    -fx-padding: 0 0 0 0;
}

.list-view .scroll-bar .increment-arrow, .list-view .scroll-bar .decrement-arrow {
    -fx-shape: "|";
}

.list-view .scroll-bar:vertical .increment-arrow, .list-view .scroll-bar:vertical .decrement-arrow {
    -fx-padding: 1 8 1 8;
}

.list-view .scroll-bar:horizontal .increment-arrow, .list-view .scroll-bar:horizontal .decrement-arrow {
    -fx-padding: 8 1 8 1;
}

/* resultBox */
.text-area  {
    -fx-text-fill: black;
}

#resultBox {
    -fx-background-color: transparent;
    -fx-opacity: 0.45;
    -fx-background-radius: 2px 2px 2px 2px;
}

/* commandBox */

#commandBoxPlaceholder {
    -fx-background-color: white;
    -fx-opacity: 0.45;
    -fx-background-radius: 4px 4px 4px 4px;
}

.text-field {
    -fx-background-color: transparent;
    -fx-border-width: 1 1 1 1;
    -fx-border-color: grey;
    -fx-border-radius: 4px 4px 4px 4px;
    -fx-prompt-text-fill: black;
    -fx-text-fill: black;
}

/* statusBar */

#statusBarPlaceholder   {
    -fx-background-color: #f7e399;
    -fx-font-color: black;
    -fx-border-width: 1 0 0 0;
    -fx-border-color: #dbdad9;

}

.status-bar .label {
    -fx-font-family: "Calibri Light";
    -fx-font-size: 13;
    -fx-padding: 0 0 0 4;
    -fx-text-fill: black;
}

.grid-pane .anchor-pane {
    -fx-background-color: #f7e399;
    -fx-border-width: 1 0 0 0;
    -fx-border-color: #dbdad9;
}

/* miscs */
.background {
    -fx-background-image: url("../images/light-veneer.png");
    -fx-background-repeat: repeat;
}

.split-pane-divider {
   -fx-background-color: transparent;
}
```
###### \resources\view\EventListCard.fxml
``` fxml
<HBox id="cardPane" fx:id="cardPane" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
    <GridPane HBox.hgrow="ALWAYS">
        <columnConstraints>
            <ColumnConstraints hgrow="SOMETIMES" minWidth="10" prefWidth="150" />
        </columnConstraints>
        <VBox alignment="CENTER_LEFT" minHeight="60" GridPane.columnIndex="0">
            <padding>
                <Insets top="1" right="8" bottom="1" left="8" />
            </padding>
            <HBox spacing="5" alignment="CENTER_LEFT">
                <Label fx:id="id" styleClass="cell_big_label">
                    <minWidth>
                        <!-- Ensures that the label text is never truncated -->
                        <Region fx:constant="USE_PREF_SIZE" />
                    </minWidth>
                </Label>
                <Label fx:id="name" text="\$first" styleClass="cell_big_label" />
            </HBox>
            <Label fx:id="dateTime" styleClass="cell_small_label" text="\$dateTime" />
            <Label fx:id="locationEvent" styleClass="cell_small_label" text="\$locationEvent" />
        </VBox>
    </GridPane>
</HBox>
```
###### \resources\view\EventListPanel.fxml
``` fxml
<VBox xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
    <ListView id="eventListView" fx:id="eventListView" VBox.vgrow="ALWAYS" />
</VBox>

```
###### \resources\view\MainView.fxml
``` fxml
<SplitPane fx:id="mainView" xmlns="http://javafx.com/javafx" xmlns:fx="http://javafx.com/fxml"
           dividerPositions="0.25" styleClass="background" VBox.vgrow="ALWAYS">
<items>
    <VBox  VBox.vgrow="ALWAYS">
        <children>
            <StackPane id="taskListPanelPlaceholder" fx:id="taskListPanelPlaceholder"
                       prefHeight="350.0" VBox.vgrow="ALWAYS">
                <VBox.margin>
                    <Insets bottom="8.0" left="30.0" right="15.0" top="10.0"/>
                </VBox.margin>
            </StackPane>
        </children>
    </VBox>

    <SplitPane fx:id="mainViewSplitPane2" dividerPositions="0.34" styleClass="background" prefHeight="350.0"
               VBox.vgrow="ALWAYS">
        <items>

            <VBox styleClass="VBox" VBox.vgrow="ALWAYS">
                <children>
                    <StackPane id="eventListPanelPlaceholder" fx:id="eventListPanelPlaceholder"
                               prefHeight="350.0" VBox.vgrow="ALWAYS">
                        <VBox.margin>
                            <Insets bottom="8.0" left="15.0" right="30.0" top="10.0"/>
                        </VBox.margin>
                    </StackPane>
                </children>
            </VBox>
            <VBox>
                <children>
                    <StackPane fx:id="browserPlaceholder">
                    </StackPane>
                </children>
                <children>
                    <StackPane id="calendarPanelPlaceholder" fx:id="calendarPanelPlaceholder"
                               VBox.vgrow="ALWAYS">
                    </StackPane>
                </children>
            </VBox>
        </items>
    </SplitPane>
</items>
</SplitPane>
```
###### \resources\view\MainWindow.fxml
``` fxml
<fx:root type="javafx.stage.Stage" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1"
         minWidth="600" minHeight="350">
    <scene>
        <Scene>
            <stylesheets>
                <URL value="@ClindarStyler.css"/>
                <URL value="@Extensions.css"/>
            </stylesheets>

            <VBox styleClass="background">
                <children>

                    <MenuBar fx:id="menuBar" prefHeight="21.0" prefWidth="600.0" VBox.vgrow="NEVER">
                        <menus>
                            <Menu mnemonicParsing="false" text="File">
                                <items>
                                    <MenuItem mnemonicParsing="false" onAction="#handleExit" text="Exit"/>
                                </items>
                            </Menu>
                            <Menu mnemonicParsing="false" text="Help">
                                <items>
                                    <MenuItem fx:id="helpMenuItem" mnemonicParsing="false" onAction="#handleHelp"
                                              text="Help"/>
                                </items>
                            </Menu>
                        </menus>
                    </MenuBar>

                    <StackPane fx:id="centerStagePlaceholder" VBox.vgrow="ALWAYS">
                    </StackPane>

                    <StackPane id="resultBox" fx:id="resultDisplayPlaceholder" prefHeight="85.0" prefWidth="600.0">
                        <VBox.margin>
                            <Insets bottom="3.0" left="3.0" right="3.0" top="3.0"/>
                        </VBox.margin>
                    </StackPane>

                    <StackPane id="commandBoxPlaceholder" fx:id="commandBoxPlaceholder" prefWidth="589.0"
                               VBox.vgrow="NEVER">
                        <VBox.margin>
                            <Insets bottom="5.0" left="5.0" right="5.0"/>
                        </VBox.margin>
                    </StackPane>

                    <StackPane id="statusBarPlaceholder" fx:id="statusbarPlaceholder" VBox.vgrow="NEVER"/>
                </children>
            </VBox>
        </Scene>
    </scene>
</fx:root>

```
###### \resources\view\TaskListCard.fxml
``` fxml
<HBox id="cardPane" fx:id="cardPane" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
    <GridPane HBox.hgrow="ALWAYS">
        <columnConstraints>
            <ColumnConstraints hgrow="SOMETIMES" minWidth="10" prefWidth="150" />
        </columnConstraints>
        <VBox alignment="CENTER_LEFT" minHeight="60" GridPane.columnIndex="0">
            <padding>
                <Insets top="1" right="8" bottom="1" left="8" />
            </padding>
            <HBox spacing="5" alignment="CENTER_LEFT">
                <Label fx:id="id" styleClass="cell_big_label">
                    <minWidth>
                        <!-- Ensures that the label text is never truncated -->
                        <Region fx:constant="USE_PREF_SIZE" />
                    </minWidth>
                </Label>
                <Label fx:id="name" text="\$first" styleClass="cell_big_label" />
            </HBox>
            <FlowPane fx:id="status" />
            <Label fx:id="dateTime" styleClass="cell_small_label" text="\$dateTime" />
        </VBox>
    </GridPane>
</HBox>
```
###### \resources\view\TaskListPanel.fxml
``` fxml

<VBox xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
    <ListView id="taskListView" fx:id="taskListView" VBox.vgrow="ALWAYS" />
</VBox>
```
