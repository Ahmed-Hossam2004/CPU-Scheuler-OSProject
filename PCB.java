package osproject;

import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;

public class PCB {

    private final Process process;           // Real OS process (can be null in simulation)
    private String taskType;
    private final int priority;
    private final long arrivalTime;

    private long startTime = -1;
    private long completionTime = -1;
    private long endTime = -1;

    // Active (CPU) time tracking
    private long totalActiveTime = 0;
    private long lastStartTimeNano = 0;      // Using nanoTime for better precision
    private boolean isPaused = true;

    // For scheduling algorithms
    private long remainingBurstTime;

    public PCB(Process process, String taskType, int priority, long arrivalTime, long burstTime) {
        this.process = process;
        this.taskType = Objects.requireNonNull(taskType, "taskType cannot be null");
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.remainingBurstTime = burstTime;
    }

    /**
     * Resume execution and start measuring active time.
     */
    public void resumeExecution() {
        if (isPaused) {
            this.lastStartTimeNano = System.nanoTime();
            this.isPaused = false;

            if (startTime == -1) {
                this.startTime = System.currentTimeMillis(); // Wall-clock start for scheduling
            }

            // Optional: resume real process if suspended (platform dependent)
        }
    }

    /**
     * Pause execution and accumulate active time.
     */
    public void pauseExecution() {
        if (!isPaused) {
            long elapsedNano = System.nanoTime() - lastStartTimeNano;
            this.totalActiveTime += elapsedNano / 1_000_000; // Convert to ms
            this.isPaused = true;
        }
    }

    /**
     * Terminate the real OS process and finalize timing.
     */
    public void terminate() {
        pauseExecution(); // Ensure we capture final active time

        if (process != null) {
            process.destroy();
            try {
                process.waitFor(); // Optional: wait for termination
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (completionTime == -1) {
            this.completionTime = System.currentTimeMillis();
        }
        this.endTime = System.currentTimeMillis();
    }

    // ==================== Getters & Setters ====================

    public long getTotalActiveTime() {
        return totalActiveTime;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public int getPriority() {
        return priority;
    }

    public String getTaskType() {
        return taskType;
    }

    public long getRemainingTime() {
        return remainingBurstTime;
    }

    public void setRemainingTime(long remainingTime) {
        if (remainingTime < 0)
            throw new IllegalArgumentException("Remaining time cannot be negative");
        this.remainingBurstTime = remainingTime;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getStartTime() {
        return (int) startTime; // Consider changing return type to long
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Process getProcess() {
        return process;
    }

    public boolean isPaused() {
        return isPaused;
    }

    // ==================== Scheduling Helpers ====================

    /**
     * Static comparator for Priority Scheduling (higher number = higher priority)
     */
    public static Comparator<PCB> getPriorityComparator() {
        return Comparator.comparingInt(PCB::getPriority).reversed();
    }

    /**
     * Reduce remaining time (used in simulations)
     */
    public void executeFor(long timeSlice) {
        this.remainingBurstTime = Math.max(0, this.remainingBurstTime - timeSlice);
    }

    @Override
    public String toString() {
        return String.format("PCB[Type=%s, Priority=%d, Arrival=%d, Remaining=%d, ActiveTime=%dms]",
                taskType, priority, arrivalTime, remainingBurstTime, totalActiveTime);
    }

    // ==================== Demo / Test Method ====================

    public static void main(String[] args) {
        try {
            ProcessBuilder pb = new ProcessBuilder("notepad");
            Process realProcess = pb.start();

            // burstTime example: 5000 ms
            PCB pcb = new PCB(realProcess, "Computational Task", 1, 
                             System.currentTimeMillis(), 5000);

            System.out.println("Starting task... " + pcb);

            pcb.resumeExecution();
            Thread.sleep(2000);

            System.out.println("Pausing task...");
            pcb.pauseExecution();

            Thread.sleep(1000); // Simulate waiting

            System.out.println("Resuming task...");
            pcb.resumeExecution();
            Thread.sleep(1500);

            pcb.terminate();

            System.out.println("Task finished.");
            System.out.println(pcb);
            System.out.println("Total active time: " + pcb.getTotalActiveTime() + " ms");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}