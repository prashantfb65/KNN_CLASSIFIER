/**
Created By:               P R A S H A N T   G A R  G  |  STUDENT ID : 16201447
Created Date:             03/12/2016
Copyright:                University College Dublin
Subject:				  ADVANCE MACHINE LEARNING (Programming Assignment)
Description:              Pojo File handling String Key,Value Pair
Version:                  00.00.00.01

Modification history:
----------------------------------------------------------------------------------------------------------------------------
Modified By         Modified Date (dd/mm/yyyy)                Version               Description
---------------------------------------------------------------------------------------------------------------------------
 **/

/*
 * Inclusion of Header Files
 */
import java.io.Serializable;

public class KeyValue implements Serializable{
	private static final long serialVersionUID = 4617538678462140372L;
	
	//Class Variables 
	private String key;
	private String value;

	/**
	 * KeyValue Class Constructor
	 *
	 */
    public KeyValue(String key, String value){
		this.key=key;
		this.value=value;
	}

    /**
	 * Returns String Value of Key,Value as Strings
	 *
	 */
	@Override
	public String toString() {
		return "["+key+","+value+"]";
	}

    /**
	 * Getter Setter implementations for Class Variables Key and Value - Start
	 *
	 */	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * End
	 */
}

/**
 * End of File
 */



