package vn.edu.huflit.notes;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesAdapterVH> implements Filterable {

    ArrayList<Model> notesList;
    ArrayList<Model> notesFilterList;
    NotesAdapter.Listener listener;

    public NotesAdapter( ArrayList<Model> notesList, Listener listener) {
        this.notesList = notesList;
        this.listener = listener;
        this.notesFilterList = notesList;
    }

    @NonNull
    @Override
    public NotesAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_layout, parent, false);
        return new NotesAdapterVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapterVH holder, int position) {
        Model model = notesFilterList.get(position);
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
        holder.deadline.setText(model.getDeadline());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.layout.setBackgroundColor(Color.argb());
                listener.onClick(model);
            }
        });
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClick(model);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return notesFilterList.size();
    }

    @Override
    public Filter getFilter() {
        return new ModelFilter();
    }

    class ModelFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String charString = constraint.toString();
            if(charString.isEmpty()){
                notesFilterList = notesList;
            }else {
                ArrayList<Model> filteredList = new ArrayList<>();
                for (Model model : notesList){
                    if(model.getTitle().toLowerCase().contains(charString.toLowerCase())){
                        filteredList.add(model);
                    }
                }
                notesFilterList = filteredList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values= notesFilterList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notesFilterList = (ArrayList<Model>) results.values;
            notifyDataSetChanged();
        }
    }

    public class NotesAdapterVH extends RecyclerView.ViewHolder {

        TextView title, description, deadline;
        RelativeLayout layout;

        public NotesAdapterVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            deadline = itemView.findViewById(R.id.deadline);
            layout = itemView.findViewById(R.id.note_layout);
        }
    }

    interface Listener{
        void onClick(Model model);
        void onLongClick(Model model);
    }
}
