# Kyomian
###### \java\seedu\address\logic\commands\ClearCommandTest.java
``` java
public class ClearCommandTest {

    @Test
    public void clearAll_emptyDeskBoard_success() {
        Model model = new ModelManager();
        assertCommandSuccess(prepareClearAllCommand(model), model, ClearCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void clearAllTasks_emptyDeskBoard_success() {
        Model model = new ModelManager();
        assertCommandSuccess(prepareClearTasksCommand(model), model, ClearCommand.MESSAGE_CLEAR_TASK_SUCCESS, model);
    }

    @Test
    public void clearAll_nonEmptyDeskBoard_success() {
        Model model = new ModelManager(getTypicalDeskBoard(), new UserPrefs());
        assertCommandSuccess(prepareClearAllCommand(model), model, ClearCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void clearAllTasks_nonEmptyDeskBoard_success() {
        Model model = new ModelManager(getTypicalDeskBoard(), new UserPrefs());
        assertCommandSuccess(prepareClearTasksCommand(model), model, ClearCommand.MESSAGE_CLEAR_TASK_SUCCESS, model);
    }

    @Test
    public void clearAllEvents_nonEmptyDeskBoard_success() {
        Model model = new ModelManager(getTypicalDeskBoard(), new UserPrefs());
        assertCommandSuccess(prepareClearEventsCommand(model), model, ClearCommand.MESSAGE_CLEAR_EVENT_SUCCESS, model);
    }

    /**
     * Generates a new {@code ClearCommand} which upon execution,
     * clears all the content in {@code model}.
     */
    private ClearCommand prepareClearAllCommand(Model model) {
        ClearCommand command = new ClearCommand("");
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }

    /**
     * Generates a new {@code ClearCommand} to clear all tasks in {@code model}.
     */
    private ClearCommand prepareClearTasksCommand(Model model) {
        ClearCommand command = new ClearCommand("task");
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }

    /**
     * Generates a new {@code ClearCommand} to clear all events in {@code model}.
     */
    private ClearCommand prepareClearEventsCommand(Model model) {
        ClearCommand command = new ClearCommand("event");
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }
}
```
###### \java\seedu\address\logic\commands\CommandTestUtil.java
``` java
    /**
     * Removes the first activity in {@code model}'s filtered list from {@code model}'s desk board.
     */
    public static void removeFirstActivity(Model model) {
        Activity firstActivity = model.getFilteredActivityList().get(0);
        try {
            model.deleteActivity(firstActivity);
        } catch (ActivityNotFoundException pnfe) {
            throw new AssertionError("Activity in filtered list must exist in model.", pnfe);
        }
    }

```
###### \java\seedu\address\logic\commands\EventCommandIntegrationTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) for {@code EventCommand}.
 */
public class EventCommandIntegrationTest {

    private Model model;

    @Before
    public void setUp() {
        model = new ModelManager(getTypicalDeskBoard(), new UserPrefs());
    }

    @Test
    public void execute_newEvent_success() throws Exception {
        Event validEvent = new EventBuilder().build();

        Model expectedModel = new ModelManager(model.getDeskBoard(), new UserPrefs());
        expectedModel.addActivity(validEvent);

        assertCommandSuccess(prepareCommand(validEvent, model), model,
                String.format(EventCommand.MESSAGE_SUCCESS, validEvent), expectedModel);
    }

    @Test
    // Questionable - does the app check for duplicate?
    public void execute_duplicateEvent_throwsCommandException() {
        Activity activityInList = model.getDeskBoard().getActivityList().get(0);
        assertCommandFailure(prepareCommand((Event) activityInList, model), model,
                EventCommand.MESSAGE_DUPLICATE_EVENT);
    }

