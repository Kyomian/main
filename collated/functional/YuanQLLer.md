# YuanQLLer
###### \java\seedu\address\commons\events\model\DeskBoardChangedEvent.java
``` java
/** Indicates the DeskBoard in the model has changed*/
public class DeskBoardChangedEvent extends BaseEvent {

    public final ReadOnlyDeskBoard data;

    public DeskBoardChangedEvent(ReadOnlyDeskBoard data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "number of activities " + data.getActivityList().size() + ", number of tags " + data.getTagList().size();
    }
}
```
###### \java\seedu\address\commons\events\ui\ActivityPanelSelectionChangedEvent.java
``` java
/**
 * Represents a selection change in the Activity List Panel
 */
public class ActivityPanelSelectionChangedEvent extends BaseEvent {


    private final ActivityCard newSelection;

    public ActivityPanelSelectionChangedEvent(ActivityCard newSelection) {
        this.newSelection = newSelection;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public ActivityCard getNewSelection() {
        return newSelection;
    }
}
```
###### \java\seedu\address\logic\commands\CompleteCommand.java
``` java
/**
 *
 * Complete a task identified using it's last displayed index from the CLInder.
 */
public class CompleteCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "complete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Complete the task identified by the index number used in the last task listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_COMPLETE_TASK_SUCCESS = "Completed Activity: %1$s";

    private final Index targetIndex;

    private Activity activityToComplete;

    public CompleteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }


    @Override
    public CommandResult executeUndoableCommand() {
        requireNonNull(activityToComplete);
        try {
            Activity completedActivity = activityToComplete.getCompletedCopy();
            model.updateActivity(activityToComplete, completedActivity);
        } catch (ActivityNotFoundException pnfe) {
            throw new AssertionError("The target activity cannot be missing");
        } catch (DuplicateActivityException dae) {
            throw new AssertionError("The completed activity cannot be duplicated");
        }
        return new CommandResult(String.format(MESSAGE_COMPLETE_TASK_SUCCESS, activityToComplete));
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        List<Activity> lastShownList = model.getFilteredTaskList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_ACTIVITY_DISPLAYED_INDEX);
        }

        activityToComplete = lastShownList.get(targetIndex.getZeroBased());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CompleteCommand // instanceof handles nulls
                && this.targetIndex.equals(((CompleteCommand) other).targetIndex) // state check
                && Objects.equals(this.activityToComplete, ((CompleteCommand) other).activityToComplete));
    }
}
```
###### \java\seedu\address\logic\commands\EditCommand.java
``` java
/**
 * Edits the details of an existing activity in the desk board.
 */
public class EditCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the activity identified "
            + "by the index number used in the last activity listing. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_ACTIVITY_SUCCESS = "Edited Activity: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_ACTIVITY = "This activity already exists in the desk board.";

    private final Index index;
    private final EditActivityDescriptor editActivityDescriptor;

    private Activity activityToEdit;
    private Activity editedActivity;

    /**
     * @param index of the activity in the filtered activity list to edit
     * @param editActivityDescriptor details to edit the activity with
     */
    public EditCommand(Index index, EditActivityDescriptor editActivityDescriptor) {
        requireNonNull(index);
        requireNonNull(editActivityDescriptor);

        this.index = index;
        this.editActivityDescriptor = editActivityDescriptor.getCopy();
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        try {
            model.updateActivity(activityToEdit, editedActivity);
        } catch (DuplicateActivityException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_ACTIVITY);
        } catch (ActivityNotFoundException pnfe) {
            throw new AssertionError("The target activity cannot be missing");
        }
        model.updateFilteredActivityList(PREDICATE_SHOW_ALL_ACTIVITY);
        return new CommandResult(String.format(MESSAGE_EDIT_ACTIVITY_SUCCESS, editedActivity));
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        List<Activity> lastShownList;
        if (editActivityDescriptor instanceof EditTaskDescriptor) {
            lastShownList = model.getFilteredTaskList();
        } else {
            lastShownList = model.getFilteredEventList();
        }

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_ACTIVITY_DISPLAYED_INDEX);
        }

        activityToEdit = lastShownList.get(index.getZeroBased());
        editedActivity = createEditedActivity(activityToEdit, editActivityDescriptor);
    }

    /**
     * Creates and returns a {@code Activity} with the details of {@code activityToEdit}
     * edited with {@code editActivityDescriptor}.
     */
    private static Activity createEditedActivity(Activity activityToEdit,
                                                 EditActivityDescriptor editActivityDescriptor) {
        assert activityToEdit != null;

        return editActivityDescriptor.createEditedActivity(activityToEdit);
    }


    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        EditCommand e = (EditCommand) other;
        return index.equals(e.index)
                && editActivityDescriptor.equals(e.editActivityDescriptor)
                && Objects.equals(activityToEdit, e.activityToEdit);
    }

    /**
     * Stores the details to edit the activity with. Each non-empty field value will replace the
     * corresponding field value of the activity.
     */
    public static interface EditActivityDescriptor {

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited();

        public EditActivityDescriptor getCopy();
        /**
         *  Creates and returns a {@code Activity} with the details of {@code activityToEdit}
         *  edited with {@code editTaskDescriptor}.
         * @param activityToEdit
         * @return
         */
        public Activity createEditedActivity(Activity activityToEdit);
    }

    /**
     * Stores the details to edit the task with. Each non-empty field value will replace the
     * corresponding field value of the activity.
     */
    public static class EditTaskDescriptor implements EditActivityDescriptor {
        private Name name;
        private DateTime dateTime;
        private Remark remark;
        private Set<Tag> tags;

        public EditTaskDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditTaskDescriptor(EditTaskDescriptor toCopy) {
            setName(toCopy.name);
            setDateTime(toCopy.dateTime);
            setRemark(toCopy.remark);
            setTags(toCopy.tags);
        }

        /**
         * Get a copy of this object
         */
        public EditActivityDescriptor getCopy() {
            return new EditTaskDescriptor(this);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(this.name, this.dateTime, this.remark, this.tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setDateTime(DateTime dateTime) {
            this.dateTime = dateTime;
        }

        public Optional<DateTime> getDateTime() {
            return Optional.ofNullable(dateTime);
        }

        public void setRemark(Remark remark) {
            this.remark = remark;
        }

        public Optional<Remark> getRemark() {
            return Optional.ofNullable(remark);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditTaskDescriptor)) {
                return false;
            }

            // state check
            EditTaskDescriptor e = (EditTaskDescriptor) other;

            return getName().equals(e.getName())
                    && getDateTime().equals(e.getDateTime())
                    && getRemark().equals(e.getRemark())
                    && getTags().equals(e.getTags());
        }


        /**
         * Creates and returns a {@code Activity} with the details of {@code activityToEdit}
         * edited with {@code editTaskDescriptor}.
         */
        public Activity createEditedActivity(Activity activityToEdit) {
            assert activityToEdit != null;

            Name updatedName = this.getName().orElse(activityToEdit.getName());
            DateTime updatedDateTime = this.getDateTime().orElse(activityToEdit.getDateTime());
            Remark updatedRemark = this.getRemark().orElse(activityToEdit.getRemark());
            Set<Tag> updatedTags = this.getTags().orElse(activityToEdit.getTags());

            return new Task(updatedName, updatedDateTime, updatedRemark, updatedTags, activityToEdit.isCompleted());
        }
    }

    /**
     * Stores the details to edit the event with. Each non-empty field value will replace the
     * corresponding field value of the event.
     */
    public static class EditEventDescriptor implements EditActivityDescriptor {
        private Name name;
        private DateTime startDateTime;
        private DateTime endDateTime;
        private Remark remark;
        private Location location;
        private Set<Tag> tags;

        public EditEventDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditEventDescriptor(EditEventDescriptor toCopy) {
            setName(toCopy.name);
            setStartDateTime(toCopy.startDateTime);
            setEndDateTime(toCopy.endDateTime);
            setRemark(toCopy.remark);
            setLocation(toCopy.location);
            setTags(toCopy.tags);
        }

        /**
         * Get a copy of this object
         */
        public EditActivityDescriptor getCopy() {
            return new EditEventDescriptor(this);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(this.name, this.startDateTime,
                    this.endDateTime, this.location, this.remark, this.tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }


        public void setStartDateTime(DateTime startDateTime) {
            this.startDateTime = startDateTime;
        }

        public Optional<DateTime> getStartDateTime() {
            return Optional.ofNullable(startDateTime);
        }

        public void setEndDateTime(DateTime endDateTime) {
            this.endDateTime = endDateTime;
        }

        public Optional<DateTime> getEndDateTime() {
            return Optional.ofNullable(endDateTime);
        }

        public void setRemark(Remark remark) {
            this.remark = remark;
        }

        public Optional<Remark> getRemark() {
            return Optional.ofNullable(remark);
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public Optional<Location> getLocation() {
            return Optional.ofNullable(location);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditEventDescriptor)) {
                return false;
            }

            // state check
            EditEventDescriptor e = (EditEventDescriptor) other;

            return getName().equals(e.getName())
                    && getStartDateTime().equals(e.getStartDateTime())
                    && getEndDateTime().equals(e.getEndDateTime())
                    && getRemark().equals(e.getRemark())
                    && getLocation().equals(e.getLocation())
                    && getTags().equals(e.getTags());
        }
        /**
         * Creates and returns a {@code Activity} with the details of {@code activityToEdit}
         * edited with {@code editTaskDescriptor}.
         */
        public Activity createEditedActivity(Activity activityToEdit) {
            assert activityToEdit != null;
            assert activityToEdit instanceof Event;
            Event eventToEdit = (Event) activityToEdit;
            Name updatedName = this.getName().orElse(eventToEdit.getName());
            DateTime updatedStartDateTime =
                    this.getStartDateTime().orElse(eventToEdit.getStartDateTime());
            DateTime updatedEndDateTime =
                    this.getEndDateTime().orElse(eventToEdit.getEndDateTime());
            Remark updatedRemark = this.getRemark().orElse(eventToEdit.getRemark());
            Location updatedLocation = this.getLocation().orElse(eventToEdit.getLocation());
            Set<Tag> updatedTags = this.getTags().orElse(eventToEdit.getTags());

            return new Event(updatedName, updatedStartDateTime, updatedEndDateTime, updatedLocation,
                    updatedRemark, updatedTags, eventToEdit.isCompleted());
        }
    }

}
```
###### \java\seedu\address\logic\commands\SelectCommand.java
``` java
    @Override
    public CommandResult execute() throws CommandException {
        CommandResult result = null;
        if (type.equalsIgnoreCase(TYPE_TASK)) {
            List<Activity> lastShownList = model.getFilteredTaskList();

            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_ACTIVITY_DISPLAYED_INDEX);
            }

            EventsCenter.getInstance().post(new JumpToTaskListRequestEvent(targetIndex));
            result = new CommandResult(String.format(MESSAGE_SELECT_ACTIVITY_SUCCESS, targetIndex.getOneBased()));
        } else if (type.equalsIgnoreCase(TYPE_EVENT)) {
            List<Activity> lastShownList = model.getFilteredEventList();

            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_ACTIVITY_DISPLAYED_INDEX);
            }

            EventsCenter.getInstance().post(new JumpToEventListRequestEvent(targetIndex));
            result = new CommandResult(String.format(MESSAGE_SELECT_ACTIVITY_SUCCESS, targetIndex.getOneBased()));
        } else {
            assert false : "Type is neither task or event, this should not happened!";
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof SelectCommand // instanceof handles nulls
                && this.targetIndex.equals(((SelectCommand) other).targetIndex)); // state check
    }
}
```
###### \java\seedu\address\logic\parser\CompleteCommandParser.java
``` java
/**
 * Parses input arguments and creates a new CompleteCommand object
 */
public class CompleteCommandParser implements Parser<CompleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns an DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public CompleteCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new CompleteCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, CompleteCommand.MESSAGE_USAGE));
        }
    }

}
```
###### \java\seedu\address\logic\parser\EditCommandParser.java
``` java
/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    private static final String TYPE_TASK = "task";
    private static final String TYPE_EVENT = "event";
    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedargs = args.trim();
        if (trimmedargs.startsWith(TYPE_TASK)) {
            String taskargs = trimmedargs.substring(TYPE_TASK.length() + 1).trim();
            return getEditTaskCommand(taskargs);
        } else if (trimmedargs.startsWith(TYPE_EVENT)) {
            String taskargs = trimmedargs.substring(TYPE_EVENT.length() + 1).trim();
            return getEditEventCommand(taskargs);
        }
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
    }

    private EditCommand getEditTaskCommand(String args) throws ParseException {

        Index index;
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_DATE_TIME, PREFIX_REMARK, PREFIX_TAG);
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }
        EditTaskDescriptor editTaskDescriptor = new EditTaskDescriptor();
        try {
            ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME)).ifPresent(editTaskDescriptor::setName);
            ParserUtil.parseDateTime(argMultimap.getValue(PREFIX_DATE_TIME)).ifPresent(editTaskDescriptor::setDateTime);
            ParserUtil.parseRemark
            (argMultimap.getValue(PREFIX_REMARK)).ifPresent(editTaskDescriptor::setRemark);
            parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editTaskDescriptor::setTags);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }

        if (!editTaskDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editTaskDescriptor);
    }

    private EditCommand getEditEventCommand(String args) throws ParseException {

        Index index;
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_START_DATETIME, PREFIX_END_DATETIME,
                        PREFIX_LOCATION, PREFIX_REMARK, PREFIX_TAG);
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }
        EditEventDescriptor editEventDescriptor = new EditEventDescriptor();
        try {
            ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME)).ifPresent(editEventDescriptor::setName);
            ParserUtil.parseDateTime
                    (argMultimap.getValue(PREFIX_START_DATETIME)).ifPresent(editEventDescriptor::setStartDateTime);
            ParserUtil.parseDateTime
                    (argMultimap.getValue(PREFIX_END_DATETIME)).ifPresent(editEventDescriptor::setEndDateTime);
            ParserUtil.parseRemark
                    (argMultimap.getValue(PREFIX_REMARK)).ifPresent(editEventDescriptor::setRemark);
            ParserUtil.parseLocation
                    (argMultimap.getValue(PREFIX_LOCATION)).ifPresent(editEventDescriptor::setLocation);
            parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editEventDescriptor::setTags);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }

        if (!editEventDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editEventDescriptor);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws IllegalValueException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

}
```
###### \java\seedu\address\logic\parser\FindCommandParser.java
``` java
    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns an FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();

        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        String[] nameKeywords = trimmedArgs.split("\\s+");
        if (nameKeywords[FIRST_INDEX].equalsIgnoreCase(TYPE_TASK) && nameKeywords.length >= 2) {
            List<String> keywords = new ArrayList<String>(Arrays.asList(nameKeywords));
            keywords.remove(FIRST_INDEX);
            return new FindCommand(
                    new NameContainsKeywordsPredicate(keywords).or(new EventOnlyPredicate()));
        }
        if (nameKeywords[FIRST_INDEX].equalsIgnoreCase(TYPE_EVENT) && nameKeywords.length >= 2) {
            List<String> keywords = new ArrayList<String>(Arrays.asList(nameKeywords));
            keywords.remove(FIRST_INDEX);
            return new FindCommand(
                    new NameContainsKeywordsPredicate(keywords).or(new TaskOnlyPredicate()));
        }
        return new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
    }

}
```
###### \java\seedu\address\logic\parser\SelectCommandParser.java
``` java
/**
 * Parses input arguments and creates a new SelectCommand object
 */
public class SelectCommandParser implements Parser<SelectCommand> {

```
###### \java\seedu\address\logic\parser\SelectCommandParser.java
``` java
    /**
     * Parses the given {@code String} of arguments in the context of the SelectCommand
     * and returns an SelectCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public SelectCommand parse(String args) throws ParseException {
        String[] argsParts = args.trim().split(" ");

        if (argsParts.length != 2 || !isValidActivityOption(argsParts[0])) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand.MESSAGE_USAGE));
        }

        try {
            String activityOption = argsParts[0];
            Index index = ParserUtil.parseIndex(argsParts[1]);
            return new SelectCommand(index, activityOption.trim());
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand.MESSAGE_USAGE));
        }
    }

    private static boolean isValidActivityOption(String activityOption) {
        return activityOption.equals("task") || activityOption.equals("event");
    }

}
```
###### \java\seedu\address\model\activity\Activity.java
``` java
/**
 * Represents a Activity in the desk board.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public abstract class Activity {

    private final Name name;
    private final DateTime dateTime;
    private final Remark remark;

    private final UniqueTagList tags;
    private final boolean isCompleted;

    /**
     * Every field must be present and not null.
     */
    public Activity(Name name, DateTime dateTime, Remark remark, Set<Tag> tags) {
        requireAllNonNull(name, dateTime, tags);
        this.name = name;
        this.dateTime = dateTime;
        this.remark = remark;
        // protect internal tags from changes in the arg list
        this.tags = new UniqueTagList(tags);
        this.isCompleted = false;
    }

    public Activity(Name name, DateTime dateTime, Remark remark, Set<Tag> tags, boolean isCompleted) {
        requireAllNonNull(name, dateTime, tags);
        this.name = name;
        this.dateTime = dateTime;
        this.remark = remark;
        // protect internal tags from changes in the arg list
        this.tags = new UniqueTagList(tags);
        this.isCompleted = isCompleted;
    }

    public Name getName() {
        return name;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public Remark getRemark() {
        return remark;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags.toSet());
    }

    public abstract String getActivityType();

    public abstract Activity copy(Set<Tag> tags);

    public boolean isCompleted() {
        return isCompleted;
    }

    public abstract Activity getCompletedCopy();
}
```
###### \java\seedu\address\model\activity\Event.java
``` java
/**
 * Represents a Task in the desk board.
 * The field contains 3 field, name, start date/time, end date/time and
 *      (Optional)location (Optional)remark.
 * The following example would illustrate one example
 * ******** Example ******************************* *
 * NAME : CS2103 Exam
 * START DATE/TIME: 21/03/2018 23:59
 * END DATE/TIME:
 * LOCATION: TBC
 * REMARK: Submit through a pull request in git hub.
 * ************************************************ *
 */
public class Event extends Activity {

    private static final String  ACTIVITY_TYPE = "EVENT";

    private final DateTime endDateTime;
    private final Location location;

    /**
     * Every field must be present and not null.
     */
    public Event(
            Name name, DateTime startDateTime, DateTime endDateTime, Location location, Remark remark, Set<Tag> tags) {
        super(name, startDateTime, remark, tags);
        requireAllNonNull(endDateTime);
        this.endDateTime = endDateTime;
        this.location = location;
    }

    public Event(
            Name name, DateTime startDateTime, DateTime endDateTime, Location location, Remark remark, Set<Tag> tags,
            boolean isComplete) {
        super(name, startDateTime, remark, tags, isComplete);
        requireAllNonNull(endDateTime);
        this.endDateTime = endDateTime;
        this.location = location;
    }

    @Override
    public Name getName() {
        return super.getName();
    }

    public DateTime getStartDateTime() {
        return super.getDateTime();
    }

    public DateTime getEndDateTime() {
        return this.endDateTime;
    }

    public Location getLocation() {
        return this.location;
    }
    @Override
    public Remark getRemark() {
        return super.getRemark();
    }

    @Override
    public String getActivityType() {
        return ACTIVITY_TYPE;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    @Override
    public Set<Tag> getTags() {
        return super.getTags();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Event)) {
            return false;
        }

        Event otherEvent = (Event) other;
        return otherEvent.getName().equals(this.getName())
                && otherEvent.getStartDateTime().equals(this.getStartDateTime())
                && otherEvent.getEndDateTime().equals(this.getEndDateTime())
                && (location == null ? otherEvent.location == null : location.equals(otherEvent.getLocation()))
                && (getRemark() == null ? otherEvent.getRemark() == null : getRemark().equals(otherEvent.getRemark()))
                && this.isCompleted() == otherEvent.isCompleted();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Event Name: ")
                .append(getName())
                .append(" Start Date/Time: ")
                .append(getDateTime())
                .append(" End Date/Time: ")
                .append(getEndDateTime())
                .append(" Location: ")
                .append(getLocation() == null ? "" : getLocation())
                .append(" Remark: ")
                .append(getRemark() == null ? "" : getRemark())
                .append(" Tags: ")
                .append(isCompleted() ? "Uncompleted" : "Completed");
        getTags().forEach(builder::append);
        return builder.toString();
    }

    @Override
    public Activity copy(Set<Tag> tags) {
        if (tags == null) {
            return new Event(getName(), getStartDateTime(), getEndDateTime(), getLocation(), getRemark(), getTags(),
                    isCompleted());
        }
        return new Event(getName(), getStartDateTime(), getEndDateTime(), getLocation(), getRemark(), tags,
                isCompleted());
    }

    @Override
    public Activity getCompletedCopy() {
        return new Event(
                getName(), getStartDateTime(), getEndDateTime(), getLocation(), getRemark(), getTags(), true);
    }
}

```
###### \java\seedu\address\model\activity\EventOnlyPredicate.java
``` java
/**
 * This class gives a predicate that returns only the event in a list.
 */
public class EventOnlyPredicate implements Predicate<Activity> {

    public EventOnlyPredicate() {
        ;
    }

    @Override
    public boolean test(Activity activity) {
        return activity.getActivityType().equals("EVENT");
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof EventOnlyPredicate); // instanceof handles nulls
    }

}
```
###### \java\seedu\address\model\activity\exceptions\ActivityNotFoundException.java
``` java
/**
 * Signals that the operation is unable to find the specified activity.
 */
public class ActivityNotFoundException extends Exception {}
```
###### \java\seedu\address\model\activity\exceptions\DuplicateActivityException.java
``` java
/**
 * Signals that the operation will result in duplicate Activity objects.
 */
public class DuplicateActivityException extends DuplicateDataException {
    public DuplicateActivityException() {
        super("Operation would result in duplicate activities");
    }
}
```
###### \java\seedu\address\model\activity\Location.java
``` java
/**
 * This class is to store location info in an event
 */
public class Location {
    public static final String MESSAGE_LOCATION_CONSTRAINTS =
            "Event location should not be blank";

    public static final String LOCATION_VALIDATION_REGEX = ".+";

    public final String value;

    /**
     * Constructs a {@code Name}.
     *
     * @param location A valid location.
     */
    public Location(String location) {
        requireNonNull(location);
        checkArgument(isValidLocation(location), MESSAGE_LOCATION_CONSTRAINTS);
        this.value = location;
    }

    /**
     * Returns true if a given string is a valid location.
     */
    public static boolean isValidLocation(String test) {
        return test.matches(LOCATION_VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Location // instanceof handles nulls
                && this.value.equals(((Location) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
```
###### \java\seedu\address\model\activity\Remark.java
``` java
/**
 * Represents an Activity's remark in the desk board.
 * Guarantees: immutable; is valid as declared in {@link #isValidRemark(String)}
 */
public class Remark {

    public static final String MESSAGE_REMARK_CONSTRAINTS =
            "Remark can take on any values, but it should not be blank.";

    public static final String REMARK_VALIDATION_REGEX = ".+";

    public final String value;

    /**
     * Constructs an {@code Remark}.
     *
     * @param remark A valid remark.
     */
    public Remark(String remark) {
        requireNonNull(remark);
        checkArgument(isValidRemark(remark), MESSAGE_REMARK_CONSTRAINTS);
        this.value = remark;
    }

    /**
     * Returns true if a given string is a valid activity remark.
     */
    public static boolean isValidRemark(String test) {
        return test.matches(REMARK_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Remark // instanceof handles nulls
                && this.value.equals(((Remark) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
```
###### \java\seedu\address\model\activity\Task.java
``` java
/**
 * Represents a Task in the desk board.
 * The field contains 3 field, name, due date and (Optional)remark.
 * The following example would illustrate one example
 * ******** Example ******************************* *
 * NAME : CS2103 Project
 * DUE DATE/TIME: 21/03/2018 23:59
 * REMARK: Submit through a pull request in git hub.
 * ************************************************ *
 */
public class Task extends Activity {

    private static final String ACTIVITY_TYPE = "TASK";

    /**
     * Every field must be present and not null.
     */
    public Task(Name name, DateTime dueDateTime, Remark remark, Set<Tag> tags) {
        super(name, dueDateTime, remark, tags);


    }

    public Task(Name name, DateTime dueDateTime, Remark remark, Set<Tag> tags, boolean isComplete) {
        super(name, dueDateTime, remark, tags, isComplete);


    }
    @Override
    public Name getName() {
        return super.getName();
    }

    public DateTime getDueDateTime() {
        return super.getDateTime();
    }

    @Override
    public Remark getRemark() {
        return super.getRemark();
    }

    @Override
    public String getActivityType() {
        return ACTIVITY_TYPE;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    @Override
    public Set<Tag> getTags() {
        return super.getTags();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Task)) {
            return false;
        }

        Task otherTask = (Task) other;
        return otherTask.getName().equals(this.getName())
                && otherTask.getDueDateTime().equals(this.getDueDateTime())
                && (getRemark() == null ? otherTask.getRemark() == null : getRemark().equals(otherTask.getRemark()))
                && this.isCompleted() == otherTask.isCompleted();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Task Name: ")
                .append(getName())
                .append(" Due Date/Time: ")
                .append(getDateTime())
                .append(" Remark: ")
                .append(getRemark() == null ? "" : getRemark())
                .append(" Tags: ")
                .append(isCompleted() ? "Uncompleted" : "Completed");
        getTags().forEach(builder::append);
        return builder.toString();
    }

    @Override
    public Activity copy(Set<Tag> tags) {
        if (tags == null) {
            return new Task(getName(), getDueDateTime(), getRemark(), getTags(), isCompleted());
        }
        return new Task(getName(), getDueDateTime(), getRemark(), tags, isCompleted());
    }

```
###### \java\seedu\address\model\activity\TaskOnlyPredicate.java
``` java
/**
 * This class gives a predicate that returns only the tasks in a list.
 */
public class TaskOnlyPredicate implements Predicate<Activity> {
    public TaskOnlyPredicate() {
        ;
    }

    @Override
    public boolean test(Activity activity) {
        return activity.getActivityType().equals("TASK");
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TaskOnlyPredicate); // instanceof handles nulls
    }

}
```
###### \java\seedu\address\model\DeskBoard.java
``` java
    /**
     * Replaces the given activity {@code target} in the list with {@code editedActivity}.
     * {@code DeskBoard}'s tag list will be updated with the tags of {@code editedActivity}.
     *
     * @throws DuplicateActivityException if updating the activity's details causes the activity to be equivalent to
     *      another existing activity in the list.
     * @throws ActivityNotFoundException if {@code target} could not be found in the list.
     *
     * @see #syncWithMasterTagList(Activity)
     */
    public void updateActivity(Activity target, Activity editedActivity)
            throws DuplicateActivityException, ActivityNotFoundException {
        requireNonNull(editedActivity);

        Activity syncedEditedActivity = syncWithMasterTagList(editedActivity);
        // TODO: the tags master list will be updated even though the below line fails.
        // This can cause the tags master list to have additional tags that are not tagged to any activity
        // in the activity list.
        activities.setActivity(target, syncedEditedActivity);
    }

    /**
     *  Updates the master tag list to include tags in {@code activity} that are not in the list.
     *  @return a copy of this {@code activity} such that every tag in this activity points to
     *      a Tag object in the master list.
     */
    private Activity syncWithMasterTagList(Activity activity) {
        final UniqueTagList activityTags = new UniqueTagList(activity.getTags());
        tags.mergeFrom(activityTags);

        // Create map with values = tag object references in the master list
        // used for checking activity tag references
        final Map<Tag, Tag> masterTagObjects = new HashMap<>();
        tags.forEach(tag -> masterTagObjects.put(tag, tag));

        // Rebuild the list of activity tags to point to the relevant tags in the master tag list.
        final Set<Tag> correctTagReferences = new HashSet<>();
        activityTags.forEach(tag -> correctTagReferences.add(masterTagObjects.get(tag)));
        return activity.copy(correctTagReferences);
    }

```
###### \java\seedu\address\model\ModelManager.java
``` java
/**
 * Represents the in-memory model of the desk board data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final DeskBoard deskBoard;
    private final FilteredList<Activity> filteredActivities;
    /**
     * Initializes a ModelManager with the given deskBoard and userPrefs.
     */
    public ModelManager(ReadOnlyDeskBoard deskBoard, UserPrefs userPrefs) {
        super();
        requireAllNonNull(deskBoard, userPrefs);

        logger.fine("Initializing with desk board: " + deskBoard + " and user prefs " + userPrefs);

        this.deskBoard = new DeskBoard(deskBoard);
        filteredActivities = new FilteredList<>(this.deskBoard.getActivityList());
    }

    public ModelManager() {
        this(new DeskBoard(), new UserPrefs());
    }

    @Override
    public void resetData(ReadOnlyDeskBoard newData) {
        deskBoard.resetData(newData);
        indicateDeskBoardChanged();
    }

    @Override
    public ReadOnlyDeskBoard getDeskBoard() {
        return deskBoard;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateDeskBoardChanged() {
        raise(new DeskBoardChangedEvent(deskBoard));
    }

    @Override
    public synchronized void deleteActivity(Activity target) throws ActivityNotFoundException {
        deskBoard.removeActivity(target);
        indicateDeskBoardChanged();
    }

```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public synchronized void updateActivity(Activity target, Activity editedActivity)
            throws DuplicateActivityException, ActivityNotFoundException {
        requireAllNonNull(target, editedActivity);

        deskBoard.updateActivity(target, editedActivity);
        indicateDeskBoardChanged();
    }

    //=========== Filtered Activity List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Activity} backed by the internal list of
     * {@code deskBoard}
     */
    @Override
    public ObservableList<Activity> getFilteredActivityList() {
        return FXCollections.unmodifiableObservableList(filteredActivities);
    }

