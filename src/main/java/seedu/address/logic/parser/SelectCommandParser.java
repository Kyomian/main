package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.SelectCommand;
import seedu.address.logic.parser.exceptions.ParseException;


//@@author YuanQLLer
/**
 * Parses input arguments and creates a new SelectCommand object
 */
public class SelectCommandParser implements Parser<SelectCommand> {

    //@@author YuanQLLer
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
