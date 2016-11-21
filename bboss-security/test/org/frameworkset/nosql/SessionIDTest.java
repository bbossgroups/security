/**
 * 
 */
package org.frameworkset.nosql;

import org.frameworkset.security.session.impl.StandardSessionIdGenerator;
import org.frameworkset.security.session.impl.UUIDSessionIDGenerator;
import org.junit.Test;

/**
 * @author yinbp
 *
 * @Date:2016-11-21 16:38:49
 */
public class SessionIDTest {

	/**
	 * 
	 */
	public SessionIDTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void test()
	{
		StandardSessionIdGenerator g = new StandardSessionIdGenerator();
		System.out.println(g.generateID());
		
		UUIDSessionIDGenerator s = new UUIDSessionIDGenerator();
		System.out.println(s.generateID());
		System.out.println(s.generateID());
		
		
	}

}
