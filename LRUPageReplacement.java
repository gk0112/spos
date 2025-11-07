import java.util.*;

public class LRUPageReplacement {

    public static void lruPageReplacement(int[] pages, int framesCount) {
        List<Integer> frames = new ArrayList<>();
        Map<Integer, Integer> recentUse = new HashMap<>();
        int pageFaults = 0;

        System.out.println("\n--- LRU Page Replacement ---\n");
        System.out.printf("%-10s %-30s %-10s\n", "Page", "Frames", "Page Fault");

        for (int time = 0; time < pages.length; time++) {
            int page = pages[time];
            String faultStatus;

            if (!frames.contains(page)) {
                if (frames.size() < framesCount) {
                    frames.add(page);
                } else {
                    // Find least recently used page
                    int lruPage = Collections.min(recentUse.entrySet(), Map.Entry.comparingByValue()).getKey();
                    frames.set(frames.indexOf(lruPage), page);
                    recentUse.remove(lruPage);
                }
                pageFaults++;
                faultStatus = "Yes";
            } else {
                faultStatus = "No";
            }

            recentUse.put(page, time); // update last used time

            System.out.printf("%-10d %-30s %-10s\n", page, frames, faultStatus);
        }

        System.out.println("\nTotal Page Faults: " + pageFaults);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter page reference string (space separated): ");
        String[] input = sc.nextLine().split(" ");
        int[] pages = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            pages[i] = Integer.parseInt(input[i]);
        }

        System.out.print("Enter number of frames: ");
        int framesCount = sc.nextInt();

        lruPageReplacement(pages, framesCount);
        sc.close();
    }
}