```
###### \java\seedu\address\model\ModelManager.java
``` java
    /**
     * Returns an unmodifiable view of the list of {@code Event} backed by the event list of
     * {@code deskBoard}
     */
    @Override
    public ObservableList<Activity> getFilteredEventList() {
        FilteredList<Activity> eventList =  new FilteredList<>(filteredActivities, new EventOnlyPredicate());
        ObservableList<Activity> result = FXCollections.unmodifiableObservableList(eventList);
        return result;
    }

    @Override
    public void updateFilteredActivityList(Predicate<Activity> predicate) {
        requireNonNull(predicate);
        filteredActivities.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return deskBoard.equals(other.deskBoard)
                && filteredActivities.equals(other.filteredActivities);
    }

}
```
###### \java\seedu\address\model\util\SampleDataUtil.java
``` java
    public static Activity[] getSampleActivity() {
        return new Activity[] {
            new Task(new Name("Task 1"), new DateTime("01/01/2018 23:59"),
                    new Remark("Submit through ivle"), getTagSet("Urgent")),
            new Task (new Name("Task 2"), new DateTime("02/02/2018 23:59"),
                    new Remark("Submit through ivle"), getTagSet("Urgent")),
            new Task (new Name("Task 3"), new DateTime("03/03/2018 23:59"),
                    new Remark("Submit through ivle"), getTagSet("Urgent")),
            new Task (new Name("Task 4"), new DateTime("04/04/2018 23:59"),
                    new Remark("Submit through ivle"), getTagSet("Nonurgent")),
            new Task (new Name("Task 5"), new DateTime("05/05/2018 23:59"),
                    new Remark("Submit through ivle"), getTagSet("Nonurgent")),
            new Task (new Name("Task 6"), new DateTime("06/06/2018 23:59"),
                    new Remark("Submit through ivle"), getTagSet("Nonurgent")),
            new Event(new Name("Event 1"), new DateTime("01/01/2018 07:00"),
                    new DateTime("01/01/2018 08:00"), new Location("TBC"), new Remark("Remark"),
                    getTagSet("Optional")),
            new Event (new Name("Event 2"), new DateTime("02/02/2018 07:00"),
                    new DateTime("02/02/2018 08:00"), new Location("TBC"), new Remark("Remark"),
                    getTagSet("Optional")),
            new Event (new Name("Event 3"), new DateTime("03/03/2018 07:00"),
                    new DateTime("03/03/2018 08:00"), new Location("TBC"), new Remark("Remark"),
                    getTagSet("Cancelled")),
            new Event (new Name("Event 4"), new DateTime("04/04/2018 07:00"),
                    new DateTime("04/04/2018 08:00"), new Location("TBC"), new Remark("Remark"),
                    getTagSet("Important")),
            new Event (new Name("Event 5"), new DateTime("05/05/2018 07:00"),
                    new DateTime("05/05/2018 08:00"), new Location("TBC"), new Remark("Remark"),
                    getTagSet("Important")),
            new Event (new Name("Event 6"), new DateTime("06/06/2018 07:00"),
                    new DateTime("06/06/2018 08:00"), new Location("TBC"), new Remark("Remark"),
                    getTagSet("Compulsory"))
        };
    }

    public static ReadOnlyDeskBoard getSampleDeskBoard() {
        try {
            DeskBoard sampleAb = new DeskBoard();
            for (Activity sampleActivity : getSampleActivity()) {
                sampleAb.addActivity(sampleActivity);
            }
            return sampleAb;
        } catch (DuplicateActivityException e) {
            throw new AssertionError("sample data cannot contain duplicate activities", e);
        }
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        HashSet<Tag> tags = new HashSet<>();
        for (String s : strings) {
            tags.add(new Tag(s));
        }

        return tags;
    }

}
```
###### \java\seedu\address\ui\EventListPanel.java
``` java
    /**
     * Unselect a tab..
     */
    private void unselect() {
        Platform.runLater(() -> {
            eventListView.getSelectionModel().clearSelection();
        });
    }

    @Subscribe
    private void handleJumpToEventListRequestEvent(JumpToEventListRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        scrollTo(event.targetIndex);
    }

