# Kyomian
###### \java\seedu\address\logic\commands\ClearCommand.java
``` java
/**
 * Clears the deskboard.
 */
public class ClearCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "clear";
    public static final String COMMAND_ALIAS = "c";
    public static final String MESSAGE_SUCCESS = "Deskboard has been cleared!";
    public static final String MESSAGE_CLEAR_TASK_SUCCESS = "Tasks have been cleared!";
    public static final String MESSAGE_CLEAR_EVENT_SUCCESS = "Events have been cleared!";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Clears task panel, event panel or both task and event panel.\n"
            + "Parameters: [task/event]\n"
            + "Example: " + COMMAND_WORD + " OR "
            + COMMAND_ALIAS + " event";

    private final String activityOption;

    public ClearCommand(String activityOption) {
        this.activityOption = activityOption;
    }

    @Override
    public CommandResult executeUndoableCommand() {
        requireNonNull(model);
        if (activityOption.equals("")) {
            model.resetData(new DeskBoard());
            return new CommandResult(MESSAGE_SUCCESS);
        } else if (activityOption.equals("task")) {
            model.clearActivities("TASK");
            return new CommandResult(MESSAGE_CLEAR_TASK_SUCCESS);
        } else {
            model.clearActivities("EVENT");
            return new CommandResult(MESSAGE_CLEAR_EVENT_SUCCESS);
        }
    }
}
```
###### \java\seedu\address\logic\commands\EventCommand.java
``` java
/**
 * Adds an event to the desk board
 */
public class EventCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "event";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds an event to the desk board. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_START_DATETIME + " START DATETIME "
            + PREFIX_END_DATETIME + "END DATETIME "
            + PREFIX_LOCATION + "LOCATION "
            + "[" + PREFIX_REMARK + "REMARK]"
            + "[" + PREFIX_TAG + "TAG]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "Software Project "
            + PREFIX_START_DATETIME + "01/05/2018 08:00 "
            + PREFIX_END_DATETIME + "01/08/2018 08:00 "
            + PREFIX_LOCATION + "School of Computing "
            + PREFIX_REMARK + "Bring laptop charger "
            + PREFIX_TAG + "Important";

    public static final String MESSAGE_SUCCESS = "New event added: %1$s";
    public static final String MESSAGE_DUPLICATE_EVENT = "This event already exists in the desk board";

    private final Event toAdd;

    /**
     * Creates a EventCommand to add the specified {@code Event}
     */
    public EventCommand(Event event) {
        requireNonNull(event);
        toAdd = event;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);
        try {
            model.addActivity(toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
        } catch (DuplicateActivityException e) {
            throw new CommandException(MESSAGE_DUPLICATE_EVENT);
        }

    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof EventCommand // instanceof handles nulls
                && toAdd.equals(((EventCommand) other).toAdd));
    }
}
```
###### \java\seedu\address\logic\commands\OverdueCommand.java
``` java
/**
 * Lists all overdue tasks
 */
public class OverdueCommand extends Command {

    public static final String COMMAND_WORD = "overdue";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows a list of tasks that remain uncompleted after their respective due dates.";

    public static final String SHOWN_OVERDUE_MESSAGE = "Number of overdue tasks: %d";

    @Override
    public CommandResult execute() {
        int numOverdueTasks = OverdueChecker.getNumOverdueTasks();
        return new CommandResult(String.format(SHOWN_OVERDUE_MESSAGE, numOverdueTasks));
    }


}
```
###### \java\seedu\address\logic\commands\RemoveCommand.java
``` java
/**
 * Removes an activity based on its last displayed index in the desk board.
 */
public class RemoveCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "remove";
    public static final String COMMAND_ALIAS = "rm";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes task/event identified by the index number in the last displayed task/event listing.\n"
            + "Parameters: task/event INDEX (INDEX must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " task 1" + " OR "
            + COMMAND_ALIAS + " event 1";

    public static final String MESSAGE_REMOVE_TASK_SUCCESS = "Removed task: %1$s";
    public static final String MESSAGE_REMOVE_EVENT_SUCCESS = "Removed event: %1$s";

    private final Index targetIndex;
    private Activity activityToDelete;
    private final String activityOption;

    public RemoveCommand(String activityOption, Index targetIndex) {
        this.activityOption = activityOption;
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult executeUndoableCommand() {
        requireNonNull(activityToDelete);
        try {
            model.deleteActivity(activityToDelete);
        } catch (ActivityNotFoundException pnfe) {
            throw new AssertionError("The target activity cannot be missing");
        }

        if (activityOption.equals("task")) {
            return new CommandResult(String.format(MESSAGE_REMOVE_TASK_SUCCESS, activityToDelete));
        } else {
            return new CommandResult(String.format(MESSAGE_REMOVE_EVENT_SUCCESS, activityToDelete));
        }
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {

        if (activityOption.equals("task")) {
            List<Activity> lastShownTaskList = model.getFilteredTaskList();
            if (targetIndex.getZeroBased() >= lastShownTaskList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_ACTIVITY_DISPLAYED_INDEX);
            }
            activityToDelete = lastShownTaskList.get(targetIndex.getZeroBased());
        } else if (activityOption.equals("event")) {
            List<Activity> lastShownEventList = model.getFilteredEventList();
            if (targetIndex.getZeroBased() >= lastShownEventList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_ACTIVITY_DISPLAYED_INDEX);
            }
            activityToDelete = lastShownEventList.get(targetIndex.getZeroBased());
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RemoveCommand // instanceof handles nulls
                && this.targetIndex.equals(((RemoveCommand) other).targetIndex) // state check
                && Objects.equals(this.activityToDelete, ((RemoveCommand) other).activityToDelete));
    }
}
```
###### \java\seedu\address\logic\commands\TaskCommand.java
``` java
/**
 * Adds a task to the desk board.
 */
public class TaskCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "task";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a task to the desk board. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_DATE_TIME + "DATETIME "
            + "[" + PREFIX_REMARK + "REMARK] "
            + "[" + PREFIX_TAG + "TAG]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "Software Engineering Milestone 1 "
            + PREFIX_DATE_TIME + "01/08/2018 17:00 "
            + PREFIX_REMARK + "Enhance major component "
            + PREFIX_TAG + "CS2103T";

    public static final String MESSAGE_SUCCESS = "New task added: %1$s";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists in the desk board";

    private final Task toAdd;

    /**
     * Creates a TaskCommand to add the specified {@code Task}
     */
    public TaskCommand(Task task) {
        requireNonNull(task);
        toAdd = task;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);
        try {
            model.addActivity(toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
        } catch (DuplicateActivityException e) {
            throw new CommandException(MESSAGE_DUPLICATE_TASK);
        }

    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TaskCommand // instanceof handles nulls
                && toAdd.equals(((TaskCommand) other).toAdd));
    }
}
```
###### \java\seedu\address\logic\commands\util\OverdueCheckerUtil.java
``` java
/**
 * A utility class that tags task as overdue and event as finished.
 */
public class OverdueCheckerUtil {

    /**
     * Tags task as overdue.
     */
    public static void markAsOverdue(Task task, Model model)
            throws ActivityNotFoundException, DuplicateActivityException {
        Task newTask = computeNewTask(task);
        model.updateActivity(task, newTask);
    }

    /**
     * Tags event as finished.
     */
    public static void markAsFinished(Event event, Model model)
            throws ActivityNotFoundException, DuplicateActivityException {
        Event newEvent = computeNewEvent(event);
        model.updateActivity(event, newEvent);
    }

    /**
     * {@code Private} method that creates a new task with "Overdue" tag.
     */
    private static Task computeNewTask(Task task) {
        Name name = task.getName();
        DateTime dateTime = task.getDueDateTime();
        Remark remark = task.getRemark();
        Boolean isCompleted = task.isCompleted();

        HashSet<Tag> tags = new HashSet<>(task.getTags()); // copy constructor
        tags.add(new Tag("Overdue"));

        return new Task(name, dateTime, remark, tags, isCompleted);
    }

    /**
     * {@code Private} method that creates a new event with "Finished" tag.
     */
    private static Event computeNewEvent(Event event) {
        Name name = event.getName();
        DateTime startDateTime = event.getStartDateTime();
        DateTime endDateTime = event.getEndDateTime();
        Location location = event.getLocation();
        Remark remark = event.getRemark();
        Boolean isCompleted = event.isCompleted();

        HashSet<Tag> tags = new HashSet<>(event.getTags());
        tags.add(new Tag("Finished"));

        return new Event(name, startDateTime, endDateTime, location, remark, tags, isCompleted);
    }
}
```
###### \java\seedu\address\logic\DateTimeScheduler.java
``` java
/**
 * A scheduler that runs every 30 seconds to check if tasks and events have passed their
 * due dates and end dates respectively.
 */
public class DateTimeScheduler {

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    /**
     * Initialises the scheduler.
     */
    public static void initialise(Model model) {
        OverdueChecker checker = new OverdueChecker(model);
        executor.scheduleWithFixedDelay(checker, 0, 30, TimeUnit.SECONDS);
    }
}
```
###### \java\seedu\address\logic\OverdueChecker.java
``` java
/**
 * A class that checks if tasks and events have passed their due dates and end dates
 * respectively.
 *
 * A task that has passed its due date is tagged as overdue.
 * A completed task will not be tagged as overdue, even though it has passed its due date.
 * An event that has passed its end date is tagged as finished.
 *
 * This class also records the number of tasks tagged "Overdue".
 */
public class OverdueChecker implements Runnable {

    private static int numOverdueTasks;
    private Model model;
    private ObservableList<Activity> taskList;
    private ObservableList<Activity> eventList;

    // Constructor
    public OverdueChecker(Model model) {
        this.model = model;
        taskList = model.getFilteredTaskList();
        eventList = model.getFilteredEventList();
    }

    /**
     * The run method of OverdueChecker runnable
     */
    public void run() {
        numOverdueTasks = 0;
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Platform.runLater is needed as you cannot update UI components
        // from a thread other than the JavaFx Application thread
        Platform.runLater(() -> {
            try {
                markingAsOverdue(taskList, currentDateTime);
                markingAsFinished(eventList, currentDateTime);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace(); // impossible to reach here
            } catch (DuplicateActivityException e) {
                e.printStackTrace(); // impossible to reach here
            }
        });
    }

    /**
     * Task that has passed its due date and is not yet completed
     * is tagged as overdue.
     */
    private void markingAsOverdue(ObservableList<Activity> taskList, LocalDateTime currentDateTime)
            throws ActivityNotFoundException, DuplicateActivityException {
        for (int i = 0; i < taskList.size(); i++) {
            Task task = (Task) taskList.get(i);
            if (task.getDueDateTime().getLocalDateTime().isBefore(currentDateTime)
                    && !task.isCompleted()) {
                markAsOverdue(task, model);
                numOverdueTasks++;
            }
        }
    }

    /**
     * Event that has passed its end date is tagged automatically as finished.
     */
    private void markingAsFinished(ObservableList<Activity> eventList, LocalDateTime currentDateTime)
            throws ActivityNotFoundException, DuplicateActivityException {
        for (int i = 0; i < eventList.size(); i++) {
            Event event = (Event) eventList.get(i);
            if (event.getEndDateTime().getLocalDateTime().isBefore(currentDateTime)) {
                markAsFinished(event, model);
            }
        }
    }

    public static int getNumOverdueTasks() {
        return numOverdueTasks;
    }
}
```
###### \java\seedu\address\logic\parser\ClearCommandParser.java
``` java
/**
 * Parses input arguments and creates a new ClearCommand object.
 */
public class ClearCommandParser implements Parser<ClearCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ClearCommand
     * and returns a ClearCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ClearCommand parse(String args) throws ParseException {

        args = args.trim();

        if (!isValidActivityOption(args)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
        }

        return new ClearCommand(args);
    }

    private static boolean isValidActivityOption(String activityOption) {
        return activityOption.equals("") || activityOption.equals("task") || activityOption.equals("event");
    }
}
```
###### \java\seedu\address\logic\parser\CliSyntax.java
``` java
/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_DATE_TIME = new Prefix("d/");
    public static final Prefix PREFIX_FILE_PATH = new Prefix("f/");
    public static final Prefix PREFIX_REMARK = new Prefix("r/");
    public static final Prefix PREFIX_TAG = new Prefix("t/");
    public static final Prefix PREFIX_START_DATETIME = new Prefix("s/");
    public static final Prefix PREFIX_END_DATETIME = new Prefix("e/");
    public static final Prefix PREFIX_LOCATION = new Prefix("l/");

    // To be deleted soon
    public static final Prefix PREFIX_ADDRESS = new Prefix("a/");
    public static final Prefix PREFIX_EMAIL = new Prefix("em/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
}
```
###### \java\seedu\address\logic\parser\DeskBoardParser.java
``` java
/**
 * Parses user input.
 */
public class DeskBoardParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

        case TaskCommand.COMMAND_WORD:
            return new TaskCommandParser().parse(arguments);

        case CompleteCommand.COMMAND_WORD:
            return new CompleteCommandParser().parse(arguments);

        case EventCommand.COMMAND_WORD:
            return new EventCommandParser().parse(arguments);

        case EditCommand.COMMAND_WORD:
            return new EditCommandParser().parse(arguments);

        case RemoveCommand.COMMAND_WORD:
            return new RemoveCommandParser().parse(arguments);

        case RemoveCommand.COMMAND_ALIAS:
            return new RemoveCommandParser().parse(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommandParser().parse(arguments);

        case ClearCommand.COMMAND_ALIAS:
            return new ClearCommandParser().parse(arguments);

        case FindCommand.COMMAND_WORD:
            return new FindCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommandParser().parse(arguments);

        case ListCommand.COMMAND_ALIAS:
            return new ListCommandParser().parse(arguments);

        case HistoryCommand.COMMAND_WORD:
            return new HistoryCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommandParser().parse(arguments);

        case HelpCommand.COMMAND_ALIAS:
            return new HelpCommandParser().parse(arguments);

        case OverdueCommand.COMMAND_WORD:
            return new OverdueCommand();

        case UndoCommand.COMMAND_WORD:
            return new UndoCommand();

        case UndoCommand.COMMAND_ALIAS:
            return new UndoCommand();

        case RedoCommand.COMMAND_WORD:
            return new RedoCommand();

        case RedoCommand.COMMAND_ALIAS:
            return new RedoCommand();

        case SelectCommand.COMMAND_WORD:
            return new SelectCommandParser().parse(arguments);

        case ImportCommand.COMMAND_WORD:
            return new ImportCommandParser().parse(arguments);

        case ExportCommand.COMMAND_WORD:
            return new ExportCommandParser().parse(arguments);

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

}
```
###### \java\seedu\address\logic\parser\EventCommandParser.java
``` java
/**
 * Parses input arguments and creates a new EventCommand object
 */
public class EventCommandParser implements Parser<EventCommand> {

    public static final String MESSAGE_INVALID_TIME_RANGE = "Start Datetime cannot be after end Datetime";

    /**
     * Parses the given {@code String} of arguments in the context of the EventCommand
     * and returns a EventCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EventCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_START_DATETIME, PREFIX_END_DATETIME,
                        PREFIX_LOCATION, PREFIX_REMARK, PREFIX_TAG);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_START_DATETIME, PREFIX_END_DATETIME)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EventCommand.MESSAGE_USAGE));
        }

        try {
            Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME)).get();
            DateTime startDateTime = ParserUtil.parseDateTime(argMultimap.getValue(PREFIX_START_DATETIME)).get();
            DateTime endDateTime = ParserUtil.parseDateTime(argMultimap.getValue(PREFIX_END_DATETIME)).get();
            Optional<Location> location = ParserUtil.parseLocation(argMultimap.getValue(PREFIX_LOCATION));
            Optional<Remark> remark = ParserUtil.parseRemark(argMultimap.getValue(PREFIX_REMARK));
            Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

            isTimeRangeValid(startDateTime, endDateTime);

            Event event = new Event(name, startDateTime, endDateTime,
                    location.isPresent() ? location.get() : null,
                    remark.isPresent() ? remark.get() : null, tagList);

            return new EventCommand(event);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Throws {@code IllegalValueException} if start datetime is after end datetime.
     */
    private static void isTimeRangeValid(DateTime startDateTime, DateTime endDateTime) throws IllegalValueException {
        if (startDateTime.getLocalDateTime().isAfter(endDateTime.getLocalDateTime())) {
            throw new IllegalValueException(MESSAGE_INVALID_TIME_RANGE);
        }
    }
}
```
###### \java\seedu\address\logic\parser\ParserUtil.java
``` java
/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 * {@code ParserUtil} contains methods that take in {@code Optional} as parameters. However, it goes against Java's
 * convention (see https://stackoverflow.com/a/39005452) as {@code Optional} should only be used a return type.
 * Justification: The methods in concern receive {@code Optional} return values from other methods as parameters and
 * return {@code Optional} values based on whether the parameters were present. Therefore, it is redundant to unwrap the
 * initial {@code Optional} before passing to {@code ParserUtil} as a parameter and then re-wrap it into an
 * {@code Optional} return value inside {@code ParserUtil} methods.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";
    public static final String MESSAGE_INSUFFICIENT_PARTS = "Number of parts must be more than 1.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws IllegalValueException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws IllegalValueException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new IllegalValueException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws IllegalValueException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new IllegalValueException(Name.MESSAGE_NAME_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code Optional<String> name} into an {@code Optional<Name>} if {@code name} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Name> parseName(Optional<String> name) throws IllegalValueException {
        requireNonNull(name);
        return name.isPresent() ? Optional.of(parseName(name.get())) : Optional.empty();
    }

    /**
     * Parses a {@code String datetime} into a {@code DateTime}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code datetime} is invalid.
     */

    public static DateTime parseDateTime(String datetime) throws IllegalValueException {
        requireNonNull(datetime);
        String trimmedDateTime = datetime.trim();
        if (!DateTime.isValidDateTime(trimmedDateTime)) {
            throw new IllegalValueException(DateTime.MESSAGE_DATETIME_CONSTRAINTS);
        }
        return new DateTime(trimmedDateTime);
    }

    /**
     * Parses a {@code Optional<String> name} into an {@code Optional<DateTime>} if {@code datetime} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<DateTime> parseDateTime(Optional<String> datetime) throws IllegalValueException {
        requireNonNull(datetime);
        return datetime.isPresent() ? Optional.of(parseDateTime(datetime.get())) : Optional.empty();
    }


    /**
     * Parses a {@code String location} into a {@code Location}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code location} is invalid.
     */

    public static Location parseLocation(String location) throws IllegalValueException {
        requireNonNull(location);
        String trimmedLocation = location.trim();
        if (!Location.isValidLocation(trimmedLocation)) {
            throw new IllegalValueException(Location.MESSAGE_LOCATION_CONSTRAINTS);
        }
        return new Location(trimmedLocation);
    }

    /**
     * Parses a {@code Optional<String> location} into an {@code Optional<Location>} if {@code location} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Location> parseLocation(Optional<String> location) throws IllegalValueException {
        requireNonNull(location);
        return location.isPresent() ? Optional.of(parseLocation(location.get())) : Optional.empty();
    }

    /**
     * Parses a {@code String remark} into a {@code Remark}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code remark} is invalid.
     */
    public static Remark parseRemark(String remark) throws IllegalValueException {
        requireNonNull(remark);
        String trimmedRemark = remark.trim();
        if (!Remark.isValidRemark(trimmedRemark)) {
            throw new IllegalValueException(Remark.MESSAGE_REMARK_CONSTRAINTS);
        }
        return new Remark(trimmedRemark);
    }

    /**
     * Parses a {@code Optional<String> remark} into an {@code Optional<Remark>} if {@code remark} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Remark> parseRemark(Optional<String> remark) throws IllegalValueException {
        requireNonNull(remark);
        return remark.isPresent() ? Optional.of(parseRemark(remark.get())) : Optional.empty();
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws IllegalValueException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new IllegalValueException(Tag.MESSAGE_TAG_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws IllegalValueException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

```
###### \java\seedu\address\logic\parser\RemoveCommandParser.java
``` java
/**
 * Parses input arguments and creates a new RemoveCommand object
 */
public class RemoveCommandParser implements Parser<RemoveCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the RemoveCommand
     * and returns a RemoveCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public RemoveCommand parse(String args) throws ParseException {
        String[] argsParts = args.trim().split(" ");

        if (argsParts.length != 2 || !isValidActivityOption(argsParts[0])) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveCommand.MESSAGE_USAGE));
        }

        try {
            String activityOption = argsParts[0];
            Index index = ParserUtil.parseIndex(argsParts[1]);
            return new RemoveCommand(activityOption, index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveCommand.MESSAGE_USAGE));
        }
    }

    private static boolean isValidActivityOption(String activityOption) {
        return activityOption.equals("task") || activityOption.equals("event");
    }

}
```
###### \java\seedu\address\logic\parser\TaskCommandParser.java
``` java
/**
 * Parses input arguments and creates a new TaskCommand object
 */

public class TaskCommandParser implements Parser<TaskCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the TaskCommand
     * and returns a TaskCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public TaskCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_DATE_TIME, PREFIX_REMARK, PREFIX_TAG);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_DATE_TIME)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, TaskCommand.MESSAGE_USAGE));
        }

        try {
            Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME)).get();
            DateTime datetime = ParserUtil.parseDateTime(argMultimap.getValue(PREFIX_DATE_TIME)).get();
            Optional<Remark> optionalRemark = ParserUtil.parseRemark(argMultimap.getValue(PREFIX_REMARK));
            Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
            Task task;
            if (optionalRemark.isPresent()) {
                task = new Task(name, datetime, optionalRemark.get(), tagList);
            } else {
                task = new Task(name, datetime, null, tagList);
            }

            return new TaskCommand(task);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
```
###### \java\seedu\address\model\activity\DateTime.java
``` java
/**
 * Represents an Activity's datetime in the desk board.
 * Guarantees: immutable; is valid as declared in {@link #isValidDateTime(String)}
 */
public class DateTime {

    public static final String DEFAULT_DATETIME_FORMAT = "d/M/y H:m";
    public static final String MESSAGE_DATETIME_CONSTRAINTS =
            "Date and Time should be in the format of " + DEFAULT_DATETIME_FORMAT;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);

    private final LocalDateTime dateTime;
    private final String dateTimeString;

    /**
     * Constructs a {@code DateTime}.
     *
     * @param value A valid datetime.
     */
    public DateTime(String value) {
        requireNonNull(value);
        checkArgument(isValidDateTime(value), MESSAGE_DATETIME_CONSTRAINTS);
        this.dateTime = LocalDateTime.parse(value, formatter);
        this.dateTimeString = value;
    }

    /**
     * Returns true if a given string is a valid datetime.
     */
    public static boolean isValidDateTime(String value) {
        try {
            LocalDateTime.parse(value, formatter);
            return true;
        } catch (DateTimeParseException dtpe) {
            return false;
        }
    }

    public LocalDateTime getLocalDateTime() {
        return this.dateTime;
    }

    @Override
    public String toString() {
        return this.dateTimeString;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DateTime // instanceof handles nulls
                && this.dateTime.equals(((DateTime) other).dateTime)); // state check
    }

    @Override
    public int hashCode() {
        return dateTime.hashCode();
    }

}
```
###### \java\seedu\address\model\activity\Task.java
``` java
    @Override
    /**
     * Gets a completely copy of task.
     * Removes the overdue tag, if any.
     */
    public Activity getCompletedCopy() {
        HashSet<Tag> tags = new HashSet<>(getTags()); // copy constructor
        tags.remove(new Tag("Overdue"));
        return new Task(getName(), getDueDateTime(), getRemark(), tags, true);
    }
}
```
###### \java\seedu\address\model\activity\UniqueActivityList.java
``` java
    /**
     * Clears either all tasks in deskboard or all events.
     * @param activityTypeToClear
     */
    public void clear(String activityTypeToClear) {
        internalList.removeIf(activity ->
            activity.getActivityType().equals(activityTypeToClear)
        );
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Activity> internalListAsObservable() {
        return FXCollections.unmodifiableObservableList(internalList);
    }



    @Override
    public Iterator<Activity> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueActivityList // instanceof handles nulls
                        && this.internalList.equals(((UniqueActivityList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }
}
```
###### \java\seedu\address\model\DeskBoard.java
``` java
    /**
     * Removes {@code key} from this {@code DeskBoard}.
     * @throws ActivityNotFoundException if the {@code key} is not in this {@code DeskBoard}.
     */
    public boolean removeActivity(Activity key) throws ActivityNotFoundException {
        if (activities.remove(key)) {
            return true;
        } else {
            throw new ActivityNotFoundException();
        }
    }

    public void clearActivities(String activityTypeToClear) {
        activities.clear(activityTypeToClear);
    }

    //// tag-level operations

```
###### \java\seedu\address\model\Model.java
``` java
    /** Clear all tasks or all events */
    void clearActivities(String activityTypeToClear);

    /** Adds the given activity */
    void addActivity(Activity activity) throws DuplicateActivityException;

    void addActivities(ReadOnlyDeskBoard deskBoard);

    /**
     * Replaces the given activity {@code target} with {@code editedActivity}.
     *
     * @throws DuplicateActivityException if updating the activity's details causes the activity to be equivalent to
     *      another existing activity in the list.
     * @throws ActivityNotFoundException if {@code target} could not be found in the list.
     */
    void updateActivity(Activity target, Activity editedActivity)
            throws DuplicateActivityException, ActivityNotFoundException;

    /** Returns an unmodifiable view of the filtered activity list */
    ObservableList<Activity> getFilteredActivityList();

```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public synchronized void clearActivities(String activityTypeToClear) {
        deskBoard.clearActivities(activityTypeToClear);
        indicateDeskBoardChanged();
    }

