package seedu.address.model;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.model.activity.Activity;
import seedu.address.model.activity.exceptions.ActivityNotFoundException;
import seedu.address.model.activity.exceptions.DuplicateActivityException;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Activity> PREDICATE_SHOW_ALL_ACTIVITY = unused -> true;

    /** Clears existing backing model and replaces with the provided new data. */
    void resetData(ReadOnlyDeskBoard newData);

    /** Returns the DeskBoard */
    ReadOnlyDeskBoard getDeskBoard();

    /** Deletes the given activity. */
    void deleteActivity(Activity target) throws ActivityNotFoundException;

    //@@author Kyomian
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

    //@@author jasmoon
    /** Returns an unmodifiable view of the filtered task list */
    ObservableList<Activity> getFilteredTaskList();

    /** Returns an unmodifiable view of the filtered event list */
    ObservableList<Activity> getFilteredEventList();

    /**
     * Updates the filter of the filtered activity list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredActivityList(Predicate<Activity> predicate);

}
