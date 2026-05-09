[README_1.md](https://github.com/user-attachments/files/27551292/README_1.md)
<div align="center">

```
╔══════════════════════════════════════════════════════════════════╗
║                                                                  ║
║        ██████╗██████╗ ██╗   ██╗    ███████╗ ██████╗██╗  ██╗     ║
║       ██╔════╝██╔══██╗██║   ██║    ██╔════╝██╔════╝██║  ██║     ║
║       ██║     ██████╔╝██║   ██║    ███████╗██║     ███████║     ║
║       ██║     ██╔═══╝ ██║   ██║    ╚════██║██║     ██╔══██║     ║
║       ╚██████╗██║     ╚██████╔╝    ███████║╚██████╗██║  ██║     ║
║        ╚═════╝╚═╝      ╚═════╝     ╚══════╝ ╚═════╝╚═╝  ╚═╝     ║
║                                                                  ║
║              S C H E D U L E R   S I M U L A T O R              ║
║          MSA University  ·  CSE264 Operating Systems             ║
╚══════════════════════════════════════════════════════════════════╝
```

<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
<img src="https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white"/>
<img src="https://img.shields.io/badge/Java%20Swing-GUI-4A90D9?style=for-the-badge&logo=java&logoColor=white"/>
<img src="https://img.shields.io/badge/Linux%20Signals-SIGSTOP%2FSIGCONT-FCC624?style=for-the-badge&logo=linux&logoColor=black"/>
<img src="https://img.shields.io/badge/Algorithm-FCFS%20%7C%20RR%20%7C%20Priority-9B59B6?style=for-the-badge"/>
<img src="https://img.shields.io/badge/Pattern-MVC-2ECC71?style=for-the-badge"/>

<br/><br/>

> **"Bridging the gap between scheduling theory and OS reality —**
> **real processes, real signals, real control."**

<br/>

</div>

---

## 📌 Table of Contents

- [About The Project](#-about-the-project)
- [System Architecture](#-system-architecture)
- [Process State Machine](#-process-state-machine)
- [Features](#-features)
- [Scheduling Algorithms](#-scheduling-algorithms)
- [Class Breakdown](#-class-breakdown)
- [How It Works — Under the Hood](#-how-it-works--under-the-hood)
- [Test Cases & Results](#-test-cases--results)
- [Getting Started](#-getting-started)
- [Team](#-team)
- [References](#-references)

---

## 🧠 About The Project

This is **not** your average scheduling simulator that just crunches numbers on paper.

This project is a **fully operational CPU Scheduler Simulator** that spawns **real Ubuntu Linux processes**, controls them with **kernel-level signals (`SIGSTOP` / `SIGCONT`)**, and visualizes their execution live on a **Gantt Chart GUI** built with Java Swing.

It was developed for the **CSE264 Operating Systems** course at **MSA University (Faculty of Engineering)** under **Assoc. Professor Mokhtar A. A. Mohamed**.

### Why This Project Stands Out

```
  ┌──────────────────────────────────┬────────────────────────────────────────┐
  │     Traditional Simulators       │         This Simulator                 │
  ├──────────────────────────────────┼────────────────────────────────────────┤
  │  Calculate numbers theoretically │  Spawns REAL OS processes              │
  │  No kernel involvement           │  Uses SIGSTOP / SIGCONT kernel signals │
  │  Static output                   │  Live Gantt Chart (real-time updates)  │
  │  Fixed single algorithm          │  Switch FCFS / RR / Priority on the fly│
  │  No process differentiation      │  CPU-bound vs I/O-bound worker types   │
  │  No idle time handling           │  Logical clock jumps over CPU gaps     │
  └──────────────────────────────────┴────────────────────────────────────────┘
```

---

## 🏗 System Architecture

The project follows a clean **Model-View-Controller (MVC)** architecture:

```
  ╔══════════════════════════════════════════════════════════════╗
  ║                        VIEW  LAYER                          ║
  ║                                                             ║
  ║    ┌─────────────────────┐     ┌───────────────────────┐   ║
  ║    │   SchedulerGUI.java │────▶│  GanttChartPanel.java │   ║
  ║    │  (Swing Interface)  │     │  (Custom Renderer)    │   ║
  ║    └──────────┬──────────┘     └───────────────────────┘   ║
  ╚═══════════════╪════════════════════════════════════════════╝
                  │  triggers simulation
  ╔═══════════════╪════════════════════════════════════════════╗
  ║               ▼      CONTROLLER  LAYER                     ║
  ║    ┌──────────────────────────────────────┐               ║
  ║    │        SchedulerController.java      │               ║
  ║    │  manages queue · routes algo · report│               ║
  ║    └──────────┬───────────────────────────┘               ║
  ╚═══════════════╪════════════════════════════════════════════╝
        ┌─────────┴──────────┐
        ▼                    ▼
  ╔═══════════╗    ╔══════════════════════════════════════════╗
  ║   MODEL   ║    ║              ENGINE  LAYER               ║
  ║           ║    ║                                          ║
  ║  PCB.java ║    ║  SchedulingAlgorithms.java               ║
  ║           ║    ║  ProcessLauncher.java                    ║
  ║ GanttChart║    ║  WorkerProcess.java                      ║
  ║  .java    ║    ║  TelemtryReporter.java                   ║
  ╚═══════════╝    ╚═══════════════════╤══════════════════════╝
                                       │  spawn + signal
                   ╔═══════════════════▼══════════════════════╗
                   ║           UBUNTU LINUX KERNEL             ║
                   ║   Real PIDs  ·  SIGSTOP  ·  SIGCONT      ║
                   ╚══════════════════════════════════════════╝
```

---

## 🔄 Process State Machine

Every worker process transitions through these states, controlled by Linux signals:

```
                        ProcessLauncher.createWorker()
                                    │
                                    ▼
                          ┌─────────────────┐
                          │      NEW        │  ← JVM spawned via ProcessBuilder
                          └────────┬────────┘
                                   │  SIGSTOP sent immediately
                                   ▼
      ┌──────────────────▶ ┌───────────────┐
      │  time quantum       │     READY     │  ← Paused by kernel,
      │  expired (SIGSTOP)  │   (SIGSTOP)   │    waiting for CPU turn
      │                     └───────┬───────┘
      │                             │  scheduler picks process
      │                             │  SIGCONT sent
      │                             ▼
      │                    ┌───────────────┐
      └────────────────────│    RUNNING    │  ← Actively on CPU
                           │   (SIGCONT)   │    (math loop / IO sleep)
                           └───────┬───────┘
                                   │  remainingTime == 0
                                   ▼
                          ┌─────────────────┐
                          │   TERMINATED    │  ← destroyForcibly()
                          └─────────────────┘
```

---

## ✨ Features

```
  ┌─────────────────────────────────────────────────────────────────┐
  │  🖥️  Java Swing GUI      │  Dark-themed window, live result table│
  │  ⚙️  3 Algorithms        │  FCFS · Round Robin · Priority        │
  │  🔁  Dynamic Quantum     │  Configurable 1–10 cycles (for RR)   │
  │  📊  Live Gantt Chart    │  Custom paintComponent, pixel-scaled  │
  │  🧬  Real OS Processes   │  Each task = actual child JVM on Linux│
  │  📡  Kernel Signals      │  SIGSTOP pauses · SIGCONT resumes     │
  │  📋  Telemetry Report    │  TAT · WT · averages printed to console│
  │  💡  CPU Idle Handling   │  logicalTime jumps over empty gaps    │
  │  🔄  Arrival-Aware RR    │  Processes admitted mid-simulation    │
  │  🧹  Auto Cleanup        │  All worker processes terminated post-run│
  └─────────────────────────────────────────────────────────────────┘
```

---

## 📐 Scheduling Algorithms

### 1. 🟦 FCFS — First-Come, First-Served
> Non-preemptive. Processes execute strictly in the order they arrive.

```
  Ready Queue (sorted by arrivalCycle):
  ┌────┬────┬────┬────┐
  │ P1 │ P2 │ P3 │ P4 │  →  executed left to right, no interruption
  └────┴────┴────┴────┘
```
- ✅ Simple and predictable
- ❌ Susceptible to the **Convoy Effect**
- 📌 Best for: batch systems with uniform burst times

---

### 2. 🟨 RR — Round Robin
> Preemptive time-sharing. Each process gets at most `quantum` cycles before being re-queued.

```
  Circular Queue (quantum = 2):
       ┌────┐     ┌────┐     ┌────┐
  ──▶  │ P1 │ ──▶ │ P2 │ ──▶ │ P3 │ ──▶ (back to P1 if not done)
       └────┘     └────┘     └────┘
        run 2      run 2      run 2
        cycles     cycles     cycles
```
- ✅ Fair — no starvation
- ✅ Arrival-aware (new processes join mid-run)
- ❌ Higher overhead from context switching
- 📌 Best for: interactive, multi-user environments

---

### 3. 🟥 Priority Scheduling
> Non-preemptive. Importance wins. Lower number = higher priority.

```
  Ready Queue (sorted by priority, then arrival as tie-breaker):
  ┌──────────────┬──────────────┬──────────────┐
  │ P2 (pri = 1) │ P3 (pri = 2) │ P1 (pri = 5) │
  └──────────────┴──────────────┴──────────────┘
      executes        then            last
       first          this
```
- ✅ Critical tasks always run first
- ❌ Low-priority processes may starve
- 📌 Best for: real-time or mission-critical systems

---

## 📂 Class Breakdown

```
  osproject/
  │
  ├── 🖥️  SchedulerGUI.java          Main Swing window · input form · result table
  ├── 📊  GanttChartPanel.java       Custom JPanel · paintComponent · pixel scaling
  ├── 📦  GanttChart.java            Data record: { pid, start, end }
  │
  ├── 🗂️  PCB.java                   Process Control Block · all process metadata
  │                                  (pid, osPid, burst, arrival, priority, remaining)
  │
  ├── 🚀  ProcessLauncher.java       Spawns child JVMs via ProcessBuilder
  │                                  Sends initial SIGSTOP → READY state
  │
  ├── ⚙️  WorkerProcess.java         Child process entry point
  │                                  CPU-bound: Math loop | IO-bound: Thread.sleep
  │
  ├── 🧠  SchedulingAlgorithms.java  Core engine: executeFCFS · executeRR · executePriority
  │                                  manageBurst: SIGCONT → sleep → SIGSTOP → tick clock
  │
  ├── 🎛️  SchedulerController.java   MVC Controller · bridges GUI ↔ engine
  │                                  manages readyQueue · sets quantum · triggers report
  │
  └── 📋  TelemtryReporter.java      Post-run console report · TAT · WT · averages
```

---

## 🔬 How It Works — Under the Hood

### Step-by-Step Process Lifecycle

```
  [1] USER ADDS PROCESS IN GUI
          │
          ▼
  [2] ProcessLauncher.createWorker(pid, type, priority, burst, arrival)
       ├─ ProcessBuilder cmd: java -cp <classpath> osproject.WorkerProcess <type> <burst>
       ├─ pb.redirectErrorStream(true)  →  merged stdout/stderr
       ├─ ubuntuProcess.toHandle().pid()  →  captures real Linux PID
       ├─ /usr/bin/kill -STOP <osPid>   →  immediately pauses process (READY state)
       └─ returns new PCB(pid, osPid, process, type, priority, burst, arrival)
          │
          ▼
  [3] USER CLICKS ▶ START  →  runSimulation() spawns a background Thread
          │
          ▼
  [4] SchedulingAlgorithms.manageBurst(pcb, runTimeCycles)
       ├─ if (logicalTime < arrivalCycle) → logicalTime = arrivalCycle  (idle jump)
       ├─ ganttRecord.add(new GanttChart(pid, start, start + cycles))
       ├─ /usr/bin/kill -CONT <osPid>   →  SIGCONT  →  process RUNS
       ├─ Thread.sleep(cycles * 100ms)  →  simulate duration
       ├─ /usr/bin/kill -STOP <osPid>   →  SIGSTOP  →  process PAUSES
       └─ logicalTime += runTimeCycles
          │
          ▼
  [5] PROCESS FINISHES (remainingTime == 0)
       ├─ pcb.setEndCycle(logicalTime)
       └─ pcb.terminate()  →  process.destroyForcibly()
          │
          ▼
  [6] SwingUtilities.invokeLater()  →  updateOutputUI()
       ├─ Result table: TAT = endCycle - arrivalCycle | WT = TAT - burstTime
       └─ GanttChartPanel.setGanttData(ganttRecord)  →  repaint()
```

### Logical Cycle Clock

```
  Wall-clock time is NEVER used for calculations.

  Each burst = a discrete integer (e.g., 3 cycles).
  Thread.sleep(cycles × 100ms) only controls animation speed.

  ┌────────────────────────────────────────────────────────┐
  │  TAT  =  Completion Cycle  −  Arrival Cycle            │
  │  WT   =  TAT  −  Burst Time                            │
  │                                                        │
  │  Results are identical on any machine, regardless of   │
  │  CPU speed or background system load.                  │
  └────────────────────────────────────────────────────────┘
```

---

## 🧪 Test Cases & Results

---

### 🔴 Test 1 — The Convoy Effect `[FCFS]`

> **Goal:** Prove that a single long process arriving first ruins waiting time for all subsequent short processes.

**Input:**

| PID | Arrival | Burst | Priority | Type |
|:---:|:-------:|:-----:|:--------:|:----:|
| P1  | 0       | 12    | 3        | CPU  |
| P2  | 1       | 2     | 2        | IO   |
| P3  | 2       | 2     | 1        | CPU  |

**Gantt Chart:**
```
  ╔════════════════════════════════════════════════╦════════╦════════╗
  ║                      P1                        ║   P2   ║   P3   ║
  ╚════════════════════════════════════════════════╩════════╩════════╝
  0                                               12       14       16
```

**Results:**
```
  ╔══════╦═══════════╦══════════╦══════╦═══════════════════════════════╗
  ║ PID  ║  Arrival  ║  Finish  ║  TAT ║  WT                          ║
  ╠══════╬═══════════╬══════════╬══════╬═══════════════════════════════╣
  ║  P1  ║     0     ║    12    ║  12  ║   0  cycles                  ║
  ║  P2  ║     1     ║    14    ║  13  ║  11  cycles  ⚠️ waiting!      ║
  ║  P3  ║     2     ║    16    ║  14  ║  12  cycles  ⚠️ waiting!      ║
  ╠══════╩═══════════╩══════════╩══════╬═══════════════════════════════╣
  ║                                    ║  Avg TAT: 13.00               ║
  ║                                    ║  Avg WT:   7.67  ⚠️ HIGH      ║
  ╚════════════════════════════════════╩═══════════════════════════════╝
```

> 💡 **Insight:** P1's 12-cycle burst creates a **convoy** — P2 and P3 are tiny (2 cycles each) but must wait over 10 cycles. This is FCFS's biggest weakness.

---

### 🟡 Test 2 — Round Robin Preemption `[RR · Quantum = 3]`

> **Goal:** Verify that SIGSTOP/SIGCONT correctly interrupts and resumes a process mid-burst.

**Input:**

| PID | Arrival | Burst | Priority | Type |
|:---:|:-------:|:-----:|:--------:|:----:|
| P1  | 0       | 7     | 2        | CPU  |
| P2  | 0       | 7     | 2        | IO   |

**Gantt Chart:**
```
  ╔══════════╦══════════╦══════════╦══════════╦════╦════╗
  ║    P1    ║    P2    ║    P1    ║    P2    ║ P1 ║ P2 ║
  ║  (q=3)   ║  (q=3)   ║  (q=3)   ║  (q=3)   ║r=1 ║r=1 ║
  ╚══════════╩══════════╩══════════╩══════════╩════╩════╝
  0          3          6          9          12  13   14
                ↑ SIGSTOP ↑ SIGCONT alternating every 3 cycles
```

**Results:**
```
  ╔══════╦═══════════╦══════════╦══════╦═══════════════════════════════╗
  ║ PID  ║  Arrival  ║  Finish  ║  TAT ║  WT                          ║
  ╠══════╬═══════════╬══════════╬══════╬═══════════════════════════════╣
  ║  P1  ║     0     ║    13    ║  13  ║   6  cycles                  ║
  ║  P2  ║     0     ║    14    ║  14  ║   7  cycles                  ║
  ╠══════╩═══════════╩══════════╩══════╬═══════════════════════════════╣
  ║                                    ║  Avg TAT: 13.50               ║
  ║                                    ║  Avg WT:   6.50               ║
  ╚════════════════════════════════════╩═══════════════════════════════╝
```

> 💡 **Insight:** Both processes share the CPU fairly — no starvation. Each SIGCONT/SIGSTOP pair confirms preemption works correctly at the kernel level.

---

### 🟢 Test 3 — Priority with Late Arrival `[PRIORITY]`

> **Goal:** Test if the comparator correctly prioritizes importance over arrival time.

**Input:**

| PID | Arrival | Burst | Priority     | Type |
|:---:|:-------:|:-----:|:------------:|:----:|
| P1  | 0       | 6     | 5 🔴 Low     | CPU  |
| P2  | 2       | 4     | 1 🟢 High    | IO   |
| P3  | 2       | 3     | 2 🟡 Medium  | CPU  |

**Gantt Chart:**
```
  ╔══════════╦══════════════╦══════════════╗
  ║    P2    ║      P3      ║      P1      ║
  ║ (pri=1)  ║   (pri=2)    ║   (pri=5)    ║
  ╚══════════╩══════════════╩══════════════╝
  0          4              7             13
     HIGH        MEDIUM           LOW
   runs first   runs second     runs last
                                (arrived first but lowest priority!)
```

**Results:**
```
  ╔══════╦═══════════╦══════════╦══════╦═══════════════════════════════╗
  ║ PID  ║  Arrival  ║  Finish  ║  TAT ║  WT                          ║
  ╠══════╬═══════════╬══════════╬══════╬═══════════════════════════════╣
  ║  P2  ║     2     ║     6    ║   4  ║   0  cycles  ✅               ║
  ║  P3  ║     2     ║     9    ║   7  ║   4  cycles  ✅               ║
  ║  P1  ║     0     ║    15    ║  15  ║   9  cycles  ⚠️ starved       ║
  ╠══════╩═══════════╩══════════╩══════╬═══════════════════════════════╣
  ║                                    ║  Avg TAT:  8.67               ║
  ║                                    ║  Avg WT:   4.33               ║
  ╚════════════════════════════════════╩═══════════════════════════════╝
```

> 💡 **Insight:** P1 arrived first (cycle 0) but executes **last** because its priority is the lowest. The comparator correctly overrides arrival order with importance level.

---

### 🔵 Test 4 — CPU Idle Time `[FCFS · The Gap]`

> **Goal:** Ensure the scheduler handles a gap where the CPU has nothing to run.

**Input:**

| PID | Arrival | Burst | Priority | Type |
|:---:|:-------:|:-----:|:--------:|:----:|
| P1  | 0       | 3     | 1        | CPU  |
| P2  | 10      | 4     | 1        | IO   |

**Gantt Chart:**
```
  ╔════════╦══════════════════════════════════════╦══════════════╗
  ║   P1   ║               IDLE                   ║      P2      ║
  ║        ║          (7 cycles gap)               ║              ║
  ╚════════╩══════════════════════════════════════╩══════════════╝
  0        3                                      10             14
           ↑
           logicalTime jumps 3 → 10 automatically
           (no phantom process, no crash)
```

**Results:**
```
  ╔══════╦═══════════╦══════════╦══════╦═══════════════════════════════╗
  ║ PID  ║  Arrival  ║  Finish  ║  TAT ║  WT                          ║
  ╠══════╬═══════════╬══════════╬══════╬═══════════════════════════════╣
  ║  P1  ║     0     ║     3    ║   3  ║   0  cycles  ✅               ║
  ║  P2  ║    10     ║    14    ║   4  ║   0  cycles  ✅               ║
  ╠══════╩═══════════╩══════════╩══════╬═══════════════════════════════╣
  ║                                    ║  Avg TAT:  3.50               ║
  ║                                    ║  Avg WT:   0.00  ✅ PERFECT   ║
  ╚════════════════════════════════════╩═══════════════════════════════╝
```

> 💡 **Insight:** `manageBurst()` detects `logicalTime < arrivalCycle` and jumps the clock to cycle 10. The 7-cycle idle gap is handled cleanly — no crashes, no phantom execution.

---

### 🟣 Test 5 — Perfect Fit Quantum `[RR · Quantum = 3]`

> **Goal:** Observe RR efficiency when burst times are exact multiples of the quantum — no partial slices wasted.

**Input:**

| PID | Arrival | Burst | Priority | Type |
|:---:|:-------:|:-----:|:--------:|:----:|
| P1  | 0       | 6     | 2        | CPU  |
| P2  | 0       | 6     | 2        | IO   |
| P3  | 0       | 6     | 2        | CPU  |

**Gantt Chart:**
```
  ╔══════════╦══════════╦══════════╦══════════╦══════════╦══════════╗
  ║    P1    ║    P2    ║    P3    ║    P1    ║    P2    ║    P3    ║
  ║  (q=3)   ║  (q=3)   ║  (q=3)   ║  (q=3)   ║  (q=3)   ║  (q=3)   ║
  ╚══════════╩══════════╩══════════╩══════════╩══════════╩══════════╝
  0          3          6          9         12         15         18
  ├────────── Round 1 ────────────┤├────────── Round 2 ────────────┤
```

**Results:**
```
  ╔══════╦═══════════╦══════════╦══════╦═══════════════════════════════╗
  ║ PID  ║  Arrival  ║  Finish  ║  TAT ║  WT                          ║
  ╠══════╬═══════════╬══════════╬══════╬═══════════════════════════════╣
  ║  P1  ║     0     ║    12    ║  12  ║   6  cycles                  ║
  ║  P2  ║     0     ║    15    ║  15  ║   9  cycles                  ║
  ║  P3  ║     0     ║    18    ║  18  ║  12  cycles                  ║
  ╠══════╩═══════════╩══════════╩══════╬═══════════════════════════════╣
  ║                                    ║  Avg TAT: 15.00               ║
  ║                                    ║  Avg WT:   9.00               ║
  ╚════════════════════════════════════╩═══════════════════════════════╝
```

> 💡 **Insight:** Burst = 2 × quantum → each process completes in exactly 2 clean turns. Zero wasted cycles, zero partial slices. Queue transitions are perfectly efficient.

---

### 🟠 Test 6 — Staggered Heavy Arrivals `[FCFS vs RR · Quantum = 4]`

> **Goal:** Compare how FCFS and RR handle a "staircase" arrival pattern where each new heavy process arrives exactly when the current one is halfway through.

**Input:**

| PID | Arrival | Burst | Priority | Type |
|:---:|:-------:|:-----:|:--------:|:----:|
| P1  | 0       | 8     | 1        | CPU  |
| P2  | 4       | 8     | 1        | IO   |
| P3  | 8       | 8     | 1        | CPU  |

**FCFS Gantt:**
```
  ╔════════════════╦════════════════╦════════════════╗
  ║       P1       ║       P2       ║       P3       ║
  ╚════════════════╩════════════════╩════════════════╝
  0                8               16               24
```

**RR (Quantum = 4) Gantt:**
```
  ╔════════╦════════╦════════╦════════╦════════╦════════╗
  ║   P1   ║   P2   ║   P3   ║   P1   ║   P2   ║   P3   ║
  ║  (q=4) ║  (q=4) ║  (q=4) ║  (q=4) ║  (q=4) ║  (q=4) ║
  ╚════════╩════════╩════════╩════════╩════════╩════════╝
  0        4        8       12       16       20       24
```

**Head-to-Head Comparison:**
```
  ╔═══════════╦══════════╦══════════╦══════════╦═══════════╦═══════════╗
  ║ Algorithm ║ P1 (TAT) ║ P2 (TAT) ║ P3 (TAT) ║  Avg TAT  ║  Avg WT   ║
  ╠═══════════╬══════════╬══════════╬══════════╬═══════════╬═══════════╣
  ║   FCFS    ║  8 cyc   ║ 12 cyc   ║ 16 cyc   ║   12.00   ║  4.00  ✅ ║
  ║  RR (Q=4) ║ 16 cyc   ║ 16 cyc   ║ 16 cyc   ║   16.00   ║  8.00  ❌ ║
  ╚═══════════╩══════════╩══════════╩══════════╩═══════════╩═══════════╝
```

> 💡 **Insight:** For perfectly staggered equal-burst processes, **FCFS outperforms RR**. RR's context-switching overhead adds up when no process actually benefits from preemption — they all arrive right on time.

---

## 🚀 Getting Started

### Prerequisites

```
  ✅  Java JDK 11 or higher
  ✅  Ubuntu Linux  (SIGSTOP / SIGCONT are Linux-only signals)
  ✅  /usr/bin/kill available in the system path
  ✅  Terminal access for compiling and running
```

### Project Structure

```
  CPU-Scheduler-OSProject/
  ├── src/
  │   └── osproject/
  │       ├── GanttChart.java
  │       ├── GanttChartPanel.java
  │       ├── PCB.java
  │       ├── ProcessLauncher.java
  │       ├── SchedulerController.java
  │       ├── SchedulerGUI.java
  │       ├── SchedulingAlgorithms.java
  │       ├── TelemtryReporter.java
  │       └── WorkerProcess.java
  └── README.md
```

### Compile

```bash
# From project root
javac -d out src/osproject/*.java
```

### Run

```bash
java -cp out osproject.SchedulerGUI
```

### Usage Guide

```
  Step 1 │ Enter PID, Arrival Cycle, Burst Cycles, Priority
  Step 2 │ Click [ Add to List ] — repeat for all processes
  Step 3 │ Select Algorithm:  FCFS  /  RR  /  PRIORITY
  Step 4 │ If using RR → set Quantum value (1 – 10 cycles)
  Step 5 │ Click [ ▶ Start Scheduler ]
  Step 6 │ Watch the Gantt Chart render live in real-time
  Step 7 │ View TAT and WT in the results table
  Step 8 │ Check console for the full Performance Report
  Step 9 │ Click [ ↺ Reset All ] to start a fresh simulation
```

---

## 👥 Team

<div align="center">

```
  ╔═════════════════════════════════════════════════════════════════════════╗
  ║                            PROJECT  TEAM                                ║
  ╠══════════════════════════╦══════════════╦══════════════════════════════╣
  ║  Name                    ║  Student ID  ║  Contributions               ║
  ╠══════════════════════════╬══════════════╬══════════════════════════════╣
  ║  Ahmed Hossam Mohamed    ║   248141     ║  SchedulerGUI.java           ║
  ║  Ezz-Eldin Mohamed       ║              ║  GanttChart.java             ║
  ║                          ║              ║  GanttChartPanel.java        ║
  ║                          ║              ║  SchedulerController.java    ║
  ║                          ║              ║  ProcessLauncher.java        ║
  ╠══════════════════════════╬══════════════╬══════════════════════════════╣
  ║  Ahmed Amr Ahmed         ║   241659     ║  SchedulingAlgorithms.java   ║
  ║  Ismail Jabr             ║              ║  WorkerProcess.java          ║
  ╠══════════════════════════╬══════════════╬══════════════════════════════╣
  ║  Mohamed Amr Mohamed     ║   243835     ║  PCB.java                    ║
  ║  Salama Mohamed Eissa    ║              ║  TelemtryReporter.java       ║
  ╚══════════════════════════╩══════════════╩══════════════════════════════╝

  Teaching Assistant  :  Eng. Hussien Mostafa
  Course Instructor   :  Assoc. Professor Mokhtar A. A. Mohamed
  Institution         :  MSA University — Faculty of Engineering · CSE Dept
  Semester            :  Spring 2026
```

</div>

---

## 📚 References

- Oracle Corporation. *Java SE Documentation — ProcessBuilder Class.*
  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ProcessBuilder.html

- Silberschatz, A., Galvin, P. B., & Gagne, G. (2018). *Operating System Concepts* (10th ed.). Wiley.

- The IEEE and The Open Group. (2018). *The Open Group Base Specifications — kill Utility.*
  https://pubs.opengroup.org/onlinepubs/9699919799/utilities/kill.html

---

<div align="center">

```
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
       CSE264 Operating Systems  ·  MSA University  ·  Spring 2026
              Made with  ☕ Java  and  🐧 Linux signals
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

</div>
