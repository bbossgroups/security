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
package org.frameworkset.security.rsa;

import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.frameworkset.security.KeyCacheUtil;
import org.frameworkset.security.ecc.BaseECCCoder;



/**
 * <p>Title: RsaCoder.java</p> 
 * <p>Description: </p>
 * <p>bboss workgroup</p>
 * <p>Copyright (c) 2008</p>
 * @Date 2014年4月22日
 * @author biaoping.yin
 * @version 3.8.0
 */
public class RsaCoder extends BaseECCCoder {
	 
	
	
	
	public  PrivateKey _evalECPrivateKey(byte[] privateKey)
	{
		return    KeyCacheUtil._evalECPrivateKey(privateKey);
	}

	 
	public  PublicKey _evalECPublicKey(byte[] publicKey)
	{
		
		return    KeyCacheUtil._evalECPublicKey(publicKey);
		
	}

	
	
	/**
	 * 解密<br>
	 * 用私钥解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public  byte[] decrypt(byte[] data, Key priKey) throws Exception {
		return KeyCacheUtil.decrypt(data, priKey);
	}

	
	
	/**
	 * 加密<br>
	 * 用公钥加密
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public  byte[] encrypt(byte[] data, Key pubKey)
			throws Exception {
		

		return KeyCacheUtil.encrypt(data, pubKey);
	}
	
	

	@Override
	public KeyPair _genECKeyPair() throws Exception {
				return KeyCacheUtil.genECKeyPair();
	}

}
