package osproject;

/*
  The Process Control Block (PCB) represents a process in our custom OS simulator.
  It stores scheduling metadata (cycles, priority) and maintains a reference to 
  the actual underlying Ubuntu operating system process.
 */
public class PCB {
    // Unique identifier for the process in the simulation (e.g., "P1")
    private final String pid;
    
    // The actual Process ID assigned by the Ubuntu Linux kernel
    private final long osPid;
    
    // Java Process object used to monitor and manage the worker's lifecycle
    private final Process process;
    
    // Category of the task (e.g., "CPU-bound" or "IO-bound")
    private final String taskType;
    
    // Priority level (lower numbers usually indicate higher priority)
    private final int priority;
    
    // --- Time Tracking in Logical Cycles ---
    
    // The cycle number when the process entered the system
    private final int arrivalCycle; 
    
    // The cycle number when the process completed its execution
    private int endCycle = 0;
    
    // The total number of cycles required to complete the task
    private final int burstTime;
    
    // The number of cycles left to execute (used primarily for Round Robin)
    private int remainingTime;

    /*
      Constructs a new PCB and initializes time-tracking metrics.
      @param pid          The simulation Process ID.
      @param osPid       The real PID from the Linux OS.
      @param process      The actual Java Process instance.
      @param taskType     The behavior type of the process.
      @param priority     The scheduling priority level.
      @param burstTime    The total execution time in cycles.
      @param arrivalCycle The entry time in cycles.
     */
    public PCB(String pid, long osPid, Process process, String taskType, int priority, int burstTime, int arrivalCycle) {
        this.pid = pid;
        this.osPid = osPid;
        this.process = process;
        this.taskType = taskType;
        this.priority = priority;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.arrivalCycle = arrivalCycle;
    }

    // --- Getters ---
    
    public String getPid() { return pid; }
    
    /* @return The actual Linux Kernel Process ID. */
    public long getOsPid() { return osPid; } 
    
    public Process getProcess() { return process; }
    public String getTaskType() { return taskType; }
    public int getPriority() { return priority; }
    public int getArrivalCycle() { return arrivalCycle; }
    public int getEndCycle() { return endCycle; }
    public int getBurstTime() { return burstTime; }
    public int getRemainingTime() { return remainingTime; }

    // --- Setters ---

    /* @param endCycle The final cycle count when execution finished. */
    public void setEndCycle(int endCycle) { this.endCycle = endCycle; }
    
    /* @param time The updated remaining burst cycles. */
    public void setRemainingTime(int time) { this.remainingTime = time; }

    /*
      Terminates the real Ubuntu process.
      This method ensures that worker processes are cleaned up from the host 
      operating system after the simulation finishes.
     */
    public void terminate() {
        if (process != null && process.isAlive()) {
            process.destroyForcibly();
        }
    }
}