```
###### \java\seedu\address\ui\EventListPanel.java
``` java
    @Subscribe
    private void handleJumpToTaskListRequestEvent(JumpToTaskListRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        unselect();
    }

    @Subscribe
    private void handleDeselectListCellEvent(DeselectEventListCellEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        selectedIndex = -1;
        event.getPanel().getSelectionModel().clearSelection(event.getTargetIndex());
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code EventCard}.
     */
    class EventListViewCell extends ListCell<EventCard> {

        @Override
        protected void updateItem(EventCard event, boolean empty) {
            super.updateItem(event, empty);

            if (empty || event == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(event.getRoot());
            }
        }
    }

    /**
     * Getter method for eventListView
     * @return eventListView
     */
    public ListView<EventCard> getEventListView()   {
        return eventListView;
    }

    public void setData(TaskListPanel taskListPanel) {
        this.taskListPanel = taskListPanel;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
}
```
###### \java\seedu\address\ui\TaskListPanel.java
``` java
    /**
     * Unselect a tab..
     */
    private void unselect() {
        Platform.runLater(() -> {
            taskListView.getSelectionModel().clearSelection();
        });
    }

    @Subscribe
    private void handleJumpToTaskListRequestEvent(JumpToTaskListRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        scrollTo(event.targetIndex);
    }

    @Subscribe
    private void handleJumpToEventListRequestEvent(JumpToEventListRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        unselect();
    }

```
###### \java\seedu\address\ui\TaskListPanel.java
``` java
    @Subscribe
    private void handleDeselectTaskListCellEvent(DeselectTaskListCellEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        selectedIndex = -1;
        event.getPanel().getSelectionModel().clearSelection(event.getTargetIndex());
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code TaskCard}.
     */
    class TaskListViewCell extends ListCell<TaskCard> {

        @Override
        protected void updateItem(TaskCard activity, boolean empty) {
            super.updateItem(activity, empty);

            if (empty || activity == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(activity.getRoot());
            }
        }
    }

```
