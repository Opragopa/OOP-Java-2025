import static java.lang.System.out;

public abstract class AbstractProgram {
    public enum State {UNKNOWN, STOPPING, RUNNING, FATAL_ERROR}

    protected State state = State.UNKNOWN;
    protected final Object monitor = new Object();
    protected volatile boolean isRunning = true;
    protected boolean firstChange = true;

    public State getState() {
        synchronized (monitor) {
            return state;
        }
    }

    public void setState(State newState) {
        synchronized (monitor) {
            if (firstChange) {
                firstChange = false;
            } else if (newState == State.UNKNOWN) {
                return;
            }

            this.state = newState;
            out.println("State changed to - " + newState);
            monitor.notifyAll();

            if (newState == State.FATAL_ERROR) {
                System.exit(1);
            }
        }
    }

    public void stop() {
        isRunning = false;
    }

    public abstract void startRandomChanges();
}