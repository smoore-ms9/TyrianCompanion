package melted.tyrian.Adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import melted.tyrian.ANet.APIHandler;
import melted.tyrian.ANet.JItem;
import melted.tyrian.ANet.JMat;
import melted.tyrian.Async.GetMats;
import melted.tyrian.Helpers.KeyHelper;
import melted.tyrian.Local.Item;
import melted.tyrian.Local.Mat;
import melted.tyrian.MainActivity;
import melted.tyrian.R;

/**
 * Created by Stephen on 6/30/2015.
 */

public class MatsAdapter extends RecyclerView.Adapter<MatsAdapter.MatsViewHolder> {

    public static boolean[] mLoadingFlags;
    public static final HashMap<Integer, Integer> CAT_NUMS =
            new HashMap<Integer, Integer>() {
                {
                    {
                        put(5, 0);
                        put(6, 1);
                        put(29, 2);
                        put(30, 3);
                        put(37, 4);
                        put(38, 5);
                        put(46, 6);
                    }
                }
            };
    private final String[] MAT_CATS = {
            "Cooking Materials",
            "Common Crafting Materials",
            "Fine Crafting Materials",
            "Gemstones and Jewels",
            "Rare Crafting Materials",
            "Festive Crafting Materials",
            "Ascended Crafting Materials"};
    private int cardNum;
    public static boolean preLoading;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MatsAdapter() {
        this.cardNum = 0;
        this.mLoadingFlags = new boolean[7];
        this.preLoading = false;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MatsViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        // create a new view
        CardView card = (CardView) LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_matstab, parent, false);

        // set the view's size, margins, paddings and layout parameters...
        MatsViewHolder vh = new MatsViewHolder(card, cardNum);
        cardNum++;
        if (cardNum > 6) cardNum = 0;
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MatsViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        if (MainActivity.mCats[position].size() == 0 || !MainActivity.mCaches[position])
            holder.mTitle.setText(MAT_CATS[position] + " (Loading...)");
        else holder.mTitle.setText(MAT_CATS[position]);
        MatsItemAdapter mAdapter = new MatsItemAdapter(holder.mContext, position, holder.mTitle, holder.mGrid);
        holder.mGrid.setAdapter(mAdapter);
        if ((MainActivity.mCats[position].size() == 0 && !mLoadingFlags[position]) ||
                (MainActivity.mCats[position].size() > 0 && !MainActivity.mCaches[position])) {
            MainActivity.mCats[position] = new ArrayList<>();
            new GetMats(mAdapter, position).execute();
        }
        //((MatsItemAdapter)holder.mGrid.getAdapter()).notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 7;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MatsViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTitle;
        public GridView mGrid;
        public Context mContext;

        public MatsViewHolder(CardView cv, int _cardNum) {
            super(cv);
            mContext = cv.getContext();
            mTitle = (TextView) cv.findViewById(R.id.tv_title);
            mGrid = (GridView) cv.findViewById(R.id.gv_grid);
            mGrid.setAdapter(new MatsItemAdapter(cv.getContext(), _cardNum, mTitle, mGrid));
        }
    }

    public class MatsItemAdapter extends BaseAdapter {
        private final int _cardNum;
        private final GridView mHost;
        private TextView mTitle;
        private final Context mContext;

        public MatsItemAdapter(Context c, int _cardNum, TextView mTitle, GridView host) {
            this.mContext = c;
            this._cardNum = _cardNum;
            this.mTitle = mTitle;
            this.mHost = host;
        }

        public int getCount() {
            if (!MainActivity.mCaches[_cardNum]) {
                //mHost.setMinimumHeight(50);
                return 0;
            }
            else {
                ViewGroup.LayoutParams layoutParams = mHost.getLayoutParams();
                layoutParams.height = 120 * ((int)Math.ceil(MainActivity.mCats[_cardNum].size() / 10.0)); //this is in pixels
                mHost.setLayoutParams(layoutParams);
                return MainActivity.mCats[_cardNum].size();
            }
        }

        public Object getItem(int position) {
            if (!MainActivity.mCaches[_cardNum]) return null;
            else return MainActivity.mCats[_cardNum].get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View _view;

            if (convertView == null) {
                _view = inflater.inflate(R.layout.grid_item_bank, null);
            } else {
                _view = convertView;
            }

            _view.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            _view.findViewById(R.id.rl_container).setLayoutParams(
                    new LinearLayout.LayoutParams(120, 120));

            ImageView imageView = (ImageView) _view.findViewById(R.id.iv_item);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(5, 5, 5, 5);
            imageView.setImageResource(R.drawable.empty_slot);

            TextView tView = (TextView) _view.findViewById(R.id.tv_label);
            ArrayList<Mat> mItems;

            mItems = MainActivity.mCats[_cardNum];

            if (mItems.size() > 0 && mItems.size() > position && !mLoadingFlags[_cardNum]) {
                mTitle.setText(MAT_CATS[_cardNum]);
                Collections.sort(mItems);
                if (mItems.get(position) != null && mItems.get(position).getItem() != null) {
                    imageView.setImageBitmap(mItems.get(position).getItem().getIcon());
                    if (mItems.get(position).getCount() > 0) {
                        tView.setText(Integer.toString(mItems.get(position).getCount()));
                        imageView.setImageAlpha(255);
                    } else {
                        tView.setText("");
                        imageView.setImageAlpha(50);
                    }
                }
            }
            return _view;
        }
    }
}