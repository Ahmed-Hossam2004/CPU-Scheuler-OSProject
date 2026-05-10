package osproject;

import java.io.IOException;
import java.io.File;

/*
  The ProcessLauncher class is responsible for spawning real operating system processes.
  It uses the Java ProcessBuilder to execute the WorkerProcess as a separate child 
  process and immediately manages its execution state via Linux signals.
 */
public class ProcessLauncher {
    
    /*
      Creates and launches a new worker process in the host OS.
      @param pid           The logical Process ID assigned by the GUI.
      @param type          The behavior type (CPU or IO bound).
      @param priority      The scheduling priority.
      @param burstCycles   The total workload duration.
      @param arrivalCycle  The logical time this process enters the system.
      @return              A PCB object containing the real OS PID and process handle, 
      or null if the launch fails.
     */
    public PCB createWorker(String pid, String type, int priority, int burstCycles, int arrivalCycle) {
        try {
            /*
              1. CONSTRUCT COMMAND
              We execute a new JVM instance running 'osproject.WorkerProcess'.
              -cp: Passes the current classpath so Java can find the compiled classes.
             */
            ProcessBuilder pb = new ProcessBuilder(
                    "java",
                    "-cp", System.getProperty("java.class.path"),
                    "osproject.WorkerProcess",
                    type,
                    String.valueOf(burstCycles)
            );

            // Set the working directory to the project root
            pb.directory(new File(System.getProperty("user.dir")));
            
            // Merge error and standard output streams for easier debugging
            pb.redirectErrorStream(true);
            
            /*
              2. SPAWN PROCESS
              Starts the process and captures the Kernel-assigned Process ID (PID).
             */
            Process ubuntuProcess = pb.start();
            long osPid = ubuntuProcess.toHandle().pid();

            /*
              3. STATE MANAGEMENT (SIGSTOP)
              In an OS, a new process should enter the 'READY' state, not 'RUNNING'.
              We send a SIGSTOP signal (-STOP) to pause the process immediately.
              This ensures the process only consumes CPU cycles when the scheduler
              explicitly sends a SIGCONT signal later.
             */
            if (!System.getProperty("os.name").toLowerCase().contains("win")) {
                // Using absolute path for 'kill' ensures compatibility in Ubuntu environments
                new ProcessBuilder("/usr/bin/kill", "-STOP", String.valueOf(osPid)).start();
            }

            /*
              4. DATA ENCAPSULATION
              Wrap the real OS process handle and metadata into a PCB for the scheduler.
             */
            return new PCB(pid, osPid, ubuntuProcess, type, priority, burstCycles, arrivalCycle);
            
        } catch (IOException e) {
            // Logs failures (e.g., java not in PATH or insufficient permissions)
            System.err.println("CRITICAL: Failed to launch process " + pid + ": " + e.getMessage());
            return null;
        }
    }
}