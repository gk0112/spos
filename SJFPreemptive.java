import java.util.*;

public class SJFPreemptive {

    static class Process {
        String pid;
        int arrival, burst, remaining, completion, turnaround, waiting;

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

        processes.sort(Comparator.comparingInt(p -> p.arrival));

        int time = 0, completed = 0;
        int[] finishTime = new int[n];
        int[] waitingTime = new int[n];
        int[] turnaroundTime = new int[n];
        boolean[] isCompleted = new boolean[n];

        while (completed != n) {
            int shortest = -1;
            int minRemaining = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                Process p = processes.get(i);
                if (p.arrival <= time && p.remaining > 0) {
                    if (p.remaining < minRemaining) {
                        minRemaining = p.remaining;
                        shortest = i;
                    }
                }
            }

            if (shortest == -1) {
                time++;
                continue;
            }

            Process current = processes.get(shortest);
            current.remaining--;
            time++;

            if (current.remaining == 0) {
                completed++;
                current.completion = time;
                current.turnaround = current.completion - current.arrival;
                current.waiting = current.turnaround - current.burst;

                finishTime[shortest] = current.completion;
                turnaroundTime[shortest] = current.turnaround;
                waitingTime[shortest] = current.waiting;

                isCompleted[shortest] = true;
            }
        }

        System.out.println("\n--- SJF (Preemptive) CPU Scheduling ---\n");
        System.out.printf("%-5s %-10s %-10s %-12s %-12s %-10s\n",
                "PID", "Arrival", "Burst", "Completion", "Turnaround", "Waiting");

        double totalTAT = 0, totalWT = 0;
        for (Process p : processes) {
            System.out.printf("%-5s %-10d %-10d %-12d %-12d %-10d\n",
                    p.pid, p.arrival, p.burst, p.completion, p.turnaround, p.waiting);
            totalTAT += p.turnaround;
            totalWT += p.waiting;
        }

        System.out.printf("\nAverage Turnaround Time: %.2f\n", totalTAT / n);
        System.out.printf("Average Waiting Time: %.2f\n", totalWT / n);

        sc.close();
    }
}
