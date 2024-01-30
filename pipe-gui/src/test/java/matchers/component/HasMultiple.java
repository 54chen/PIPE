package matchers.component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.mockito.ArgumentMatcher;

import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;

/**
 * Class that can have multiple @link{Has} items for a Connectable type
 */
public class HasMultiple<T extends PetriNetComponent> implements ArgumentMatcher<T> {
    List<Has<T>> has_items = new LinkedList<>();

    public HasMultiple(Has<T>... items)
    {
        Collections.addAll(has_items, items);
    }
    @Override
    public boolean matches(T argument) {
      T connectable = (T) argument;
      for (Has<T> has : has_items)
      {
          if (!has.matches(connectable))
          {
              return false;
          }
      }
      return true;

    }
}