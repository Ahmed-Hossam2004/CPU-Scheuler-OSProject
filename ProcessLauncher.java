package osproject;

import java.io.IOException;

class ProcessLauncher {
    
    // FACTORY CLASS: Interfaces with the Ubuntu OS to spawn worker processes.
    // 1. COMMANDS: Defines specific terminal commands for CPU and IO tasks.
    // 2. EXECUTION: Uses ProcessBuilder to launch child processes in the kernel.
    // 3. ENCAPSULATION: Wraps live processes into PCBs with arrival timestamps.
    // 4. HANDOFF: Returns initialized PCBs to the Controller for scheduling.

    public ProcessControlBlock createWorker(String type, int priority) 
    {
        String command;
        
        if (type.equalsIgnoreCase("CPU"))
        {
            // long-running command that does math
            command = "cat /dev/urandom | md5sum";
        } else
        {
            // command that search the file system
            command = "find / -name 'test_file'";
        }
        
        try
        {
            // record the exact time of arrival.
            long arrivalTime = System.currentTimeMillis();
            
            // launch the process inthe UPUNTu Kernel 
            // use "sh", "-c" to allow the use of pipes (|) in the command.
            
            ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
            Process ubuntuProcess = pb.start();
            
            // wrap the live process into a new PCB and return it.
            return new ProcessControlBlock(ubuntuProcess, type, priority, arrivalTime);
        } catch (IOException e)
        {
            System.out.println("Critical Error: Could not launch Ubuntu process.");
            e.printStackTrace();
            return null;  
        }
    }
    
}
