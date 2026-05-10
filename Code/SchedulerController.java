package osproject;

import java.util.*;

/*
  The SchedulerController coordinates the interaction between the User Interface 
  and the scheduling logic. It manages the list of active processes (Ready Queue),
  configures simulation parameters like the time quantum, and triggers the 
  selected scheduling algorithm.
 */
public class SchedulerController {
    // The list of Process Control Blocks (PCBs) currently loaded for simulation
    private final List<PCB> readyQueue = new ArrayList<>();
    
    // The engine containing the logic for FCFS, Round Robin, and Priority scheduling
    private final SchedulingAlgorithms algorithms = new SchedulingAlgorithms();
    
    // The time slice duration used exclusively by the Round Robin (RR) algorithm
    private int quantum = 2; 

    /*
      Updates the time quantum value based on user input from the GUI.
      @param quantum The number of logical cycles a process can run before preemption.
     */
    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    /*
      @return The current list of processes in the Ready Queue.
     */
    public List<PCB> getReadyQueue() {
        return readyQueue;
    }

    /*
      Retrieves the visual timeline data generated during the last simulation run.
      @return A list of GanttChart objects used for UI rendering.
     */
    public List<GanttChart> getGanttRecord() {
        return algorithms.ganttRecord;
    }

    /*
      The primary entry point to start the OS simulation.
      This method initializes the simulation clock, executes the chosen algorithm,
      and triggers the final performance reporting.
      
      @param mode         The scheduling algorithm to use ("FCFS", "RR", or "PRIORITY").
      @param simStartTime The system timestamp used to synchronize logical cycles.
     */
    public void runScheduler(String mode, long simStartTime) {
        /*
          1. PREPARATION
          Reset the internal state of the algorithms and set the logical clock to zero.
         */
        algorithms.setSimStartTime(simStartTime);
        
        /*
          2. ALGORITHM EXECUTION
          Route the execution based on the 'mode' string received from the GUI.
         */
        switch (mode) {
            case "FCFS":
                // First-Come, First-Served: Non-preemptive scheduling
                algorithms.executeFCFS(readyQueue);
                break;
            case "RR":
                // Round Robin: Preemptive scheduling using the dynamic quantum
                algorithms.executeRR(readyQueue, quantum);
                break;
            case "PRIORITY":
                // Priority Scheduling: Executes processes based on their importance
                algorithms.executePriority(readyQueue);
                break;
            default:
                System.err.println("Error: Unknown scheduling mode '" + mode + "'.");
                break;
        }

        /*
          3. TELEMETRY & REPORTING
          Once the simulation cycles are complete, generate the performance metrics
          (Wait Time, Turnaround Time) for the console output.
         */
        TelemtryReporter reporter = new TelemtryReporter();
        reporter.showComparisonResults(readyQueue);
    }
}