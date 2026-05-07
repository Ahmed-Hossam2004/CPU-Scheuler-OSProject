package osproject;

import java.util.*;

/*
  This class implements the core CPU scheduling logic.
  It manages a logical simulation clock and provides methods to execute
  First-Come First-Served (FCFS), Round Robin (RR), and Priority scheduling.
  It also controls real OS processes using SIGCONT and SIGSTOP signals.
 */
public class SchedulingAlgorithms {
    // Stores the history of process execution for the Gantt Chart UI
    public List<GanttChart> ganttRecord = new ArrayList<>();
    
    // The reference point for real-time tracking (if needed)
    private long simStartTime;
    
    // The discrete simulation clock that counts logical "cycles"
    private int logicalTime = 0;

    /*
      Initializes the simulation state for a new run.
      @param time The starting system timestamp.
     */
    public void setSimStartTime(long time) {
        this.simStartTime = time;
        this.ganttRecord.clear();
        this.logicalTime = 0;
    }

    /*
      Handles the low-level execution of a process burst.
      Manages the logical clock, Gantt recording, and Linux signal control.
      @param pcb           The process to be executed.
      @param runTimeCycles The number of cycles to run in this specific burst.
     */
    private void manageBurst(PCB pcb, int runTimeCycles) {
        /*
          IDLE TIME LOGIC: 
          If the CPU clock is at cycle 5 but the process doesn't arrive until cycle 10,
          we must advance the clock to cycle 10 (simulating CPU idle time).
         */
        if (logicalTime < pcb.getArrivalCycle()) {
            logicalTime = pcb.getArrivalCycle();
        }

        try {
            // 1. Record the start of this execution burst for the UI
            int burstStart = logicalTime;
            ganttRecord.add(new GanttChart(pcb.getPid(), burstStart, burstStart + runTimeCycles));

            /*
              2. REAL PROCESS CONTROL (Linux Only):
              We send a SIGCONT signal to wake up the real Ubuntu worker process.
             */
            if (!System.getProperty("os.name").toLowerCase().contains("win")) {
                Runtime.getRuntime().exec("/usr/bin/kill -CONT " + pcb.getOsPid());
            }

            /*
              3. BURST SIMULATION:
              We slow down the simulation (100ms per cycle) so the user can see 
              the progress in the GUI in real-time.
             */
            Thread.sleep(runTimeCycles * 100L); 

            /*
              4. PREEMPTION/PAUSE (Linux Only):
              We send a SIGSTOP signal to pause the worker process until its next turn.
             */
            if (!System.getProperty("os.name").toLowerCase().contains("win")) {
                Runtime.getRuntime().exec("/usr/bin/kill -STOP " + pcb.getOsPid());
            }

            // Advance the logical simulation clock
            logicalTime += runTimeCycles;

        } catch (Exception e) {
            e.printStackTrace();
            pcb.terminate(); // Clean up the OS process if an error occurs
        }
    }

    /*
      Executes First-Come, First-Served scheduling.
      Processes are executed in the order of their arrival time.
      @param queue The list of processes to schedule.
     */
    public void executeFCFS(List<PCB> queue) {
        // Ensure chronological order based on arrival cycle
        queue.sort(Comparator.comparingInt(PCB::getArrivalCycle));

        for (PCB p : queue) {
            manageBurst(p, p.getBurstTime());
            p.setEndCycle(logicalTime); // Store finish time for statistics
            p.terminate();              // Process is finished; kill real OS process
        }
    }

    /*
      Executes Round Robin scheduling with preemption.
      Each process runs for a maximum of 'quantum' cycles before being moved 
      to the back of the queue.
      @param queue   The list of processes to schedule.
      @param quantum The maximum time slice for each process burst.
     */
    

   
    public void executeRR(List<PCB> allProcesses, int quantum) {
    // 1. Sort the master list by arrival time
    allProcesses.sort(Comparator.comparingInt(PCB::getArrivalCycle));
    
    // 2. The active queue of processes that have "arrived"
    Queue<PCB> readyQueue = new LinkedList<>();
    
    int n = allProcesses.size();
    int processIndex = 0;
    int completedCount = 0;

    // Reset logical time if necessary, or start from 0
    logicalTime = 0; 

    while (completedCount < n) {
        // STEP A: Fill the queue with processes that arrived AT or BEFORE the current time
        while (processIndex < n && allProcesses.get(processIndex).getArrivalCycle() <= logicalTime) {
            readyQueue.add(allProcesses.get(processIndex));
            processIndex++;
        }

        // STEP B: Handle CPU Idle Time
        // If no one is ready, but we aren't done yet, jump to the arrival of the next process
        if (readyQueue.isEmpty()) {
            if (processIndex < n) {
                logicalTime = allProcesses.get(processIndex).getArrivalCycle();
                // Re-run this loop iteration to add the process that just "arrived"
                continue; 
            }
        }

        // STEP C: Execute the head of the queue
        PCB currentP = readyQueue.poll();
        int burst = Math.min(currentP.getRemainingTime(), quantum);
        
        // This method increases 'logicalTime' and manages the Linux SIGCONT/SIGSTOP
        manageBurst(currentP, burst);
        currentP.setRemainingTime(currentP.getRemainingTime() - burst);

        // STEP D: Check for NEW arrivals that happened WHILE 'currentP' was running
        while (processIndex < n && allProcesses.get(processIndex).getArrivalCycle() <= logicalTime) {
            readyQueue.add(allProcesses.get(processIndex));
            processIndex++;
        }

        // STEP E: Re-queue currentP if not finished, otherwise terminate
        if (currentP.getRemainingTime() > 0) {
            readyQueue.add(currentP);
        } else {
            currentP.setEndCycle(logicalTime);
            currentP.terminate();
            completedCount++;
        }
    }
}
     /*
      Executes Non-preemptive Priority scheduling.
      Processes are executed based on their priority value (lower value = higher priority).
      @param queue The list of processes to schedule.
     */
    public void executePriority(List<PCB> queue) {
        // Primary sort by priority, secondary sort by arrival (tie-breaker)
        queue.sort(Comparator.comparingInt(PCB::getPriority)
                  .thenComparingInt(PCB::getArrivalCycle));

        for (PCB p : queue) {
            manageBurst(p, p.getBurstTime());
            p.setEndCycle(logicalTime);
            p.terminate();
        }
    }
}
