package backtracker;

import java.util.Optional;

public class Backtracker<T> {
    private boolean debug;

    public Backtracker(boolean debug) {
        this.debug = debug;
    }

    public Optional<Configuration<T>> solve(Configuration<T> config) {
        if (debug) System.out.println("Checking: " + config);

        if (!config.isValid()) {
            if (debug) System.out.println("Invalid config: " + config);
            return Optional.empty();
        }

        if (config.isGoal()) {
            if (debug) System.out.println("Goal found: " + config);
            return Optional.of(config);
        }

        for (Configuration<T> child : config.getSuccessors()) {
            Optional<Configuration<T>> solution = solve(child);
            if (solution.isPresent()) {
                return solution;
            }
        }

        return Optional.empty();
    }
}
