import java.util.*;

public class FIFOPageReplacement {
    public static void fifoPageReplacement(int[] pages, int framesCount) {
        Queue<Integer> frames = new LinkedList<>();
        int pageFaults = 0;

        System.out.println("\n--- FIFO Page Replacement ---\n");
        System.out.printf("%-10s %-30s %-10s\n", "Page", "Frames", "Page Fault");

        for (int page : pages) {
            String faultStatus;

            if (!frames.contains(page)) {
                if (frames.size() < framesCount) {
                    frames.add(page);
                } else {
                    frames.poll(); // remove oldest page
                    frames.add(page);
                }
                pageFaults++;
                faultStatus = "Yes";
            } else {
                faultStatus = "No";
            }

            System.out.printf("%-10d %-30s %-10s\n", page, frames, faultStatus);
        }

        System.out.println("\nTotal Page Faults: " + pageFaults);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter page reference string (space separated): ");
        String[] pageInput = sc.nextLine().split(" ");
        int[] pages = new int[pageInput.length];
        for (int i = 0; i < pageInput.length; i++) {
            pages[i] = Integer.parseInt(pageInput[i]);
        }

        System.out.print("Enter number of frames: ");
        int framesCount = sc.nextInt();

        fifoPageReplacement(pages, framesCount);
        sc.close();
    }
}
