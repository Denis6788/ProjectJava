package backtracker;

import java.util.List;

public interface Configuration<T> {
    boolean isGoal();
    List<Configuration<T>> getSuccessors();
    boolean isValid();
}
