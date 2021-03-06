package adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;

import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ekostenkodev.cosmotracker.CosmoDataBase;
import com.ekostenkodev.cosmotracker.ImageHelper;
import com.ekostenkodev.cosmotracker.QueryConstructor;
import com.ekostenkodev.cosmotracker.R;
import com.ekostenkodev.cosmotracker.Subscription;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;

import activities.InfoActivity;
import pojo.CosmoObject;



public class CosmoAdapter extends BaseAdapter {

    public static final int MIN_SIZE = 2;
    private int CosmoSize = MIN_SIZE;

    private ArrayList<CosmoObject> list;
    private LayoutInflater layoutInflater;
    private AssetManager assets;
    private Context context;
    private QueryConstructor queryConstructor;
    private int globalSize;
    private Button downButton;

    private void setDownButton(ListView listView){

        downButton = new Button(context);
        downButton.setBackgroundResource(R.color.back_dark);
        downButton.setText(R.string.down);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150);
        downButton.setLayoutParams(layoutParams);
        downButton.setVisibility(View.VISIBLE);

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // todo пустить загрузку

                if(list.size() + MIN_SIZE >= globalSize) {
                    Button button = (Button)v;
                    button.setVisibility(View.INVISIBLE);// todo кнопка продолжает занимать место, исправить
                }

                CosmoSize += MIN_SIZE;
                fillList(CosmoSize);
                notifyDataSetChanged();
            }
        });

        listView.addFooterView(downButton);

    }

    public void refresh(){
        if(QueryConstructor.isChanged()) { // todo изменить механизм обновления списка
            fillList(CosmoAdapter.MIN_SIZE);
            downButton.setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();

    }

    public CosmoAdapter(Context context, ListView listView, QueryConstructor queryConstructor) {
        assets = context.getAssets();
        this.list = new ArrayList<>();
        this.queryConstructor = queryConstructor;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        listView.setAdapter(this); // todo перенести в меин

        downButton = listView.findViewById(R.id.nav_sort);
        globalSize = CosmoDataBase.getSize(context, queryConstructor.getType());

        setDownButton(listView);

    }

    public void fillList(int size){


        list.clear();
        list.addAll(CosmoDataBase.getData(context, queryConstructor.getQuery(size)));

    }

    public void listSubscriptionCLick(int cosmoID){

        if(Subscription.isSubscribe(context,cosmoID))
            Subscription.deleteSubscribtion(context, cosmoID);
        else
            Subscription.addSubscribtion(context, cosmoID);


    }

    public void listCosmoInfoClick(int cosmoID){

        Intent intent = new Intent(context, InfoActivity.class);
        intent.putExtra("cosmo", cosmoID);
        context.startActivity(intent);
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public CosmoObject getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) { return 0; }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        View view = convertView;
        if(view == null){
            view = layoutInflater.inflate(R.layout.cosmoobject_layout, viewGroup, false);
        }

        CosmoObject cosmo = (CosmoObject) getItem(position);


        TextView name = view.findViewById(R.id.list_name);
        name.setText(cosmo.get_name()); // todo длинное имя западает на след строку, исправить


        TextView info = view.findViewById(R.id.list_info);
        info.setText(cosmo.get_info());

        ImageView image = view.findViewById(R.id.list_image);
        try {
            // get input stream
            InputStream ims = assets.open("cosmoimages/"+cosmo.get_image());
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            image.setImageDrawable(d);
        }
        catch(IOException ex) {
            return null;
        }




        ImageView vis = view.findViewById(R.id.list_visibility);
        vis.setImageResource(ImageHelper.getVisibility(cosmo.get_visibility()));

        ImageView frame = view.findViewById(R.id.list_frame);
        frame.setImageResource(ImageHelper.getFrame(cosmo.get_type()));

        ImageView type = view.findViewById(R.id.list_type);
        type.setImageResource(ImageHelper.getType(cosmo.get_type()));

        ImageButton sub = view.findViewById(R.id.list_sub);

        if(Subscription.isSubscribe(context,cosmo.get_id()))
            sub.setImageResource(R.drawable.icon_sub_on);
        else
            sub.setImageResource(R.drawable.icon_sub_off);


        sub.setTag(position);
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listSubscriptionCLick(getItem(position).get_id());
                notifyDataSetChanged();
            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listCosmoInfoClick(getItem(position).get_id());
            }
        });

        TextView timer = view.findViewById(R.id.list_timer);

        Date nextArrival = cosmo.get_nextArrival();
        Date current = new Date(System.currentTimeMillis());

        long days = (nextArrival.getTime() - current.getTime())/(1000 * 60 * 60 * 24);

        timer.setText(getCorrectDay(days, "дней","день","дня"));

        return view;
    }

    private String getCorrectDay(long days,String str1,String str2,String str3) {

        long value = days % 100;

        if (value > 10 && value < 20) return days + " " + str1;
        else {
            value = days % 10;

            if (value == 1) return days + " " + str2;
            else if (value > 1 && value < 5) return days + " " + str3;
            else return days + " " + str1;
        }

    }





}





