import java.util.*;

public class LRUPageReplacement {
    public static void main(String[] args) {
        int[] pages = {2, 3, 2, 1, 5, 2, 4, 5, 3, 2, 5, 2};
        int frames = 3;

        List<Integer> memory = new ArrayList<>();
        Map<Integer, Integer> recentUse = new HashMap<>();

        int pageFaults = 0;

        System.out.println("=== LRU Page Replacement ===");
        System.out.println("Reference String: " + Arrays.toString(pages));
        System.out.println("Number of Frames: " + frames + "\n");

        for (int i = 0; i < pages.length; i++) {
            int page = pages[i];

            System.out.println("Referencing Page: " + page);

            if (memory.contains(page)) {
                System.out.println("Page " + page + " is already in memory (No Page Fault)");
            } 
            else {
                if (memory.size() < frames) {
                    memory.add(page);
                } else {
                    int lruPage = findLRU(recentUse, memory);
                    memory.remove(Integer.valueOf(lruPage));
                    memory.add(page);
                }
                pageFaults++;
                System.out.println("Page " + page + " caused a PAGE FAULT");
            }

            recentUse.put(page, i);

            System.out.println("Current Frames: " + memory);
            System.out.println("------------------------------------");
        }

        System.out.println("\n=== Summary ===");
        System.out.println("Total Page Faults = " + pageFaults);
        System.out.println("Total Page Hits = " + (pages.length - pageFaults));
    }

    private static int findLRU(Map<Integer, Integer> recentUse, List<Integer> memory) {
        int lruPage = memory.get(0);
        int minIndex = recentUse.get(lruPage);

        for (int page : memory) {
            int lastUsed = recentUse.get(page);
            if (lastUsed < minIndex) {
                minIndex = lastUsed;
                lruPage = page;
            }
        }
        return lruPage;
    }
}
