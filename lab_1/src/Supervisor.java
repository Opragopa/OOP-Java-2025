import static java.lang.System.out;

public class Supervisor implements Runnable {
    final int STATUS_CHECK_DELAY_IN_SECONDS = 2;
    private final AbstractProgram program;
    private volatile boolean running = true;

    public Supervisor(AbstractProgram program) {
        this.program = program;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        out.println("Supervisor starting...");

        while (running) {
            AbstractProgram.State state = program.getState();
            out.println("Current state: " + state);

            switch (state) {
                case FATAL_ERROR -> {
                    out.println("FATAL ERROR OCCURRED");
                    program.stop();
                    running = false;
                }
                case STOPPING -> {
                    out.println("Breaking. Rebooting...");
                    program.setState(AbstractProgram.State.RUNNING);
                }
            }

            try {
                synchronized (program) {
                    program.wait(STATUS_CHECK_DELAY_IN_SECONDS * 1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        out.println("Supervisor shutdown...");
    }
}