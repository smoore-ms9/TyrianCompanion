package melted.tyrian.Local;

/**
 * Created by Stephen on 6/28/2015.
 */
public class Mat implements Comparable<Mat> {

    private final int id;
    private Item item;
    private int count;
    private int category;
    private int position;

    public Mat(int id, int count, int category, int position) {
        this.id = id;
        this.count = count;
        this.category = category;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item i) {
        this.item = i;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int compareTo(Mat m) {
        return position - m.getPosition();
    }
}
