package melted.tyrian.Adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import melted.tyrian.ContentFragment;
import melted.tyrian.Local.LCharacter;
import melted.tyrian.Local.Mat;
import melted.tyrian.MainActivity;
import melted.tyrian.R;

/**
 * Created by Stephen on 7/3/2015.
 */
public class CharacterListAdapter extends RecyclerView.Adapter<CharacterListAdapter.CharacterViewHolder> {

    private final HashMap<String, Integer> PROF_BGS = new HashMap<String, Integer>() {
        {{  put("Thief", R.drawable.cbg_thief);
            put("Ranger", R.drawable.cbg_ranger);
            put("Mesmer", R.drawable.cbg_mesmer);
            put("Necromancer", R.drawable.cbg_necro);
            put("Guardian", R.drawable.cbg_guard);
            put("Warrior", R.drawable.cbg_war);
            put("Engineer", R.drawable.cbg_engi);
            put("Elementalist", R.drawable.cbg_ele);
            put("Revenant", R.drawable.cbg_thief);
        }}
    };
    private final FragmentActivity mActivity;

    public CharacterListAdapter(FragmentActivity a) {
        this.mActivity = a;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView card = (CardView) LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_character, parent, false);

        // set the view's size, margins, paddings and layout parameters...
        CharacterViewHolder vh = new CharacterViewHolder(card);
        return vh;
    }

    @Override
    public void onBindViewHolder(CharacterViewHolder holder, int position) {
        LCharacter lc = MainActivity.mCharacters.get(position);
        holder.mNameView.setText(lc.getName() + " (" + lc.getLevel() + ")");
        holder.mDetailView.setText(lc.getGender() + " " + lc.getRace() + " " + lc.getProfession());
        holder.mCardView.findViewById(R.id.rl_char).setBackgroundResource(PROF_BGS.get(lc.getProfession()));
        holder.mCharacter = lc;
    }

    @Override
    public int getItemCount() {
        return MainActivity.mCharacters.size();
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {
        private final TextView mNameView;
        private final TextView mDetailView;
        private final CardView mCardView;
        // each data item is just a string in this case
        public Context mContext;
        public LCharacter mCharacter;

        public CharacterViewHolder(CardView cv) {
            super(cv);
            mContext = cv.getContext();
            mCardView = cv;
            mNameView = (TextView) cv.findViewById(R.id.tv_c_name);
            mDetailView = (TextView) cv.findViewById(R.id.tv_c_details);

            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //View rView = mInflater.inflate(R.layout.fragment_character_details, null);
                    FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ContentFragment.newInstance(999))
                            .commit();
                    mCharacter.QueryInventory();
                    MainActivity.mSelCharacter = mCharacter;
                }
            });
        }
    }
}
