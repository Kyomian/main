package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_ACTIVITY;

import org.junit.Test;

import seedu.address.logic.commands.CompleteCommand;

//@@author YuanQLLer
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
