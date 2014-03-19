package com.Stu.chuffchart3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DetailsFragmentTest extends Fragment {

	public static final String ARG_SECTION_NUMBER = "section_number";

	public DetailsFragmentTest() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.details,
				container, false);
		return rootView;
	}
}