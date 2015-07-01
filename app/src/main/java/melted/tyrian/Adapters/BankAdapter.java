package melted.tyrian.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import melted.tyrian.ANet.APIHandles;
import melted.tyrian.ANet.BankItem;
import melted.tyrian.ANet.Item;
import melted.tyrian.ANet.JBankItem;
import melted.tyrian.ANet.JItem;
import melted.tyrian.MainActivity;
import melted.tyrian.NavigationDrawerFragment;
import melted.tyrian.R;

/**
 * Created by Stephen on 6/30/2015.
 */


public class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankViewHolder> {

    private boolean loading;
    private int index;
    private int cardNum;

    // Provide a suitable constructor (depends on the kind of dataset)
    public BankAdapter() {
        loading = false;
        index = 0;
        this.cardNum = 0;
    }

    private class PopulateBItems extends AsyncTask<Void, Void, Void>
    {
        private final BankAdapter adapter;

        public PopulateBItems(BankAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        protected Void doInBackground(Void... params) {
            loading = true;
            MainActivity.bCached = false;
            index = 0;
            HttpsURLConnection connection;
            try {
                connection = (HttpsURLConnection) new URL(APIHandles.getAuthUri(
                        APIHandles.BASE_API_BANK_URI, NavigationDrawerFragment.key))
                        .openConnection();

                // fetch data from server
                JsonReader reader = new JsonReader(
                        new InputStreamReader(connection.getInputStream()));
                JsonParser parser = new JsonParser();
                JsonElement jsonResponse = parser.parse(reader);
                ArrayList<JBankItem> jbItems = new Gson().fromJson(jsonResponse,
                        new TypeToken<List<JBankItem>>() {
                        }.getType());
                BankItem[] _bItems = new BankItem[30];
                MainActivity.bItems = new ArrayList<>();
                MainActivity.bItems.add(_bItems);
                int i = 0;
                for (JBankItem _j : jbItems) {
                    BankItem _b;
                    if (_j != null) {
                        _b = new BankItem(_j.id, _j.count, i);
                    } else {
                        _b = new BankItem(0, 0, i);
                    }
                    if (i >= 30) {
                        _bItems = new BankItem[30];
                        MainActivity.bItems.add(_bItems);
                        i = 0;
                    }
                    _bItems[i] = _b;
                    i++;
                }
                MainActivity.bCached = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            loading = false;
            adapter.notifyDataSetChanged();
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class BankViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final TextView mTitle;
        public final GridView mGrid;

        public BankViewHolder(CardView cv, int cardNum) {
            super(cv);
            mTitle = (TextView) cv.findViewById(R.id.tv_title);
            mGrid = (GridView) cv.findViewById(R.id.gv_grid);
            mGrid.setAdapter(new BankItemAdapter(cv.getContext(), cardNum));
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BankAdapter.BankViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        CardView card = (CardView) LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_banktab, parent, false);

        if ((MainActivity.bItems.size() == 0 && !loading) ||
                (MainActivity.bItems.size() > 0 && !MainActivity.bCached))
            new PopulateBItems(this).execute();

        // set the view's size, margins, paddings and layout parameters...
        BankViewHolder vh = new BankViewHolder(card, cardNum);
        cardNum++;
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(BankViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (MainActivity.bItems.size() == 0) holder.mTitle.setText("Loading...");
        else if (MainActivity.bItems.get(position) != null)
            holder.mTitle.setText("Bank Tab " + Integer.toString(position + 1));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (MainActivity.bItems.size() > 0) return MainActivity.bItems.size();
        return 1;
    }

    public class BankItemAdapter extends BaseAdapter {
        private Context mContext;
        private BankItem[] tItems;
        private int cardNum;
        private HashMap<BankItem, ImageView> _idMap;

        public BankItemAdapter(Context c, int cardNum) {
            this.cardNum = cardNum;
            mContext = c;
        }

        public int getCount() {
            if (MainActivity.bItems == null) return 1;
            else return 30;
        }

        public Object getItem(int position) {
            if (MainActivity.bItems == null) return null;
            else return MainActivity.bItems.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View _view;

            if (_idMap == null) _idMap = new HashMap<>();

            //if (cardNum == -1)
            //cardNum = index/30;

            int itemNum = (30 * cardNum) + position;

            if (tItems == null && MainActivity.bItems != null &&
                    MainActivity.bItems.size() > cardNum)
                this.tItems = MainActivity.bItems.get(cardNum);

            if (MainActivity.bThumbnails == null)
                MainActivity.bThumbnails = new ArrayList<>();

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

            TextView tView = (TextView) _view.findViewById(R.id.tv_label);

            if (MainActivity.bThumbnails.size() <= itemNum ||
                    MainActivity.bThumbnails.get(itemNum) == null) {

                imageView.setImageResource(R.drawable.empty_slot);
                if (tItems != null) {
                    BankItem b = tItems[position];
                    tView.setText(Integer.toString(b.count));
                    _idMap.put(b, imageView);
                    if (position == 29) {
                        ItemFetcher iFetcher = new ItemFetcher(_idMap, this);
                        iFetcher.execute();
                    }
                    MainActivity.bThumbnails.add(imageView);
                    index++;
                }
                return _view;
            } else {

                if (tItems != null) {
                    BankItem b = tItems[position];
                    tView.setText(Integer.toString(b.count));
                }

                imageView.setImageDrawable(MainActivity.bThumbnails.get(itemNum).getDrawable());
                return _view;
            }
        }

        private class ItemFetcher extends AsyncTask<ArrayList<Integer>, Void, ArrayList<Item>>
        {
            private final BankItemAdapter ADAPTER;
            private final HashMap<Integer, ImageView> IV_MAP;
            private final HashMap<ImageView, Bitmap> ICON_MAP;
            private final int[] IDS;

            public ItemFetcher(HashMap<BankItem, ImageView> _idMap, BankItemAdapter adapter) {
                this.IV_MAP = new HashMap<>();
                this.ICON_MAP = new HashMap<>();
                this.ADAPTER = adapter;
                this.IDS = new int[30];
                int i = 0;
                for (BankItem b : _idMap.keySet()) {
                    this.IDS[i] = b.id;
                    this.IV_MAP.put(b.position, _idMap.get(b));
                    i++;
                }
            }

            private void FetchItem () throws IOException {
                String queryIDs = "";
                for (int id : IDS) {
                    if (queryIDs.equals(""))
                        queryIDs = Integer.toString(id);
                    else
                        queryIDs += "," + Integer.toString(id);
                }

                HttpsURLConnection connection =
                        (HttpsURLConnection) new URL(APIHandles.BASE_API_ITEMS_URI
                                + "?ids=" + queryIDs
                                + "&lang=" + Locale.getDefault().getLanguage())
                                .openConnection();

                // fetch data from server
                JsonReader reader = new JsonReader(
                        new InputStreamReader(connection.getInputStream()));
                JsonParser parser = new JsonParser();
                JsonElement jsonResponse = parser.parse(reader);

                try {
                    JItem[] items = new Gson().fromJson(jsonResponse, JItem[].class);
                    for (JItem ji : items) {
                        try {
                            if (ji != null) {
                                InputStream in = new URL(ji.icon).openStream();
                                Item _item = new Item(ji.ID);
                                _item.setName(ji.name);
                                _item.setDescription(ji.description);
                                _item.setType(ji.type);
                                _item.setLevel(ji.level);
                                _item.setRarity(ji.rarity);
                                _item.setVendor_value(ji.vendor_value);
                                _item.setIcon(BitmapFactory.decodeStream(in));
                                for (BankItem b : MainActivity.bItems.get(cardNum))
                                    if (b.id == _item.getID()) {
                                        b.setItem(_item);
                                        ImageView imageView = IV_MAP.get(b.position);
                                        if (imageView != null && _item.getIcon() != null) {
                                            ICON_MAP.put(imageView, _item.getIcon());
                                        }
                                    }
                                //_item.game_types = item.game_types;
                                //_item.flags = item.flags;
                                //_item.restrictions = item.restrictions;
                                //_item.details = item.details;
                            }
                        } catch (Exception e) {
                            String t = e.getMessage();
                        }
                    }
                } catch (Exception e) {
                    String t = e.getMessage();
                }
            }

            @Override
            protected ArrayList<Item> doInBackground (ArrayList<Integer>... params){
                ArrayList<Item> _items = new ArrayList<>();
                try {
                    FetchItem();
                } catch (IOException e) {
                    //TODO: Error handle
                    e.printStackTrace();
                }
                return _items;
            }
            protected void onPostExecute(ArrayList<Item> _items) {
                for (ImageView iv : ICON_MAP.keySet()) {
                    iv.setImageBitmap(ICON_MAP.get(iv));
                }
                ADAPTER.notifyDataSetChanged();
            }
        }
    }
}

