import java.io.*;
import java.util.*;

public class MacroPass2 {

    static Map<String, Integer> MNT = new LinkedHashMap<>();
    static List<String> MDT = new ArrayList<>();
    static Map<String, List<String>> ALA = new LinkedHashMap<>();
    static List<String> INTERMEDIATE = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        File mntFile = new File("MNT.txt");
        File mdtFile = new File("MDT.txt");
        File alaFile = new File("ALA.txt");
        File interFile = new File("intermediate.asm");

        if (!mntFile.exists() || !mdtFile.exists() || !alaFile.exists() || !interFile.exists()) {
            System.out.println("Error: One or more required files not found!");
            System.out.println("Expected files: MNT.txt, MDT.txt, ALA.txt, intermediate.asm");
            return;
        }

        loadMNT(mntFile);
        loadMDT(mdtFile);
        loadALA(alaFile);
        loadIntermediate(interFile);

        expandMacros();
    }

    static void loadMNT(File f) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        boolean headerSkipped = false;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (!headerSkipped && (line.toUpperCase().contains("MACRO") || line.toUpperCase().contains("MDT"))) {
                headerSkipped = true;
                continue;
            }
            String[] parts = line.split("\\s+");
            if (parts.length >= 2) {
                MNT.put(parts[0], Integer.parseInt(parts[1]));
            }
        }
        br.close();
    }

    static void loadMDT(File f) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        boolean headerSkipped = false;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (!headerSkipped && line.toUpperCase().contains("LINE")) {
                headerSkipped = true;
                continue;
            }
            MDT.add(line.replaceFirst("^\\d+\\s+", ""));
        }
        br.close();
    }

    static void loadALA(File f) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        String currentMacro = null;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (line.toUpperCase().startsWith("MACRO NAME:")) {
                currentMacro = line.split(":")[1].trim();
                ALA.put(currentMacro, new ArrayList<>());
            } else if (currentMacro != null) {
                ALA.get(currentMacro).add(line.replace("&", "").trim());
            }
        }
        br.close();
    }

    static void loadIntermediate(File f) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) INTERMEDIATE.add(line);
        }
        br.close();
    }

    static void expandMacros() {
        List<String> expanded = new ArrayList<>();

        for (String line : INTERMEDIATE) {
            String[] tokens = line.split("\\s+", 2);
            String first = tokens[0];

            if (MNT.containsKey(first)) {
                int startIndex = MNT.get(first);
                List<String> formals = ALA.get(first);
                List<String> actuals = new ArrayList<>();

                if (tokens.length > 1) {
                    String[] args = tokens[1].split(",");
                    for (String a : args) actuals.add(a.trim());
                }

                Map<String, String> argMap = new LinkedHashMap<>();
                for (int i = 0; i < formals.size() && i < actuals.size(); i++) {
                    argMap.put("&" + formals.get(i), actuals.get(i));
                }

                for (int i = startIndex + 1; i < MDT.size(); i++) {
                    String macroLine = MDT.get(i).trim();
                    if (macroLine.equalsIgnoreCase("MEND")) break;
                    for (Map.Entry<String, String> e : argMap.entrySet()) {
                        macroLine = macroLine.replace(e.getKey(), e.getValue());
                    }
                    expanded.add(macroLine);
                }
            } else {
                expanded.add(line);
            }
        }

        System.out.println("\n--- EXPANDED MACRO CODE (PASS-II OUTPUT) ---\n");
        for (String s : expanded) System.out.println(s);

        try (PrintWriter out = new PrintWriter("expanded_output.asm")) {
            for (String s : expanded) out.println(s);
        } catch (IOException e) {
            System.out.println("Error writing expanded_output.asm");
        }

        System.out.println("\nExpanded code saved to expanded_output.asm");
    }
}
