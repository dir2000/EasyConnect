package com.zhurylomihaylo.www.easyconnect;

class FieldDescription{
	private String name;
	private String header;
	private Class type;
	int order;
	boolean visible;
	
	FieldDescription(String name, String header, Class type, int order, boolean visible) {
		this.name = name;
		this.header = header;
		this.type = type;
		this.order = order;
	}

	String getName() {
		return name;
	}

	String getHeader() {
		return header;
	}

	Class getType() {
		return type;
	}

	int getOrder() {
		return order;
	}

	boolean isVisible() {
		return visible;
	}
}
