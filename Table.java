import java.util.ArrayList;

public class Table {
    private ArrayList<Integer> functionTime;
    private ArrayList<String> functionName;
    private ArrayList<Process> waitTimeProcesses;
    public int waitTime;

    public Table(ArrayList<Integer> functionTime, ArrayList<String> functionName, ArrayList<Process> waitTimeProcesses) {
        this.functionTime = functionTime;
        this.functionName = functionName;
        this.waitTimeProcesses = waitTimeProcesses;
        this.waitTime = 0;
    }

    public String startFunction() {
        StringBuilder rowProcessName = new StringBuilder();
        StringBuilder divProcessTimeBottom = new StringBuilder();
        StringBuilder divProcessTimeTop = new StringBuilder();
        divProcessTimeTop.append("a");

        for (int i = 0; i < functionTime.size(); i++) {
            String line = "----";
            if (functionTime.size() - 1 > i) {
                if (functionTime.get(i) >= 10) {
                    line = "---";
                } else if (functionTime.get(i) >= 100) {
                    line = "-";
                }
                divProcessTimeBottom.append(String.format("%s%s", functionTime.get(i), line));
            } else {
                divProcessTimeBottom.append(String.format("%s\n", functionTime.get(i)));
            }

            if (functionTime.size() - 1 != i) {
                divProcessTimeTop.append("-----");
                if (functionName.size() <= i) rowProcessName.append(String.format("| %s ", functionName.get(functionName.size() - 1)));
                else rowProcessName.append(String.format("| %s ", functionName.get(i)));
            }
        }

        divProcessTimeTop.delete(divProcessTimeTop.length()-1, divProcessTimeTop.length());
        divProcessTimeTop.append("c\n");
        rowProcessName.append("|\n");
        String header = formatDiv(divProcessTimeTop.toString());
        header += formatRow(rowProcessName.toString());
        header += formatDiv(divProcessTimeBottom.toString());
        return header;
    }

    public String startWaitTime() {
        StringBuilder rowProcessName = new StringBuilder();
        StringBuilder divProcessTimeBottom = new StringBuilder();
        StringBuilder divProcessTimeTop = new StringBuilder();

        divProcessTimeTop.append("a----------b---------------c\n");
        for (Process pr : waitTimeProcesses) {
            waitTime += pr.waitTime;

            String space = "  ";
            if (pr.waitTime >= 10) {
                space = " ";
            }
            rowProcessName.append(String.format("| Name: %s | Wait time: %s%s|\n", pr.name, pr.waitTime, space));
        }
        divProcessTimeBottom.append("g----------h---------------i\n");

        String header = formatDiv(divProcessTimeTop.toString());
        header += formatRow(rowProcessName.toString());
        header += formatDiv(divProcessTimeBottom.toString());
        return header;
    }

    public static String formatRow(String str) {
        return str.replace('|', '\u2502');
    }

    public static String formatDiv(String str) {
        return str.replace('a', '\u250c')
                .replace('b', '\u252c')
                .replace('c', '\u2510')
                .replace('d', '\u251c')
                .replace('e', '\u253c')
                .replace('f', '\u2524')
                .replace('g', '\u2514')
                .replace('h', '\u2534')
                .replace('i', '\u2518')
                .replace('-', '\u2500');
    }
}
