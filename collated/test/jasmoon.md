# jasmoon
###### \java\guitests\guihandles\EventListPanelHandle.java
``` java

/**
 * Handle for Event List Panel
 */
public class EventListPanelHandle extends NodeHandle<ListView<EventCard>> {
    public static final String EVENT_LIST_VIEW_ID = "#eventListView";

    private Optional<EventCard> lastRememberedSelectedEventCard;

    public EventListPanelHandle(ListView<EventCard> eventListPanelNode) {
        super(eventListPanelNode);
    }

    /**
     * Returns a handle to the selected {@code EventCardHandle}.
     * A maximum of 1 item can be selected at any time.
     * @throws AssertionError if no card is selected, or more than 1 card is selected.
     */
    public EventCardHandle getHandleToSelectedCard() {
        List<EventCard> eventList = getRootNode().getSelectionModel().getSelectedItems();

        if (eventList.size() != 1) {
            throw new AssertionError("Event list size expected 1.");
        }

        return new EventCardHandle(eventList.get(0).getRoot());
    }

    /**
     * Returns the index of the selected card.
     */
    public int getSelectedCardIndex() {
        return getRootNode().getSelectionModel().getSelectedIndex();
    }

    /**
     * Returns true if a card is currently selected.
     */
    public boolean isAnyCardSelected() {
        List<EventCard> selectedCardsList = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedCardsList.size() > 1) {
            throw new AssertionError("Card list size expected 0 or 1.");
        }

        return !selectedCardsList.isEmpty();
    }

    /**
     * Navigates the listview to display and select the event.
     */
    public void navigateToCard(Activity event) {
        List<EventCard> cards = getRootNode().getItems();
        Optional<EventCard> matchingCard = cards.stream().filter(card -> card.event.equals(event)).findFirst();

        if (!matchingCard.isPresent()) {
            throw new IllegalArgumentException("Event does not exist.");
        }

        guiRobot.interact(() -> {
            getRootNode().scrollTo(matchingCard.get());
            getRootNode().getSelectionModel().select(matchingCard.get());
        });
        guiRobot.pauseForHuman();
    }

    /**
     * Returns the event card handle of a event associated with the {@code index} in the list.
     */
    public EventCardHandle getEventCardHandle(int index) {
        return getEventCardHandle(getRootNode().getItems().get(index).event);
    }

    /**
     * Returns the {@code EventCardHandle} of the specified {@code event} in the list.
     */
    public EventCardHandle getEventCardHandle(Activity event) {
        Optional<EventCardHandle> handle = getRootNode().getItems().stream()
                .filter(card -> card.event.equals(event))
                .map(card -> new EventCardHandle(card.getRoot()))
                .findFirst();
        return handle.orElseThrow(() -> new IllegalArgumentException("Event does not exist."));
    }

    /**
     * Selects the {@code EventCard} at {@code index} in the list.
     */
    public void select(int index) {
        getRootNode().getSelectionModel().select(index);
    }

    /**
     * Remembers the selected {@code EventCard} in the list.
     */
    public void rememberSelectedEventCard() {
        List<EventCard> selectedItems = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedItems.size() == 0) {
            lastRememberedSelectedEventCard = Optional.empty();
        } else {
            lastRememberedSelectedEventCard = Optional.of(selectedItems.get(0));
        }
    }

    /**
     * Returns true if the selected {@code EventCard} is different from the value remembered by the most recent
     * {@code rememberSelectedEventCard()} call.
     */
    public boolean isSelectedEventCardChanged() {
        List<EventCard> selectedItems = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedItems.size() == 0) {
            return lastRememberedSelectedEventCard.isPresent();
        } else {
            return !lastRememberedSelectedEventCard.isPresent()
                    || !lastRememberedSelectedEventCard.get().equals(selectedItems.get(0));
        }
    }

    /**
     * Returns the size of the list.
     */
    public int getListSize() {
        return getRootNode().getItems().size();
    }
}
```
###### \java\guitests\guihandles\TaskCardHandle.java
``` java
/**
 * Handler for task card
 */
public class TaskCardHandle extends NodeHandle<Node> {
    private static final String ID_FIELD_ID = "#id";
    private static final String NAME_FIELD_ID = "#name";
    private static final String DUEDATE_FIELD_ID = "#dueDate";

    private final Label idLabel;
    private final Label nameLabel;
    private final Label dueDateLabel;

    public TaskCardHandle(Node cardNode) {
        super(cardNode);

        this.idLabel = getChildNode(ID_FIELD_ID);
        this.nameLabel = getChildNode(NAME_FIELD_ID);
        this.dueDateLabel = getChildNode(DUEDATE_FIELD_ID);
        /*this.remarkLabel = getChildNode(REMARK_FIELD_ID);

        Region tagsContainer = getChildNode(TAGS_FIELD_ID);
        this.tagLabels = tagsContainer
                .getChildrenUnmodifiable()
                .stream()
                .map(Label.class::cast)
                .collect(Collectors.toList());*/
    }

    public String getId() {
        return idLabel.getText();
    }

    public String getName() {
        return nameLabel.getText();
    }

    public String getDateTime() {
        return dueDateLabel.getText();
    }

    /*public String getRemark() {
        return remarkLabel.getText();
    }

    public List<String> getTags() {
        return tagLabels
                .stream()
                .map(Label::getText)
                .collect(Collectors.toList());
    }*/
}
```
###### \java\guitests\guihandles\TaskListPanelHandle.java
``` java

/**
 * Handle for Task List Panel
 */
public class TaskListPanelHandle extends NodeHandle<ListView<TaskCard>> {
    public static final String TASK_LIST_VIEW_ID = "#taskListView";

    private Optional<TaskCard> lastRememberedSelectedTaskCard;

    public TaskListPanelHandle(ListView<TaskCard> taskListPanelNode) {
        super(taskListPanelNode);
    }

    /**
     * Returns a handle to the selected {@code TaskCardHandle}.
     * A maximum of 1 item can be selected at any time.
     * @throws AssertionError if no card is selected, or more than 1 card is selected.
     */
    public TaskCardHandle getHandleToSelectedCard() {
        List<TaskCard> taskList = getRootNode().getSelectionModel().getSelectedItems();

        if (taskList.size() != 1) {
            throw new AssertionError("Task list size expected 1.");
        }

        return new TaskCardHandle(taskList.get(0).getRoot());
    }

    /**
     * Returns the index of the selected card.
     */
    public int getSelectedCardIndex() {
        return getRootNode().getSelectionModel().getSelectedIndex();
    }

    /**
     * Returns true if a card is currently selected.
     */
    public boolean isAnyCardSelected() {
        List<TaskCard> selectedCardsList = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedCardsList.size() > 1) {
            throw new AssertionError("Card list size expected 0 or 1.");
        }

        return !selectedCardsList.isEmpty();
    }

    /**
     * Navigates the listview to display and select the task.
     */
    public void navigateToCard(Activity task) {
        List<TaskCard> cards = getRootNode().getItems();
        Optional<TaskCard> matchingCard = cards.stream().filter(card -> card.task.equals(task)).findFirst();

        if (!matchingCard.isPresent()) {
            throw new IllegalArgumentException("Task does not exist.");
        }

        guiRobot.interact(() -> {
            getRootNode().scrollTo(matchingCard.get());
            getRootNode().getSelectionModel().select(matchingCard.get());
        });
        guiRobot.pauseForHuman();
    }

    /**
     * Returns the task card handle of a task associated with the {@code index} in the list.
     */
    public TaskCardHandle getTaskCardHandle(int index) {
        return getTaskCardHandle(getRootNode().getItems().get(index).task);
    }

    /**
     * Returns the {@code TaskCardHandle} of the specified {@code task} in the list.
     */
    public TaskCardHandle getTaskCardHandle(Activity task) {
        Optional<TaskCardHandle> handle = getRootNode().getItems().stream()
                .filter(card -> card.task.equals(task))
                .map(card -> new TaskCardHandle(card.getRoot()))
                .findFirst();
        return handle.orElseThrow(() -> new IllegalArgumentException("Task does not exist."));
    }

    /**
     * Selects the {@code TaskCard} at {@code index} in the list.
     */
    public void select(int index) {
        getRootNode().getSelectionModel().select(index);
    }

    /**
     * Remembers the selected {@code TaskCard} in the list.
     */
    public void rememberSelectedTaskCard() {
        List<TaskCard> selectedItems = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedItems.size() == 0) {
            lastRememberedSelectedTaskCard = Optional.empty();
        } else {
            lastRememberedSelectedTaskCard = Optional.of(selectedItems.get(0));
        }
    }

    /**
     * Returns true if the selected {@code TaskCard} is different from the value remembered by the most recent
     * {@code rememberSelectedTaskCard()} call.
     */
    public boolean isSelectedTaskCardChanged() {
        List<TaskCard> selectedItems = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedItems.size() == 0) {
            return lastRememberedSelectedTaskCard.isPresent();
        } else {
            return !lastRememberedSelectedTaskCard.isPresent()
                    || !lastRememberedSelectedTaskCard.get().equals(selectedItems.get(0));
        }
    }

    /**
     * Returns the size of the list.
     */
    public int getListSize() {
        return getRootNode().getItems().size();
    }
}
```
###### \java\seedu\address\logic\commands\CommandTestUtil.java
``` java
    /**
     * Executes the given {@code command}, confirms that <br>
     * - the result message matches {@code expectedMessage} <br>
     */
    public static void assertCommandSuccess(Command command, String expectedMessage)   {
        try {
            CommandResult result = command.execute();
            assertEquals(expectedMessage, result.feedbackToUser);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     */
    public static void assertCommandFailure(Command command, String expectedMessage) {
        try {
            command.execute();
            fail("The expected CommandException was not thrown.");
        } catch (CommandException e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

```
###### \java\seedu\address\logic\commands\EventCommandTest.java
``` java
        @Override
        public ObservableList<Activity> getFilteredTaskList()    {
            fail("This method should not be called.");
            return null;
        }

        @Override
        public ObservableList<Activity> getFilteredEventList() {
            fail("This method should not be called.");
            return null;
        }

        @Override
        public void updateFilteredActivityList(Predicate<Activity> predicate) {
            fail("This method should not be called.");
        }
    }

    /**
     * A Model stub that always throw a DuplicateActivityException when trying to add a activity.
     */
    private class ModelStubThrowingDuplicateActivityException extends ModelStub {
        @Override
        public void addActivity(Activity activity) throws DuplicateActivityException {
            throw new DuplicateActivityException();
        }

        @Override
        public ReadOnlyDeskBoard getDeskBoard() {
            return new DeskBoard();
        }
    }

    /**
     * A Model stub that always accept the activity being added.
     */
    private class ModelStubAcceptingEventAdded extends ModelStub {
        final ArrayList<Activity> eventsAdded = new ArrayList<>();

        @Override
        public void addActivity(Activity activity) throws DuplicateActivityException {
            requireNonNull(activity);
            eventsAdded.add(activity);
        }

        @Override
        public ReadOnlyDeskBoard getDeskBoard() {
            return new DeskBoard();
        }
    }
}
```
###### \java\seedu\address\logic\commands\ListCommandTest.java
``` java
    @Test
    public void execute_listForTask_success() {
        ListCommand command = new ListCommand("task");
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        assertCommandSuccess(command, ListCommand.MESSAGE_SUCCESS_TASK);
        assertTrue(eventsCollectorRule.eventsCollector.getMostRecent() instanceof ShowTaskOnlyRequestEvent);
        assertTrue(eventsCollectorRule.eventsCollector.getSize() == 1);
    }

    @Test
    public void execute_listForEvent_success() {
        ListCommand command = new ListCommand("event");
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        assertCommandSuccess(command, ListCommand.MESSAGE_SUCCESS_EVENT);
        assertTrue(eventsCollectorRule.eventsCollector.getMostRecent() instanceof ShowEventOnlyRequestEvent);
        assertTrue(eventsCollectorRule.eventsCollector.getSize() == 1);
    }

    /**
     * Note: Setup is mainView, calling ListCommand("") will not trigger a ShowActivityOnlyRequest Event.
     */
    @Test
    public void execute_listForActivity_success()    {
        ListCommand command = new ListCommand("");
        assertCommandSuccess(command, ListCommand.MESSAGE_SUCCESS);
        assertTrue(eventsCollectorRule.eventsCollector.getSize() == 0);
    }

    @Test
    public void execute_invalidArgs_throwsCommandException() throws Exception   {
        ListCommand command = new ListCommand("hello");
        assertCommandFailure(command, String.format(ListCommand.MESSAGE_INVALID_LIST_REQUEST, "hello"));
    }
}
```
###### \java\seedu\address\logic\commands\TaskCommandTest.java
``` java
        @Override
        public ObservableList<Activity> getFilteredTaskList()    {
            fail("This method should not be called.");
            return null;
        }

        @Override
        public ObservableList<Activity> getFilteredEventList() {
            fail("This method should not be called.");
            return null;
        }

        @Override
        public void updateFilteredActivityList(Predicate<Activity> predicate) {
            fail("This method should not be called.");
        }
    }

    /**
     * A Model stub that always throw a DuplicateActivityException when trying to add a activity.
     */
    private class ModelStubThrowingDuplicateActivityException extends ModelStub {
        @Override
        public void addActivity(Activity activity) throws DuplicateActivityException {
            throw new DuplicateActivityException();
        }

        @Override
        public ReadOnlyDeskBoard getDeskBoard() {
            return new DeskBoard();
        }
    }

    /**
     * A Model stub that always accept the activity being added.
     */
    private class ModelStubAcceptingTaskAdded extends ModelStub {
        final ArrayList<Activity> tasksAdded = new ArrayList<>();

        @Override
        public void addActivity(Activity activity) throws DuplicateActivityException {
            requireNonNull(activity);
            tasksAdded.add(activity);
        }

        @Override
        public ReadOnlyDeskBoard getDeskBoard() {
            return new DeskBoard();
        }
    }

}
```
###### \java\seedu\address\logic\parser\DeskBoardParserTest.java
``` java
    @Test
    public void parseCommand_event() throws Exception {
        Event event = new EventBuilder().build();
        EventCommand command = (EventCommand) parser.parseCommand(EventUtil.getEventCommand(event));
        assertTrue(command instanceof EventCommand);
    }

```
###### \java\seedu\address\logic\parser\DeskBoardParserTest.java
``` java
    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_ALIAS + " task") instanceof HelpCommand);
        try {
            parser.parseCommand(HelpCommand.COMMAND_WORD + " 3");
            fail("The expected ParseException was not thrown.");
        } catch (ParseException pe) {
            assertEquals(String.format(Messages.MESSAGE_INVALID_HELP_REQUEST, "3"), pe.getMessage());
        }
    }
//
//
//    public void parseCommand_history() throws Exception {
//        assertTrue(parser.parseCommand(HistoryCommand.COMMAND_WORD) instanceof HistoryCommand);
//        assertTrue(parser.parseCommand(HistoryCommand.COMMAND_WORD + " 3") instanceof HistoryCommand);
//
//        try {
//            parser.parseCommand("histories");
//            fail("The expected ParseException was not thrown.");
//        } catch (ParseException pe) {
//            assertEquals(MESSAGE_UNKNOWN_COMMAND, pe.getMessage());
//        }
//    }
//
//
    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_ALIAS + " task") instanceof ListCommand);
    }
//
//
//    public void parseCommand_redoCommandWord_returnsRedoCommand() throws Exception {
//        assertTrue(parser.parseCommand(RedoCommand.COMMAND_WORD) instanceof RedoCommand);
//        assertTrue(parser.parseCommand("redo 1") instanceof RedoCommand);
//    }
//
//
//    public void parseCommand_undoCommandWord_returnsUndoCommand() throws Exception {
//        assertTrue(parser.parseCommand(UndoCommand.COMMAND_WORD) instanceof UndoCommand);
//        assertTrue(parser.parseCommand("undo 3") instanceof UndoCommand);
//    }
//

```
###### \java\seedu\address\logic\parser\DeskBoardParserTest.java
``` java
    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " task") instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " event") instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_ALIAS) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_ALIAS + " task") instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_ALIAS + " event") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        parser.parseCommand(" ");
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        thrown.expectMessage(MESSAGE_UNKNOWN_COMMAND);
        parser.parseCommand("unknownCommand");
    }
}
```
###### \java\seedu\address\logic\parser\HelpCommandParserTest.java
``` java
public class HelpCommandParserTest {

    private HelpCommandParser parser = new HelpCommandParser();

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(Messages.MESSAGE_INVALID_HELP_REQUEST, "a"));
    }
}
```
###### \java\seedu\address\logic\parser\ListCommandParserTest.java
``` java
public class ListCommandParserTest {

    private ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "hello",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }
}
```
###### \java\seedu\address\ui\EventCardTest.java
``` java
public class EventCardTest extends GuiUnitTest {

    /**
     * Test
     */
    public void display() {
        // no tags
        Activity activityWithNoTags = new EventBuilder().withTags(new String[0]).build();
        EventCard eventCard = new EventCard(activityWithNoTags, 1);
        uiPartRule.setUiPart(eventCard);
        assertCardDisplay(eventCard, activityWithNoTags, 1);

        // with tags
        Activity activityWithTags = new EventBuilder().build();
        eventCard = new EventCard(activityWithTags, 2);
        uiPartRule.setUiPart(eventCard);
        assertCardDisplay(eventCard, activityWithTags, 2);
    }

    @Test
    public void equals() {
        Activity activity = new EventBuilder().build();
        EventCard eventCard = new EventCard(activity, 0);

        // same activity, same index -> returns true
        EventCard copy = new EventCard(activity, 0);
        assertTrue(eventCard.equals(copy));

        // same object -> returns true
        assertTrue(eventCard.equals(eventCard));

        // null -> returns false
        assertFalse(eventCard.equals(null));

        // different types -> returns false
        assertFalse(eventCard.equals(0));

        // different activity, same index -> returns false
        Activity differentActivity = new EventBuilder().withName("differentName").build();
        assertFalse(eventCard.equals(new EventCard(differentActivity, 0)));

        // same activity, different index -> returns false
        assertFalse(eventCard.equals(new EventCard(activity, 1)));
    }

    /**
     * Asserts that {@code eventCard} displays the details of {@code expectedActivity} correctly and matches
     * {@code expectedId}.
     */
    private void assertCardDisplay(EventCard eventCard, Activity expectedActivity, int expectedId) {
        guiRobot.pauseForHuman();

        EventCardHandle eventCardHandle = new EventCardHandle(eventCard.getRoot());

        // verify id is displayed correctly
        assertEquals(Integer.toString(expectedId) + ". ", eventCardHandle.getId());

        // verify activity details are displayed correctly
        assertCardDisplaysEvent(expectedActivity, eventCardHandle);
    }

}
```
###### \java\seedu\address\ui\TaskCardTest.java
``` java
public class TaskCardTest extends GuiUnitTest {

    /**
     * Test
     */
    public void display() {
        // no tags
        Activity activityWithNoTags = new TaskBuilder().withTags(new String[0]).build();
        TaskCard taskCard = new TaskCard(activityWithNoTags, 1);
        uiPartRule.setUiPart(taskCard);
        assertCardDisplay(taskCard, activityWithNoTags, 1);

        // with tags
        Activity activityWithTags = new TaskBuilder().build();
        taskCard = new TaskCard(activityWithTags, 2);
        uiPartRule.setUiPart(taskCard);
        assertCardDisplay(taskCard, activityWithTags, 2);
    }

    @Test
    public void equals() {
        Activity activity = new TaskBuilder().build();
        TaskCard taskCard = new TaskCard(activity, 0);

        // same activity, same index -> returns true
        TaskCard copy = new TaskCard(activity, 0);
        assertTrue(taskCard.equals(copy));

        // same object -> returns true
        assertTrue(taskCard.equals(taskCard));

        // null -> returns false
        assertFalse(taskCard.equals(null));

        // different types -> returns false
        assertFalse(taskCard.equals(0));

        // different activity, same index -> returns false
        Activity differentActivity = new TaskBuilder().withName("differentName").build();
        assertFalse(taskCard.equals(new TaskCard(differentActivity, 0)));

        // same activity, different index -> returns false
        assertFalse(taskCard.equals(new TaskCard(activity, 1)));
    }

    /**
     * Asserts that {@code taskCard} displays the details of {@code expectedActivity} correctly and matches
     * {@code expectedId}.
     */
    private void assertCardDisplay(TaskCard taskCard, Activity expectedActivity, int expectedId) {
        guiRobot.pauseForHuman();

        TaskCardHandle taskCardHandle = new TaskCardHandle(taskCard.getRoot());

        // verify id is displayed correctly
        assertEquals(Integer.toString(expectedId) + ". ", taskCardHandle.getId());

        // verify activity details are displayed correctly
        assertCardDisplaysTask(expectedActivity, taskCardHandle);
    }

}
```
###### \java\seedu\address\ui\testutil\GuiTestAssert.java
``` java
/**
 * A set of assertion methods useful for writing GUI tests.
 */
public class GuiTestAssert {
    /**
     * Asserts that {@code actualTaskCard} displays the same values as {@code expectedTaskCard}.
     */
    public static void assertTaskCardEquals(TaskCardHandle expectedTaskCard, TaskCardHandle actualTaskCard) {
        assertEquals(expectedTaskCard.getId(), actualTaskCard.getId());
        assertEquals(expectedTaskCard.getName(), actualTaskCard.getName());
        assertEquals(expectedTaskCard.getDateTime(), actualTaskCard.getDateTime());
    }

    /**
     * Asserts that {@code actualEventCard} displays the same values as {@code expectedEventCard}.
     */
    public static void assertCardEqualsEvent(EventCardHandle expectedEventCard, EventCardHandle actualEventCard) {
        assertEquals(expectedEventCard.getId(), actualEventCard.getId());
        assertEquals(expectedEventCard.getName(), actualEventCard.getName());
        assertEquals(expectedEventCard.getStartDateTime(), actualEventCard.getStartDateTime());
        assertEquals(expectedEventCard.getEndDateTime(), actualEventCard.getEndDateTime());
        assertEquals(expectedEventCard.getLocation(), actualEventCard.getLocation());
        assertEquals(expectedEventCard.getRemark(), actualEventCard.getRemark());
        assertEquals(expectedEventCard.getTags(), actualEventCard.getTags());
    }

    //TODO: status tag
    /**
     * Asserts that {@code actualTaskCard} displays the details of {@code expectedActivity}.
     */
    public static void assertCardDisplaysTask(Activity expectedTask, TaskCardHandle actualTaskCard) {
        assertEquals(expectedTask.getName().fullName, actualTaskCard.getName());
        assertEquals(getDisplayedDateTime((Task) expectedTask), actualTaskCard.getDateTime().toString());
    }

    /**
     * Asserts that {@code actualEventCard} displays the details of {@code expectedEvent}.
     */
    public static void assertCardDisplaysEvent(Activity expectedActivity, EventCardHandle actualEventCard) {
        Event expectedEvent = (Event) expectedActivity;
        assertEquals(expectedEvent.getName().fullName, actualEventCard.getName());
        assertEquals(expectedEvent.getStartDateTime().toString(), actualEventCard.getStartDateTime());
        assertEquals(expectedEvent.getEndDateTime().toString(), actualEventCard.getEndDateTime());
        assertEquals(expectedEvent.getLocation(), actualEventCard.getLocation());
        assertEquals(expectedEvent.getRemark().value, actualEventCard.getRemark());
        assertEquals(expectedEvent.getTags().stream().map(tag -> tag.tagName).collect(Collectors.toList()),
                actualEventCard.getTags());
    }

    /**
     * Asserts that the list in {@code taskListPanelHandle} displays the details of {@code tasks} correctly and
     * in the correct order.
     */
    public static void assertTaskListMatching(TaskListPanelHandle taskListPanelHandle, Activity... tasks) {
        for (int i = 0; i < tasks.length; i++) {
            assertCardDisplaysTask(tasks[i], taskListPanelHandle.getTaskCardHandle(i));
        }
    }

    /**
     * Asserts that the list in {@code taskListPanelHandle} displays the details of {@code activities} correctly and
     * in the correct order.
     */
    public static void assertTaskListMatching(TaskListPanelHandle taskListPanelHandle, List<Activity> activities) {
        assertTaskListMatching(taskListPanelHandle, activities.toArray(new Activity[0]));
    }

    /**
     * Asserts that the list in {@code eventListPanelHandle} displays the details of {@code events} correctly and
     * in the correct order.
     */
    public static void assertEventListMatching(EventListPanelHandle eventListPanelHandle, Activity... activities) {
        for (int i = 0; i < activities.length; i++) {
            Event event = (Event) activities[i];
            assertCardDisplaysEvent(event, eventListPanelHandle.getEventCardHandle(i));
        }
    }

    /**
     * Asserts that the list in {@code eventListPanelHandle} displays the details of {@code activities} correctly and
     * in the correct order.
     */
    public static void assertEventListMatching(EventListPanelHandle eventListPanelHandle, List<Activity> activities) {
        assertEventListMatching(eventListPanelHandle, activities.toArray(new Activity[0]));
    }

    /**
     * Asserts the size of the list in {@code taskListPanelHandle} equals to {@code size}.
     */
    public static void assertTaskListSize(TaskListPanelHandle taskListPanelHandle, int size) {
        int numberOfPeople = taskListPanelHandle.getListSize();
        assertEquals(size, numberOfPeople);
    }

    /**
     * Asserts the size of the list in {@code eventListPanelHandle} equals to {@code size}.
     */
    public static void assertEventListSize(EventListPanelHandle eventListPanelHandle, int size) {
        int numberOfPeople = eventListPanelHandle.getListSize();
        assertEquals(size, numberOfPeople);
    }

    /**
     * Asserts the message shown in {@code resultDisplayHandle} equals to {@code expected}.
     */
    public static void assertResultMessage(ResultDisplayHandle resultDisplayHandle, String expected) {
        assertEquals(expected, resultDisplayHandle.getText());
    }
}
```
