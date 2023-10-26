package hwdp;
import java.util.HashMap;

// TODO HWDP P3

public class SourceObserver implements Observer {
	private String name;
	private HashMap<Subject, String> subjectToPastStateName;
	
	public SourceObserver(String n) {
		// TODO?
	}

	@Override
	public void update(Subject o) {
		// TODO?
	}

	@Override
	public String toString() {
		return name;
	}
}
