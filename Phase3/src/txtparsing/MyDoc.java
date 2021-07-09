package txtparsing;

/**
 *
 * @author Tonia Kyriakopoulou
 */
public class MyDoc {

    private String id;
    private String title;
    private String text;

    public MyDoc(String id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
    }

    @Override
    public String toString() {
        String ret = "MyDoc{"
                + "\n\tID: " + id
                + "\n\tTitle: " + title
                + "\n\tText: " + text;
        return ret + "\n}";
    }

    //---- Getters & Setters definition ----
    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}