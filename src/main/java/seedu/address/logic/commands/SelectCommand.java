package seedu.address.logic.commands;

import java.util.List;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.events.ui.JumpToEventListRequestEvent;
import seedu.address.commons.events.ui.JumpToTaskListRequestEvent;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.activity.Activity;

/**
 * Selects a activity identified using it's last displayed index from the address book.
 */
public class SelectCommand extends Command {

    public static final String COMMAND_WORD = "select";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Selects the activity identified by the index number used in the last activity listing.\n"
            + "Parameters: select task INDEX (must be a positive integer)\n"
            + "or select event INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " task 1";

    public static final String MESSAGE_SELECT_ACTIVITY_SUCCESS = "Selected Activity: %1$s";

    private static final String TYPE_TASK = "task";
    private static final String TYPE_EVENT = "event";

    private final Index targetIndex;
    private final String type;

    public SelectCommand(Index targetIndex, String type) {
        this.targetIndex = targetIndex;
        this.type = type;
    }

    //@@author YuanQLLer
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
