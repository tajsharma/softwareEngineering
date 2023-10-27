package hwdp;

public class OnShelf implements LBState {
    private static OnShelf instance;

    private OnShelf() {}

    public static OnShelf getInstance() {
        if (instance == null) {
            instance = new OnShelf();
        }
        return instance;
    }

    @Override
    public void shelf(LibraryBook book) throws BadOperationException {
        throw new BadOperationException();
    }

    @Override
    public void issue(LibraryBook book) {
        LibraryLogger.getInstance().writeLine("Leaving State OnShelf for State Borrowed");
        book.setState(Borrowed.getInstance());
    }

    @Override
    public void returnIt(LibraryBook book) throws BadOperationException {
        throw new BadOperationException();
    }
    @Override
    public void extend(LibraryBook book) throws BadOperationException {
        throw new BadOperationException();
    }

    @Override
    public String toString() {
        return "OnShelf";
    }
}


