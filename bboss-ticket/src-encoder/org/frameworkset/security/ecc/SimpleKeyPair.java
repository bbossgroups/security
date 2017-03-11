/*
 *  Copyright 2008 bbossgroups
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.frameworkset.security.ecc;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * <p>Title: KeyPairs.java</p> 
 * <p>Description: </p>
 * <p>bboss workgroup</p>
 * <p>Copyright (c) 2008</p>
 * @Date 2014年4月22日
 * @author biaoping.yin
 * @version 3.8.0
 */
public class SimpleKeyPair {
	private String privateKey;
	private String publicKey;
	private transient Key pubKey;
	private transient Key priKey;
//	private transient Key key;
	private transient String certAlgorithm;
//	public Key getKey() {
//		return key;
//	}
//	public void setKey(Key key) {
//		this.key = key;
//	}
	public String getCertAlgorithm() {
		return certAlgorithm;
	}
	public void setCertAlgorithm(String certAlgorithm) {
		this.certAlgorithm = certAlgorithm;
	}
	public SimpleKeyPair(){
		
	}
//	public SimpleKeyPair(String privateKey, String publicKey,
//			Key pubKey, Key priKey) {
//		super();
//		this.privateKey = privateKey;
//		this.publicKey = publicKey;
//		this.pubKey = pubKey;
//		this.priKey = priKey;
//	}
	
	public SimpleKeyPair(String privateKey, String publicKey,
			Key pubKey, Key priKey,String certAlgorithm) {
		super();
		this.privateKey = privateKey;
		this.publicKey = publicKey;
		this.pubKey = pubKey;
		this.priKey = priKey;
		this.certAlgorithm =  certAlgorithm;
	}
//	public SimpleKeyPair(  String publicKey,
//			Key key) {
//		super();
//		 
//		this.publicKey = publicKey;
//		this.key = key;
//	}
	
//	public SimpleKeyPair(  String publicKey,
//			String certAlgorithm) {
//		super();
//		 
//		this.publicKey = publicKey;
//		this.certAlgorithm = certAlgorithm;
//	}
	public String getPrivateKey() {
		return privateKey;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public  Key getPubKey() {
		return pubKey;
	}
	public  Key getPriKey() {
		return priKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public void setPubKey(Key pubKey) {
		this.pubKey = pubKey;
	}
	public void setPriKey(Key priKey) {
		this.priKey = priKey;
	}
}
