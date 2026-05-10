package osproject;

/*
  The WorkerProcess class represents a real child process spawned by the simulator.
  It simulates two types of OS workloads:
  1. CPU-Bound: High computational tasks using mathematical operations.
  2. I/O-Bound: Tasks that block execution (simulated with Thread.sleep).
 */
public class WorkerProcess {
    /*
      Main entry point for the child process.
      @param args Command line arguments:
      args[0] - The task type ("CPU" or "IO")
      args[1] - The duration in logical cycles
     */
    public static void main(String[] args) {
        // Parse input arguments or use default values if none provided
        String type = (args.length > 0) ? args[0] : "CPU";
        int duration = (args.length > 1) ? Integer.parseInt(args[1]) : 5;

        System.out.println("Worker started. Type: " + type + " for " + duration + " cycles.");

        try {
            /*
              Loop through the assigned duration.
              Each iteration represents one "logical cycle" of work.
             */
            for (int i = 1; i <= duration; i++) {
                
                if (type.equalsIgnoreCase("CPU")) {
                    /*
                      CPU-BOUND SIMULATION
                      Performs complex math to consume actual processor cycles.
                      This ensures that when the scheduler sends SIGCONT, 
                      there is a real load being processed.
                     */
                    double result = Math.sqrt(i) * Math.atan(i);
                } else {
                    /*
                      I/O-BOUND SIMULATION
                      Simulates a process waiting for a peripheral or network response.
                      Thread.sleep causes the process to enter a 'Waiting' state.
                     */
                    Thread.sleep(500);
                }
                
                // Feedback for the parent process (logged in standard output)
                System.out.println("Cycle " + i + " completed.");
            }
        } catch (InterruptedException e) {
            /*
              Exception handling for process interruption.
              This occurs if the Java process is terminated while sleeping.
             */
            System.out.println("Worker was interrupted!");
        }
    }
}