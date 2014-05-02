package com.mathtimeexplorer.tutorials;

import java.util.ArrayList;
import java.util.List;

import com.example.matheducator.R;

public class Measurement {

	public static final List<Integer> lengthList = new ArrayList<Integer>(); 
	public static final List<Integer> massList = new ArrayList<Integer>();
	public static final List<Integer> moneyList = new ArrayList<Integer>();
	public static final List<Integer> timeList = new ArrayList<Integer>();
	public static final List<Integer> divisionList = new ArrayList<Integer>();
	
	static {
		Measurement.lengthList.add(R.drawable.mes_len_p1);
		Measurement.lengthList.add(R.drawable.mes_len_p2);
		Measurement.lengthList.add(R.drawable.mes_len_p3);
		Measurement.lengthList.add(R.drawable.mes_len_p4);
		Measurement.lengthList.add(R.drawable.mes_len_p5);
		Measurement.lengthList.add(R.drawable.mes_len_p6);
	}
	
	static {
		Measurement.massList.add(R.drawable.mes_mass_p1);
		Measurement.massList.add(R.drawable.mes_mass_p2);
		Measurement.massList.add(R.drawable.mes_mass_p3);
		Measurement.massList.add(R.drawable.mes_mass_p4);
		Measurement.massList.add(R.drawable.mes_mass_p5);
	}

	static {
		Measurement.moneyList.add(R.drawable.mes_mon_p1);
		Measurement.moneyList.add(R.drawable.mes_mon_p2);
		Measurement.moneyList.add(R.drawable.mes_mon_p3);
		Measurement.moneyList.add(R.drawable.mes_mon_p4);
		Measurement.moneyList.add(R.drawable.mes_mon_p5);
	}
	
	static {
		Measurement.timeList.add(R.drawable.mes_time_p1);
		Measurement.timeList.add(R.drawable.mes_time_p2);
		Measurement.timeList.add(R.drawable.mes_time_p3);
		Measurement.timeList.add(R.drawable.mes_time_p4);
		Measurement.timeList.add(R.drawable.mes_time_p5);
	}
	
	static {
		Measurement.lengthList.add(R.drawable.mes_vol_p1);
		Measurement.lengthList.add(R.drawable.mes_vol_p2);
		Measurement.lengthList.add(R.drawable.mes_vol_p3);
		Measurement.lengthList.add(R.drawable.mes_vol_p4);
		Measurement.lengthList.add(R.drawable.mes_vol_p5);
	}
}
