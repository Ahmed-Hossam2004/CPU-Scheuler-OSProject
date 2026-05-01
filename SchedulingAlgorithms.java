package osproject;

import java.io.*;
import java.util.*;

public class SchedulingAlgorithms {

    // 1: FCFS - Executes processes in the order they were created
    public void executeFCFS(List<PCB> queue) {
        System.out.println("\n--- Running FCFS Policy ---");
        for (PCB p : queue) {
            executeRealProcess(p, p.burstTime());
            p.setEndTime(System.currentTimeMillis()); // Record completion
        }
    }

    // 2: Priority - Reorders by priority variable[cite: 1]
    public void executePriority(List<PCB> queue) {
        System.out.println("\n--- Running Priority Scheduling ---");
        // Use the getter method getPriority()
        queue.sort(Comparator.comparingInt(PCB::getPriority));
        
        for (PCB p : queue) {
            executeRealProcess(p, p.burstTime());
            p.setEndTime(System.currentTimeMillis());
        }
    }

    // 3: Round Robin - Uses a fixed time quantum[cite: 1]
    public void executeRR(List<PCB> queue, int quantum) {
        System.out.println("\n--- Running Round Robin ---");
        Queue<PCB> readyQueue = new LinkedList<>(queue);
        
        while (!readyQueue.isEmpty()) {
            PCB p = readyQueue.poll();
            
            // Logic: run for quantum or whatever is left
            int timeToRun = Math.min(p.remainingTime(), quantum);

            executeRealProcess(p, timeToRun);
            
            // Corrected: Use setter to update remaining time[cite: 1]
            p.setRemainingTime(p.remainingTime() - timeToRun);

            if (p.remainingTime() > 0) {
                readyQueue.add(p);
            } else {
                p.setEndTime(System.currentTimeMillis());
            }
        }
    }

    private void executeRealProcess(PCB pcb, int burst) {
        // Track the first time the process is active[cite: 1]
        if (pcb.getStartTime() == 0) {
            pcb.setStartTime(System.currentTimeMillis());
        }

        try {
            // Requirement: Launch actual child processes in Ubuntu[cite: 1]
            // Pass the type and the burst duration as arguments (args)
            ProcessBuilder pb = new ProcessBuilder("java", "osproject.WorkerProcess", pcb.getType(), String.valueOf(burst));
            pb.inheritIO(); // So you see the "Cycle completed" messages[cite: 1]
            
            Process realProcess = pb.start(); 
            realProcess.waitFor(); // Wait for this burst to finish[cite: 1]
            
        } catch (Exception e) {
            System.out.println("Execution Error: " + e.getMessage());
        }
    }
}
