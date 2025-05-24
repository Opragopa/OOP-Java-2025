import java.util.Random;

public class Main {
    public static void main(String[] args) {
        AbstractProgram program = new AbstractProgram() {
            private final Random random = new Random();

            @Override
            public void startRandomChanges() {
                new Thread(() -> {
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
                }).start();
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

        try {
            supervisorThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}