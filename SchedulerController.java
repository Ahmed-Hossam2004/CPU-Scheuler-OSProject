package osproject;

import java.util.*;

public class SchedulerController {
    private List<PCB> readyQueue = new ArrayList<>();
    private SchedulingAlgorithms algorithms = new SchedulingAlgorithms();
    private TelemtryReporter reporter = new TelemtryReporter(); // Fixed spelling

    public void runScheduler(String mode, int workerCount) {
        ProcessLauncher launcher = new ProcessLauncher();

        // 1. GENERATION: Create the child process metadata (PCBs)[cite: 1]
        for (int i = 0; i < workerCount; i++) {
            String type;
            if (i % 2 == 0) 
            {
                type = "CPU";
            }else
            {
                type = "IO";
            }
            // The launcher creates the PCB and stamps Arrival Time[cite: 1]
            readyQueue.add(launcher.createWorker(type, i));
        }

        System.out.println("Executing " + mode + " policy...");
        
        // 2. POLICY: Dispatch to the selected algorithm[cite: 1]
        switch (mode.toUpperCase()) {
            case "FCFS":
                algorithms.executeFCFS(readyQueue);
                break;
            case "RR":
                // 2000ms Time Quantum[cite: 1]
                algorithms.executeRR(readyQueue, 2000); 
                break;
            case "PRIORITY":
                algorithms.executePriority(readyQueue);
                break; 
            default:
                System.out.println("Error: Unknown Algorithm Mode!");
                return;
        }
        
        // 3. ANALYSIS: Report results like Turnaround and Waiting time[cite: 1]
        reporter.showComparisonResults(readyQueue);
    }
}
