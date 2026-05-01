package osproject;

import java.io.*;
import java.util.*;

public class SchedulerController {

    public static void main(String[] args) {
        // 1. Create a list that will hold all processes
       
        List<ProcessControlBlock> processList = new ArrayList<>();
        
        // Adding initial processes to the list: (Name, Type, BurstTime, Priority)"Assumed"
        processList.add(new ProcessControlBlock("P1", "CPU", 6, 2)); 
        processList.add(new ProcessControlBlock("P2", "IO", 4, 1));
        processList.add(new ProcessControlBlock("P3", "CPU", 2, 3));

        // 2. Setup scanner to take the user's choice
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select Scheduling Policy:");
        System.out.println("1. First-Come, First-Served (FCFS)");
        System.out.println("2. Priority Control");
        System.out.println("3. Round Robin (RR)");
        
        int choice = scanner.nextInt();

        // Standard if-else to select the scheduling algorithm
        if (choice == 1) {
            runFCFS(processList);
        } else if (choice == 2) {
            runPriority(processList);
        } else if (choice == 3) {
            System.out.print("Enter Time Quantum: ");
            int quantum = scanner.nextInt();
            runRoundRobin(processList, quantum);
        } else {
            System.out.println("Invalid Selection. System exiting.");
        }
    }

    //  1: FCFS 
    // Executes processes in the order they were created
    public static void runFCFS(List<PCB> queue) {
        System.out.println("\n--- Running FCFS Policy ---");
        for (int i = 0; i < queue.size(); i++) {
            PCB p = queue.get(i);
            executeRealProcess(p, p.burstTime);
        }
        printFinalReport(queue);
    }

    // 2: Priority 
    // Reorders the list so that the highest priority runs first
    public static void runPriority(List<PCB> queue) {
        System.out.println("\n--- Running Priority Scheduling ---");
        // Sort the list based on the priority variable in the PCB
        queue.sort(Comparator.comparingInt(p -> p.priority));
        
        for (int i = 0; i < queue.size(); i++) {
            PCB p = queue.get(i);
            executeRealProcess(p, p.burstTime);
        }
        printFinalReport(queue);
    }

    // 3: Round Robin
    // Switches between processes after a fixed time quantum
    public static void runRoundRobin(List<PCB> queue, int quantum) {
        System.out.println("\n--- Running Round Robin ---");
        Queue<PCB> readyQueue = new LinkedList<>(queue);
        
        while (readyQueue.isEmpty() == false) {
            PCB p = readyQueue.poll();
            
            // Determine how many cycles to run in this turn
            int timeToRun;
            if (p.remainingTime > quantum) {
                timeToRun = quantum;
            } else {
                timeToRun = p.remainingTime;
            }

            executeRealProcess(p, timeToRun);
            p.remainingTime = p.remainingTime - timeToRun;

            // If the process still has work, move it to the back 
            if (p.remainingTime > 0) {
                readyQueue.add(p);
            } else {
                // If finished, record the completion time
                p.endTime = System.currentTimeMillis();
            }
        }
        printFinalReport(queue);
    }

    // Core Method: Launching Real Processes 
    //  Handles the actual OS interaction using ProcessBuilder
    private static void executeRealProcess(PCB pcb, int burst) {
        // Record the exact time the process started its first burst
        if (pcb.startTime == 0) {
            pcb.startTime = System.currentTimeMillis();
        }

        try {
            // Runs the command: java WorkerProcess [type] [burst]
            ProcessBuilder pb = new ProcessBuilder("java", "WorkerProcess", pcb.type, String.valueOf(burst));
            
            // Redirect the child process output to our console
            pb.inheritIO(); 
            
            Process realProcess = pb.start(); 
            realProcess.waitFor(); // Wait until the child finishes this burst
            
        } catch (Exception e) {
            System.out.println("Execution Error: " + e.getMessage());
        }
    }

    //  Final Telemetry Report 
    // Calculates and displays the performance of each process
    public static void printFinalReport(List<PCB> results) {
        System.out.println("\n--- Performance Report ---");
        for (int i = 0; i < results.size(); i++) {
            PCB p = results.get(i);
            // Turnaround time = Completion Time - Arrival Time
            long turnaround = p.endTime - p.arrivalTime;
            System.out.println("Process: " + p.name + " | Status: Finished | Turnaround: " + turnaround + " ms");
        }
    }
}
        
        
    
    

