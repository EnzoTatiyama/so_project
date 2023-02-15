import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.ArrayList;

public class RoundRobin {
    private ArrayList<Process> processes = new ArrayList<>();
    private ArrayList<Process> queue = new ArrayList<>();
    private ArrayList<Process> waitTimeProcesses = new ArrayList<>();
    private ArrayList<Integer> functionTime = new ArrayList<>();
    private ArrayList<String> functionName = new ArrayList<>();

    public RoundRobin() {
        start();
    }

    private void readFile() {
        try (Scanner sc = new Scanner(new FileReader("entry.txt"))) {
            try {
                while (true) {
                    String text = sc.nextLine();
                    String[] splittedText = text.split(" ");

                    String name = splittedText[0];
                    int duration = Integer.parseInt(splittedText[1]);
                    int arrivalTime = Integer.parseInt(splittedText[2]);
                    String io_operation = "";
                    if (splittedText.length == 4) {
                        io_operation = splittedText[3];
                    }

                    Process pr = new Process(name, duration, arrivalTime, io_operation);
                    processes.add(pr);
                }
            } catch (RuntimeException re) {
                sc.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasFinishedProcess() {
        Process pr = queue.get(0);

        if (pr.duration != 0) return false;

        System.out.printf("#[event] CLOSING <%s>%n", pr.name);
        waitTimeProcesses.add(pr);
        queue.remove(0);
        return true;
    }

    private void hasArrivedProcess(int time) {
        if (processes.size() == 0) return;

        Process pr = processes.get(0);
        if (pr.arrivalTime != time) return;

        if (time == 0) functionName.add(pr.name);

        if (queue.size() != 0) {
            System.out.printf("#[event] ARRIVAL <%s>%n", pr.name);
        }
        queue.add(pr);
        processes.remove(0);
    }

    private Process newProcessCame(int time, Process pr) {
        queue.remove(0);
        pr.lastTimeCPU = time;
        queue.add(pr);
        return queue.get(0);
    }

    private String formatterQueueLabel() {
        String queueLabel = "";
        for (int i = 1; i < queue.size(); i++) {
            Process queuePr = queue.get(i);
            queueLabel += String.format("%s(%s) ", queuePr.name, queuePr.duration);
        }
        return queueLabel;
    }

    // First come, first served
    private int startFCFS(int waitTimeFCFS) {
        for (int i = 0; i < processes.size() - 1; i++) {
            waitTimeFCFS += (processes.get(i).duration + waitTimeFCFS);
        }
        return waitTimeFCFS;
    }

    // Shortest job first
    private int startSJF(int waitTimeSJF) {
        ArrayList<Process> sjfArray = new ArrayList<>(processes);
        sjfArray.sort((p1, p2) -> Integer.compare(p1.duration, p2.duration));

        for (int i = 0; i < sjfArray.size() - 1; i++) {
            waitTimeSJF += (sjfArray.get(i).duration + waitTimeSJF);
        }
        return waitTimeSJF;
    }

    public void start() {
        System.out.println("*********************************");
        System.out.println("***** SCHEDULER ROUND ROBIN *****");

        Scanner sc = new Scanner(System.in);
        System.out.print("What quantity of quantum: ");
        int limitQuantum = sc.nextInt();
        sc.close();

        System.out.println("---------------------------------");
        System.out.println("------- STARTING SIMULATION -----");
        System.out.println("---------------------------------");

        readFile();

        int time = 0, quantum = 0, qtEscalation = 0, waitTimeFCFS = 0, waitTimeSJF = 0;
        boolean newProcess = false;

        processes.sort((p1, p2) -> Integer.compare(p1.arrivalTime, p2.arrivalTime));

        functionTime.add(time);
        waitTimeFCFS = startFCFS(waitTimeFCFS);
        waitTimeSJF = startSJF(waitTimeSJF);
        hasArrivedProcess(time);

        while (true) {
            System.out.printf("********** TIME %d **************%n", time);
            String queueLabel = "";
            String cpuLabel;

            if (hasFinishedProcess()) {
                quantum = 0;
                newProcess = true;
                functionTime.add(time);
            }

            if (queue.size() == 0) {
                System.out.println("QUEUE: There are no processes");
                System.out.println("PROCESSES ARE OVER!!!");
                break;
            }

            Process pr = queue.get(0);
            boolean hasIoOperation = pr.hasIoOperation();

            if (quantum == limitQuantum || hasIoOperation) {
                if (hasIoOperation) {
                    System.out.printf("#[event] OPERATION I/O <%s>%n", pr.name);
                }
                if (queue.size() > 1) {
                    pr = newProcessCame(time, pr);
                    newProcess = true;
                }
                quantum = 0;
                functionTime.add(time);
            }

            if (newProcess) {
                pr.waitTime += (time - pr.lastTimeCPU);
                qtEscalation += 1;
                newProcess = false;
                functionName.add(pr.name);
            }

            hasArrivedProcess(time);

            cpuLabel = String.format("%s(%d)", pr.name, pr.duration);
            pr.duration--;
            pr.timeInCPU++;
            quantum++;

            if (queue.size() > 1) queueLabel = formatterQueueLabel();
            if (queueLabel.equals("")) queueLabel = "There are no processes";
            if (cpuLabel.equals("")) cpuLabel = "PROCESSES ARE OVER!!!";

            System.out.println("QUEUE: " + queueLabel);
            System.out.println("CPU: " + cpuLabel);

            time++;
        }

        System.out.println("---------------------------------------");
        System.out.println("------- Shutting down simulation ------");
        System.out.println("---------------------------------------");
        System.out.println();

        System.out.println("*********** INFORMATIONS ***************");

        String header;
        Table table = new Table(functionTime, functionName, waitTimeProcesses);

        header = table.startWaitTime();
        System.out.println(header);

        header = table.startFunction();
        System.out.print(header);

        int waitTime = table.waitTime;

        System.out.println();
        System.out.println("Quantum: " + limitQuantum);
        System.out.println("Total quantity of processes: " + waitTimeProcesses.size());
        System.out.println("Quantity of escalation: " + qtEscalation);
        System.out.println("Total wait time: " + waitTime);
        System.out.println("Avarage Round-robin waiting time: " + waitTime / waitTimeProcesses.size());
        System.out.println("Avarage First come, first served waiting time: " + waitTimeFCFS / waitTimeProcesses.size());
        System.out.println("Avarage Shortest job first waiting time: " + waitTimeSJF / waitTimeProcesses.size());
    }
}
