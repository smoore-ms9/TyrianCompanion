package melted.tyrian.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import melted.tyrian.Async.GetBag;
import melted.tyrian.Local.Bag;
import melted.tyrian.Local.Item;
import melted.tyrian.Local.Mat;
import melted.tyrian.MainActivity;
import melted.tyrian.R;

/**
 * Created by Stephen on 7/4/2015.
 */
public class BagAdapter extends BaseAdapter {

    private final int mSlot;

    public BagAdapter(int slot, TextView bName, Context c) {
        this.mSlot = slot;
        Bag b = MainActivity.mSelCharacter.getBags()[mSlot];
        if (b.inventory == null) {
            bName.setText("Loading...");
            new GetBag(this, mSlot, bName, c).execute();
        } else if (b != null && b.bagItem != null) bName.setText(b.bagItem.getName());
        else bName.setText("Bag " + Integer.toString(mSlot + 1));
    }

    @Override
    public int getCount() {
        return MainActivity.mSelCharacter.getBags()[mSlot].getSize();
    }

    @Override
    public Object getItem(int position) {
        if (MainActivity.mSelCharacter.getBags()[mSlot].inventory == null)
            return MainActivity.mSelCharacter.getBags()[mSlot].inventory[position];
        else return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View _view;

        if (convertView == null) {
            _view = inflater.inflate(R.layout.grid_item_bank, null);
        } else {
            _view = convertView;
        }

        LinearLayout.LayoutParams vParams = new LinearLayout.LayoutParams(120, 120);
        _view.setLayoutParams(vParams);

        _view.findViewById(R.id.rl_container).setLayoutParams(
                new LinearLayout.LayoutParams(120, 120));

        ImageView imageView = (ImageView) _view.findViewById(R.id.iv_item);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setPadding(1, 1, 1, 1);
        imageView.setImageResource(R.drawable.empty_slot);

        TextView tView = (TextView) _view.findViewById(R.id.tv_label);

        Bag b = MainActivity.mSelCharacter.getBags()[mSlot];
        if (b.inventory != null) {
            Item[] mItems = b.inventory;
            if (mItems.length > 0 && mItems.length > position) {
                if (mItems[position] != null) {
                    imageView.setImageBitmap(mItems[position].getIcon());
                    if (mItems[position].getCount() > 0)
                        tView.setText(Integer.toString(mItems[position].getCount()));
                    else tView.setText("");
                }
            }
        }

        return _view;
    }
}
