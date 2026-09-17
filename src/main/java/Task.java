public class Task {

    private String title;
    private String assignee;
    private double estimateHours;
    private int priority;
    private boolean done;

    public Task(String title, String assignee, double estimateHours,
                int priority, boolean done) {
        this.title = title;
        this.assignee = assignee;
        this.estimateHours = estimateHours;
        this.priority = priority;
        this.done = done;
    }

    public String getTitle() {
        return title;
    }

    public String getAssignee() {
        return assignee;
    }

    public double getEstimateHours() {
        return estimateHours;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isDone() {
        return done;
    }
}
