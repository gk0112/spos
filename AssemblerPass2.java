import java.io.*;
import java.util.*;

public class AssemblerPass2 {

    static class Symbol {
        String name;
        int address;
        Symbol(String n, int a) { name = n; address = a; }
    }

    static class Literal {
        String value;
        int address;
        Literal(String v, int a) { value = v; address = a; }
    }

    static Map<Integer, Symbol> SYMTAB = new LinkedHashMap<>();
    static Map<Integer, Literal> LITTAB = new LinkedHashMap<>();
    static List<String> intermediate = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        File symFile = new File("symbol_table.txt");
        File litFile = new File("literal_table.txt");
        File icFile = new File("intermediate.txt");

        if (!symFile.exists() || !litFile.exists() || !icFile.exists()) {
            System.out.println("Error: Required input files not found!");
            System.out.println("Make sure Pass-I has generated symbol_table.txt, literal_table.txt, and intermediate.txt.");
            return;
        }

        loadSymbolTable(symFile);
        loadLiteralTable(litFile);
        loadIntermediate(icFile);

        PrintWriter mc = new PrintWriter("machine_code.txt");
        System.out.println("\n--- MACHINE CODE ---");
        System.out.printf("%-6s %-6s %-6s %-8s %-10s\n", "LC", "OP", "REG", "MEM/CONST", "MNEMONIC");

        int LC = 0;
        for (String line : intermediate) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.contains("(AD,01)")) {
                LC = getConstant(line);
                continue;
            }

            if (line.contains("(AD,02)")) break;

            if (line.contains("(AD,03)")) {
                LC = getConstant(line);
                continue;
            }

            if (line.contains("(AD,04)") || line.contains("(AD,05)")) continue;

            if (line.contains("(DL,01)")) {
                int c = getConstant(line);
                System.out.printf("%-6d %-6s %-6s %-8d %-10s\n", LC, "00", "00", c, "DC");
                mc.printf("%-6d %-6s %-6s %-8d\n", LC, "00", "00", c);
                LC++;
                continue;
            }

            if (line.contains("(DL,02)")) {
                int words = getConstant(line);
                LC += words;
                continue;
            }

            if (line.contains("(IS")) {
                String op = getCode(line);
                int opcode = Integer.parseInt(op);
                int reg = extractRegister(line);
                int mem = 0;

                if (line.contains("(S,")) {
                    int sIndex = getIndex("S", line);
                    if (SYMTAB.get(sIndex) != null) mem = SYMTAB.get(sIndex).address;
                } else if (line.contains("(L,")) {
                    int lIndex = getIndex("L", line);
                    if (LITTAB.get(lIndex) != null) mem = LITTAB.get(lIndex).address;
                } else if (line.contains("(C,")) {
                    mem = getConstant(line);
                }

                String mnemonic = getMnemonic(opcode);
                System.out.printf("%-6d %-6d %-6d %-8d %-10s\n", LC, opcode, reg, mem, mnemonic);
                mc.printf("%-6d %-6d %-6d %-8d\n", LC, opcode, reg, mem);
                LC++;
            }
        }

        mc.close();
        System.out.println("\n✅ Machine code generated: machine_code.txt");
    }

    static void loadSymbolTable(File f) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        boolean headerSkipped = false;
        while ((line = br.readLine()) != null) {
            if (!headerSkipped) { headerSkipped = true; continue; }
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");
            if (parts.length >= 3) {
                int index = Integer.parseInt(parts[0]);
                String name = parts[1];
                int addr = Integer.parseInt(parts[2]);
                SYMTAB.put(index, new Symbol(name, addr));
            }
        }
        br.close();
    }

    static void loadLiteralTable(File f) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        boolean headerSkipped = false;
        while ((line = br.readLine()) != null) {
            if (!headerSkipped) { headerSkipped = true; continue; }
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");
            if (parts.length >= 3) {
                int index = Integer.parseInt(parts[0]);
                String lit = parts[1].replace("='", "").replace("'", "");
                int addr = parts[2].equals("-") ? 0 : Integer.parseInt(parts[2]);
                LITTAB.put(index, new Literal(lit, addr));
            }
        }
        br.close();
    }

    static void loadIntermediate(File f) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        while ((line = br.readLine()) != null)
            intermediate.add(line);
        br.close();
    }

    static String getCode(String line) {
        int comma = line.indexOf(',');
        int close = line.indexOf(')', comma);
        return line.substring(comma + 1, close).replaceAll("\\D", "");
    }

    static int getConstant(String line) {
        int cPos = line.indexOf("(C,");
        if (cPos < 0) return 0;
        int end = line.indexOf(')', cPos);
        return Integer.parseInt(line.substring(cPos + 3, end).replaceAll("\\D", ""));
    }

    static int getIndex(String type, String line) {
        int pos = line.indexOf("(" + type + ",");
        int end = line.indexOf(')', pos);
        return Integer.parseInt(line.substring(pos + 3, end).replaceAll("\\D", ""));
    }

    static int extractRegister(String line) {
        if (line.contains("(R,")) {
            int pos = line.indexOf("(R,");
            int end = line.indexOf(')', pos);
            return Integer.parseInt(line.substring(pos + 3, end));
        }
        return 0;
    }

    static String getMnemonic(int opcode) {
        switch (opcode) {
            case 0: return "STOP";
            case 1: return "ADD";
            case 2: return "SUB";
            case 3: return "MULT";
            case 4: return "MOVER";
            case 5: return "MOVEM";
            case 6: return "COMP";
            case 7: return "BC";
            case 8: return "DIV";
            case 9: return "READ";
            case 10: return "PRINT";
            default: return "-";
        }
    }
}
