import java.util.*;

class Process {
    String pid;
    int arrival;
    int burst;
    int start;
    int completion;
    int turnaround;
    int waiting;

    public Process(String pid, int arrival, int burst) {
        this.pid = pid;
        this.arrival = arrival;
        this.burst = burst;
    }
}

public class FCFS {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Process> processes = new ArrayList<>();

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        sc.nextLine(); // consume newline

        for (int i = 0; i < n; i++) {
            System.out.println("\nEnter details for process " + (i + 1) + ":");
            System.out.print("Enter Process ID: ");
            String pid = sc.nextLine();
            System.out.print("Enter Arrival Time: ");
            int arrival = sc.nextInt();
            System.out.print("Enter Burst (Service) Time: ");
            int burst = sc.nextInt();
            sc.nextLine(); // consume newline
            processes.add(new Process(pid, arrival, burst));
        }

        // Sort processes by arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrival));

        int currentTime = 0;
        for (Process p : processes) {
            if (currentTime < p.arrival) {
                currentTime = p.arrival;
            }
            p.start = currentTime;
            p.completion = p.start + p.burst;
            p.turnaround = p.completion - p.arrival;
            p.waiting = p.turnaround - p.burst;
            currentTime = p.completion;
        }

        System.out.println("\n--- FCFS CPU Scheduling ---\n");
        System.out.printf("%-5s %-10s %-10s %-10s %-12s %-12s %-10s\n",
                "PID", "Arrival", "Burst", "Start", "Completion", "Turnaround", "Waiting");

        for (Process p : processes) {
            System.out.printf("%-5s %-10d %-10d %-10d %-12d %-12d %-10d\n",
                    p.pid, p.arrival, p.burst, p.start, p.completion, p.turnaround, p.waiting);
        }

        double avgTAT = processes.stream().mapToDouble(p -> p.turnaround).average().orElse(0);
        double avgWT = processes.stream().mapToDouble(p -> p.waiting).average().orElse(0);

        System.out.println("\nAverage Turnaround Time: " + String.format("%.2f", avgTAT));
        System.out.println("Average Waiting Time: " + String.format("%.2f", avgWT));

        sc.close();
    }
}
