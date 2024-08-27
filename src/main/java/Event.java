package main.java;

public class Event extends Task {
    private String start;
    private String end;
    public Event(String description, boolean isDone, String start, String end) {
        super(description, "E", isDone);
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        String superString = super.toString();
        String dueDate = String.format(" (from: %s to: %s)", this.start, this.end);
        return superString + dueDate;
    }
}
