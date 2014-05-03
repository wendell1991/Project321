package com.mathtimeexplorer.worksheets;

import java.util.Random;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.matheducator.R;

public class QuizFragment extends Fragment {

	private View quizFragView;
	private String question;
	private String qnMark = "?";
	private TextView qnView;
	private RadioButton optionA, optionB, optionC, optionD;

	public static final QuizFragment newInstance(Question question)
	{
		QuizFragment f = new QuizFragment();
		Bundle bdl = new Bundle();
		bdl.putString("Question", question.getQuestion());
		bdl.putString("A",question.getA());
		bdl.putString("B",question.getB());
		bdl.putString("C",question.getC());
		bdl.putString("D",question.getD());
		f.setArguments(bdl);
		return f;
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		quizFragView = inflater.inflate(R.layout.fragment_quiz, container, false);

		init(quizFragView);
		initQuestions();		  
		return quizFragView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}

	private void initQuestions() {

		qnView.setText(getArguments().getString("Question"));
		optionA.setText(getArguments().getString("A"));
		optionB.setText(getArguments().getString("B"));
		optionC.setText(getArguments().getString("C"));
		optionD.setText(getArguments().getString("D"));

	}

	private void init(View view){
		qnView = (TextView) view.findViewById(R.id.qnView);
		optionA = (RadioButton) view.findViewById(R.id.optionA);
		optionB = (RadioButton) view.findViewById(R.id.optionB);
		optionC = (RadioButton) view.findViewById(R.id.optionC);
		optionD = (RadioButton) view.findViewById(R.id.optionD);
	}
}
