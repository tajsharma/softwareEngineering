package hwdp;

public class Borrowed implements LBState {
    private static Borrowed instance;

    private Borrowed() {}

    public static Borrowed getInstance() {
        if (instance == null) {
            instance = new Borrowed();
        }
        return instance;
    }

    @Override
    public void shelf(LibraryBook book) {
        LibraryLogger.getInstance().writeLine("Leaving State Borrowed for State OnShelf");
        book.setState(OnShelf.getInstance());
    }

    @Override
    public void issue(LibraryBook book) throws BadOperationException {
        throw new BadOperationException();
    }

    @Override
    public void extend(LibraryBook book) {
        LibraryLogger.getInstance().writeLine("Leaving State Borrowed for State Borrowed");
    }

    @Override
    public void returnIt(LibraryBook book) {
        LibraryLogger.getInstance().writeLine("Leaving State Borrowed for State GotBack");
        book.setState(GotBack.getInstance());
    }

    @Override
    public String toString() {
        return "Borrowed";
    }
}

