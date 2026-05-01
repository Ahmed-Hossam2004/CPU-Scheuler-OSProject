package osproject;

import java.util.List;

class TelemtryReporter {
    
    // PERFORMANCE ANALYSIS: Calculates system efficiency once processes finish.
    // 1. DATA ACCESS: Reads completion and arrival timestamps from each PCB.
    // 2. MATH: Calculates Turnaround Time (TAT) for every individual worker.
    // 3. AVERAGING: Finds the system-wide average to compare algorithms.
    // 4. DISPLAY: Prints a simple summary table for the final report.

    public void showComparisonResults(List<ProcessControlBlock> readyQueue) 
    {
        System.out.println("\n--- FINAL PERFORMANCE REPORT ---");
        
        long totalTurnaround = 0;
        int count = readyQueue.size();
        
        for (int i = 0; i < count; i++)
        {
            ProcessControlBlock pcb = readyQueue.get(i);
            
            // calculate TAT = completion Time - Arrival Time.
            long tat = pcb.getCompletionTime() - pcb.getArrivalTime();
            totalTurnaround += tat;
            
            // outPut line for each process.
            System.out.println("Worker " + i + " [" + pcb.getTaskType() + "] | TAT: " + tat + " ms");
        }
        if (count >0)
        {
            double averageTAT = (double)totalTurnaround / count;
            System.out.println("---------------------------------");
            System.out.printf("Average Turnaround Time: %.2f ms\n", averageTAT);
            System.out.println("---------------------------------\n");
        }
    }
    
}
