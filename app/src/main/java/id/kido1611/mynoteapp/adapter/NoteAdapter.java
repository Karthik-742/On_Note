package id.kido1611.mynoteapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.kido1611.mynoteapp.CustomOnItemClickListener;
import id.kido1611.mynoteapp.NoteAddUpdateActivity;
import id.kido1611.mynoteapp.R;
import id.kido1611.mynoteapp.entity.Note;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>{

    private ArrayList<Note> listNotes = new ArrayList<>();
    private Activity activity;

    public NoteAdapter(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<Note> getListNotes() {
        return listNotes;
    }

    public void setListNotes(List<Note> listNotes) {
        if(listNotes.size() > 0){
            this.listNotes.clear();
        }
        this.listNotes.addAll(listNotes);

        notifyDataSetChanged();
    }

    public void additem(Note item){
        this.listNotes.add(item);
        notifyItemInserted(this.listNotes.size() - 1);
    }

    public void updateItem(int position, Note note){
        this.listNotes.set(position, note);
        notifyItemChanged(position, note);
    }

    public void removeItem(int position){
        this.listNotes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, this.listNotes.size());
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note currentNote = listNotes.get(position);
        holder.tvTitle.setText(currentNote.getTitle());
        holder.tvDescription.setText(currentNote.getDescription());
        holder.tvDate.setText(currentNote.getDate());
        holder.cvNote.setOnClickListener(new CustomOnItemClickListener(position, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(activity, NoteAddUpdateActivity.class);
                intent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE, listNotes.get(position));
                intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION, position);
                activity.startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_UPDATE);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return this.listNotes.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvDescription, tvDate;
        final CardView cvNote;
        NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
            tvDate = itemView.findViewById(R.id.tv_item_date);
            cvNote = itemView.findViewById(R.id.cv_item_note);
        }
    }
}
