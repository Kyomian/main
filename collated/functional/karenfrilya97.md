# karenfrilya97
###### \java\seedu\address\logic\commands\Command.java
``` java
    /**
     * Provides any needed dependencies to the command.
     */
    public void setData(Model model, Storage storage, CommandHistory history, UndoRedoStack undoRedoStack) {
        this.setData(model, history, undoRedoStack);
        this.storage = storage;
    }
}
```
###### \java\seedu\address\logic\commands\ExportCommand.java
``` java
/**
 * Exports Desk Board data into an xml file in desired directory.
 */
public class ExportCommand extends Command {

    public static final String COMMAND_WORD = "export";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Exports desk board data into an xml file in the specified directory. "
            + "Parameters: "
            + PREFIX_FILE_PATH + "FILE_PATH\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_FILE_PATH + "C:\\data\\deskBoard.xml";

    public static final String MESSAGE_SUCCESS = "Data exported to: %1$s";
    public static final String MESSAGE_FILE_EXISTS = "File %s already exists";

    private final FilePath filePath;

    /**
     * Creates an ExportCommandParser to import data from the specified {@code filePath}.
     */
    public ExportCommand(FilePath filePath) {
        requireNonNull(filePath);
        this.filePath = filePath;
    }

    @Override
    public CommandResult execute() throws CommandException {
        requireNonNull(model);
        requireNonNull(storage);

        if (FileUtil.isFileExists(new File(filePath.value))) {
            throw new CommandException(String.format(MESSAGE_FILE_EXISTS, filePath.value));
        }

        try {
            ReadOnlyDeskBoard deskBoard = model.getDeskBoard();
            storage.saveDeskBoard(deskBoard, filePath.value);
            return new CommandResult(String.format(MESSAGE_SUCCESS, filePath.value));
        } catch (IOException ioe) {
            throw new CommandException(ioe.getMessage());
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ExportCommand // instanceof handles nulls
                && this.filePath.equals(((ExportCommand) other).filePath)); // state check
    }
}
```
###### \java\seedu\address\logic\commands\ImportCommand.java
``` java
/**
 * Imports desk board data from a given xml file.
 */
public class ImportCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "import";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Imports desk board data from a given xml file. "
            + "Parameters: "
            + PREFIX_FILE_PATH + "FILE PATH\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_FILE_PATH + "C:\\data\\deskBoard.xml";

    public static final String MESSAGE_SUCCESS = "Data imported from: %1$s";
    public static final String MESSAGE_FILE_NOT_FOUND = "Desk board file %s not found";
    public static final String MESSAGE_ILLEGAL_VALUES_IN_FILE = "Illegal values found in file: %s";

    private final FilePath filePath;

    /**
     * Creates an ImportCommand to import data from the specified {@code filePath}.
     */
    public ImportCommand(FilePath filePath) {
        requireNonNull(filePath);
        this.filePath = filePath;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);
        requireNonNull(storage);

        try {
            ReadOnlyDeskBoard toImport = storage.readDeskBoard(filePath.value)
                    .orElseThrow(() -> new CommandException(String.format(MESSAGE_FILE_NOT_FOUND, filePath)));

            model.addActivities(toImport);
            return new CommandResult(String.format(MESSAGE_SUCCESS, filePath));
        } catch (IOException ioe) {
            throw new CommandException(String.format(MESSAGE_FILE_NOT_FOUND, filePath));
        } catch (DataConversionException dce) {
            throw new CommandException(String.format(MESSAGE_ILLEGAL_VALUES_IN_FILE, filePath.value));
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ImportCommand // instanceof handles nulls
                && filePath.equals(((ImportCommand) other).filePath));
    }
}
```
###### \java\seedu\address\logic\parser\ExportCommandParser.java
``` java
/**
 * Parses input arguments and creates a new ExportCommand object
 */
public class ExportCommandParser implements Parser<ExportCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ExportCommand
     * and returns an ExportCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format.
     */
    public ExportCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_FILE_PATH);

        if (!arePrefixesPresent(argMultimap, PREFIX_FILE_PATH)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
        }

        try {
            FilePath filePath = ParserUtil.parseFilePath(argMultimap.getValue(PREFIX_FILE_PATH)).get();
            return new ExportCommand(filePath);
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
###### \java\seedu\address\logic\parser\ImportCommandParser.java
``` java
/**
 * Parses input arguments and creates a new ImportCommand object
 */