```
###### \java\seedu\address\ui\util\DateTimeUtil.java
``` java
/**
 * Formats DateTime for display in UI
 * Example: 01/08/2018 08:00 is displayed as 1 Aug 2018 08:00 in the UI
 */
public class DateTimeUtil {

    private static final String DISPLAYED_DATETIME_FORMAT = "d MMM y HH:mm";
    private static final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern(DISPLAYED_DATETIME_FORMAT);

    /**
     * Formats DateTime of task as day, name of month, year, hours and minutes
     */
    public static String getDisplayedDateTime(Task task) {
        DateTime dateTime = task.getDateTime();
        String displayedDateTime = displayFormatter.format(dateTime.getLocalDateTime());
        return displayedDateTime;
    }

    /**
     * Formats StartDateTime of event as day, name of month, year, hours and minutes
     */
    public static String getDisplayedStartDateTime(Event event) {
        DateTime dateTime = event.getStartDateTime();
        String displayedDateTime = displayFormatter.format(dateTime.getLocalDateTime());
        return displayedDateTime;
    }

    /**
     * Formats EndDateTime of event as day, name of month, year, hours and minutes
     */
    public static String getDisplayedEndDateTime(Event event) {
        DateTime dateTime = event.getEndDateTime();
        String displayedDateTime = displayFormatter.format(dateTime.getLocalDateTime());
        return displayedDateTime;
    }
}
```
