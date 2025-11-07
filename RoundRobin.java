import java.util.*;

public class RoundRobin {

    static class Process {
        String pid;
        int arrival, burst, remaining, completion, turnaround, waiting;
        boolean visited = false;

        Process(String pid, int arrival, int burst) {
            this.pid = pid;
            this.arrival = arrival;
            this.burst = burst;
            this.remaining = burst;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Process> processes = new ArrayList<>();

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        System.out.print("Enter Time Quantum: ");
        int timeQuantum = sc.nextInt();

        for (int i = 0; i < n; i++) {
            System.out.println("\nEnter details for process " + (i + 1) + ":");
            System.out.print("Enter Process ID: ");
            String pid = sc.next();
            System.out.print("Enter Arrival Time: ");
            int arrival = sc.nextInt();
            System.out.print("Enter Burst Time: ");
            int burst = sc.nextInt();
            processes.add(new Process(pid, arrival, burst));
        }

        // Sort by arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrival));

        Queue<Process> readyQueue = new LinkedList<>();
        int time = 0, completed = 0;

        while (completed != n) {
            for (Process p : processes) {
                if (p.arrival <= time && !p.visited) {
                    readyQueue.add(p);
                    p.visited = true;
                }
            }

            if (readyQueue.isEmpty()) {
                time++;
                continue;
            }

            Process current = readyQueue.poll();
            int execTime = Math.min(timeQuantum, current.remaining);
            current.remaining -= execTime;
            time += execTime;

            for (Process p : processes) {
                if (p.arrival <= time && !p.visited) {
                    readyQueue.add(p);
                    p.visited = true;
                }
            }

            if (current.remaining == 0) {
                current.completion = time;
                completed++;
            } else {
                readyQueue.add(current);
            }
        }

        double totalTAT = 0, totalWT = 0;
        for (Process p : processes) {
            p.turnaround = p.completion - p.arrival;
            p.waiting = p.turnaround - p.burst;
            totalTAT += p.turnaround;
            totalWT += p.waiting;
        }

        System.out.println("\n--- Round Robin CPU Scheduling (Preemptive) ---\n");
        System.out.printf("%-5s %-10s %-10s %-12s %-12s %-10s\n",
                "PID", "Arrival", "Burst", "Completion", "Turnaround", "Waiting");

        for (Process p : processes) {
            System.out.printf("%-5s %-10d %-10d %-12d %-12d %-10d\n",
                    p.pid, p.arrival, p.burst, p.completion, p.turnaround, p.waiting);
        }

        System.out.printf("\nAverage Turnaround Time: %.2f\n", totalTAT / n);
        System.out.printf("Average Waiting Time: %.2f\n", totalWT / n);

        sc.close();
    }
}
