package de.maryvofin.stundenplan.app.modules.planconfig;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import de.maryvofin.stundenplan.app.R;
import de.maryvofin.stundenplan.app.modules.planconfig.ExpandableSemesterListAdapter;


public class SemesterSelectionFragment extends Fragment {

    ExpandableSemesterListAdapter semesterListAdapter;
    boolean[] semesterListExpanded = null;
    int semesterListFirstVisiblePosition = 0;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_semester_selection,container,false);

        semesterListAdapter = new ExpandableSemesterListAdapter(this.getActivity());
        ExpandableListView semesterList = (ExpandableListView)view.findViewById(R.id.listview);
        semesterList.setAdapter(semesterListAdapter);
        semesterListAdapter.notifyDataSetChanged();

        return view;
    }

    public void restoreSemesterListState() {
        ExpandableListView v = (ExpandableListView)view.findViewById(R.id.listview);

        if(semesterListExpanded != null) {
            for(int i=0;i<semesterListExpanded.length;i++) {
                if(semesterListExpanded[i])v.expandGroup(i);
            }
            v.setSelection(semesterListFirstVisiblePosition);
        }

    }

    public void storeSemesterListState() {
        ExpandableListView v = (ExpandableListView)view.findViewById(R.id.listview);

        semesterListExpanded = new boolean[semesterListAdapter.getGroupCount()];
        for(int i=0;i<semesterListExpanded.length;i++) {
            semesterListExpanded[i] = v.isGroupExpanded(i);
        }
        semesterListFirstVisiblePosition = v.getFirstVisiblePosition();
    }
}