    /**
     * Generates a new {@code EventCommand} which upon execution, adds {@code event} into the {@code model}.
     */
    private EventCommand prepareCommand(Event event, Model model) {
        EventCommand command = new EventCommand(event);
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }
}
```
###### \java\seedu\address\logic\commands\EventCommandTest.java
``` java
public class EventCommandTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void constructor_nullEvent_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new EventCommand(null);
    }

    @Test
    public void execute_eventAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingEventAdded modelStub = new ModelStubAcceptingEventAdded();
        Event validEvent = new EventBuilder().build();

        CommandResult commandResult = getEventCommandForGivenEvent(validEvent, modelStub).execute();

        assertEquals(String.format(EventCommand.MESSAGE_SUCCESS, validEvent), commandResult.feedbackToUser);
        assertEquals(Arrays.asList(validEvent), modelStub.eventsAdded);
    }

    @Test
    public void execute_duplicateEvent_throwsCommandException() throws Exception {
        ModelStub modelStub = new ModelStubThrowingDuplicateActivityException();
        Event validEvent = new EventBuilder().build();

        thrown.expect(CommandException.class);
        thrown.expectMessage(EventCommand.MESSAGE_DUPLICATE_EVENT);

        getEventCommandForGivenEvent(validEvent, modelStub).execute();
    }

    @Test
    public void equals() {
        Event cip = new EventBuilder().build();
        Event cca = new EventBuilder().withName("CCA").build();
        EventCommand addCipCommand = new EventCommand(cip);
        EventCommand addCcaCommand = new EventCommand(cca);

        // same object -> returns true
        assertTrue(addCipCommand.equals(addCipCommand));

        // same values -> returns true
        EventCommand addAssignmentCommandCopy = new EventCommand(cip);
        assertTrue(addCipCommand.equals(addAssignmentCommandCopy));

        // different types -> returns false
        assertFalse(addCipCommand.equals(1));

        // null -> returns false
        assertFalse(addCipCommand.equals(null));

        // different activity -> returns false
        assertFalse(addCipCommand.equals(addCcaCommand));
    }

    /**
     * Generates a new EventCommand with the details of the given event.
     */
    EventCommand getEventCommandForGivenEvent(Event event, Model model) {
        EventCommand command = new EventCommand(event);
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void addActivity(Activity activity) throws DuplicateActivityException {
            fail("This method should not be called.");
        }

        @Override
        public void addActivities(ReadOnlyDeskBoard deskBoard) {
            fail("This method should not be called.");
        }

        @Override
        public void resetData(ReadOnlyDeskBoard newData) {
            fail("This method should not be called.");
        }

        @Override
        public ReadOnlyDeskBoard getDeskBoard() {
            fail("This method should not be called.");
            return null;
        }

        @Override
        public void deleteActivity(Activity target) throws ActivityNotFoundException {
            fail("This method should not be called.");
        }

        @Override
        public void clearActivities(String activityTypeToClear) {
            fail("This method should not be called.");
        }

        @Override
        public void updateActivity(Activity target, Activity editedActivity)
                throws DuplicateActivityException {
            fail("This method should not be called.");
        }

        @Override
        public ObservableList<Activity> getFilteredActivityList() {
            fail("This method should not be called.");
            return null;
        }

```
###### \java\seedu\address\logic\commands\HelpCommandTest.java
``` java
    @Test
    public void execute_helpForHelp_success() {
        HelpCommand command = new HelpCommand("help");
        assertCommandSuccess(command, HelpCommand.MESSAGE_USAGE);
    }

    @Test
    public void execute_helpForMan_success() {
        HelpCommand command = new HelpCommand("man");
        assertCommandSuccess(command, HelpCommand.MESSAGE_USAGE);
    }

    @Test
    public void execute_helpForList_success() {
        HelpCommand command = new HelpCommand("list");
        assertCommandSuccess(command, ListCommand.MESSAGE_USAGE);

        command = new HelpCommand("ls");
        assertCommandSuccess(command, ListCommand.MESSAGE_USAGE);
    }

    @Test
    public void execute_helpForOverdue_success() {
        HelpCommand command = new HelpCommand("overdue");
        assertCommandSuccess(command, OverdueCommand.MESSAGE_USAGE);
    }

    @Test
    public void execute_helpForClear_success() {
        HelpCommand command = new HelpCommand("clear");
        assertCommandSuccess(command, ClearCommand.MESSAGE_USAGE);

        command = new HelpCommand("c");
        assertCommandSuccess(command, ClearCommand.MESSAGE_USAGE);
    }

    @Test
    public void execute_helpForUndo_success() {
        HelpCommand command = new HelpCommand("undo");
        assertCommandSuccess(command, UndoCommand.MESSAGE_USAGE);

        command = new HelpCommand("u");
        assertCommandSuccess(command, UndoCommand.MESSAGE_USAGE);
    }

    @Test
    public void execute_helpForRedo_success() {
        HelpCommand command = new HelpCommand("redo");
        assertCommandSuccess(command, RedoCommand.MESSAGE_USAGE);

        command = new HelpCommand("r");
        assertCommandSuccess(command, RedoCommand.MESSAGE_USAGE);
    }

    @Test
    public void execute_invalidArgs_throwsCommandException()   {
        HelpCommand command = new HelpCommand("hello");
        assertCommandFailure(command, HelpCommand.MESSAGE_USAGE);
    }

}
```
###### \java\seedu\address\logic\commands\RemoveCommandTest.java
``` java
/**
 * Contains unit tests for {@code RemoveCommand} and integration tests (interactions with Model, UndoCommand
 * and RedoCommand)
 */
public class RemoveCommandTest {

    private Model model = new ModelManager(getTypicalDeskBoard(), new UserPrefs());

