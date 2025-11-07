import java.io.*;
import java.util.*;

public class AssemblerPass1 {

    static Map<String, Integer> IS = new HashMap<>();
    static Map<String, Integer> DL = new HashMap<>();
    static Map<String, Integer> AD = new HashMap<>();
    static Map<String, Integer> REG = new HashMap<>();
    static Map<String, Integer> COND = new HashMap<>();

    static {
        IS.put("STOP", 0);
        IS.put("ADD", 1);
        IS.put("SUB", 2);
        IS.put("MULT", 3);
        IS.put("MOVER", 4);
        IS.put("MOVEM", 5);
        IS.put("COMP", 6);
        IS.put("BC", 7);
        IS.put("DIV", 8);
        IS.put("READ", 9);
        IS.put("PRINT", 10);

        DL.put("DC", 1);
        DL.put("DS", 2);

        AD.put("START", 1);
        AD.put("END", 2);
        AD.put("ORIGIN", 3);
        AD.put("EQU", 4);
        AD.put("LTORG", 5);

        REG.put("AREG", 1);
        REG.put("BREG", 2);
        REG.put("CREG", 3);
        REG.put("DREG", 4);

        COND.put("LT", 1);
        COND.put("LE", 2);
        COND.put("EQ", 3);
        COND.put("GT", 4);
        COND.put("GE", 5);
        COND.put("ANY", 6);
    }

    static LinkedHashMap<String, Integer> SYMTAB = new LinkedHashMap<>();
    static List<String> LITTAB = new ArrayList<>();
    static List<Integer> LITADDR = new ArrayList<>();
    static List<Integer> POOLTAB = new ArrayList<>();

    static int LC = 0;

    public static void main(String[] args) throws Exception {
        File inputFile = new File("input.asm");
        if (!inputFile.exists()) {
            System.out.println("Error: input.asm not found!");
            return;
        }

        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        PrintWriter icFile = new PrintWriter("intermediate.txt");

        POOLTAB.add(0);
        String line;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            int len = parts.length;

            String label = null, opcode, operand = null;
            int idx = 0;

            if (!IS.containsKey(parts[0].toUpperCase()) &&
                !DL.containsKey(parts[0].toUpperCase()) &&
                !AD.containsKey(parts[0].toUpperCase())) {
                label = parts[0];
                if (!SYMTAB.containsKey(label))
                    SYMTAB.put(label, LC);
                idx = 1;
            }

            if (idx >= len) continue;
            opcode = parts[idx].toUpperCase();
            if (idx + 1 < len)
                operand = parts[idx + 1];

            if (opcode.equals("START")) {
                LC = Integer.parseInt(operand);
                icFile.println("   -> (AD,01) (C," + LC + ")");
                continue;
            }

            if (opcode.equals("END")) {
                handleLTORG(icFile);
                icFile.println("   -> (AD,02)");
                break;
            }

            if (opcode.equals("ORIGIN")) {
                int val = evaluate(operand);
                LC = val;
                icFile.println("   -> (AD,03) (C," + LC + ")");
                continue;
            }

            if (opcode.equals("EQU")) {
                int val = evaluate(operand);
                if (label != null) SYMTAB.put(label, val);
                icFile.println("   -> (AD,04) (C," + val + ")");
                continue;
            }

            if (opcode.equals("LTORG")) {
                handleLTORG(icFile);
                icFile.println("   -> (AD,05)");
                continue;
            }

            if (DL.containsKey(opcode)) {
                icFile.print(LC + " -> (DL,0" + DL.get(opcode) + ") ");
                if (opcode.equals("DS")) {
                    icFile.println("(C," + operand + ")");
                    LC += Integer.parseInt(operand);
                } else if (opcode.equals("DC")) {
                    icFile.println("(C," + operand.replace("'", "") + ")");
                    LC++;
                }
                continue;
            }

            if (IS.containsKey(opcode)) {
                icFile.print(LC + " -> (IS,0" + IS.get(opcode) + ") ");
                String[] ops = operand != null ? operand.split(",") : new String[0];
                for (String op : ops) {
                    op = op.trim();
                    if (REG.containsKey(op))
                        icFile.print("(R," + REG.get(op) + ") ");
                    else if (COND.containsKey(op))
                        icFile.print("(C," + COND.get(op) + ") ");
                    else if (op.startsWith("='")) {
                        LITTAB.add(op);
                        icFile.print("(L," + LITTAB.size() + ") ");
                    } else if (!op.isEmpty()) {
                        if (!SYMTAB.containsKey(op)) SYMTAB.put(op, -1);
                        int symIndex = new ArrayList<>(SYMTAB.keySet()).indexOf(op) + 1;
                        icFile.print("(S," + symIndex + ") ");
                    }
                }
                icFile.println();
                LC++;
            }
        }

        br.close();
        icFile.close();
        printTables();
    }

    static int evaluate(String expr) {
        if (expr.contains("+")) {
            String[] p = expr.split("\\+");
            return SYMTAB.getOrDefault(p[0], 0) + Integer.parseInt(p[1]);
        } else if (expr.contains("-")) {
            String[] p = expr.split("-");
            return SYMTAB.getOrDefault(p[0], 0) - Integer.parseInt(p[1]);
        } else if (SYMTAB.containsKey(expr)) {
            return SYMTAB.get(expr);
        } else {
            return Integer.parseInt(expr);
        }
    }

    static void handleLTORG(PrintWriter icFile) {
        for (int i = POOLTAB.get(POOLTAB.size() - 1); i < LITTAB.size(); i++) {
            LITADDR.add(LC);
            icFile.println(LC + " -> (DL,01) (C," + LITTAB.get(i).replace("='", "").replace("'", "") + ")");
            LC++;
        }
        POOLTAB.add(LITTAB.size());
    }

    static void printTables() throws IOException {
        System.out.println("\n--- INTERMEDIATE CODE ---");
        BufferedReader br = new BufferedReader(new FileReader("intermediate.txt"));
        br.lines().forEach(System.out::println);
        br.close();

        System.out.println("\n--- SYMBOL TABLE ---");
        System.out.printf("%-5s %-10s %-10s\n", "Idx", "Symbol", "Address");
        int i = 1;
        for (Map.Entry<String, Integer> e : SYMTAB.entrySet())
            System.out.printf("%-5d %-10s %-10d\n", i++, e.getKey(), e.getValue());

        System.out.println("\n--- LITERAL TABLE ---");
        System.out.printf("%-5s %-10s %-10s\n", "Idx", "Literal", "Address");
        for (int j = 0; j < LITTAB.size(); j++) {
            int addr = j < LITADDR.size() ? LITADDR.get(j) : -1;
            System.out.printf("%-5d %-10s %-10s\n", j + 1, LITTAB.get(j), (addr == -1 ? "-" : addr));
        }

        System.out.println("\n--- POOL TABLE ---");
        for (int j = 0; j < POOLTAB.size(); j++)
            System.out.println("Pool " + (j + 1) + " -> starts from literal " + (POOLTAB.get(j) + 1));
    }
}
