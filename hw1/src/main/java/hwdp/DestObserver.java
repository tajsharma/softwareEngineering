package hwdp;

public class DestObserver implements Observer {
    private String name;

    public DestObserver(String name) {
        this.name = name;
    }

    @Override
    public void update(Subject subject) {
        LibraryLogger.getInstance().writeLine(name + " OBSERVED " + subject + " REACHING STATE: " + subject.getStateName());
    }

    @Override
    public String toString() {
        return name;
    }
}