    @Test
    public void execute_validTaskOptionValidIndex_success() throws Exception {
        Activity activityToDelete = model.getFilteredTaskList().get(INDEX_FIRST_ACTIVITY.getZeroBased());
        RemoveCommand removeCommand = prepareCommand("task", INDEX_FIRST_ACTIVITY);

        String expectedMessage = String.format(RemoveCommand.MESSAGE_REMOVE_TASK_SUCCESS, activityToDelete);

        ModelManager expectedModel = new ModelManager(model.getDeskBoard(), new UserPrefs());
        expectedModel.deleteActivity(activityToDelete);

        assertCommandSuccess(removeCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validEventOptionValidIndex_success() throws Exception {
        Activity activityToDelete = model.getFilteredEventList().get(INDEX_FIRST_ACTIVITY.getZeroBased());
        RemoveCommand removeCommand = prepareCommand("event", INDEX_FIRST_ACTIVITY);

        String expectedMessage = String.format(RemoveCommand.MESSAGE_REMOVE_EVENT_SUCCESS, activityToDelete);

        ModelManager expectedModel = new ModelManager(model.getDeskBoard(), new UserPrefs());
        expectedModel.deleteActivity(activityToDelete);

        assertCommandSuccess(removeCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validOptionInvalidIndex_throwsCommandException() throws Exception {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredTaskList().size() + 1);
        RemoveCommand removeCommand = prepareCommand("task", outOfBoundIndex);

        assertCommandFailure(removeCommand, model, Messages.MESSAGE_INVALID_ACTIVITY_DISPLAYED_INDEX);
    }

    @Test
    public void executeUndoRedo_validOptionValidIndex_success() throws Exception {
        UndoRedoStack undoRedoStack = new UndoRedoStack();
        UndoCommand undoCommand = prepareUndoCommand(model, undoRedoStack);
        RedoCommand redoCommand = prepareRedoCommand(model, undoRedoStack);
        Activity activityToDelete = model.getFilteredTaskList().get(INDEX_FIRST_ACTIVITY.getZeroBased());
        RemoveCommand removeCommand = prepareCommand("task", INDEX_FIRST_ACTIVITY);
        Model expectedModel = new ModelManager(model.getDeskBoard(), new UserPrefs());

        // delete -> first activity deleted
        removeCommand.execute();
        undoRedoStack.push(removeCommand);

        // undo -> reverts desk board back to previous state
        assertCommandSuccess(undoCommand, model, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> same first activity deleted again
        expectedModel.deleteActivity(activityToDelete);
        assertCommandSuccess(redoCommand, model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void executeUndoRedo_validOptionInvalidIndex_failure() {
        UndoRedoStack undoRedoStack = new UndoRedoStack();
        UndoCommand undoCommand = prepareUndoCommand(model, undoRedoStack);
        RedoCommand redoCommand = prepareRedoCommand(model, undoRedoStack);
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredTaskList().size() + 1);
        RemoveCommand removeCommand = prepareCommand("task", outOfBoundIndex);

        // execution failed -> deleteCommand not pushed into undoRedoStack
        assertCommandFailure(removeCommand, model, Messages.MESSAGE_INVALID_ACTIVITY_DISPLAYED_INDEX);

        // no commands in undoRedoStack -> undoCommand and redoCommand fail
        assertCommandFailure(undoCommand, model, UndoCommand.MESSAGE_FAILURE);
        assertCommandFailure(redoCommand, model, RedoCommand.MESSAGE_FAILURE);
    }

    @Test
    public void equals() throws Exception {
        RemoveCommand removeFirstCommand = prepareCommand("task", INDEX_FIRST_ACTIVITY);
        RemoveCommand removeSecondCommand = prepareCommand("task", INDEX_SECOND_ACTIVITY);

        // same object -> returns true
        assertTrue(removeFirstCommand.equals(removeFirstCommand));

        // same values -> returns true
        RemoveCommand removeFirstCommandCopy = prepareCommand("task", INDEX_FIRST_ACTIVITY);
        assertTrue(removeFirstCommand.equals(removeFirstCommandCopy));

        // one command preprocessed when previously equal -> returns false
        removeFirstCommandCopy.preprocessUndoableCommand();
        assertFalse(removeFirstCommand.equals(removeFirstCommandCopy));

        // different types -> returns false
        assertFalse(removeFirstCommand.equals(1));

        // null -> returns false
        assertFalse(removeFirstCommand.equals(null));

        // different activity -> returns false
        assertFalse(removeFirstCommand.equals(removeSecondCommand));
    }

    private RemoveCommand prepareCommand(String activityOption, Index index) {
        RemoveCommand removeCommand = new RemoveCommand(activityOption, index);
        removeCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return removeCommand;
    }
}
```
###### \java\seedu\address\logic\commands\TaskCommandIntegrationTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) for {@code TaskCommand}.
 */
public class TaskCommandIntegrationTest {

    private Model model;

    @Before
    public void setUp() {
        model = new ModelManager(getTypicalDeskBoard(), new UserPrefs());
    }

    @Test
    public void execute_newTask_success() throws Exception {
        Task validTask = new TaskBuilder().build();

        Model expectedModel = new ModelManager(model.getDeskBoard(), new UserPrefs());
        expectedModel.addActivity(validTask);

        assertCommandSuccess(prepareCommand(validTask, model), model,
                String.format(TaskCommand.MESSAGE_SUCCESS, validTask), expectedModel);
    }

    @Test
    // Questionable - does the app check for duplicate task?
    public void execute_duplicateTask_throwsCommandException() {
        Activity activityInList = model.getDeskBoard().getActivityList().get(1);
        assertCommandFailure(prepareCommand((Task) activityInList, model), model, TaskCommand.MESSAGE_DUPLICATE_TASK);
    }

    /**
     * Generates a new {@code TaskCommand} which upon execution, adds {@code task} into the {@code model}.
     */
    private TaskCommand prepareCommand(Task task, Model model) {
        TaskCommand command = new TaskCommand(task);
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }
}
```
###### \java\seedu\address\logic\commands\TaskCommandTest.java
``` java
public class TaskCommandTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void constructor_nullTask_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new TaskCommand(null);
    }

    @Test
    public void execute_taskAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingTaskAdded modelStub = new ModelStubAcceptingTaskAdded();
        Task validTask = new TaskBuilder().build();

        CommandResult commandResult = getTaskCommandForGivenTask(validTask, modelStub).execute();

        assertEquals(String.format(TaskCommand.MESSAGE_SUCCESS, validTask), commandResult.feedbackToUser);
        assertEquals(Arrays.asList(validTask), modelStub.tasksAdded);
    }

    @Test
    public void execute_duplicateTask_throwsCommandException() throws Exception {
        ModelStub modelStub = new ModelStubThrowingDuplicateActivityException();
        Task validTask = new TaskBuilder().build();

        thrown.expect(CommandException.class);
        thrown.expectMessage(TaskCommand.MESSAGE_DUPLICATE_TASK);

        getTaskCommandForGivenTask(validTask, modelStub).execute();
    }

    @Test
    public void equals() {
        Task assignment = new TaskBuilder().build();
        Task project = new TaskBuilder().withName("Project").build();
        TaskCommand addAssignmentCommand = new TaskCommand(assignment);
        TaskCommand addProjectCommand = new TaskCommand(project);

        // same object -> returns true
        assertTrue(addAssignmentCommand.equals(addAssignmentCommand));

        // same values -> returns true
        TaskCommand addAssignmentCommandCopy = new TaskCommand(assignment);
        assertTrue(addAssignmentCommand.equals(addAssignmentCommandCopy));

        // different types -> returns false
        assertFalse(addAssignmentCommand.equals(1));

        // null -> returns false
        assertFalse(addAssignmentCommand.equals(null));

        // different activity -> returns false
        assertFalse(addAssignmentCommand.equals(addProjectCommand));
    }

    /**
     * Generates a new TaskCommand with the details of the given task.
     */
    TaskCommand getTaskCommandForGivenTask(Task task, Model model) {
        TaskCommand command = new TaskCommand(task);
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void addActivity(Activity activity) throws DuplicateActivityException {
            fail("This method should not be called.");
        }

        @Override
        public void addActivities(ReadOnlyDeskBoard deskBoard) {
            fail("This method should not be called.");
        }

        @Override
        public void resetData(ReadOnlyDeskBoard newData) {
            fail("This method should not be called.");
        }

        @Override
        public ReadOnlyDeskBoard getDeskBoard() {
            fail("This method should not be called.");
            return null;
        }

        @Override
        public void deleteActivity(Activity target) throws ActivityNotFoundException {
            fail("This method should not be called.");
        }

        @Override
        public void clearActivities(String activityTypeToClear) {
            fail("This method should not be called.");
        }

        @Override
        public void updateActivity(Activity target, Activity editedActivity)
                throws DuplicateActivityException {
            fail("This method should not be called.");
        }

        @Override
        public ObservableList<Activity> getFilteredActivityList() {
            fail("This method should not be called.");
            return null;
        }

```
###### \java\seedu\address\logic\logictestutil\EventTestConstants.java
``` java
/**
 * Constants for event object.
 */
public class EventTestConstants {

    public static final String VALID_NAME_CCA = "CCA";
    public static final String VALID_NAME_CAMP = "Orientation Camp";
    public static final String VALID_START_DATETIME_CCA = "15/5/2018 17:00";
    public static final String VALID_START_DATETIME_CAMP = "1/8/2018 8:00";
    public static final String VALID_END_DATETIME_CCA = "15/5/2018 21:00";
    public static final String VALID_END_DATETIME_CAMP = "8/8/2018 17:00";
    public static final String VALID_LOCATION_CCA = "NUS Utown";
    public static final String VALID_LOCATION_CAMP = "NUS School of Computing";
    public static final String VALID_REMARK_CCA = "Bring flute";
    public static final String VALID_REMARK_CAMP = "Arrive earlier for briefing";
    public static final String VALID_TAG_CCA = "Band";
    public static final String VALID_TAG_CAMP = "Orientation";
    public static final String VALID_TAG_IMPORTANT = "Important";

    public static final String NAME_DESC_CCA = " " + PREFIX_NAME + VALID_NAME_CCA;
    public static final String NAME_DESC_CAMP = " " + PREFIX_NAME + VALID_NAME_CAMP;
    public static final String START_DATETIME_DESC_CCA = " " + PREFIX_START_DATETIME
            + VALID_START_DATETIME_CCA;
    public static final String START_DATETIME_DESC_CAMP = " " + PREFIX_START_DATETIME
            + VALID_START_DATETIME_CAMP;
    public static final String END_DATETIME_DESC_CCA = " " + PREFIX_END_DATETIME
            + VALID_END_DATETIME_CCA;
    public static final String END_DATETIME_DESC_CAMP = " " + PREFIX_END_DATETIME
            + VALID_END_DATETIME_CAMP;
    public static final String LOCATION_DESC_CCA = " " + PREFIX_LOCATION + VALID_LOCATION_CCA;
    public static final String LOCATION_DESC_CAMP = " " + PREFIX_LOCATION + VALID_LOCATION_CAMP;
    public static final String REMARK_DESC_CCA = " " + PREFIX_REMARK + VALID_REMARK_CCA;
    public static final String REMARK_DESC_CAMP = " " + PREFIX_REMARK + VALID_REMARK_CAMP;
    public static final String TAG_DESC_CCA = " " + PREFIX_TAG + VALID_TAG_CCA;
    public static final String TAG_DESC_CAMP = " " + PREFIX_TAG + VALID_TAG_CAMP;
    public static final String TAG_DESC_IMPORTANT = " " + PREFIX_TAG + VALID_TAG_IMPORTANT;

    public static final String AFTEREND_START_DATETIME_CCA = "16/5/2018 8:00";
    public static final String AFTEREND_START_DATETIME_DESC_CCA = " " + PREFIX_START_DATETIME
            + AFTEREND_START_DATETIME_CCA;

    public static final String INVALID_EVENT_NAME_DESC = " " + PREFIX_NAME + "Orbital&"; // '&' not allowed
    public static final String INVALID_EVENT_START_DATETIME_DESC = " " + PREFIX_START_DATETIME + "2018-03-04 17:00";
    public static final String INVALID_EVENT_END_DATETIME_DESC = " " + PREFIX_END_DATETIME + "2018-03-10 17:00";
    public static final String INVALID_EVENT_LOCATION_DESC = " " + PREFIX_LOCATION + ""; // '' not allowed
    // whitespace in front not allowed

    public static final String INVALID_EVENT_REMARK_DESC = " " + PREFIX_REMARK + ""; // '' not allowed
    public static final String INVALID_EVENT_TAG_DESC = " " + PREFIX_TAG + "Important*"; // '*' not allowed in tags
}
```
###### \java\seedu\address\logic\logictestutil\TaskTestConstants.java
``` java
    public static final String VALID_TAG_URGENT = "Urgent";

```
###### \java\seedu\address\logic\logictestutil\TaskTestConstants.java
``` java
    public static final String TAG_DESC_URGENT = " " + PREFIX_TAG + VALID_TAG_URGENT;

    public static final String INVALID_TASK_NAME_DESC = " " + PREFIX_NAME + "CS2106 Assignment&"; // '&' not allowed
    public static final String INVALID_TASK_DATE_TIME_DESC = " " + PREFIX_DATE_TIME + "2018-03-04 17:00";
    public static final String INVALID_TASK_REMARK_DESC = " " + PREFIX_REMARK + ""; // '' not allowed
    public static final String INVALID_TASK_TAG_DESC = " " + PREFIX_TAG + "CS2106*"; // '*' not allowed in tags
}
```
###### \java\seedu\address\logic\parser\DeskBoardParserTest.java
``` java
public class DeskBoardParserTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final DeskBoardParser parser = new DeskBoardParser();

    @Test
    public void parseCommand_task() throws Exception {
        Task task = new TaskBuilder().build();
        TaskCommand command = (TaskCommand) parser.parseCommand(TaskUtil.getTaskCommand(task));
        assertEquals(new TaskCommand(task), command);
    }

```
###### \java\seedu\address\logic\parser\DeskBoardParserTest.java
``` java
    @Test
    public void parseCommand_remove() throws Exception {
        assertTrue(parser.parseCommand(RemoveCommand.COMMAND_WORD + " task "
                + INDEX_FIRST_ACTIVITY.getOneBased()) instanceof RemoveCommand);
        assertTrue(parser.parseCommand(RemoveCommand.COMMAND_WORD + " event "
                + INDEX_FIRST_ACTIVITY.getOneBased()) instanceof RemoveCommand);
        assertTrue(parser.parseCommand(RemoveCommand.COMMAND_ALIAS + " task "
                + INDEX_FIRST_ACTIVITY.getOneBased()) instanceof RemoveCommand);
        assertTrue(parser.parseCommand(RemoveCommand.COMMAND_ALIAS + " event "
                + INDEX_FIRST_ACTIVITY.getOneBased()) instanceof RemoveCommand);
    }


//    public void parseCommand_edit() throws Exception {
//        Person person = new PersonBuilder().build();
//        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
//        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
//                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getPersonDetails(person));
//        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
//    }
//
//    public void parseCommand_find() throws Exception {
//        List<String> keywords = Arrays.asList("foo", "bar", "baz");
//        FindCommand command = (FindCommand) parser.parseCommand(
//                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
//        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
//    }

```
###### \java\seedu\address\logic\parser\EventCommandParserTest.java
``` java
public class EventCommandParserTest {
    private EventCommandParser parser = new EventCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Event expectedEvent = new EventBuilder().withName(VALID_NAME_CCA).withStartDateTime(VALID_START_DATETIME_CCA)
                .withEndDateTime(VALID_END_DATETIME_CCA).withLocation(VALID_LOCATION_CCA).withRemark(VALID_REMARK_CCA)
                .withTags(VALID_TAG_CCA).build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_CCA + START_DATETIME_DESC_CCA
                + END_DATETIME_DESC_CCA + LOCATION_DESC_CCA + REMARK_DESC_CCA + TAG_DESC_CCA,
                new EventCommand(expectedEvent));

        // multiple names - last name accepted
        assertParseSuccess(parser, NAME_DESC_CAMP + NAME_DESC_CCA + START_DATETIME_DESC_CCA
                        + END_DATETIME_DESC_CCA + LOCATION_DESC_CCA + REMARK_DESC_CCA + TAG_DESC_CCA,
                new EventCommand(expectedEvent));


        // multiple remarks - last remark accepted
        assertParseSuccess(parser, NAME_DESC_CCA + START_DATETIME_DESC_CCA
                        + END_DATETIME_DESC_CCA + LOCATION_DESC_CCA
                        + REMARK_DESC_CAMP + REMARK_DESC_CCA + TAG_DESC_CCA,
                new EventCommand(expectedEvent));

        // multiple tags - all accepted
        Event expectedEventMultipleTags = new EventBuilder().withName(VALID_NAME_CCA)
                .withStartDateTime(VALID_START_DATETIME_CCA).withEndDateTime(VALID_END_DATETIME_CCA)
                .withLocation(VALID_LOCATION_CCA).withRemark(VALID_REMARK_CCA)
                .withTags(VALID_TAG_CCA, VALID_TAG_IMPORTANT).build();
        assertParseSuccess(parser, NAME_DESC_CCA + START_DATETIME_DESC_CCA
                        + END_DATETIME_DESC_CCA + LOCATION_DESC_CCA
                        + REMARK_DESC_CCA + TAG_DESC_CCA + TAG_DESC_IMPORTANT,
                new EventCommand(expectedEventMultipleTags));
    }

    @Test
    public void parse_optionalLocationMissing_success() {
        Event expectedEvent = new EventBuilder().withName(VALID_NAME_CCA).withStartDateTime(VALID_START_DATETIME_CCA)
                .withEndDateTime(VALID_END_DATETIME_CCA).withLocation().withRemark(VALID_REMARK_CCA)
                .withTags(VALID_TAG_CCA).build();
        assertParseSuccess(parser, NAME_DESC_CCA + START_DATETIME_DESC_CCA + END_DATETIME_DESC_CCA
                        + REMARK_DESC_CCA + TAG_DESC_CCA,
                new EventCommand(expectedEvent));
    }

    @Test
    public void parse_optionalRemarkMissing_success() {
        Event expectedEvent = new EventBuilder().withName(VALID_NAME_CCA).withStartDateTime(VALID_START_DATETIME_CCA)
                .withEndDateTime(VALID_END_DATETIME_CCA).withLocation(VALID_LOCATION_CCA).withRemark()
                .withTags(VALID_TAG_CCA).build();
        assertParseSuccess(parser, NAME_DESC_CCA + START_DATETIME_DESC_CCA + END_DATETIME_DESC_CCA
                        + LOCATION_DESC_CCA + TAG_DESC_CCA,
                new EventCommand(expectedEvent));
    }

    @Test
    public void parse_optionalTagsMissing_success() {
        Event expectedEvent = new EventBuilder().withName(VALID_NAME_CCA).withStartDateTime(VALID_START_DATETIME_CCA)
                .withEndDateTime(VALID_END_DATETIME_CCA).withLocation(VALID_LOCATION_CCA).withRemark(VALID_REMARK_CCA)
                .withTags().build();
        assertParseSuccess(parser, NAME_DESC_CCA + START_DATETIME_DESC_CCA + END_DATETIME_DESC_CCA
                + LOCATION_DESC_CCA + REMARK_DESC_CCA, new EventCommand(expectedEvent));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EventCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser, VALID_NAME_CCA + START_DATETIME_DESC_CCA + END_DATETIME_DESC_CCA
                + LOCATION_DESC_CCA + REMARK_DESC_CCA, expectedMessage);

        // missing start datetime prefix
        assertParseFailure(parser, NAME_DESC_CCA + VALID_START_DATETIME_CCA + END_DATETIME_DESC_CCA
                + LOCATION_DESC_CCA + REMARK_DESC_CCA, expectedMessage);

        // missing end datetime prefix
        assertParseFailure(parser, NAME_DESC_CCA + START_DATETIME_DESC_CCA + VALID_END_DATETIME_CCA
                + LOCATION_DESC_CCA + REMARK_DESC_CCA, expectedMessage);
    }

    @Test
    public void parse_startDateTimeAfterEndDateTime_failure() {
        Event expectedEvent = new EventBuilder().withName(VALID_NAME_CCA)
                .withStartDateTime(AFTEREND_START_DATETIME_CCA)
                .withEndDateTime(VALID_END_DATETIME_CCA)
                .withLocation(VALID_LOCATION_CCA).withRemark(VALID_REMARK_CCA)
                .withTags(VALID_TAG_CCA).build();

        assertParseFailure(parser, NAME_DESC_CCA + AFTEREND_START_DATETIME_DESC_CCA + END_DATETIME_DESC_CCA
                + LOCATION_DESC_CCA + REMARK_DESC_CCA + TAG_DESC_CCA, MESSAGE_INVALID_TIME_RANGE);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_EVENT_NAME_DESC + START_DATETIME_DESC_CCA
                + END_DATETIME_DESC_CCA + LOCATION_DESC_CCA + REMARK_DESC_CCA + TAG_DESC_CCA,
                Name.MESSAGE_NAME_CONSTRAINTS);

        // invalid start datetime
        assertParseFailure(parser, NAME_DESC_CCA + INVALID_EVENT_START_DATETIME_DESC
                        + END_DATETIME_DESC_CCA + LOCATION_DESC_CCA + REMARK_DESC_CCA + TAG_DESC_CCA,
                DateTime.MESSAGE_DATETIME_CONSTRAINTS);

        // invalid end datetime
        assertParseFailure(parser, NAME_DESC_CCA + START_DATETIME_DESC_CCA
                        + INVALID_EVENT_END_DATETIME_DESC + LOCATION_DESC_CCA + REMARK_DESC_CCA + TAG_DESC_CCA,
                DateTime.MESSAGE_DATETIME_CONSTRAINTS);

        // invalid location
        assertParseFailure(parser, NAME_DESC_CCA + START_DATETIME_DESC_CCA
                + END_DATETIME_DESC_CCA + INVALID_EVENT_LOCATION_DESC + REMARK_DESC_CCA + TAG_DESC_CCA,
                Location.MESSAGE_LOCATION_CONSTRAINTS);

        // invalid remark
        assertParseFailure(parser, NAME_DESC_CCA + START_DATETIME_DESC_CCA
                + END_DATETIME_DESC_CCA + LOCATION_DESC_CCA + INVALID_EVENT_REMARK_DESC + TAG_DESC_CCA,
                Remark.MESSAGE_REMARK_CONSTRAINTS);

        // invalid tag
        assertParseFailure(parser, NAME_DESC_CCA + START_DATETIME_DESC_CCA
                + END_DATETIME_DESC_CCA + LOCATION_DESC_CCA + REMARK_DESC_CCA + INVALID_EVENT_TAG_DESC,
                Tag.MESSAGE_TAG_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, INVALID_EVENT_NAME_DESC + INVALID_EVENT_START_DATETIME_DESC
                        + END_DATETIME_DESC_CCA + LOCATION_DESC_CCA + REMARK_DESC_CCA + TAG_DESC_CCA,
                Name.MESSAGE_NAME_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_CCA + START_DATETIME_DESC_CCA
                        + END_DATETIME_DESC_CCA + LOCATION_DESC_CCA + REMARK_DESC_CCA + TAG_DESC_CCA,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EventCommand.MESSAGE_USAGE));
    }

}
```
###### \java\seedu\address\logic\parser\ParserUtilTest.java
``` java
    @Test
    public void parseFilePath_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> ParserUtil.parseFilePath((String) null));
        Assert.assertThrows(NullPointerException.class, () -> ParserUtil.parseFilePath((Optional<String>) null));
    }

    @Test
    public void parseFilePath_invalidValue_throwsIllegalValueException() {
        Assert.assertThrows(IllegalValueException.class, () -> ParserUtil.parseFilePath(INVALID_FILEPATH));
        Assert.assertThrows(IllegalValueException.class, () -> ParserUtil.parseFilePath(Optional.of(INVALID_FILEPATH)));
    }

    @Test
    public void parseFilePath_optionalEmpty_returnsOptionalEmpty() throws Exception {
        assertFalse(ParserUtil.parseFilePath(Optional.empty()).isPresent());
    }

    @Test
    public void parseFilePath_validValueWithoutWhitespace_returnsFilePath() throws Exception {
        FilePath expectedFilePath = new FilePath(VALID_FILEPATH);
        assertEquals(expectedFilePath, ParserUtil.parseFilePath(VALID_FILEPATH));
        assertEquals(Optional.of(expectedFilePath), ParserUtil.parseFilePath(Optional.of(VALID_FILEPATH)));
    }

    @Test
    public void parseFilePath_validValueWithWhitespace_returnsTrimmedFilePath() throws Exception {
        String filePathWithWhitespace = WHITESPACE + VALID_FILEPATH + WHITESPACE;
        FilePath expectedFilePath = new FilePath(VALID_FILEPATH);
        assertEquals(expectedFilePath, ParserUtil.parseFilePath(filePathWithWhitespace));
        assertEquals(Optional.of(expectedFilePath), ParserUtil.parseFilePath(Optional.of(filePathWithWhitespace)));
    }
}
```
###### \java\seedu\address\logic\parser\RemoveCommandParserTest.java
``` java
public class RemoveCommandParserTest {

    private RemoveCommandParser parser = new RemoveCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "task 1", new RemoveCommand("task", INDEX_FIRST_ACTIVITY));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // Invalid activity option
        assertParseFailure(parser, "invalidOption 1", String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveCommand
                .MESSAGE_USAGE));

        // Only one argument
        assertParseFailure(parser, "task", String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveCommand
                .MESSAGE_USAGE));

    }
}
```
###### \java\seedu\address\logic\parser\TaskCommandParserTest.java
``` java
public class TaskCommandParserTest {
    private TaskCommandParser parser = new TaskCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Task expectedTask = new TaskBuilder().withName(VALID_NAME_CS2010_QUIZ).withDateTime(VALID_DATE_TIME_CS2010_QUIZ)
                .withRemark(VALID_REMARK_CS2010_QUIZ).withTags(VALID_TAG_CS2010).build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_CS2010_QUIZ + DATE_TIME_DESC_CS2010_QUIZ
                + REMARK_DESC_CS2010_QUIZ + TAG_DESC_CS2010, new TaskCommand(expectedTask));

        // multiple names - last name accepted
        assertParseSuccess(parser, NAME_DESC_MA2108_HOMEWORK + NAME_DESC_CS2010_QUIZ
                + DATE_TIME_DESC_CS2010_QUIZ + REMARK_DESC_CS2010_QUIZ + TAG_DESC_CS2010,
                new TaskCommand(expectedTask));

        // multiple date times - last date time accepted
        assertParseSuccess(parser, NAME_DESC_CS2010_QUIZ + DATE_TIME_DESC_MA2108_HOMEWORK
                + DATE_TIME_DESC_CS2010_QUIZ + REMARK_DESC_CS2010_QUIZ
                + TAG_DESC_CS2010, new TaskCommand(expectedTask));

        // multiple remarks - last remark accepted
        assertParseSuccess(parser, NAME_DESC_CS2010_QUIZ + DATE_TIME_DESC_CS2010_QUIZ
                + REMARK_DESC_MA2108_HOMEWORK + REMARK_DESC_CS2010_QUIZ + TAG_DESC_CS2010,
                new TaskCommand(expectedTask));

        // multiple tags - all accepted
        Task expectedTaskMultipleTags = new TaskBuilder().withName(VALID_NAME_CS2010_QUIZ)
                .withDateTime(VALID_DATE_TIME_CS2010_QUIZ)
                .withRemark(VALID_REMARK_CS2010_QUIZ)
                .withTags(VALID_TAG_CS2010, VALID_TAG_URGENT).build();
        assertParseSuccess(parser, NAME_DESC_CS2010_QUIZ + DATE_TIME_DESC_CS2010_QUIZ
                + REMARK_DESC_CS2010_QUIZ + TAG_DESC_URGENT + TAG_DESC_CS2010,
                new TaskCommand(expectedTaskMultipleTags));
    }


    @Test
    public void parse_optionalRemarkMissing_success() {
        Task expectedTask = new TaskBuilder().withName(VALID_NAME_CS2010_QUIZ).withDateTime(VALID_DATE_TIME_CS2010_QUIZ)
                .withRemark().withTags(VALID_TAG_CS2010).build();
        assertParseSuccess(parser, NAME_DESC_CS2010_QUIZ + DATE_TIME_DESC_CS2010_QUIZ + TAG_DESC_CS2010,
                new TaskCommand(expectedTask));
    }

    @Test
    public void parse_optionalTagsMissing_success() {
        Task expectedTask = new TaskBuilder().withName(VALID_NAME_CS2010_QUIZ).withDateTime(VALID_DATE_TIME_CS2010_QUIZ)
                .withRemark(VALID_REMARK_CS2010_QUIZ).withTags().build();
        assertParseSuccess(parser, NAME_DESC_CS2010_QUIZ + DATE_TIME_DESC_CS2010_QUIZ
                        + REMARK_DESC_CS2010_QUIZ, new TaskCommand(expectedTask));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, TaskCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser, VALID_NAME_CS2010_QUIZ + DATE_TIME_DESC_CS2010_QUIZ
                        + REMARK_DESC_CS2010_QUIZ, expectedMessage);

        // missing date time prefix
        assertParseFailure(parser, NAME_DESC_CS2010_QUIZ + VALID_DATE_TIME_CS2010_QUIZ
                        + REMARK_DESC_CS2010_QUIZ, expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_TASK_NAME_DESC + DATE_TIME_DESC_CS2010_QUIZ
                + REMARK_DESC_CS2010_QUIZ + TAG_DESC_CS2010, Name.MESSAGE_NAME_CONSTRAINTS);

        // invalid date time
        assertParseFailure(parser, NAME_DESC_CS2010_QUIZ + INVALID_TASK_DATE_TIME_DESC
                + REMARK_DESC_CS2010_QUIZ + TAG_DESC_CS2010, DateTime.MESSAGE_DATETIME_CONSTRAINTS);

        // invalid remark
        assertParseFailure(parser, NAME_DESC_CS2010_QUIZ + DATE_TIME_DESC_CS2010_QUIZ
                + INVALID_TASK_REMARK_DESC + TAG_DESC_CS2010, Remark.MESSAGE_REMARK_CONSTRAINTS);

        // invalid tag
        assertParseFailure(parser, NAME_DESC_CS2010_QUIZ + DATE_TIME_DESC_CS2010_QUIZ
                + REMARK_DESC_CS2010_QUIZ + INVALID_TASK_TAG_DESC, Tag.MESSAGE_TAG_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, INVALID_TASK_NAME_DESC + INVALID_TASK_DATE_TIME_DESC
                        + REMARK_DESC_CS2010_QUIZ,
                Name.MESSAGE_NAME_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_CS2010_QUIZ + DATE_TIME_DESC_CS2010_QUIZ
                        + REMARK_DESC_CS2010_QUIZ + TAG_DESC_CS2010,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, TaskCommand.MESSAGE_USAGE));
    }
}
```
###### \java\seedu\address\model\activity\DateTimeTest.java
``` java
public class DateTimeTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new DateTime(null));
    }

    @Test
    public void constructor_invalidDateTime_throwsIllegalArgumentException() {
        String invalidDateTime = "";
        Assert.assertThrows(IllegalArgumentException.class, () -> new DateTime(invalidDateTime));
    }

    @Test
    public void isValidDateTime_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> DateTime.isValidDateTime(null));
    }

    @Test
    public void isValidDateTime_validDateTime() {
        assertTrue(DateTime.isValidDateTime("01/08/1995 12:00"));
        assertTrue(DateTime.isValidDateTime("03/03/2019 12:00"));
        assertTrue(DateTime.isValidDateTime("3/3/2019 00:00"));
        assertTrue(DateTime.isValidDateTime("3/3/2019 23:59"));

    }

    @Test
    public void isValidDateTime_invalidDateTime() {
        assertFalse(DateTime.isValidDateTime(""));
        assertFalse(DateTime.isValidDateTime(" "));
        assertFalse(DateTime.isValidDateTime("2019/03/03 12:00")); // YYMMDD
        assertFalse(DateTime.isValidDateTime("12:00 03/03/2019")); // time before date
        assertFalse(DateTime.isValidDateTime("03-03-2019 12:00")); // dash, instead of slash
        assertFalse(DateTime.isValidDateTime("32/12/2018 12:00")); // wrong day
        assertFalse(DateTime.isValidDateTime("1/13/2018 10:00")); // wrong month
        assertFalse(DateTime.isValidDateTime("1/12/0000 12:00")); // wrong year
        assertFalse(DateTime.isValidDateTime("1/8/1995 25:00")); // wrong time
    }
}
```
###### \java\seedu\address\testutil\ActivityBuilder.java
``` java
    T build();

}
```
###### \java\seedu\address\testutil\EventBuilder.java
``` java
    /**
     * Overloaded method.
     * Sets the {@code Remark} of the {@code Activity} to null.
     */
    public EventBuilder withRemark() {
        this.remark = null;
        return this;
    }

```
###### \java\seedu\address\testutil\EventBuilder.java
``` java
    /**
     * Overloaded method.
     * Sets the {@code Location} of the {@code Activity} to null.
     */
    public EventBuilder withLocation() {
        this.location = null;
        return this;
    }

    public Event build() {
        return new Event(name, startDateTime, endDateTime, location, remark, tags);
    }

}
```
###### \java\seedu\address\testutil\EventUtil.java
``` java
/**
 * A utility class for Event.
 */
public class EventUtil {
    /**
     * Returns an event command string for adding the {@code event}.
     */
    public static String getEventCommand(Event event) {
        return EventCommand.COMMAND_WORD + " " + getEventDetails(event);
    }

    /**
     * Returns the part of command string for the given {@code event}'s details.
     */
    public static String getEventDetails(Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + event.getName().fullName + " ");
        sb.append(PREFIX_START_DATETIME + event.getStartDateTime().toString() + " ");
        sb.append(PREFIX_END_DATETIME + event.getEndDateTime().toString() + " ");
        sb.append(PREFIX_LOCATION + event.getLocation().toString() + " ");
        sb.append(PREFIX_REMARK + event.getRemark().value + " ");
        event.getTags().stream().forEach(
            s -> sb.append(PREFIX_TAG + s.tagName + " ")
        );
        return sb.toString();
    }
}
```
###### \java\seedu\address\testutil\TaskBuilder.java
``` java
    /**
     * Overloaded method.
     * Sets the {@code Remark} of the {@code Activity} to null.
     */
    public TaskBuilder withRemark() {
        this.remark = null;
        return this;
    }

```
###### \java\seedu\address\testutil\TaskBuilder.java
``` java
    public Task build() {
        return new Task(name, dateTime, remark, tags);
    }
}
```
###### \java\seedu\address\testutil\TaskUtil.java
``` java
/**
 * A utility class for Task.
 */
public class TaskUtil {
    /**
     * Returns a task command string for adding the {@code task}.
     */
    public static String getTaskCommand(Task task) {
        return TaskCommand.COMMAND_WORD + " " + getTaskDetails(task);
    }

    /**
     * Returns the part of command string for the given {@code task}'s details.
     */
    public static String getTaskDetails(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + task.getName().fullName + " ");
        sb.append(PREFIX_DATE_TIME + task.getDueDateTime().toString() + " ");
        sb.append(PREFIX_REMARK + task.getRemark().value + " ");
        task.getTags().stream().forEach(
            s -> sb.append(PREFIX_TAG + s.tagName + " ")
        );
        return sb.toString();
    }
}
```
###### \java\seedu\address\testutil\TypicalActivities.java
``` java
    /**
     * Returns an {@code ObservableList} of typical tasks.
     */
    public static ObservableList<Activity> getTypicalTasks() {
        List<Activity> taskList = Arrays.asList(ASSIGNMENT1, ASSIGNMENT2, QUIZ);
        return FXCollections.observableList(taskList);
    }

    /**
     * Returns an {@code ObservableList} of typical events.
     */
    public static ObservableList<Activity> getTypicalEvents() {
        List<Activity> eventList = Arrays.asList(CCA, CIP, EXAM1, IFG);
        return FXCollections.observableList(eventList);
    }
}
```
