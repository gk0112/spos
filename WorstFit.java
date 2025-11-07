import java.util.*;

public class WorstFit {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter memory partitions (space separated in K): ");
        String[] partInput = sc.nextLine().trim().split("\\s+");
        int n = partInput.length;
        int[] partitions = new int[n];
        for (int i = 0; i < n; i++) partitions[i] = Integer.parseInt(partInput[i]);

        System.out.print("Enter process sizes (space separated in K): ");
        String[] procInput = sc.nextLine().trim().split("\\s+");
        int m = procInput.length;
        int[] processes = new int[m];
        for (int i = 0; i < m; i++) processes[i] = Integer.parseInt(procInput[i]);

        int[] allocation = new int[m];
        Arrays.fill(allocation, -1);

        for (int i = 0; i < m; i++) {
            int worstIdx = -1;
            for (int j = 0; j < n; j++) {
                if (partitions[j] >= processes[i]) {
                    if (worstIdx == -1 || partitions[j] > partitions[worstIdx])
                        worstIdx = j;
                }
            }
            if (worstIdx != -1) {
                allocation[i] = worstIdx;
                partitions[worstIdx] -= processes[i];
            }
        }

        System.out.println("\n--- Worst Fit Memory Allocation ---\n");
        System.out.printf("%-10s %-15s %-15s\n", "Process", "Size (K)", "Allocated Partition");
        for (int i = 0; i < m; i++) {
            if (allocation[i] != -1)
                System.out.printf("%-10s %-15d %-15s\n", "P" + (i + 1), processes[i], "Partition " + (allocation[i] + 1));
            else
                System.out.printf("%-10s %-15d %-15s\n", "P" + (i + 1), processes[i], "Not Allocated");
        }

        System.out.println("\nRemaining Memory in Each Partition:");
        for (int i = 0; i < n; i++)
            System.out.println("Partition " + (i + 1) + ": " + partitions[i] + "K");

        sc.close();
    }
}
