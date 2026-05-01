package osproject;

import java.io.IOException;
import java.util.Comparator;

public class PCB {
    private final Process process;
    private String taskType;
    private final int priority;
    private final long arrivalTime; // Added to store the 4th argument
    
    private long totalActiveTime = 0;
    private long lastStartTime = 0;
    private boolean isPaused = true;

    // FIX: Updated to accept 4 arguments so Line 88 works
    public PCB(Process process, String taskType, int priority, long arrivalTime) {
        this.process = process;
        this.taskType = taskType;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
    }

    public void resumeExecution() {
        this.lastStartTime = System.currentTimeMillis();
        this.isPaused = false;
    }

    public void pauseExecution() {
        if (!isPaused) {
            this.totalActiveTime += (System.currentTimeMillis() - lastStartTime);
            this.isPaused = true;
        }
    }

    public void terminate() {
        if (!isPaused) {
            pauseExecution();
        }
        if (process != null) {
            process.destroy();
        }
    }

    // FIX: Ensures this method exists for Line 101
    public long getTotalActiveTime() { 
        return totalActiveTime; 
    }

    public static void Love() {
        try {

            ProcessBuilder pb = new ProcessBuilder("notepad"); 
            Process realProcess = pb.start();

            PCB myProcessControlBlock = new PCB(realProcess, "Computational Task", 1, System.currentTimeMillis());

            System.out.println("Starting task...");
            myProcessControlBlock.resumeExecution(); 

            Thread.sleep(2000); 

            System.out.println("Pausing task...");
            myProcessControlBlock.pauseExecution(); 

            myProcessControlBlock.terminate();
           
            System.out.println("Total active time: " + myProcessControlBlock.getTotalActiveTime() + "ms");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    long getCompletionTime() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    long getArrivalTime() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    String getTaskType() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    int burstTime() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    Comparator<PCB> priority() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    int remainingTime() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    long endTime() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    int startTime() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    String getPriority() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    Object getProcess() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void setCompletionTime(long currentTimeMillis) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    public String getType(){
        String type = taskType ;
        return type;
    }
}
