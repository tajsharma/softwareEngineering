package hwdp;

import java.util.HashMap;
import java.util.Map;

public class SourceObserver implements Observer {
	private String name;
	private Map<Subject, String> lastState = new HashMap<>();

	public SourceObserver(String name) {
		this.name = name;
	}

	@Override
	public void update(Subject subject) {
		String prevState = lastState.getOrDefault(subject, "UNOBSERVED");
		LibraryLogger.getInstance().writeLine(name + " OBSERVED " + subject + " LEAVING STATE: " + prevState);
		lastState.put(subject, subject.getStateName());
	}

	@Override
	public String toString() {
		return name;
	}
}

