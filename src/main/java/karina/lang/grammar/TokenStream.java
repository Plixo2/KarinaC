package karina.lang.grammar;

public interface TokenStream {

    void advance();
    void setPosition(int position);
    int position();
    boolean hasNext();

}
