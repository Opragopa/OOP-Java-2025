import static java.lang.System.out;

public class Supervisor implements Runnable {
    private final AbstractProgram program;
    private volatile boolean running = true;

    public Supervisor(AbstractProgram program) {
        this.program = program;
    }

    public void stop() {
        running = false;
        synchronized (program.monitor) {
            program.monitor.notifyAll();
        }
    }

    public void startProgram() {
        synchronized (program.monitor) {
            program.setState(AbstractProgram.State.RUNNING);
        }
    }

    public void stopProgram() {
        synchronized (program.monitor) {
            program.setState(AbstractProgram.State.STOPPING);
        }
    }

    @Override
    public void run() {
        out.println("Supervisor starting...");

        while (running) {
            synchronized (program.monitor) {
                AbstractProgram.State state = program.getState();
                out.println("Current state: " + state);

                switch (state) {
                    case FATAL_ERROR -> {
                        out.println("FATAL ERROR: Stopping the program...");
                        program.stop();
                        running = false;
                        break;
                    }
                    case STOPPING -> {
                        out.println("Program stopping. Restarting...");
                        program.setState(AbstractProgram.State.RUNNING);
                    }
                }

                try {
                    program.monitor.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        out.println("Supervisor shutdown...");
    }
}