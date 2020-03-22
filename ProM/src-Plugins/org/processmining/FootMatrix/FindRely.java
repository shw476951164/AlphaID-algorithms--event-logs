package org.processmining.FootMatrix;

import java.util.ArrayList;

public class FindRely implements Comparable<FindRely> {
	private String str;
	private String str0;
	private String str1;
	private String flow1;
	private int number;
	private int number0;
	private int number1;
	private int flow1number;
	private ArrayList<String> flow = new ArrayList<>();
	
	public String clear() {
		this.setFlow(null);
		this.setNumber(0);
		this.setNumber0(0);
		this.setNumber1(0);
		this.setStr(null);
		this.setStr0(null);
		this.setStr1(null);
		
		return null;
	}
	
	
	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}
	public String getStr0() {
		return str0;
	}

	public void setStr1(String str1) {
		this.str1= str1;
	}
	public String getStr1() {
		return str1;
	}

	public void setStr0(String str0) {
		this.str0 = str0;
	}
	
	public int getNumber2() {
		return flow1number;
	}

	public void setNumber2(int flow1number) {
		this.flow1number = flow1number;
	}
	
	public int getNumber0() {
		return number0;
	}

	public void setNumber0(int number0) {
		this.number0 = number0;
	}
	
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	public int getNumber1() {
		return number1;
	}

	public void setNumber1(int number1) {
		this.number1 = number1;
	}

	public ArrayList<String> getFlow() {
		return flow;
	}

	public void setFlow(ArrayList<String> flow) {
		this.flow = flow;
	}
	
	public void setFlow1(String flow1) {
		this.flow1 = flow1;
	}
	public String getFlow1() {
		return flow1;
	}
	
	public void addFlow(String str){
		flow.add(str);
	}

	@Override
	public int compareTo(FindRely o) {
		// TODO Auto-generated method stub
		if (str.compareTo(o.str) != 0) {
			return str.compareTo(o.str);
		} else if (!flow.containsAll(o.flow)||!o.flow.containsAll(flow)) {
			return flow.hashCode() - o.flow.hashCode();
		} else {
			return number - o.number;
		}
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flow1 == null) ? 0 : flow1.hashCode());
		result = prime * result + number;
		result = prime * result + ((str == null) ? 0 : str.hashCode());
		result = prime * result + number0;
		result = prime * result + ((str0 == null) ? 0 : str0.hashCode());
		result = prime * result + number1;
		result = prime * result + ((str1 == null) ? 0 : str1.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		FindRely other = (FindRely) obj;
		
		
	/*	if (flow == null) {
			if (other.flow != null)
				return false;
		} else if (!flow.equals(other.flow))
			return false;*/
		
		if (flow1 == null) {
			if (other.flow1 != null)
				return false;
		} else if (!flow1.equals(other.flow1))
			return false;
		
		
		
		if (number != other.number)
			return false;
		if (number0 != other.number0)
			return false;
		if (number1 != other.number1)
			return false;
		
		
		if (str == null) {
			if (other.str != null)
				return false;
		} else if (!str.equals(other.str))
			return false;
		if (str0 == null) {
			if (other.str0 != null)
				return false;
		} else if (!str0.equals(other.str0))
			return false;
	/*	if (str1 == null) {
			if (other.str1 != null)
				return false;
		} else if (!str1.equals(other.str1))
			return false;
		*/
		
		return true;
	}

	public String ttoString() {
		return "FindRely1 [str=" + str + ", number=" + number + ", flow=" + flow + "]";
	}
	public String toString() {
		return "FindRely [str=" + str + ", number=" + number + ", str0=" + str0 +", number0=" + number0 + ", flow1=" + flow1 +", flow1number=" + number1 + "]";
	}
	
}
