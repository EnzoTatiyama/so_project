import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Process {
    public String name;
    public int duration;
    public int arrivalTime;
    public ArrayList<String> io_operation = new ArrayList<>();
    public int timeInCPU;
    public int lastTimeCPU;
    public int waitTime;

    public Process(String name, int duration, int arrivalTime, String io_operation) {
        this.name = name;
        this.duration = duration;
        this.arrivalTime = arrivalTime;
        this.timeInCPU = 0;
        this.waitTime = 0;
        this.lastTimeCPU = 0;
        setIo_operation(io_operation);
    }

    public void setIo_operation(String sIoOperation) {
        String[] arr = sIoOperation.split(",");
        Collections.addAll(io_operation, arr);
    }

    public boolean hasIoOperation() {
        if (io_operation.size() != 0 && io_operation.get(0) != "") {
            if (Integer.parseInt(io_operation.get(0)) == timeInCPU) {
                io_operation.remove(0);
                return true;
            }
        }
        return false;
    }
}
