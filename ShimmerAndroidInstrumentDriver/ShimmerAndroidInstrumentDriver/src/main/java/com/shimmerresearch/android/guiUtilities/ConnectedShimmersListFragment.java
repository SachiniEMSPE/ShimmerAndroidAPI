package com.shimmerresearch.android.guiUtilities;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.shimmerresearch.driver.ShimmerDevice;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConnectedShimmersListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectedShimmersListFragment extends ListFragment {

    OnShimmerDeviceSelectedListener mCallBack;
    String selectedDeviceAddress, selectedDeviceName;
    final static String LOG_TAG = "SHIMMER";
    ListView savedListView = null;
    ArrayAdapter<String> savedListAdapter = null;
    int selectedItemPos = -1;
    List<ShimmerDevice> shimmerDeviceList;
    Context context;
    int selectedDevicePos = -1;


    public ConnectedShimmersListFragment() {
        // Required empty public constructor
    }

    //Container Activity must implement this interface
    public interface OnShimmerDeviceSelectedListener {
        public void onShimmerDeviceSelected(String macAddress, String deviceName);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Ensure that the container activity has implemented the callback interface.
        try {
            mCallBack = (OnShimmerDeviceSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnShimmerDeviceSelectedListener");
        }
    }

    public static ConnectedShimmersListFragment newInstance() {
        ConnectedShimmersListFragment fragment = new ConnectedShimmersListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void buildShimmersConnectedListView(final List<ShimmerDevice> deviceList, final Context context) {

        shimmerDeviceList = deviceList;
        this.context = context;
        if(deviceList == null) {
            //String[] displayList = {"Service not yet initialised"};
            String[] displayList = {"No devices connected"};
            ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, displayList);
            setListAdapter(listAdapter);
        }
//        else if(deviceList.isEmpty()) {
//            String[] displayList = {"No Shimmers connected"};
//            ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, displayList);
//            setListAdapter(listAdapter);
//        }
        else {
            final String[] nameList = new String[deviceList.size()];
            final String[] macList = new String[deviceList.size()];
            final String[] displayList = new String[deviceList.size()];

            for (int i = 0; i < nameList.length; i++) {
                nameList[i] = deviceList.get(i).getShimmerUserAssignedName();
                macList[i] = deviceList.get(i).getMacId();
                displayList[i] = nameList[i] + "\n" + macList[i];
            }

            ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_multiple_choice, displayList);

            //Set the list of devices to be displayed in the Fragment
            setListAdapter(listAdapter);

            final ListView listView = getListView();
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedItemPos = position;

                    selectedDeviceAddress = macList[position];
                    selectedDeviceName = nameList[position];
                    selectedDevicePos = position;

                    try {
                        mCallBack.onShimmerDeviceSelected(macList[position], nameList[position]);
                    } catch (ClassCastException cce) {

                    }
                }
            });

            //Save the listView so that it can be restored in onCreateView when returning to the Fragment.
            savedListView = listView;
            savedListAdapter = listAdapter;

            //Ensure that the selected item's checkbox is checked
            if(selectedDeviceAddress != null) {
                for (int i = 0; i < listView.getAdapter().getCount(); i++) {

                    View view = getViewByPosition(i, listView);
                    CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(android.R.id.text1);
                    if (checkedTextView != null) {
                        String text = checkedTextView.getText().toString();
                        if (text.contains(selectedDeviceAddress)) {
                            listView.setItemChecked(i, true);
                        } else {
                            listView.setItemChecked(i, false);
                        }
                    }

                }
            }


        }
    }

    @Override
    public void onDestroyView() {
        Log.e(LOG_TAG, "ConnectedShimmersListFragment onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(LOG_TAG, "ConnectedShimmersListFragment onCreateView()");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        Log.e(LOG_TAG, "ConnectedShimmersListFragment onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(LOG_TAG, "ConnectedShimmersListFragment onActivityCreated()");
    }

    private void setOnItemClickListener(ListView mListView) {

    }

    @Override
    public void onResume() {
        if(savedListView != null && savedListAdapter != null) {
            buildShimmersConnectedListView(shimmerDeviceList, context);
        } else {
            buildShimmersConnectedListView(null, getActivity().getApplicationContext());
        }
        Log.e(LOG_TAG, "ConnectedShimmersListFragment onResume()");
        super.onResume();
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

}