public class ImportCommandParser implements Parser<ImportCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ImportCommand
     * and returns an ImportCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format.
     */
    public ImportCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_FILE_PATH);

        if (!arePrefixesPresent(argMultimap, PREFIX_FILE_PATH)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE));
        }

        try {
            FilePath filePath = ParserUtil.parseFilePath(argMultimap.getValue(PREFIX_FILE_PATH)).get();
            return new ImportCommand(filePath);
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
###### \java\seedu\address\logic\parser\ParserUtil.java
``` java
    /**
     * Parses a {@code String filePath} into a {@code FilePath}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code filePath} is invalid.
     */
    public static FilePath parseFilePath(String filePath) throws IllegalValueException {
        requireNonNull(filePath);
        String trimmedFilePath = filePath.trim();
        if (!FilePath.isValidFilePath(trimmedFilePath)) {
            throw new IllegalValueException(FilePath.MESSAGE_FILE_PATH_CONSTRAINTS);
        }
        return new FilePath(trimmedFilePath);
    }

    /**
     * Parses an {@code Optional<String> filePath} into an {@code Optional<FilePath>} if {@code filePath} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<FilePath> parseFilePath(Optional<String> filePath) throws IllegalValueException {
        requireNonNull(filePath);
        return filePath.isPresent() ? Optional.of(parseFilePath(filePath.get())) : Optional.empty();
    }
}
```
###### \java\seedu\address\model\activity\UniqueActivityList.java
``` java
        Collections.sort(internalList, dateTimeComparator);
    }

    /**
     * Removes the equivalent activity from the list and its respective task or event list.
     *
     * @throws ActivityNotFoundException if no such activity could be found in the list.
     */
    public boolean remove(Activity toRemove) throws ActivityNotFoundException {
        requireNonNull(toRemove);
        final boolean activityFoundAndDeleted = internalList.remove(toRemove);
        if (!activityFoundAndDeleted) {
            throw new ActivityNotFoundException();
        } else  {
            internalList.remove(toRemove);
        }
        return activityFoundAndDeleted;
    }

```
###### \java\seedu\address\model\DeskBoard.java
``` java
    /**
     * Adds all activities from UniqueActivityList {@code activities} to the desk board.
     * Also checks each new activity's tags and updates {@link #tags} with any new tags found,
     * and updates the Tag objects in the activity to point to those in {@link #tags}.
     *
     * If an equivalent activity already exists, that activity will be ignored.
     */
    public void addActivities(List<Activity> toAdd) {
        for (Activity activity : toAdd) {
            activity = syncWithMasterTagList(activity);
            try {
                activities.add(activity);
            } catch (DuplicateActivityException e) {
                // Ignore duplicate activity.
            }
        }
    }

```
###### \java\seedu\address\model\FilePath.java
``` java
/**
 * Represents a Desk Board's file path.
 * Guarantees: immutable; is valid as declared in {@link #isValidFilePath(String)}
 */
public class FilePath {

    public static final String MESSAGE_FILE_PATH_CONSTRAINTS =
            "Desk Board file path should end with '.xml'.";

    public static final String FILE_PATH_VALIDATION_REGEX = ".+" + ".xml";

    public final String value;

    /**
     * Constructs a {@code FilePath}.
     *
     * @param filePath A valid file path.
     */
    public FilePath (String filePath) {
        requireNonNull(filePath);
        checkArgument(isValidFilePath(filePath), MESSAGE_FILE_PATH_CONSTRAINTS);
        this.value = filePath;
    }

    /**
     * Returns true if a given string is a valid Desk Board file path.
     */
    public static boolean isValidFilePath(String filePath) {
        return filePath.matches(FILE_PATH_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FilePath // instanceof handles nulls
                && this.value.equals(((FilePath) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
```
###### \java\seedu\address\model\ModelManager.java
``` java
    /** Adds all activities in the given desk board, except for those already found in the existing desk board*/
    public synchronized void addActivities(ReadOnlyDeskBoard otherDeskBoard) {
        deskBoard.addActivities(otherDeskBoard.getActivityList());
        updateFilteredActivityList(PREDICATE_SHOW_ALL_ACTIVITY);
        indicateDeskBoardChanged();
    }

```
###### \java\seedu\address\model\util\DateTimeComparator.java
``` java
/**
 * Comparator class that compares Activity based on dateTime attributes.
 * For Event objects, this class only compares based on the startDateTime,
 * and does not take into account the endDateTime.
 */
public class DateTimeComparator implements Comparator<Activity> {

    public DateTimeComparator () {
    }

    /**
     * Compares two activities
     * @param o1 and
     * @param o2,
     * @return a negative integer, zero, or a positive integer
     * if the first activity's dateTime is earlier than, equal to or later than
     * the second activity's dateTime respectively.
     */
    public int compare(Activity o1, Activity o2) {
        DateTime dt1 = o1.getDateTime();
        DateTime dt2 = o2.getDateTime();
        return dt1.getLocalDateTime().compareTo(dt2.getLocalDateTime());
    }
}
```
###### \java\seedu\address\storage\XmlAdaptedActivity.java
``` java
/**
 * JAXB-friendly version of the Activity.
 */
public abstract class XmlAdaptedActivity {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "%s's %s field is missing!";

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String dateTime;
    @XmlElement
    protected String remark;
    @XmlElement
    protected boolean isCompleted;
    @XmlElement
    protected List<XmlAdaptedTag> tagged = new ArrayList<>();

    /**
     * Constructs an XmlAdaptedActivity.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedActivity() {}

    /**
     * Constructs an {@code XmlAdaptedActivity} with the given activity details.
     */
    public XmlAdaptedActivity(String name, String dateTime, String remark, List<XmlAdaptedTag> tagged) {
        this.name = name;
        this.dateTime = dateTime;
        this.remark = remark;
        if (tagged != null) {
            this.tagged = new ArrayList<>(tagged);
        }
        isCompleted = false;
    }

    /**
     * Converts a given Activity into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedActivity
     */
    public XmlAdaptedActivity(Activity source) {
        name = source.getName().fullName;
        dateTime = source.getDateTime().toString();
        if (source.getRemark() != null) {
            remark = source.getRemark().value;
        }
        tagged = new ArrayList<>();
        for (Tag tag : source.getTags()) {
            tagged.add(new XmlAdaptedTag(tag));
        }
        isCompleted = source.isCompleted();
    }

    /**
     * Converts this jaxb-friendly adapted activity object into the model's Activity object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted activity
     */
    public abstract Activity toModelType() throws IllegalValueException;

    public abstract String getActivityType();

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof XmlAdaptedActivity)) {
            return false;
        }

        XmlAdaptedActivity otherActivity = (XmlAdaptedActivity) other;
        return Objects.equals(name, otherActivity.name)
                && Objects.equals(dateTime, otherActivity.dateTime)
                && Objects.equals(remark, otherActivity.remark)
                && tagged.equals(otherActivity.tagged)
                && this.isCompleted == otherActivity.isCompleted;
    }
}
```
###### \java\seedu\address\storage\XmlAdaptedEvent.java
``` java
/**
 * JAXB-friendly version of the Event.
 */
public class XmlAdaptedEvent extends XmlAdaptedActivity {

    private static final String ACTIVITY_TYPE = "Event";

    @XmlElement(required = true)
    private String endDateTime;
    @XmlElement(required = true)
    private String location;

    /**
     * Constructs an XmlAdaptedEvent.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedEvent() {}

    /**
     * Constructs an {@code XmlAdaptedEvent} with the given event details.
     */
    public XmlAdaptedEvent(String name, String startDateTime, String endDateTime,
                           String location, String remark, List<XmlAdaptedTag> tagged) {
        super(name, startDateTime, remark, tagged);
        this.endDateTime = endDateTime;
        this.location = location;
    }

    /**
     * Converts a given Event into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedEvent
     */
    public XmlAdaptedEvent(Event source) {
        super((Activity) source);
        endDateTime = source.getEndDateTime().toString();
        if (source.getLocation() != null) {
            location = source.getLocation().toString();
        }
    }

    /**
     * Converts this jaxb-friendly adapted Event object into the model's Event object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted ectivity
     */
    @Override
    public Event toModelType() throws IllegalValueException {
        final List<Tag> activityTags = new ArrayList<>();
        for (XmlAdaptedTag tag : tagged) {
            activityTags.add(tag.toModelType());
        }

        if (this.name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    getActivityType(), "name"));
        }
        if (!Name.isValidName(this.name)) {
            throw new IllegalValueException(Name.MESSAGE_NAME_CONSTRAINTS);
        }
        final Name name = new Name(this.name);

        if (this.dateTime == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    getActivityType(), "start date/time"));
        }
        if (!DateTime.isValidDateTime(this.dateTime)) {
            throw new IllegalValueException(DateTime.MESSAGE_DATETIME_CONSTRAINTS);
        }
        final DateTime startDateTime = new DateTime(this.dateTime);

        if (this.endDateTime == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    getActivityType(), "end date/time"));
        }
        if (!DateTime.isValidDateTime(this.endDateTime)) {
            throw new IllegalValueException(DateTime.MESSAGE_DATETIME_CONSTRAINTS);
        }
        final DateTime endDateTime = new DateTime(this.endDateTime);
        final Location location;
        if (this.location == null) {
            location = null;
        } else if (!Location.isValidLocation(this.location)) {
            throw new IllegalValueException(Location.MESSAGE_LOCATION_CONSTRAINTS);
        } else {
            location = new Location(this.location);
        }

        final Remark remark;
        if (this.remark == null) {
            remark = null;
        } else if (!Remark.isValidRemark(this.remark)) {
            throw new IllegalValueException(Remark.MESSAGE_REMARK_CONSTRAINTS);
        } else {
            remark = new Remark(this.remark);
        }

        final Set<Tag> tags = new HashSet<>(activityTags);
        return new Event(name, startDateTime, endDateTime, location, remark, tags, this.isCompleted);
    }

    @Override
    public String getActivityType() {
        return ACTIVITY_TYPE;
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }

        if (!(other instanceof XmlAdaptedEvent)) {
            return false;
        }

        XmlAdaptedEvent otherEvent = (XmlAdaptedEvent) other;
        return Objects.equals(endDateTime, otherEvent.endDateTime)
                && Objects.equals(location, otherEvent.location);
    }

}
```
###### \java\seedu\address\storage\XmlAdaptedTask.java
``` java
/**
 * JAXB-friendly version of the Task.
 */
public class XmlAdaptedTask extends XmlAdaptedActivity {

    private static final String ACTIVITY_TYPE = "Task";

    /**
     * Constructs an XmlAdaptedTask.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedTask() {}

    /**
     * Constructs an {@code XmlAdaptedTask} with the given task details.
     */
    public XmlAdaptedTask(String name, String dueDateTime, String remark, List<XmlAdaptedTag> tagged) {
        super(name, dueDateTime, remark, tagged);
    }

    /**
     * Converts a given Task into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedTask
     */
    public XmlAdaptedTask(Task source) {
        super(source);
    }

    /**
     * Converts this jaxb-friendly adapted Task object into the model's Task object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted task
     */
    public Task toModelType() throws IllegalValueException {
        final List<Tag> activityTags = new ArrayList<>();
        for (XmlAdaptedTag tag : tagged) {
            activityTags.add(tag.toModelType());
        }

        if (this.name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    getActivityType(), "name"));
        }
        if (!Name.isValidName(this.name)) {
            throw new IllegalValueException(Name.MESSAGE_NAME_CONSTRAINTS);
        }
        final Name name = new Name(this.name);

        if (this.dateTime == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    getActivityType(), "due date/time"));
        }
        if (!DateTime.isValidDateTime(this.dateTime)) {
            throw new IllegalValueException(DateTime.MESSAGE_DATETIME_CONSTRAINTS);
        }
        final DateTime dateTime = new DateTime(this.dateTime);

        final Remark remark;
        if (this.remark == null) {
            remark = null;
        } else if (!Remark.isValidRemark(this.remark)) {
            throw new IllegalValueException(Remark.MESSAGE_REMARK_CONSTRAINTS);
        } else {
            remark = new Remark(this.remark);
        }

        final Set<Tag> tags = new HashSet<>(activityTags);

        return new Task(name, dateTime, remark, tags, this.isCompleted);
    }

    @Override
    public String getActivityType() {
        return ACTIVITY_TYPE;
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && other instanceof XmlAdaptedTask;
    }

}
```
###### \java\seedu\address\storage\XmlSerializableDeskBoard.java
``` java
/**
 * An immutable {@link DeskBoard} that is serializable to XML format
 */
@XmlRootElement(name = "deskBoard")
public class XmlSerializableDeskBoard {

    @XmlElement
    private List<XmlAdaptedTask> tasks;
    @XmlElement
    private List<XmlAdaptedEvent> events;
    @XmlElement
    private List<XmlAdaptedTag> tags;

    /**
     * Creates an empty XmlSerializableDeskBoard.
     * This empty constructor is required for marshalling.
     */
    public XmlSerializableDeskBoard() {
        tasks = new ArrayList<>();
        events = new ArrayList<>();
        tags = new ArrayList<>();
    }

    /**
     * Conversion
     */
    public XmlSerializableDeskBoard(ReadOnlyDeskBoard src) {
        this();
        for (Activity activity : src.getActivityList()) {
            if (activity instanceof Task) {
                tasks.add(new XmlAdaptedTask((Task) activity));
            } else if (activity instanceof Event) {
                events.add(new XmlAdaptedEvent((Event) activity));
            }
        }
        tags.addAll(src.getTagList().stream().map(XmlAdaptedTag::new).collect(Collectors.toList()));
    }

    /**
     * Converts this addressbook into the model's {@code DeskBoard} object.
     *
     * @throws IllegalValueException if there were any data constraints violated or duplicates in the
     * {@code XmlAdaptedActivity} or {@code XmlAdaptedTag}.
     */
    public DeskBoard toModelType() throws IllegalValueException {
        DeskBoard deskBoard = new DeskBoard();
        for (XmlAdaptedTag t : tags) {
            deskBoard.addTag(t.toModelType());
        }
        for (XmlAdaptedActivity a : tasks) {
            deskBoard.addActivity(((XmlAdaptedTask) a).toModelType());
        }
        for (XmlAdaptedActivity e : events) {
            deskBoard.addActivity(((XmlAdaptedEvent) e).toModelType());
        }
        return deskBoard;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof XmlSerializableDeskBoard)) {
            return false;
        }

        XmlSerializableDeskBoard otherDb = (XmlSerializableDeskBoard) other;
        return tasks.equals(otherDb.tasks) && events.equals(otherDb.events) && tags.equals(otherDb.tags);
    }
}
```
