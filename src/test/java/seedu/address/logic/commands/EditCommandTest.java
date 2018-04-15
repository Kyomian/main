package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_CS2010_QUIZ;
import static seedu.address.logic.commands.CommandTestUtil.DESC_MA2108_HOMEWORK;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.prepareRedoCommand;
import static seedu.address.logic.commands.CommandTestUtil.prepareUndoCommand;
import static seedu.address.logic.commands.CommandTestUtil.showActivityAtIndex;
import static seedu.address.logic.logictestutil.TaskTestConstants.VALID_DATE_TIME_CS2010_QUIZ;
import static seedu.address.logic.logictestutil.TaskTestConstants.VALID_NAME_CS2010_QUIZ;
import static seedu.address.logic.logictestutil.TaskTestConstants.VALID_TAG_MA2108;
import static seedu.address.testutil.TypicalActivities.getTypicalDeskBoard;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_ACTIVITY;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_ACTIVITY;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.commands.EditCommand.EditActivityDescriptor;
import seedu.address.model.DeskBoard;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.activity.Activity;
import seedu.address.testutil.EditTaskDescriptorBuilder;
import seedu.address.testutil.TaskBuilder;

/**
* Contains integration tests (interaction with the Model, UndoCommand and RedoCommand) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalDeskBoard(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() throws Exception {
        Activity editedActivity = new TaskBuilder().build();
        EditActivityDescriptor descriptor = new EditTaskDescriptorBuilder(editedActivity).build();
        EditCommand editCommand = prepareCommand(INDEX_FIRST_ACTIVITY, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_ACTIVITY_SUCCESS, editedActivity);

        Model expectedModel = new ModelManager(new DeskBoard(model.getDeskBoard()), new UserPrefs());
        expectedModel.updateActivity(model.getFilteredTaskList().get(0), editedActivity);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() throws Exception {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredTaskList().size());
        Activity lastActivity = model.getFilteredTaskList().get(indexLastPerson.getZeroBased());

        TaskBuilder personInList = new TaskBuilder(lastActivity);
        Activity editedActivity = personInList.withName(VALID_NAME_CS2010_QUIZ)
                .withDateTime(VALID_DATE_TIME_CS2010_QUIZ)
                .withTags(VALID_TAG_MA2108).build();

        EditActivityDescriptor descriptor = new EditTaskDescriptorBuilder().withName(VALID_NAME_CS2010_QUIZ)
                .withDateTime(VALID_DATE_TIME_CS2010_QUIZ).withTags(VALID_TAG_MA2108).build();
        EditCommand editCommand = prepareCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_ACTIVITY_SUCCESS, editedActivity);

        Model expectedModel = new ModelManager(new DeskBoard(model.getDeskBoard()), new UserPrefs());
        expectedModel.updateActivity(lastActivity, editedActivity);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = prepareCommand(INDEX_FIRST_ACTIVITY, new EditCommand.EditTaskDescriptor());
        Activity editedActivity = model.getFilteredTaskList().get(INDEX_FIRST_ACTIVITY.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_ACTIVITY_SUCCESS, editedActivity);

        Model expectedModel = new ModelManager(new DeskBoard(model.getDeskBoard()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }


    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Activity firstActivity = model.getFilteredTaskList().get(INDEX_FIRST_ACTIVITY.getZeroBased());
        EditActivityDescriptor descriptor = new EditTaskDescriptorBuilder(firstActivity).build();
        EditCommand editCommand = prepareCommand(INDEX_SECOND_ACTIVITY, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_ACTIVITY);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredActivityList().size() + 1);
        EditActivityDescriptor descriptor = new EditTaskDescriptorBuilder()
                .withName(VALID_NAME_CS2010_QUIZ).build();
        EditCommand editCommand = prepareCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_ACTIVITY_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showActivityAtIndex(model, INDEX_FIRST_ACTIVITY);
        Index outOfBoundIndex = INDEX_SECOND_ACTIVITY;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getDeskBoard().getActivityList().size());

        EditCommand editCommand = prepareCommand(outOfBoundIndex,
                new EditTaskDescriptorBuilder().withName(VALID_NAME_CS2010_QUIZ).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_ACTIVITY_DISPLAYED_INDEX);
    }

    @Test
    public void executeUndoRedo_validIndexUnfilteredList_success() throws Exception {
        UndoRedoStack undoRedoStack = new UndoRedoStack();
        UndoCommand undoCommand = prepareUndoCommand(model, undoRedoStack);
        RedoCommand redoCommand = prepareRedoCommand(model, undoRedoStack);
        Activity editedActivity = new TaskBuilder().build();
        Activity activityToEdit = model.getFilteredTaskList().get(INDEX_FIRST_ACTIVITY.getZeroBased());
        EditActivityDescriptor descriptor = new EditTaskDescriptorBuilder(editedActivity).build();
        EditCommand editCommand = prepareCommand(INDEX_FIRST_ACTIVITY, descriptor);
        Model expectedModel = new ModelManager(new DeskBoard(model.getDeskBoard()), new UserPrefs());

        // edit -> first activity edited
        editCommand.execute();
        undoRedoStack.push(editCommand);

        // undo -> reverts addressbook back to previous state and filtered activity list to show all persons
        assertCommandSuccess(undoCommand, model, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> same first activity edited again
        expectedModel.updateActivity(activityToEdit, editedActivity);
        assertCommandSuccess(redoCommand, model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void executeUndoRedo_invalidIndexUnfilteredList_failure() {
        UndoRedoStack undoRedoStack = new UndoRedoStack();
        UndoCommand undoCommand = prepareUndoCommand(model, undoRedoStack);
        RedoCommand redoCommand = prepareRedoCommand(model, undoRedoStack);
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredActivityList().size() + 1);
        EditActivityDescriptor descriptor = new EditTaskDescriptorBuilder()
                .withName(VALID_NAME_CS2010_QUIZ).build();
        EditCommand editCommand = prepareCommand(outOfBoundIndex, descriptor);

        // execution failed -> editCommand not pushed into undoRedoStack
        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_ACTIVITY_DISPLAYED_INDEX);

        // no commands in undoRedoStack -> undoCommand and redoCommand fail
        assertCommandFailure(undoCommand, model, UndoCommand.MESSAGE_FAILURE);
        assertCommandFailure(redoCommand, model, RedoCommand.MESSAGE_FAILURE);
    }

    /**
     * 1. Edits a {@code Activity} from a filtered list.
     * 2. Undo the edit.
     * 3. The unfiltered list should be shown now. Verify that the index of the previously edited activity in the
     * unfiltered list is different from the index at the filtered list.
     * 4. Redo the edit. This ensures {@code RedoCommand} edits the activity object regardless of indexing.
     */
    @Test
    public void executeUndoRedo_validIndexFilteredList_samePersonEdited() throws Exception {
        UndoRedoStack undoRedoStack = new UndoRedoStack();
        UndoCommand undoCommand = prepareUndoCommand(model, undoRedoStack);
        RedoCommand redoCommand = prepareRedoCommand(model, undoRedoStack);
        Activity editedActivity = new TaskBuilder().build();
        EditActivityDescriptor descriptor = new EditTaskDescriptorBuilder(editedActivity).build();
        EditCommand editCommand = prepareCommand(INDEX_FIRST_ACTIVITY, descriptor);
        Model expectedModel = new ModelManager(new DeskBoard(model.getDeskBoard()), new UserPrefs());

        showActivityAtIndex(model, INDEX_SECOND_ACTIVITY);
        Activity activityToEdit = model.getFilteredActivityList().get(INDEX_FIRST_ACTIVITY.getZeroBased());
        // edit -> edits second activity in unfiltered activity list / first activity in filtered activity list
        editCommand.execute();
        undoRedoStack.push(editCommand);

        // undo -> reverts addressbook back to previous state and filtered activity list to show all persons
        assertCommandSuccess(undoCommand, model, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        expectedModel.updateActivity(activityToEdit, editedActivity);
        assertNotEquals(model.getFilteredActivityList().get(INDEX_FIRST_ACTIVITY.getZeroBased()), activityToEdit);
        // redo -> edits same second activity in unfiltered activity list
        assertCommandSuccess(redoCommand, model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() throws Exception {
        final EditCommand standardCommand = prepareCommand(INDEX_FIRST_ACTIVITY, DESC_MA2108_HOMEWORK);

        // same values -> returns true
        EditCommand.EditActivityDescriptor copyDescriptor = new EditCommand.EditTaskDescriptor(DESC_MA2108_HOMEWORK);
        EditCommand commandWithSameValues = prepareCommand(INDEX_FIRST_ACTIVITY, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // one command preprocessed when previously equal -> returns false
        commandWithSameValues.preprocessUndoableCommand();
        assertFalse(standardCommand.equals(commandWithSameValues));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand("")));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_ACTIVITY, DESC_MA2108_HOMEWORK)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_ACTIVITY, DESC_CS2010_QUIZ)));
    }

    /**
     * Returns an {@code EditCommand} with parameters {@code index} and {@code descriptor}
     */
    private EditCommand prepareCommand(Index index, EditCommand.EditActivityDescriptor descriptor) {
        EditCommand editCommand = new EditCommand(index, descriptor);
        editCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return editCommand;
    }
}
