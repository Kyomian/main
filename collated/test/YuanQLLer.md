# YuanQLLer
###### \java\seedu\address\logic\commands\CommandTestUtil.java
``` java
    /**
     * Removes the first activity in {@code model}'s filtered list from {@code model}'s desk board.
     */
    public static void removeFirstTask(Model model) {
        Activity firstActivity = model.getFilteredTaskList().get(0);
        try {
            model.deleteActivity(firstActivity);
        } catch (ActivityNotFoundException pnfe) {
            throw new AssertionError("Activity in filtered list must exist in model.", pnfe);
        }
    }

```
###### \java\seedu\address\logic\commands\CompleteCommandTest.java
``` java
public class CompleteCommandTest {
    private Model model = new ModelManager(getTypicalDeskBoard(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() throws Exception {
        Activity activityToComplete = model.getFilteredTaskList().get(INDEX_FIRST_ACTIVITY.getZeroBased());
        CompleteCommand completeCommand = prepareCommand(INDEX_FIRST_ACTIVITY);

        String expectedMessage = String.format(CompleteCommand.MESSAGE_COMPLETE_TASK_SUCCESS, activityToComplete);

        ModelManager expectedModel = new ModelManager(model.getDeskBoard(), new UserPrefs());
        Activity completedActivity = activityToComplete.getCompletedCopy();
        expectedModel.updateActivity(activityToComplete, completedActivity);

        assertCommandSuccess(completeCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() throws Exception {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredTaskList().size() + 1);
        CompleteCommand completeCommand = prepareCommand(outOfBoundIndex);

        assertCommandFailure(completeCommand, model, Messages.MESSAGE_INVALID_ACTIVITY_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showActivityAtIndex(model, INDEX_FIRST_ACTIVITY);

        Index outOfBoundIndex = INDEX_SECOND_ACTIVITY;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getDeskBoard().getActivityList().size());

        CompleteCommand completeCommand = prepareCommand(outOfBoundIndex);

        assertCommandFailure(completeCommand, model, Messages.MESSAGE_INVALID_ACTIVITY_DISPLAYED_INDEX);
    }

    @Test
    public void executeUndoRedo_validIndexUnfilteredList_success() throws Exception {
        UndoRedoStack undoRedoStack = new UndoRedoStack();
        UndoCommand undoCommand = prepareUndoCommand(model, undoRedoStack);
        RedoCommand redoCommand = prepareRedoCommand(model, undoRedoStack);
        Activity activityToComplete = model.getFilteredTaskList().get(INDEX_FIRST_ACTIVITY.getZeroBased());
        CompleteCommand completeCommand = prepareCommand(INDEX_FIRST_ACTIVITY);
        Model expectedModel = new ModelManager(model.getDeskBoard(), new UserPrefs());

        // complete -> first activity completed
        completeCommand.execute();
        undoRedoStack.push(completeCommand);

        // undo -> reverts desk board back to previous state and filtered activity list to show all activities
        assertCommandSuccess(undoCommand, model, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> same first activity completed again
        Activity completedActivity = activityToComplete.getCompletedCopy();
        expectedModel.updateActivity(activityToComplete, completedActivity);
        assertCommandSuccess(redoCommand, model, RedoCommand.MESSAGE_SUCCESS, expectedModel);

    }

    @Test
    public void executeUndoRedo_invalidIndexUnfilteredList_failure() {
        UndoRedoStack undoRedoStack = new UndoRedoStack();
        UndoCommand undoCommand = prepareUndoCommand(model, undoRedoStack);
        RedoCommand redoCommand = prepareRedoCommand(model, undoRedoStack);
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredActivityList().size() + 1);
        CompleteCommand completeCommandCommand = prepareCommand(outOfBoundIndex);

        // execution failed -> completeCommandCommand not pushed into undoRedoStack
        assertCommandFailure(completeCommandCommand, model, Messages.MESSAGE_INVALID_ACTIVITY_DISPLAYED_INDEX);

        // no commands in undoRedoStack -> undoCommand and redoCommand fail
        assertCommandFailure(undoCommand, model, UndoCommand.MESSAGE_FAILURE);
        assertCommandFailure(redoCommand, model, RedoCommand.MESSAGE_FAILURE);
    }

    /**
     * 1. Complete a {@code Task} from a filtered list.
     * 2. Undo the completion.
     * 3. The unfiltered list should be shown now. Verify that the index of the previously completed activity in the
     * unfiltered list is different from the index at the filtered list.
     * 4. Redo the completion. This ensures {@code RedoCommand} copletes the activity object regardless of indexing.
     */
    @Test
    public void executeUndoRedo_validIndexFilteredList_samePersonCompleted() throws Exception {
        UndoRedoStack undoRedoStack = new UndoRedoStack();
        UndoCommand undoCommand = prepareUndoCommand(model, undoRedoStack);
        RedoCommand redoCommand = prepareRedoCommand(model, undoRedoStack);
        CompleteCommand completeCommand = prepareCommand(INDEX_FIRST_ACTIVITY);
        Model expectedModel = new ModelManager(model.getDeskBoard(), new UserPrefs());

        showActivityAtIndex(model, INDEX_SECOND_ACTIVITY);
        Activity activityToComplete = model.getFilteredTaskList().get(INDEX_FIRST_ACTIVITY.getZeroBased());
        // complete -> completes second activity in unfiltered activity list / first activity in filtered activity list
        completeCommand.execute();
        undoRedoStack.push(completeCommand);

        // undo -> reverts desk board back to previous state and filtered activity list to show all persons
        assertCommandSuccess(undoCommand, model, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        Activity completedActivity = activityToComplete.getCompletedCopy();
        expectedModel.updateActivity(activityToComplete, completedActivity);
        assertNotEquals(activityToComplete, model.getFilteredActivityList().get(INDEX_FIRST_ACTIVITY.getZeroBased()));
        // redo -> completes same second activity in unfiltered activity list
        assertCommandSuccess(redoCommand, model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() throws Exception {
        CompleteCommand completeFirstCommand = prepareCommand(INDEX_FIRST_ACTIVITY);
        CompleteCommand completeSecondCommand = prepareCommand(INDEX_SECOND_ACTIVITY);

        // same object -> returns true
        assertTrue(completeFirstCommand.equals(completeFirstCommand));

        // same values -> returns true
        CompleteCommand completeFirstCommandCopy = prepareCommand(INDEX_FIRST_ACTIVITY);
        assertTrue(completeFirstCommand.equals(completeFirstCommandCopy));

        // one command preprocessed when previously equal -> returns false
        completeFirstCommandCopy.preprocessUndoableCommand();
        assertFalse(completeFirstCommand.equals(completeFirstCommandCopy));

        // different types -> returns false
        assertFalse(completeFirstCommand.equals(1));

        // null -> returns false
        assertFalse(completeFirstCommand.equals(null));

        // different activity -> returns false
        assertFalse(completeFirstCommand.equals(completeSecondCommand));
    }

    /**
     * Returns a {@code CompleteCommand} with the parameter {@code index}.
     */
    private CompleteCommand prepareCommand(Index index) {
        CompleteCommand completeCommand = new CompleteCommand(index);
        completeCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return completeCommand;
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredActivityList(p -> false);

        assertTrue(model.getFilteredActivityList().isEmpty());
    }
}
```
###### \java\seedu\address\logic\commands\EditTaskDescriptorTest.java
``` java
public class EditTaskDescriptorTest {


    @Test
    public void equals() {
        // same values -> returns true
        EditCommand.EditTaskDescriptor descriptorWithSameValues =
                new EditCommand.EditTaskDescriptor(DESC_MA2108_HOMEWORK);
        assertTrue(DESC_MA2108_HOMEWORK.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_MA2108_HOMEWORK.equals(DESC_MA2108_HOMEWORK));

        // null -> returns false
        assertFalse(DESC_MA2108_HOMEWORK.equals(null));

        // different types -> returns false
        assertFalse(DESC_MA2108_HOMEWORK.equals(5));

        // different values -> returns false
        assertFalse(DESC_MA2108_HOMEWORK.equals(DESC_CS2010_QUIZ));

        // different name -> returns false
        EditCommand.EditTaskDescriptor editedAmy = new EditTaskDescriptorBuilder(DESC_MA2108_HOMEWORK)
                .withName(VALID_NAME_CS2010_QUIZ).build();
        assertFalse(DESC_MA2108_HOMEWORK.equals(editedAmy));

        // different phone -> returns false
        editedAmy = new EditTaskDescriptorBuilder(DESC_MA2108_HOMEWORK)
                .withDateTime(VALID_DATE_TIME_CS2010_QUIZ).build();
        assertFalse(DESC_MA2108_HOMEWORK.equals(editedAmy));

        // different address -> returns false
        editedAmy = new EditTaskDescriptorBuilder(DESC_MA2108_HOMEWORK)
                .withRemark(VALID_REMARK_CS2010_QUIZ).build();
        assertFalse(DESC_MA2108_HOMEWORK.equals(editedAmy));

        // different tags -> returns false
        editedAmy = new EditTaskDescriptorBuilder(DESC_MA2108_HOMEWORK).withTags(VALID_TAG_MA2108).build();
        assertFalse(DESC_MA2108_HOMEWORK.equals(editedAmy));
    }
}
```
###### \java\seedu\address\logic\parser\CompleteCommandParserTest.java
``` java
public class CompleteCommandParserTest {

    private CompleteCommandParser parser = new CompleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new CompleteCommand(INDEX_FIRST_ACTIVITY));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // Invalid activity option
        assertParseFailure(parser, "invalidOption", String.format(MESSAGE_INVALID_COMMAND_FORMAT, CompleteCommand
                .MESSAGE_USAGE));

        // Multiple argument
        assertParseFailure(parser, "1 2", String.format(MESSAGE_INVALID_COMMAND_FORMAT, CompleteCommand
                .MESSAGE_USAGE));

    }
}
```
###### \java\seedu\address\logic\parser\EditCommandParserTest.java
``` java
public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private static final String PREFIX_TASK = "task";

    private static final String PREFIX_EVENT = "event";

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_MA2108_HOMEWORK, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "task 1", EditCommand.MESSAGE_NOT_EDITED);
        assertParseFailure(parser, "event 1", EditCommand.MESSAGE_NOT_EDITED);
        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_MA2108_HOMEWORK, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_MA2108_HOMEWORK, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "task 1" + INVALID_TASK_NAME_DESC,
                Name.MESSAGE_NAME_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "task 1" + INVALID_TASK_DATE_TIME_DESC,
                DateTime.MESSAGE_DATETIME_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "task 1" + INVALID_TASK_REMARK_DESC,
                Remark.MESSAGE_REMARK_CONSTRAINTS); // invalid address
        assertParseFailure(parser, "task 1" + INVALID_TASK_TAG_DESC,
                Tag.MESSAGE_TAG_CONSTRAINTS); // invalid tag

        // invalid phone followed by valid email
        assertParseFailure(parser, "task 1" + INVALID_TASK_DATE_TIME_DESC
                + REMARK_DESC_CS2010_QUIZ, DateTime.MESSAGE_DATETIME_CONSTRAINTS);

        // valid phone followed by invalid phone. The test case for invalid phone followed by valid phone
        // is tested at {@code parse_invalidValueFollowedByValidValue_success()}
        assertParseFailure(parser, "task 1" + DATE_TIME_DESC_CS2010_QUIZ + INVALID_TASK_DATE_TIME_DESC,
                DateTime.MESSAGE_DATETIME_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Activity} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "task 1" + TAG_DESC_URGENT + TAG_DESC_MA2108 + TAG_EMPTY,
                Tag.MESSAGE_TAG_CONSTRAINTS);
        assertParseFailure(parser, "task 1" + TAG_DESC_MA2108 + TAG_EMPTY + TAG_DESC_URGENT,
                Tag.MESSAGE_TAG_CONSTRAINTS);
        assertParseFailure(parser, "task 1" + TAG_EMPTY + TAG_DESC_CS2010 + TAG_DESC_URGENT,
                Tag.MESSAGE_TAG_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "task 1" + INVALID_TASK_NAME_DESC
                + INVALID_TASK_DATE_TIME_DESC + VALID_REMARK_MA2108_HOMEWORK
                + VALID_DATE_TIME_MA2108_HOMEWORK,
                Name.MESSAGE_NAME_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_ACTIVITY;
        String userInput = PREFIX_TASK + " " + targetIndex.getOneBased() + DATE_TIME_DESC_CS2010_QUIZ
                + DATE_TIME_DESC_CS2010_QUIZ + REMARK_DESC_MA2108_HOMEWORK + NAME_DESC_MA2108_HOMEWORK
                + TAG_DESC_CS2010;

        EditActivityDescriptor descriptor = new EditTaskDescriptorBuilder().withName(VALID_NAME_MA2108_HOMEWORK)
                .withDateTime(VALID_DATE_TIME_CS2010_QUIZ).withRemark(VALID_REMARK_MA2108_HOMEWORK)
                .withTags(VALID_TAG_CS2010).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_ACTIVITY;
        String userInput = PREFIX_TASK + " " + targetIndex.getOneBased()
                + DATE_TIME_DESC_CS2010_QUIZ + REMARK_DESC_CS2010_QUIZ;

        EditActivityDescriptor descriptor = new EditTaskDescriptorBuilder()
                .withDateTime(VALID_DATE_TIME_CS2010_QUIZ).withRemark(VALID_REMARK_CS2010_QUIZ).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_ACTIVITY;
        String userInput = PREFIX_TASK + " " + targetIndex.getOneBased() + NAME_DESC_MA2108_HOMEWORK;
        EditActivityDescriptor descriptor = new EditTaskDescriptorBuilder()
                .withName(VALID_NAME_MA2108_HOMEWORK).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = PREFIX_TASK + " " + targetIndex.getOneBased() + DATE_TIME_DESC_CS2010_QUIZ;
        descriptor = new EditTaskDescriptorBuilder().withDateTime(VALID_DATE_TIME_CS2010_QUIZ).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = PREFIX_TASK + " " + targetIndex.getOneBased() + REMARK_DESC_CS2010_QUIZ;
        descriptor = new EditTaskDescriptorBuilder().withRemark(VALID_REMARK_CS2010_QUIZ).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = PREFIX_TASK + " " + targetIndex.getOneBased() + REMARK_DESC_CS2010_QUIZ;
        descriptor = new EditTaskDescriptorBuilder().withRemark(VALID_REMARK_CS2010_QUIZ).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = PREFIX_TASK + " " + targetIndex.getOneBased() + TAG_DESC_CS2010;
        descriptor = new EditTaskDescriptorBuilder().withTags(VALID_TAG_CS2010).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_acceptsLast() {
        Index targetIndex = INDEX_FIRST_ACTIVITY;
        String userInput = PREFIX_TASK + " " + targetIndex.getOneBased()  + DATE_TIME_DESC_CS2010_QUIZ
                + REMARK_DESC_MA2108_HOMEWORK + DATE_TIME_DESC_MA2108_HOMEWORK
                + DATE_TIME_DESC_MA2108_HOMEWORK + REMARK_DESC_MA2108_HOMEWORK
                + DATE_TIME_DESC_MA2108_HOMEWORK
                + TAG_DESC_MA2108 + DATE_TIME_DESC_CS2010_QUIZ + REMARK_DESC_CS2010_QUIZ
                + DATE_TIME_DESC_CS2010_QUIZ + TAG_DESC_CS2010;

        EditCommand.EditActivityDescriptor descriptor = new EditTaskDescriptorBuilder()
                .withDateTime(VALID_DATE_TIME_CS2010_QUIZ).withRemark(VALID_REMARK_CS2010_QUIZ)
                .withTags(VALID_TAG_CS2010, VALID_TAG_MA2108).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_invalidValueFollowedByValidValue_success() {
        // no other valid values specified
        Index targetIndex = INDEX_FIRST_ACTIVITY;
        String userInput = PREFIX_TASK + " " + targetIndex.getOneBased()
                + INVALID_TASK_DATE_TIME_DESC + DATE_TIME_DESC_CS2010_QUIZ;
        EditCommand.EditActivityDescriptor descriptor = new EditTaskDescriptorBuilder()
                .withDateTime(VALID_DATE_TIME_CS2010_QUIZ).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // other valid values specified
        userInput = PREFIX_TASK + " " + targetIndex.getOneBased()
                + DATE_TIME_DESC_CS2010_QUIZ + INVALID_TASK_DATE_TIME_DESC
                + REMARK_DESC_CS2010_QUIZ + DATE_TIME_DESC_CS2010_QUIZ;
        descriptor = new EditTaskDescriptorBuilder().withDateTime(VALID_DATE_TIME_CS2010_QUIZ)
                .withRemark(VALID_REMARK_CS2010_QUIZ).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_ACTIVITY;
        String userInput = PREFIX_TASK + " " + targetIndex.getOneBased() + TAG_EMPTY;

        EditActivityDescriptor descriptor = new EditTaskDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
```
###### \java\seedu\address\logic\parser\SelectCommandParserTest.java
``` java
public class SelectCommandParserTest {

    private SelectCommandParser parser = new SelectCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "task 1", new SelectCommand(INDEX_FIRST_ACTIVITY, "task"));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // Invalid activity option
        assertParseFailure(parser, "! 1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand.MESSAGE_USAGE));

        // Only one argument
        assertParseFailure(parser, "task", String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand
                .MESSAGE_USAGE));

    }
}
```
###### \java\seedu\address\testutil\EventBuilder.java
``` java
/**
 * This the class to build event
 */
public class EventBuilder implements ActivityBuilder {

    public static final String DEFAULT_NAME = "CIP";
    public static final String DEFAULT_START_DATETIME = "04/04/2018 08:10";
    public static final String DEFAULT_END_DATETIME = "04/04/2018 10:00";
    public static final String DEFAULT_LOCATION = "123, Jurong West Ave 6";
    public static final String DEFAULT_REMARK = "nil";
    public static final String DEFAULT_TAGS = "optional";

    private Name name;
    private DateTime startDateTime;
    private DateTime endDateTime;
    private Location location;
    private Remark remark;
    private Set<Tag> tags;

    public EventBuilder() {
        name = new Name(DEFAULT_NAME);
        startDateTime = new DateTime(DEFAULT_START_DATETIME);
        endDateTime = new DateTime(DEFAULT_END_DATETIME);
        location = new Location(DEFAULT_LOCATION);
        remark = new Remark(DEFAULT_REMARK);
        tags = SampleDataUtil.getTagSet(DEFAULT_TAGS);
    }

    /**
     * Initializes the EventBuilder with the data of {@code activityToCopy}.
     */
    public EventBuilder(Event eventToCopy) {
        name = eventToCopy.getName();
        startDateTime = eventToCopy.getStartDateTime();
        endDateTime = eventToCopy.getEndDateTime();
        location = eventToCopy.getLocation();
        remark = eventToCopy.getRemark();
        tags = new HashSet<>(eventToCopy.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code Activity} that we are building.
     */
    public EventBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code Activity} that we are building.
     */
    public EventBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Remark} of the {@code Activity} that we are building.
     */
    public EventBuilder withRemark(String remark) {
        this.remark = new Remark(remark);
        return this;
    }

```
###### \java\seedu\address\testutil\EventBuilder.java
``` java
    /**
     * Sets the {@code DateTime} of the {@code Activity} that we are building.
     */
    public EventBuilder withStartDateTime(String dateTime) {
        this.startDateTime = new DateTime(dateTime);
        return this;
    }

    /**
     * Sets the {@code DateTime} of the {@code Activity} that we are building.
     */
    public EventBuilder withEndDateTime(String dateTime) {
        this.endDateTime = new DateTime(dateTime);
        return this;
    }

    /**
     * Sets the {@code Location} of the {@code Activity} that we are building.
     */
    public EventBuilder withLocation(String location) {
        this.location = new Location(location);
        return this;
    }

```
###### \java\seedu\address\testutil\TaskBuilder.java
``` java
/**
 * Build task for testing
 */
public class TaskBuilder implements ActivityBuilder {
    public static final String DEFAULT_NAME = "Assignment";
    public static final String DEFAULT_DATETIME = "04/04/2018 08:10";
    public static final String DEFAULT_REMARK = "Urgent";
    public static final String DEFAULT_TAGS = "Optional";

    private Name name;
    private DateTime dateTime;
    private Remark remark;
    private Set<Tag> tags;

    public TaskBuilder() {
        name = new Name(DEFAULT_NAME);
        dateTime = new DateTime(DEFAULT_DATETIME);
        remark = new Remark(DEFAULT_REMARK);
        tags = SampleDataUtil.getTagSet(DEFAULT_TAGS);
    }

    /**
     * Initializes the ActivityBuilder with the data of {@code taskToCopy}.
     */
    public TaskBuilder(Task taskToCopy) {
        name = taskToCopy.getName();
        dateTime = taskToCopy.getDateTime();
        remark = taskToCopy.getRemark();
        tags = new HashSet<>(taskToCopy.getTags());
    }

    /**
     * Initializes the ActivityBuilder with the data of {@code taskToCopy}.
     */
    public TaskBuilder(Activity activityToCopy) {
        name = activityToCopy.getName();
        dateTime = activityToCopy.getDateTime();
        remark = activityToCopy.getRemark();
        tags = new HashSet<>(activityToCopy.getTags());
    }
    /**
     * Sets the {@code Name} of the {@code Activity} that we are building.
     */
    public TaskBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code Activity} that we are building.
     */
    public TaskBuilder withTags(String... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Remark} of the {@code Activity} that we are building.
     */
    public TaskBuilder withRemark(String remark) {
        this.remark = new Remark(remark);
        return this;
    }

```
###### \java\seedu\address\testutil\TaskBuilder.java
``` java
    /**
     * Sets the {@code DateTime} of the {@code Activity} that we are building.
     */
    public TaskBuilder withDateTime(String dateTime) {
        this.dateTime = new DateTime(dateTime);
        return this;
    }

```
###### \java\seedu\address\testutil\TypicalActivities.java
``` java
/**
 * A utility class containing a list of {@code Activity} objects to be used in tests.
 */
public class TypicalActivities {

    public static final Task ASSIGNMENT1 = new TaskBuilder().withName("CS2101Assignment")
            .withRemark(" ")
            .withDateTime("04/03/2018 23:59")
            .withTags("CS2101").build();
    public static final Task ASSIGNMENT2 = new TaskBuilder().withName("CS2102Assignment")
            .withRemark(" ")
            .withDateTime("15/03/2018 23:59")
            .withTags("CS2102").build();
    public static final Task QUIZ = new TaskBuilder().withName("CS2101Quiz")
            .withDateTime("19/03/2018 23:59")
            .withRemark("IVLE Quiz").build();
    public static final Event CCA = new EventBuilder().withName("CCA")
            .withStartDateTime("01/04/2018 20:00")
            .withEndDateTime("01/04/2018 21:00")
            .withLocation("Campus")
            .withRemark("nil").build();
    public static final Event CIP = new EventBuilder().withName("CIP")
            .withStartDateTime("02/04/2018 08:00")
            .withEndDateTime("02/04/2018 12:00")
            .withLocation("michegan ave")
            .withRemark("nil")
            .withTags("CIP").build();
    public static final Event EXAM1 = new EventBuilder().withName("CS2101Exam")
            .withStartDateTime("28/04/2018 09:00")
            .withEndDateTime("28/04/2018 11:00")
            .withLocation("MPSH")
            .withRemark("nil")
            .withTags("CS2101").build();
    public static final Event IFG = new EventBuilder().withName("InterFacultyGame")
            .withStartDateTime("04/01/2018 20:00")
            .withEndDateTime("04/01/2018 22:00")
            .withLocation("MPSH 1")
            .withRemark("nil").build();

    // Manually added
    public static final Task ASSIGNMENT3 = new TaskBuilder().withName("CS2102Assignment")
            .withDateTime("01/04/2018 20:00")
            .withRemark("nil")
            .withTags("CS2102").build();
    public static final Event DEMO1 = new EventBuilder().withName("CS2102ProjectDemo")
            .withStartDateTime("04/04/2018 09:00")
            .withEndDateTime("04/04/2018 10:00")
            .withLocation("COM1")
            .withRemark("FinalDemo")
            .withTags("CS2102").build();

    public static final String KEYWORD_MATCHING_MEIER = "Meier"; // A keyword that matches MEIER

    private TypicalActivities() {} // prevents instantiation

    /**
     * Returns an {@code DeskBoard} with all the typical activities.
     */
    public static DeskBoard getTypicalDeskBoard() {
        DeskBoard deskBoard = new DeskBoard();
        for (Activity activity : getTypicalActivities()) {
            try {
                deskBoard.addActivity(activity);
            } catch (DuplicateActivityException e) {
                throw new AssertionError("Not possible");
            }
        }
        return deskBoard;
    }

    public static List<Activity> getTypicalActivities() {
        return new ArrayList<>(Arrays.asList(ASSIGNMENT1, ASSIGNMENT2, QUIZ, CCA, CIP, EXAM1, IFG));
    }

```
