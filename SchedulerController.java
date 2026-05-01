package osproject;

import java.util.*;

public class SchedulerController 
{
    private List<ProcessControlBlock> readyQueue = new ArrayList<>();
    private SchedulingAlgorithms algorithms = new SchedulingAlgorithms();
    private TelemtryReporter reporter = new TelemtryReporter();
    
        // Main execution engine for the CPU Scheduler simulation.
        // Coordinates workload generation, algorithm selection, and reporting.
        // 1. GENERATION: Spawns real Ubuntu child processes (alternating CPU/IO).
        // 2. QUEUEING: Populates the Ready Queue with Process Control Blocks.
        // 3. POLICY: Dispatches the queue to the selected algorithm (FCFS, RR, or Priority).
        // 4. ANALYSIS: Triggers TelemetryReporter once all processes terminate. 

    public void runScheduler (String mode, int workerCount)
    {
        ProcessLauncher launcher = new ProcessLauncher();
        
        // MECHANISM: Mixed Workload Generation
        // CPU-BOUND: Heavy math tasks used to test for the 'Convoy Effect' in FCFS.
        // IO-BOUND: Processes that yield the CPU, testing context switching efficiency.
        // STRATEGY: Mixing types (i % 2) creates a realistic scenario to compare 
        // how algorithms handle different process behaviors and waiting times.

        for (int i = 0; i < workerCount; i++)
        {
            String type;
            
            if (i%2 == 0)
            {
                type = "CPU";
            } else 
            {
                type = "IO";
            }
            readyQueue.add(launcher.createWorker(type, i));
        }
        System.out.println("Executing " + mode + " policy...");
        
        // Order to select which algorithm to execute depending on the mode.
        
        switch (mode.toUpperCase())
        {
            case "FCFS":
                algorithms.executeFCFS(readyQueue);
                break;
            case "RR":
                algorithms.executeRR(readyQueue, 2000);
                break;
            case "PRIORITY":
                algorithms.executePriority(readyQueue);
                break; 
            default:
                System.out.println("Error: Unknown Algorithm Mode!");
        }
        
        reporter.showComparisonResults(readyQueue);
    }
    
}
