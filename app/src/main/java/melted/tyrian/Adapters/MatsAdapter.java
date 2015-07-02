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

import melted.tyrian.ANet.APIHandles;
import melted.tyrian.Helpers.KeyHelper;
import melted.tyrian.Local.Item;
import melted.tyrian.ANet.JItem;
import melted.tyrian.ANet.JMat;
import melted.tyrian.Local.Mat;
import melted.tyrian.MainActivity;
import melted.tyrian.NavigationDrawerFragment;
import melted.tyrian.R;

/**
 * Created by Stephen on 6/30/2015.
 */

public class MatsAdapter extends RecyclerView.Adapter<MatsAdapter.MatsViewHolder> {

    private int cardNum;

    private static boolean[] mLoadingFlags;

    private final HashMap<Integer, Integer> CAT_NUMS =
            new HashMap<Integer, Integer>(){
                {{
                    put(5,  0);
                    put(6,  1);
                    put(29, 2);
                    put(30, 3);
                    put(37, 4);
                    put(38, 5);
                    put(46, 6);
                }}
            };

    private final String[] MAT_CATS = {
            "Cooking Materials",
            "Common Crafting Materials",
            "Fine Crafting Materials",
            "Gemstones and Jewels",
            "Rare Crafting Materials",
            "Festive Crafting Materials",
            "Ascended Crafting Materials"};

    private boolean preLoading;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MatsAdapter() {
        this.cardNum = 0;
        this.mLoadingFlags = new boolean[7];
        this.preLoading = false;
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
            mGrid.setAdapter(new MatsItemAdapter(cv.getContext(), _cardNum));
            mGrid.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
        }
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
            holder.mTitle.setText("Loading...");
        else holder.mTitle.setText(MAT_CATS[position]);
        if ((MainActivity.mCats[position].size() == 0 && !mLoadingFlags[position]) ||
                (MainActivity.mCats[position].size() > 0 && !MainActivity.mCaches[position])) {
            MainActivity.mCats[position] = new ArrayList<>();
            new PopulateMItems(this, position).execute();
        }
        //((MatsItemAdapter)holder.mGrid.getAdapter()).notifyDataSetChanged();
        holder.mGrid.setAdapter(new MatsItemAdapter(holder.mContext, position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 7;
    }

    private class PopulateMItems extends AsyncTask<Void, Void, Void>
    {
        private final MatsAdapter mAdapter;
        private final int mCardNum;

        public PopulateMItems(MatsAdapter adapter, int cardNum) {
            this.mAdapter = adapter;
            this.mCardNum = cardNum;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mLoadingFlags[mCardNum] = true;
            HttpsURLConnection connection;
            JsonReader reader;
            JsonParser parser;
            JsonElement jsonResponse;
            try {
                if (MainActivity.mIDs.size() == 0 && !preLoading) {
                    preLoading = true;
                    MainActivity.mIDs = new ArrayList<>();
                    connection = (HttpsURLConnection) new URL(APIHandles.getAuthUri(
                            APIHandles.BASE_API_MATS_URI, KeyHelper.key))
                            .openConnection();

                    reader = new JsonReader(
                            new InputStreamReader(connection.getInputStream()));
                    parser = new JsonParser();
                    jsonResponse = parser.parse(reader);

                    ArrayList<JMat> jMats = new Gson().fromJson(jsonResponse,
                            new TypeToken<List<JMat>>() {
                            }.getType());

                    for (JMat _j : jMats) {
                        if (_j != null) {
                            MainActivity.mIDs.add(_j.id);
                            MainActivity.jMatsById.get(CAT_NUMS.get(_j.category)).put(_j.id, _j);
                        }
                    }
                    preLoading = false;
                }

                while(preLoading) { Thread.sleep(10); }

                String queryIDs = "";
                for (int id : MainActivity.jMatsById.get(mCardNum).keySet()) {
                    if (queryIDs.equals(""))
                        queryIDs = Integer.toString(id);
                    else
                        queryIDs += "," + Integer.toString(id);
                }

                connection =
                        (HttpsURLConnection) new URL(APIHandles.BASE_API_ITEMS_URI
                                + "?ids=" + queryIDs
                                + "&lang=" + Locale.getDefault().getLanguage())
                                .openConnection();

                // fetch data from server
                reader = new JsonReader(
                        new InputStreamReader(connection.getInputStream()));
                parser = new JsonParser();
                jsonResponse = parser.parse(reader);
                JItem[] items = new Gson().fromJson(jsonResponse, JItem[].class);
                int offset = 0;
                if (MainActivity.mCats[mCardNum].size()  == 0 || !MainActivity.mCaches[mCardNum])
                    for (JItem ji : items) {
                        try {
                            if (ji != null) {
                                JMat jm = MainActivity.jMatsById.get(mCardNum).get(ji.ID);
                                InputStream in = new URL(ji.icon).openStream();
                                Item _item = new Item(ji.ID);
                                _item.setName(ji.name);
                                _item.setDescription(ji.description);
                                _item.setType(ji.type);
                                _item.setLevel(ji.level);
                                _item.setRarity(ji.rarity);
                                _item.setVendor_value(ji.vendor_value);
                                _item.setIcon(BitmapFactory.decodeStream(in));
                                //_item.game_types = item.game_types;
                                //_item.flags = item.flags;
                                //_item.restrictions = item.restrictions;
                                //_item.details = item.details;
                                Mat _m = new Mat(jm.id, jm.count, jm.category,
                                        MainActivity.mIDs.indexOf(jm.id));
                                _m.setItem(_item);
                                MainActivity.mCats[mCardNum].add(_m);
                            }
                        } catch (Exception e) {
                            String t = e.getMessage();
                            offset++;
                        }
                    }
                if (MainActivity.mCats[mCardNum].size() == (items.length - offset))
                    MainActivity.mCaches[mCardNum] = true;
            } catch (IOException e) {
                MainActivity.mCats[mCardNum] = new ArrayList<>();
                e.printStackTrace();
            } catch (InterruptedException e) {
                MainActivity.mCats[mCardNum] = new ArrayList<>();
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            mLoadingFlags[mCardNum] = false;
            mAdapter.notifyDataSetChanged();
        }
    }

    public class MatsItemAdapter extends BaseAdapter {
        private final int _cardNum;
        private final Context mContext;

        public MatsItemAdapter(Context c, int _cardNum) {
            this.mContext = c;
            this._cardNum = _cardNum;
        }

        public int getCount() {
            if (!MainActivity.mCaches[_cardNum]) return 0;
            else return MainActivity.mCats[_cardNum].size();
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
            imageView.setPadding(8, 8, 8, 8);
            imageView.setImageResource(R.drawable.empty_slot);

            TextView tView = (TextView) _view.findViewById(R.id.tv_label);
            ArrayList<Mat> mItems;

            mItems = MainActivity.mCats[_cardNum];

            if (mItems.size() > 0 && mItems.size() > position && !mLoadingFlags[_cardNum]) {
                Collections.sort(mItems);
                if (mItems.get(position) != null && mItems.get(position).getItem() != null) {
                    imageView.setImageBitmap(mItems.get(position).getItem().getIcon());
                    tView.setText(Integer.toString(mItems.get(position).getCount()));
                }
            }
            return _view;
        }
    }
}