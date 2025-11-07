import java.util.Scanner;

public class FirstFit {
    public static void firstFit(int[] partitions, int[] processes) {
        int n = partitions.length;
        int m = processes.length;
        int[] allocation = new int[m];

        for (int i = 0; i < m; i++) {
            allocation[i] = -1;
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (partitions[j] >= processes[i]) {
                    allocation[i] = j;
                    partitions[j] -= processes[i];
                    break;
                }
            }
        }

        System.out.println("\n--- First Fit Memory Allocation ---\n");
        System.out.printf("%-10s %-15s %-20s\n", "Process", "Size (K)", "Allocated Partition");
        for (int i = 0; i < m; i++) {
            if (allocation[i] != -1) {
                System.out.printf("%-10s %-15d %-20s\n", "P" + (i + 1), processes[i], "Partition " + (allocation[i] + 1));
            } else {
                System.out.printf("%-10s %-15d %-20s\n", "P" + (i + 1), processes[i], "Not Allocated");
            }
        }

        System.out.println("\nRemaining Memory in Each Partition:");
        for (int i = 0; i < n; i++) {
            System.out.println("Partition " + (i + 1) + ": " + partitions[i] + "K");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter memory partitions (space separated in K): ");
        String[] partitionInput = sc.nextLine().split(" ");
        int[] partitions = new int[partitionInput.length];
        for (int i = 0; i < partitionInput.length; i++) {
            partitions[i] = Integer.parseInt(partitionInput[i]);
        }

        System.out.print("Enter process sizes (space separated in K): ");
        String[] processInput = sc.nextLine().split(" ");
        int[] processes = new int[processInput.length];
        for (int i = 0; i < processInput.length; i++) {
            processes[i] = Integer.parseInt(processInput[i]);
        }

        firstFit(partitions, processes);

        sc.close();
    }
}
