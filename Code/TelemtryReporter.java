package osproject;

import java.util.List;

/*
  The TelemtryReporter class handles the post-simulation analysis.
  It calculates and displays key performance metrics, such as Turnaround Time (TAT) 
  and Waiting Time (WT), based on the logical cycles recorded in the PCBs.
 */
public class TelemtryReporter {
    
    /*
      Calculates performance metrics for all processes and prints a formatted
      summary table to the console.
      @param readyQueue The list of processes that have completed their execution.
     */
    public void showComparisonResults(List<PCB> readyQueue) {
        System.out.println("\n--- PERFORMANCE REPORT (Cycle-Based Data) ---");
        System.out.println("---------------------------------------------------------");
        
        int totalTAT = 0;
        int totalWT = 0;

        /*
          Iterate through the processes to calculate individual metrics.
          Formulas used are standard Operating System scheduling metrics.
         */
        for (PCB p : readyQueue) {
            /*
              FORMULA: Turnaround Time (TAT)
              TAT = Completion Time - Arrival Time
              This represents the total time a process spent in the system.
             */
            int tat = p.getEndCycle() - p.getArrivalCycle();
            
            /*
              FORMULA: Waiting Time (WT)
              WT = Turnaround Time - Burst Time
              This represents the total time a process spent in the ready queue 
              without the CPU.
             */
            int wt = tat - p.getBurstTime();

            // Accumulate totals for average calculation
            totalTAT += tat;
            // Math.max(0, wt) prevents negative results in cases of unexpected clock behavior
            totalWT += Math.max(0, wt);

            // Print formatted row for each process
            System.out.printf("Worker %-5s | Type: %-3s | Arrival: %-2d | TAT: %-3d Cycles | WT: %-3d Cycles\n",
                    p.getPid(), 
                    p.getTaskType(), 
                    p.getArrivalCycle(),
                    tat, 
                    Math.max(0, wt));
        }

        /*
          Calculate and display system-wide averages.
         */
        if (!readyQueue.isEmpty()) {
            int size = readyQueue.size();
            System.out.println("---------------------------------------------------------");
            
            // Casting to double ensures precise decimal results for averages
            System.out.printf("Average Turnaround Time: %.2f Cycles\n", (double) totalTAT / size);
            System.out.printf("Average Waiting Time:    %.2f Cycles\n", (double) totalWT / size);
            
            System.out.println("---------------------------------------------------------\n");
        } else {
            System.out.println("No process data found to generate report.");
        }
    }
}