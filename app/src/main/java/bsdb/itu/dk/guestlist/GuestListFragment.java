package bsdb.itu.dk.guestlist;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GuestListFragment extends Fragment {

    // Interface til at kommunikere mellem aktivitet og fragment.
    public interface GuestListItemClicked {
        public void onGuestListItemClick(int guestId);
    }

    // Vores aktivitet som imlpementerer GuestListItemClicked
    private GuestListItemClicked guestListListener;

    // Liste af alle gæster og en filtreret liste der vises i vores ListView
    private List<Guest> guests;
    private List<Guest> filteredList;

    public GuestListFragment() {

        GuestStore.initialize();
        guests = GuestStore.getAll();
        filteredList = new ArrayList<Guest>();
        filteredList.addAll(guests);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guest_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        guestListListener = (GuestListItemClicked) context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        ListView guestListView = (ListView) getActivity().findViewById(R.id.guest_list);
        final GuestListAdapter adapter = new GuestListAdapter();

        guestListView.setAdapter(adapter);

        final EditText searchField = (EditText) getActivity().findViewById(R.id.search_field);
        searchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                filteredList.clear();

                String searchString = searchField.getText().toString().toLowerCase();

                for(Guest g : guests) {
                    if(g.getName().toLowerCase().contains(searchString)) {
                        filteredList.add(g);
                    }
                }

                adapter.notifyDataSetChanged();

                return false;
            }
        });

        guestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Guest g = filteredList.get(position);
                guestListListener.onGuestListItemClick(g.getId());
            }
        });
    }

    class GuestListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return filteredList.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;

            if(view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.guest_list_item, null);
            }

            TextView guestName = (TextView) view.findViewById(R.id.guest_name);
            TextView arrival = (TextView) view.findViewById(R.id.guest_arrival);

            Guest g = filteredList.get(position);

            if(g != null) {
                guestName.setText(g.getName());
                arrival.setText(g.getSimpleDate());
            }

            return view;
        }
    }
}
