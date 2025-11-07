import java.util.*;

public class PriorityScheduling {

    static class Process {
        String pid;
        int arrival, burst, priority, start, completion, turnaround, waiting;

        Process(String pid, int arrival, int burst, int priority) {
            this.pid = pid;
            this.arrival = arrival;
            this.burst = burst;
            this.priority = priority;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Process> processes = new ArrayList<>();

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        for (int i = 0; i < n; i++) {
            System.out.println("\nEnter details for process " + (i + 1) + ":");
            System.out.print("Enter Process ID: ");
            String pid = sc.next();
            System.out.print("Enter Arrival Time: ");
            int arrival = sc.nextInt();
            System.out.print("Enter Burst Time: ");
            int burst = sc.nextInt();
            System.out.print("Enter Priority (lower number = higher priority): ");
            int priority = sc.nextInt();
            processes.add(new Process(pid, arrival, burst, priority));
        }

        // Sort by arrival time first, then by priority
        processes.sort(Comparator.comparingInt((Process p) -> p.arrival)
                .thenComparingInt(p -> p.priority));

        int time = 0;
        List<Process> completed = new ArrayList<>();
        List<Process> remaining = new ArrayList<>(processes);

        while (!remaining.isEmpty()) {
            List<Process> readyQueue = new ArrayList<>();
            for (Process p : remaining) {
                if (p.arrival <= time) readyQueue.add(p);
            }

            if (readyQueue.isEmpty()) {
                time++;
                continue;
            }

            readyQueue.sort(Comparator.comparingInt(p -> p.priority));
            Process current = readyQueue.get(0);

            current.start = time;
            current.completion = time + current.burst;
            current.turnaround = current.completion - current.arrival;
            current.waiting = current.turnaround - current.burst;

            time = current.completion;
            completed.add(current);
            remaining.remove(current);
        }

        System.out.println("\n--- Priority Scheduling (Non-Preemptive) ---\n");
        System.out.printf("%-5s %-10s %-10s %-10s %-10s %-12s %-12s %-10s\n",
                "PID", "Arrival", "Burst", "Priority", "Start", "Completion", "Turnaround", "Waiting");

        double totalTAT = 0, totalWT = 0;
        for (Process p : completed) {
            System.out.printf("%-5s %-10d %-10d %-10d %-10d %-12d %-12d %-10d\n",
                    p.pid, p.arrival, p.burst, p.priority, p.start, p.completion, p.turnaround, p.waiting);
            totalTAT += p.turnaround;
            totalWT += p.waiting;
        }

        System.out.printf("\nAverage Turnaround Time: %.2f\n", totalTAT / n);
        System.out.printf("Average Waiting Time: %.2f\n", totalWT / n);

        sc.close();
    }
}
