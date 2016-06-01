package com.gedesoft.ddd.controladores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gedesoft.ddd.R;
import com.gedesoft.ddd.modelos.Categorias;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

/**
 * Created by Jonny on 29/05/2016.
 */
public class AdaptadorCategorias extends RecyclerView.Adapter<AdaptadorCategorias.ViewHolderCates> {
    private Context mContext;
    private ArrayList<Categorias> mCategoriases;
    private myOnclick mMyOnclick;

    public interface myOnclick{
        public void onClick(ViewHolderCates viewHolderCates, int id);
    }

    public AdaptadorCategorias(Context context, ArrayList<Categorias> categoriases, myOnclick myOnclick){
        mContext = context;
        mCategoriases = categoriases;
        mMyOnclick = myOnclick;
    }

    @Override
    public ViewHolderCates onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.detalles_lista_categorias, parent, false);

        return new ViewHolderCates(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolderCates holder, int position) {
        Categorias cateActual = mCategoriases.get(position);

        holder.mTextView.setText(cateActual.getNombre());
        holder.circularImageView.setImageResource(cateActual.getImagen());


    }

    @Override
    public int getItemCount() {
        return mCategoriases.size();
    }

    public class ViewHolderCates extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTextView;
        private CircularImageView circularImageView;

        public ViewHolderCates(View itemView) {
            super(itemView);

            mTextView = (TextView) itemView.findViewById(R.id.textoCate);
            circularImageView = (CircularImageView) itemView.findViewById(R.id.circularIMagen);
            circularImageView.setBorderColor(itemView.getResources().getColor(R.color.colorAccent));
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mMyOnclick.onClick(this, getAdapterPosition());
            notifyDataSetChanged();
        }
    }
}

