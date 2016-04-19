package org.frameworkset.security.session.impl;

public class ModifyValue {
	public ModifyValue(String name, Object value, int valuetype,int optype) {
		super();
		this.name = name;
		this.value = value;
		this.valuetype = valuetype;
		this.optype = optype;
	}

	private String name;
	private Object value;
	private int valuetype;
	public static final int type_base = 1;
	public static final int type_data = 2;
	private int optype;
	
	public static final int type_add = 1;
	public static final int type_expired = 3;
	public static final int type_remove = 2;
	public ModifyValue() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getValuetype() {
		return valuetype;
	}

	public void setValuetype(int valuetype) {
		this.valuetype = valuetype;
	}

	public int getOptype() {
		return optype;
	}

}
