package org.frameworkset.security.session.impl;

import java.util.UUID;

import org.frameworkset.security.session.SessionIDGenerator;

public class UUIDSessionIDGenerator implements SessionIDGenerator {

	@Override
	public String generateID() {
		String token = UUID.randomUUID().toString();
		return token;
	}

}
