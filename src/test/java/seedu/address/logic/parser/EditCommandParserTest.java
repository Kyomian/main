package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.logictestutil.TaskTestConstants.DATE_TIME_DESC_CS2010_QUIZ;
import static seedu.address.logic.logictestutil.TaskTestConstants.DATE_TIME_DESC_MA2108_HOMEWORK;
import static seedu.address.logic.logictestutil.TaskTestConstants.INVALID_TASK_DATE_TIME_DESC;
import static seedu.address.logic.logictestutil.TaskTestConstants.INVALID_TASK_NAME_DESC;
import static seedu.address.logic.logictestutil.TaskTestConstants.INVALID_TASK_REMARK_DESC;
import static seedu.address.logic.logictestutil.TaskTestConstants.INVALID_TASK_TAG_DESC;
import static seedu.address.logic.logictestutil.TaskTestConstants.NAME_DESC_MA2108_HOMEWORK;
import static seedu.address.logic.logictestutil.TaskTestConstants.REMARK_DESC_CS2010_QUIZ;
import static seedu.address.logic.logictestutil.TaskTestConstants.REMARK_DESC_MA2108_HOMEWORK;
import static seedu.address.logic.logictestutil.TaskTestConstants.TAG_DESC_CS2010;
import static seedu.address.logic.logictestutil.TaskTestConstants.TAG_DESC_MA2108;
import static seedu.address.logic.logictestutil.TaskTestConstants.TAG_DESC_URGENT;
import static seedu.address.logic.logictestutil.TaskTestConstants.VALID_DATE_TIME_CS2010_QUIZ;
import static seedu.address.logic.logictestutil.TaskTestConstants.VALID_DATE_TIME_MA2108_HOMEWORK;
import static seedu.address.logic.logictestutil.TaskTestConstants.VALID_NAME_MA2108_HOMEWORK;
import static seedu.address.logic.logictestutil.TaskTestConstants.VALID_REMARK_CS2010_QUIZ;
import static seedu.address.logic.logictestutil.TaskTestConstants.VALID_REMARK_MA2108_HOMEWORK;
import static seedu.address.logic.logictestutil.TaskTestConstants.VALID_TAG_CS2010;
import static seedu.address.logic.logictestutil.TaskTestConstants.VALID_TAG_MA2108;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_ACTIVITY;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_ACTIVITY;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_ACTIVITY;

import org.junit.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditActivityDescriptor;
import seedu.address.model.activity.DateTime;
import seedu.address.model.activity.Name;
import seedu.address.model.activity.Remark;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditTaskDescriptorBuilder;

//@@author YuanQLLer
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
