package org.frameworkset.security.session;

import java.util.Enumeration;
import java.util.Iterator;

public class AttributeNamesEnumeration<E> implements Enumeration<E> {
	private Iterator<E> attributeNames;
	public AttributeNamesEnumeration(Iterator<E> attributeNames) {
		this.attributeNames = attributeNames;
	}

	@Override
	public boolean hasMoreElements() {
		// TODO Auto-generated method stub
		return attributeNames.hasNext();
	}

	@Override
	public E nextElement() {
		// TODO Auto-generated method stub
		return attributeNames.next();
	}

}
