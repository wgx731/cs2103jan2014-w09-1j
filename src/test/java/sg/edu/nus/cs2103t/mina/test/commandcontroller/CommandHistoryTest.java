package sg.edu.nus.cs2103t.mina.test.commandcontroller;

import static org.junit.Assert.*;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import sg.edu.nus.cs2103t.mina.commandcontroller.CommandHistory;
import sg.edu.nus.cs2103t.mina.model.DeadlineTask;
import sg.edu.nus.cs2103t.mina.model.EventTask;
import sg.edu.nus.cs2103t.mina.model.TodoTask;
import sg.edu.nus.cs2103t.mina.model.parameter.FilterParameter;

/**
 * Actual test case for CommandHistory
 */
// @author A0099324X

public class CommandHistoryTest {
	@Test
	public void testUndo() {
		CommandHistory commandHistory = new CommandHistory();
		assertTrue(commandHistory.isEmptyUndo());

		SortedSet<TodoTask> undoTodoUncompletedSet = new TreeSet<TodoTask>();
		SortedSet<DeadlineTask> undoDeadlineUncompletedSet = new TreeSet<DeadlineTask>();
		SortedSet<EventTask> undoEventUncompletedSet = new TreeSet<EventTask>();

		SortedSet<TodoTask> undoTodoCompletedSet = new TreeSet<TodoTask>();
		SortedSet<DeadlineTask> undoDeadlineCompletedSet = new TreeSet<DeadlineTask>();
		SortedSet<EventTask> undoEventCompletedSet = new TreeSet<EventTask>();

		FilterParameter latestFilter = new FilterParameter();		
		
		commandHistory.addUndo(undoTodoUncompletedSet, undoDeadlineUncompletedSet, undoEventUncompletedSet, undoTodoCompletedSet, undoDeadlineCompletedSet, undoEventCompletedSet, latestFilter, 0, 0, 0, 0);
		
		assertFalse(commandHistory.isEmptyUndo());
		
		commandHistory.addUndoAfter(latestFilter, 1, 2, 3, 4);
		
		assertFalse(commandHistory.isEmptyUndo());
		
		commandHistory.getUndoDeadlineCompleted();
		commandHistory.getUndoDeadlineUncompleted();
		commandHistory.getUndoEventCompleted();
		commandHistory.getUndoEventUncompleted();
		commandHistory.getUndoTodoCompleted();
		commandHistory.getUndoTodoUncompleted();
		commandHistory.getUndoFilterParameter();
		commandHistory.getUndoFilterParameterAfter();
		int[] undoPageBefore = commandHistory.getUndoPageChanged();
		assertEquals(0, undoPageBefore[0]);
		assertEquals(0, undoPageBefore[1]);
		assertEquals(0, undoPageBefore[2]);
		int[] undoPageAfter = commandHistory.getUndoPageChangedAfter();
		assertEquals(2, undoPageAfter[0]);
		assertEquals(3, undoPageAfter[1]);
		assertEquals(4, undoPageAfter[2]);
		int undoTabBefore = commandHistory.getUndoTabSelected();
		assertEquals(0, undoTabBefore);
		int undoTabAfter = commandHistory.getUndoTabSelectedAfter();
		assertEquals(1, undoTabAfter);
		
		assertTrue(commandHistory.isEmptyUndo());
	}
	
	@Test
	public void testRedo() {
		CommandHistory commandHistory = new CommandHistory();
		assertTrue(commandHistory.isEmptyRedo());

		SortedSet<TodoTask> redoTodoUncompletedSet = new TreeSet<TodoTask>();
		SortedSet<DeadlineTask> redoDeadlineUncompletedSet = new TreeSet<DeadlineTask>();
		SortedSet<EventTask> redoEventUncompletedSet = new TreeSet<EventTask>();

		SortedSet<TodoTask> redoTodoCompletedSet = new TreeSet<TodoTask>();
		SortedSet<DeadlineTask> redoDeadlineCompletedSet = new TreeSet<DeadlineTask>();
		SortedSet<EventTask> redoEventCompletedSet = new TreeSet<EventTask>();

		FilterParameter latestFilter = new FilterParameter();		
		
		commandHistory.addRedo(redoTodoUncompletedSet, redoDeadlineUncompletedSet, redoEventUncompletedSet, redoTodoCompletedSet, redoDeadlineCompletedSet, redoEventCompletedSet, latestFilter, 0, 0, 0, 0);
		
		assertFalse(commandHistory.isEmptyRedo());
		
		commandHistory.addRedoAfter(latestFilter, 1, 2, 3, 4);
		
		assertFalse(commandHistory.isEmptyRedo());
		
		commandHistory.getRedoDeadlineCompleted();
		commandHistory.getRedoDeadlineUncompleted();
		commandHistory.getRedoEventCompleted();
		commandHistory.getRedoEventUncompleted();
		commandHistory.getRedoTodoCompleted();
		commandHistory.getRedoTodoUncompleted();
		commandHistory.getRedoFilterParameter();
		commandHistory.getRedoFilterParameterAfter();
		int[] redoPageBefore = commandHistory.getRedoPageChanged();
		assertEquals(0, redoPageBefore[0]);
		assertEquals(0, redoPageBefore[1]);
		assertEquals(0, redoPageBefore[2]);
		int[] redoPageAfter = commandHistory.getRedoPageChangedAfter();
		assertEquals(2, redoPageAfter[0]);
		assertEquals(3, redoPageAfter[1]);
		assertEquals(4, redoPageAfter[2]);
		int redoTabBefore = commandHistory.getRedoTabSelected();
		assertEquals(0, redoTabBefore);
		int redoTabAfter = commandHistory.getRedoTabSelectedAfter();
		assertEquals(1, redoTabAfter);
		
		assertTrue(commandHistory.isEmptyRedo());
	}
}
