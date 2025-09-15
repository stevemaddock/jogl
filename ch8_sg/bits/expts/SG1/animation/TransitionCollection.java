package animation;

import java.util.HashMap;
import java.util.Map;

public class TransitionCollection {

  private Map<String,Transition<?>> transitions;

  public TransitionCollection() {
    transitions = new HashMap<String, Transition<?>>();
  }

  public void add(String name, Transition<?> transition) {
    transitions.put(name, transition);
  }

  public Transition<?> get(String name) {
    return transitions.get(name);
  }
  
}