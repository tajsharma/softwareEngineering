package hwdp;

public class GotBack implements LBState {
    private static GotBack instance;

    private GotBack() {}

    public static GotBack getInstance() {
        if (instance == null) {
            instance = new GotBack();
        }
        return instance;
    }

    @Override
    public void shelf(LibraryBook book) {
        LibraryLogger.getInstance().writeLine("Leaving State GotBack for State OnShelf");
        book.setState(OnShelf.getInstance());
    }

    @Override
    public void issue(LibraryBook book) throws BadOperationException {
        throw new BadOperationException();
    }

    @Override
    public void extend(LibraryBook book) throws BadOperationException {
        throw new BadOperationException();
    }

    @Override
    public void returnIt(LibraryBook book) throws BadOperationException {
        throw new BadOperationException();
    }

    @Override
    public String toString() {
        return "GotBack";
    }
}

