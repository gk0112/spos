import java.util.*;

public class OptimalPageReplacement {

    public static void optimalPageReplacement(int[] pages, int framesCount) {
        List<Integer> frames = new ArrayList<>();
        int pageFaults = 0;

        System.out.println("\n--- Optimal Page Replacement ---\n");
        System.out.printf("%-10s %-30s %-10s\n", "Page", "Frames", "Page Fault");

        for (int i = 0; i < pages.length; i++) {
            int page = pages[i];
            String faultStatus;

            if (!frames.contains(page)) {
                if (frames.size() < framesCount) {
                    frames.add(page);
                } else {
                    // Find page that will not be used for the longest time in the future
                    List<Integer> futureUse = new ArrayList<>();
                    for (int f : frames) {
                        int nextUse = Integer.MAX_VALUE;
                        for (int k = i + 1; k < pages.length; k++) {
                            if (pages[k] == f) {
                                nextUse = k;
                                break;
                            }
                        }
                        futureUse.add(nextUse);
                    }

                    int toReplaceIndex = futureUse.indexOf(Collections.max(futureUse));
                    frames.set(toReplaceIndex, page);
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

        optimalPageReplacement(pages, framesCount);
        sc.close();
    }
}
