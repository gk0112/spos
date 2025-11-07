import java.io.*;
import java.util.*;

public class MacroPass1 {

    static class MNTEntry {
        String name;
        int mdtIndex;
        MNTEntry(String n, int idx) { name = n; mdtIndex = idx; }
    }

    static List<String> MDT = new ArrayList<>();
    static List<MNTEntry> MNT = new ArrayList<>();
    static Map<String, List<String>> ALA = new LinkedHashMap<>();

    public static void main(String[] args) throws Exception {
        File f = new File("macro_input.asm");
        if (!f.exists()) {
            System.out.println("Error: macro_input.asm not found!");
            return;
        }
        List<String> src = readAllLines(f);
        pass1(src);
        displayTables();
    }

    static List<String> readAllLines(File f) throws Exception {
        List<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(f));
        String s;
        while ((s = br.readLine()) != null) lines.add(s);
        br.close();
        return lines;
    }

    static void pass1(List<String> lines) {
        boolean inMacro = false;
        String currentMacro = null;

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;

            String upper = line.toUpperCase();

            if (upper.equals("MACRO")) {
                inMacro = true;
                currentMacro = null;
                continue;
            }

            if (!inMacro) continue;

            if (upper.equals("MEND")) {
                MDT.add("MEND");
                inMacro = false;
                currentMacro = null;
                continue;
            }

            if (currentMacro == null) {
                String[] parts = line.split("\\s+", 2);
                String macroName = parts[0].trim();
                List<String> formals = new ArrayList<>();

                if (parts.length > 1) {
                    String params = parts[1].trim();
                    if (!params.isEmpty()) {
                        String[] ps = params.split(",");
                        for (String p : ps) {
                            String t = p.trim();
                            if (t.startsWith("&")) t = t.substring(1);
                            formals.add(t);
                        }
                    }
                }

                MNT.add(new MNTEntry(macroName, MDT.size()));
                ALA.put(macroName, formals);
                MDT.add(line);
                currentMacro = macroName;
            } else {
                MDT.add(line);
            }
        }
    }

    static void displayTables() {
        System.out.println("\n--- MACRO NAME TABLE (MNT) ---");
        System.out.printf("%-6s %-12s %-10s\n", "Idx", "Macro", "MDT-Index");
        for (int i = 0; i < MNT.size(); i++) {
            MNTEntry e = MNT.get(i);
            System.out.printf("%-6d %-12s %-10d\n", (i + 1), e.name, e.mdtIndex);
        }

        System.out.println("\n--- MACRO DEFINITION TABLE (MDT) ---");
        System.out.printf("%-6s %s\n", "Idx", "Line");
        for (int i = 0; i < MDT.size(); i++) {
            System.out.printf("%-6d %s\n", i, MDT.get(i));
        }

        System.out.println("\n--- ARGUMENT LIST ARRAY (ALA) ---");
        System.out.printf("%-12s %s\n", "Macro", "Formals");
        for (Map.Entry<String, List<String>> e : ALA.entrySet()) {
            System.out.printf("%-12s %s\n", e.getKey(), e.getValue());
        }
    }
}
