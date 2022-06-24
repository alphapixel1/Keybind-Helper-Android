package com.example.keybindhelper.Room.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keybindhelper.Dialogs.ArrowProvider;
import com.example.keybindhelper.Dialogs.GroupListProvider;
import com.example.keybindhelper.R;
import com.example.keybindhelper.Room.Group;
import com.example.keybindhelper.Room.Keybind;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class KeybindAdapter extends RecyclerView.Adapter<KeybindAdapter.KeybindViewHolder> {

    private List<Keybind> keybindList;

    public KeybindAdapter(List<Keybind> keybindList){

        this.keybindList = keybindList;
    }

    @NonNull
    @Override
    public KeybindViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.keybind_view,null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(parent.getLayoutParams());
        v.setLayoutParams(params);
        return new KeybindViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull KeybindViewHolder holder, int position) {
        Keybind k=keybindList.get(position);
        k.viewHolder=holder;
        View view=holder.itemView;
        updateView(k,view);
        //end updating text

        LinearLayout main =view.findViewById(R.id.keybind_main_layout);
        /*if((position%2)==1)
            main.setBackgroundResource(R.color.offset_keybind_background);*/
        main.setOnClickListener(v -> {
            showEditDialog(k,view);
        });
        if(position==keybindList.size()-1)
            updateKeybindsBackground();
        main.setLongClickable(true);
        main.setOnLongClickListener(v -> {
            showContextMenu(k,view);
            return true;
        });
    }
    private void updateView(Keybind k,View view){
        ((TextView) view.findViewById(R.id.keybind_name)).setText(k.name);

        TextView kb1TV=view.findViewById(R.id.keybind_1_text);
        TextView kb2TV=view.findViewById(R.id.keybind_2_text);
        TextView kb3TV=view.findViewById(R.id.keybind_3_text);

        CardView kb1CV=view.findViewById(R.id.keybind_1_card);
        CardView kb2CV=view.findViewById(R.id.keybind_2_card);
        CardView kb3CV=view.findViewById(R.id.keybind_3_card);

        //Updating text
        String[] kbs=new String[]{k.kb1,k.kb2,k.kb3};
        TextView[] tvs=new TextView[]{kb1TV,kb2TV,kb3TV};
        CardView[] cvs=new CardView[]{kb1CV,kb2CV,kb3CV};
        for (int i=0;i<kbs.length;i++){
            String kb=kbs[i];
            tvs[i].setText(kb);
            if(Objects.equals(kb, "") || kb==null){
                cvs[i].setVisibility(View.GONE);
            }else{
                cvs[i].setVisibility(View.VISIBLE);
            }
        }
    }
    private void showEditDialog(Keybind k, View view){
        Context c=view.getContext();
        Dialog d=new Dialog(c);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCancelable(true);

        d.setContentView(R.layout.edut_keybind_dialog_2);
        TextInputEditText nameEt=d.findViewById(R.id.edit_keybind_name);
        TextInputEditText kb1Et=d.findViewById(R.id.edit_keybind_kb1);
        TextInputEditText kb2Et=d.findViewById(R.id.edit_keybind_kb2);
        TextInputEditText kb3Et=d.findViewById(R.id.edit_keybind_kb3);

        nameEt.setText(k.name);
        kb1Et.setText(k.kb1);
        kb2Et.setText(k.kb2);
        kb3Et.setText(k.kb3);

        d.findViewById(R.id.edit_keybind_cancel_btn).setOnClickListener(v -> {
            d.cancel();
        });
        d.findViewById(R.id.edit_keybind_done_btn).setOnClickListener(v->{
            String n=nameEt.getText().toString();
            TextView error=d.findViewById(R.id.keybind_error_text);
            if(n.length()==0) {
                error.setVisibility(View.VISIBLE);
                error.setText("Name Cannot Be Empty");
            }else {
                k.name = n;

                String kb1T = kb1Et.getText().toString();
                String kb2T = kb2Et.getText().toString();
                String kb3T = kb3Et.getText().toString();

                List<String> kbs = Arrays.asList(kb1T, kb2T, kb3T);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    kbs = kbs.stream().filter(z -> z.length() > 0).collect(Collectors.toList());
                    k.kb1 = (kbs.size() > 0) ? kbs.get(0) : "";
                    k.kb2 = (kbs.size() > 1) ? kbs.get(1) : "";
                    k.kb3 = (kbs.size() > 2) ? kbs.get(2) : "";
                } else {
                    k.kb1 = kb1T;
                    k.kb2 = kb2T;
                    k.kb3 = kb3T;
                }
                d.cancel();
                updateView(k, view);
                k.updateDB();
            }
        });
        d.show();
    }

    private void showContextMenu(Keybind k, View view) {

        Context context=view.getContext();
        Dialog d=new Dialog(context);
        d.setContentView(R.layout.keybind_settings_dialog);
        ((TextView)d.findViewById(R.id.keybind_settings_name)).setText(k.name);

        d.findViewById(R.id.keybind_copy).setOnClickListener(v->{
            k.group.AddKeybind(k.Clone(false),true);
            notifyItemInserted(keybindList.size()-1);
            d.cancel();
        });
        d.findViewById(R.id.keybind_delete).setOnClickListener(v -> {
            k.group.deleteKeybind(k);
            notifyItemRemoved(k.index);
            updateKeybindsBackground();
            d.cancel();
        });

        d.findViewById(R.id.keybind_move).setOnClickListener(z->{
            d.cancel();
            ArrowProvider ap=new ArrowProvider(context);
            ap.directionClicked=isUp -> {
                int indx=k.group.keybinds.indexOf(k);
                if(isUp){
                    if(indx>0) {
                        k.group.moveKeybindUpDown(k,-1);
                        notifyItemMoved(indx,indx-1);
                        updateKeybindsBackground();
                    }
                }else{
                    if(indx<k.group.keybinds.size()-1){
                        k.group.moveKeybindUpDown(k,1);
                        notifyItemMoved(indx,indx+1);
                        updateKeybindsBackground();
                    }
                }
            };
            ap.Show();
        });

        d.findViewById(R.id.keybind_sendtogroup).setOnClickListener(v->{
            d.cancel();
            List<Group> gs=new ArrayList<>();
            for (Group g: com.example.keybindhelper.Room.CurrentProject.Groups) {
                if(g!=k.group)
                    gs.add(g);
            }
            GroupListProvider glp=new GroupListProvider(context,"Send Keybind To",gs);
            glp.groupClick=g->{
                int indx=keybindList.indexOf(k);
                g.AddKeybind(k,false);
                notifyItemRemoved(indx);
                try {
                    k.group.currentAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    System.err.println(e);
                }
            };
            glp.Show();
        });
        d.show();
    }
    private void updateKeybindsBackground(){
        for(Keybind k :keybindList.get(0).group.keybinds){
            View main=k.viewHolder.itemView.findViewById(R.id.keybind_main_layout);;
            if(k.index%2==1)
                main.setBackgroundResource(R.color.offset_keybind_background);
            else
                main.setBackgroundResource(R.color.transparent);
        }
    }
    @Override
    public int getItemCount() {
        return keybindList.size();
    }

    public class KeybindViewHolder extends RecyclerView.ViewHolder {
        public KeybindViewHolder(View itemView){
            super(itemView);
        }
    }
}