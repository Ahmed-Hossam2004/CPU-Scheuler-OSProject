package osproject;

import java.util.Random;
/**
 *
 * @author ahmad-amr
 */
public class WorkerProcess {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
        
         // Step 1: Handling the Workload Type 
        String type;
        if (args.length > 0) {
            // If the scheduler provided a type, use it
            type = args[0];
        } else {
            // Default type if no argument is passed
            type = "CPU";
        }

        // Step 2: Handling the Duration (Cycles) 
        int duration;
        if (args.length > 1) {
            // Convert the second argument from String to Integer
            duration = Integer.parseInt(args[1]);
        } else {
            // Default duration if no argument is passed
            duration = 5;
        }

        System.out.println("Worker started. Type: " + type + " for " + duration + " cycles.");

        try {
            for (int i = 1; i <= duration; i++) {
                
                //  Step 3: Execution Logic based on Type 
                if (type.equalsIgnoreCase("CPU")) {
                    // Simulate CPU-bound task: Heavy math calculation to stress the processor
                    double result = 0;
                    for (int j = 0; j < 1000000; j++) {
                        result += Math.sqrt(j) * Math.atan(j);
                    }
                } else {
                    // Simulate I/O-bound task: Put the process to sleep (waiting simulation)
                    Thread.sleep(500); 
                }
                
                System.out.println("Cycle " + i + " completed.");
            }
        } catch (InterruptedException e) {
            // This happens if the Scheduler terminates the process unexpectedly
            System.out.println("Worker was interrupted!");
        }

        System.out.println("Worker finished its job.");
        
    }
    
}
    
    
   