package com.zhurylomihaylo.www.easyconnect;

class Pair <T> {
	private T first;
	private T second;
	
	Pair() {}
	
	Pair(T first, T second) {
		this.first = first;
		this.second = second;
	} 
	
	T getFirst() {
		return first;
	}
	
	T getSecond() {
		return second;
	}

	void setFirst(T first) {
		this.first = first;
	}

	void setSecond(T second) {
		this.second = second;
	}

	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}	
}
