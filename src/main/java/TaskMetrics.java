import java.util.List;

public class TaskMetrics {

    public double totalEstimateHours(List<Task> tasks) {
        double total = 0.0;

        for (Task task : tasks) {
            total += task.getEstimateHours();
        }

        return total;
    }

    public double averagePriority(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return 0.0;
        }

        int total = 0;

        for (Task task : tasks) {
            total += task.getPriority();
        }

        return (double) total / tasks.size();
    }

    public int countCompletedTasks(List<Task> tasks) {
        int count = 0;

        for (Task task : tasks) {
            if (task.isDone()) {
                count++;
            }
        }

        return count;
    }
}
