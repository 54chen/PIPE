package pipe.historyActions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.ac.imperial.pipe.models.petrinet.Transition;

public class AnimationHistoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private AnimationHistoryImpl history;
    private Observer observer;

    @Before
    public void setUp() {
        history = new AnimationHistoryImpl();
        observer = mock(Observer.class);
    }

    @Test
    public void addingTransitionNotifiesObserver() {
        Transition transition = mock(Transition.class);
        history.addObserver(observer);
        history.addHistoryItem(transition);
        verify(observer).update(any(Observable.class), isNull());
    }

    @Test
    public void clearNotifiesObserver() {
        history.addObserver(observer);
        history.clear();
        verify(observer).update(any(Observable.class), isNull());
    }

    @Test
    public void steppingForwardNotifiesObserver() {
        Transition transition = mock(Transition.class);
        history.addHistoryItem(transition);
        history.stepBackwards();
        history.addObserver(observer);
        history.stepForward();
        verify(observer).update(any(Observable.class), isNull());
    }

    @Test
    public void steppingBackwardNotifiesObserver() {
        Transition transition = mock(Transition.class);
        history.addHistoryItem(transition);
        history.addObserver(observer);
        history.stepBackwards();
        verify(observer).update(any(Observable.class), isNull());
    }

    @Test
    public void returnsLatestTransitionAdded() {
        Transition transition = mock(Transition.class);
        history.addHistoryItem(transition);
        assertEquals(transition, history.getCurrentTransition());
    }

    @Test
    public void incrementsCurrentPositionOnAdd() {
        Transition transition = mock(Transition.class);
        history.addHistoryItem(transition);
        assertEquals(0, history.getCurrentPosition());

        history.addHistoryItem(transition);
        assertEquals(1, history.getCurrentPosition());
    }

    @Test
    public void whenEmptyNoStepBackAllowed() {
        assertFalse(history.isStepBackAllowed());
    }

    @Test
    public void whenEmptyNoStepForwardAllowed() {
        assertFalse(history.isStepForwardAllowed());
    }

    @Test
    public void whenContainsOneItemCanStepBack() {
        Transition transition = mock(Transition.class);
        history.addHistoryItem(transition);
        assertTrue(history.isStepBackAllowed());
    }

    @Test
    public void whenAtTailCannotStepBackward() {
        Transition transition = mock(Transition.class);
        history.addHistoryItem(transition);
        history.stepBackwards();
        assertFalse(history.isStepBackAllowed());
    }

    @Test
    public void whenAtHeadCannotStepForward() {
        Transition transition = mock(Transition.class);
        history.addHistoryItem(transition);
        assertFalse(history.isStepForwardAllowed());
    }

    @Test
    public void whenNotAtHeadCanStepForward() {
        Transition transition = mock(Transition.class);
        history.addHistoryItem(transition);
        history.stepBackwards();
        assertTrue(history.isStepForwardAllowed());
    }

    /**
     * Adds three transitions
     * Takes a step back
     * Checks that the last transition has been cleared
     */
    @Test
    public void clearStepsForwardRemovesFutureSteps() {
        Transition transition1 = mock(Transition.class);
        Transition transition2 = mock(Transition.class);
        Transition transition3 = mock(Transition.class);

        history.addHistoryItem(transition1);
        history.addHistoryItem(transition2);
        history.addHistoryItem(transition3);

        history.stepBackwards();
        history.clearStepsForward();

        assertEquals(1, history.getCurrentPosition());
        assertEquals(2, history.getFiringSequence().size());
    }

    @Test
    public void getTransitionReturnsCorrectTransition() {
        Transition transition1 = mock(Transition.class);
        Transition transition2 = mock(Transition.class);

        history.addHistoryItem(transition1);
        history.addHistoryItem(transition2);

        assertEquals(transition1, history.getTransition(0));
        assertEquals(transition2, history.getTransition(1));
    }

    @Test
    public void throwsErrorIfNoTransitionsToGet() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("No transitions in history");
        history.getCurrentTransition();
    }

}
