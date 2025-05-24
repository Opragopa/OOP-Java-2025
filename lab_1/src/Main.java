import java.util.Random;
import static java.lang.System.out;

public class Main {
    public static void main(String[] args) {
        AbstractProgram program = new AbstractProgram() {
            private final Random random = new Random();

            @Override
            public void startRandomChanges() {
                Thread changeThread = new Thread(() -> {
                    while (isRunning) {
                        try {
                            Thread.sleep(2000 + random.nextInt(3000));
                            State[] states = {State.RUNNING, State.STOPPING, State.FATAL_ERROR};
                            setState(states[random.nextInt(states.length)]);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                });
                changeThread.setDaemon(true);
                changeThread.start();
            }
        };

        Supervisor supervisor = new Supervisor(program);
        program.startRandomChanges();

        Thread supervisorThread = new Thread(supervisor);
        supervisorThread.start();

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        supervisor.stop();
        program.stop();
        //System.exit(0);

        try {
            supervisorThread.interrupt();
            supervisorThread.join(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        out.println("Main: Program stopped");

    }
}