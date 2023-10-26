package hwdp;

public interface LBState {
    void shelf(LibraryBook book);
    void issue(LibraryBook book);
    void extend(LibraryBook book);
    void returnIt(LibraryBook book);
}
