package melted.tyrian.Local;

/**
 * Created by Stephen on 6/28/2015.
 */
public class BankItem {

    public int id;

    public int count;

    public Item item;

    public int position;

    public BankItem(int id, int count, int position){
        this.id = id;
        this.count = count;
        this.position = position;
    }

    public void setItem(Item i)
    {
        this.item = i;
    }
}
