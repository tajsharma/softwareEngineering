package hwdp;

import java.util.ArrayList;
import java.util.List;

public class LibraryBook implements Subject {
    private LBState state;
    private String name;
    private List<Observer> observers;

    public LibraryBook(String name) {
        this.name = name;
        this.state = OnShelf.getInstance(); // Default state
        this.observers = new ArrayList<>();
    }

    // State-related methods
    void setState(LBState newState) {
        this.state = newState;
        notifyObservers(); // Inform observers of state change
    }

    public void issue() {
        try {
            state.issue(this);
        } catch (BadOperationException e) {
            LibraryLogger.getInstance().writeLine("BadOperationException - Can't use issue in " + state + " state");
        }
    }

    public void returnIt() {
        try {
            state.returnIt(this);
        } catch (BadOperationException e) {
            LibraryLogger.getInstance().writeLine("BadOperationException - Can't use returnIt in " + state + " state");
        }
    }

    public void shelf() {
        try {
            state.shelf(this);
        } catch (BadOperationException e) {
            LibraryLogger.getInstance().writeLine("BadOperationException - Can't use shelf in " + state + " state");
        }
    }

    public void extend() {
        try {
            state.extend(this);
        } catch (BadOperationException e) {
            LibraryLogger.getInstance().writeLine("BadOperationException - Can't use extend in " + state + " state");
        }
    }

    @Override
    public String toString() {
        return state.toString();
    }

    // Observer-related methods
    @Override
    public void attach(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            LibraryLogger.getInstance().writeLine(observer + " is now watching " + name);
        } else {
            LibraryLogger.getInstance().writeLine(observer + " is already attached to " + name);
        }
    }

    @Override
    public void detach(Observer observer) {
        if (observers.remove(observer)) {
            LibraryLogger.getInstance().writeLine(observer + " is no longer watching " + name);
        }
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

    @Override
    public String getStateName() {
        return state.toString();
    }

    // Other potential methods and attributes can be added as necessary
}
