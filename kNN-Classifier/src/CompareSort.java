/**
Created By:               P R A S H A N T   G A R  G  |  STUDENT ID : 16201447
Created Date:             03/12/2016
Copyright:                University College Dublin
Subject:				  ADVANCE MACHINE LEARNING (Programming Assignment)
Description:              Implements Comparator Interface to sort Multiple String keys, Double value pairs
Version:                  00.00.00.01

Modification history:
----------------------------------------------------------------------------------------------------------------------------
Modified By         Modified Date (dd/mm/yyyy)                Version               Description
---------------------------------------------------------------------------------------------------------------------------
 **/

/*
 * Inclusion of Header Files
 */
import java.util.Comparator;

public class CompareSort implements Comparator<KeyValueDouble> {
	
	/**
	 * Sorts the List of KeyValueDouble on the decreasing order of double values
	 * @param  delta   two KeyValueDouble values
	 * @return         1 if double value of o2 value is greater or -1 if its o1's, returns 0 in case of a tie
	 */
	@Override
	public int compare(KeyValueDouble o1, KeyValueDouble o2) {
		if (o1.getValue() < o2.getValue()) return -1;
	    if (o1.getValue() > o2.getValue()) return 1;
	    return 0;
	}
}
/**
 * End of File
 */