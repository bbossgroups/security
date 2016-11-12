package org.frameworkset.web.token;

import org.frameworkset.security.ecc.ECCHelper;
import org.frameworkset.web.token.DBTokenStore;
import org.frameworkset.web.token.MemToken;
import org.frameworkset.web.token.TokenStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DBTokenTest {
	private static DBTokenStore mongodbTokenStore;
	private String account = "yinbp";
	private String worknumber = "10006673";
	private String appid = "test";
	private String secret = "c96a4686-c5dc-4556-9554-eb61846b5180";
//	 String server = "http://10.0.15.223/SanyToken";
	String server = "http://pdp.bbossgroups.com";
	boolean sign = false;
	
	@Before
	public void init() throws Exception
	{
		mongodbTokenStore = new DBTokenStore();
		mongodbTokenStore.setECCCoder(ECCHelper.getECCCoder());
		mongodbTokenStore.setValidateApplication(new NullValidateApplication());
		mongodbTokenStore.setTempTokendualtime(TokenStore.DEFAULT_TEMPTOKENLIVETIME);
		mongodbTokenStore.setTicketdualtime(TokenStore.DEFAULT_TICKETTOKENLIVETIME);
		mongodbTokenStore.setDualtokenlivetime(TokenStore.DEFAULT_DUALTOKENLIVETIME);
		Ticket ticket = mongodbTokenStore.genTicket(account, worknumber, appid, secret);
		MemToken token = null;//mongodbTokenStore.genDualToken(appid,ticket.getToken(),secret,TokenStore.DEFAULT_DUALTOKENLIVETIME);
//		Assert.assertTrue(TokenStore.token_request_validateresult_ok == mongodbTokenStore.checkToken(appid,secret,token.getToken()).getResult());
		token = mongodbTokenStore.genTempToken();
		Assert.assertTrue(TokenStore.token_request_validateresult_ok == mongodbTokenStore.checkToken(null,null,token.getToken()).getResult());
		token = mongodbTokenStore.genAuthTempToken(appid,ticket.getToken(),secret);
		Assert.assertTrue(TokenStore.token_request_validateresult_ok == mongodbTokenStore.checkToken(appid,secret,token.getToken()).getResult());
	}
	@Test
	public void genTemptokenAndValidate() throws Exception
	{		
		
		MemToken token = mongodbTokenStore.genTempToken();
		Assert.assertTrue(TokenStore.token_request_validateresult_ok == mongodbTokenStore.checkToken(null,null,token.getToken()).getResult());
	}
	
//	@Test
//	public void gendualtokenAndValidate() throws Exception
//	{
//		Ticket ticket = mongodbTokenStore.genTicket(account, worknumber, appid, secret);
//		//long start = System.currentTimeMillis();
//		MemToken token = mongodbTokenStore.genDualToken(appid,ticket.getToken(),secret,TokenStore.DEFAULT_DUALTOKENLIVETIME);
//		//long end = System.currentTimeMillis();
//		//System.out.println(end - start);
//		//start = System.currentTimeMillis();
//		Assert.assertTrue(TokenStore.token_request_validateresult_ok == mongodbTokenStore.checkToken(appid,secret,token.getToken()).getResult());
//		//end = System.currentTimeMillis();
//		//System.out.println(end - start);
//	}
	
	
	@Test
	public void gentempauthortokenAndValidate() throws Exception
	{
		Ticket ticket = mongodbTokenStore.genTicket(account, worknumber, appid, secret);
		MemToken token = mongodbTokenStore.genAuthTempToken(appid,ticket.getToken(),secret);
		Assert.assertTrue(TokenStore.token_request_validateresult_ok == mongodbTokenStore.checkToken(appid,secret,token.getToken()).getResult());
	}
	@Test
	public void livecheck() throws Exception
	{
		mongodbTokenStore.livecheck();
	}
	@Test
	public void testticket() throws Exception
	{
		Ticket ticket = mongodbTokenStore.genTicket(account, worknumber, appid, secret);
		System.out.println(":\n"+ticket.getToken());
	}
	
	@Test
	public void testtempticketandvalidate() throws Exception
	{
		Ticket ticket = mongodbTokenStore.genTempTicket(account, worknumber, appid, secret);
		System.out.println(":\n"+ticket.getToken());
		TokenResult result = mongodbTokenStore.checkTicket(appid, secret, ticket.getToken());
		Assert.assertTrue(TokenStore.token_request_validateresult_ok ==result.getResult());
		 result =  mongodbTokenStore.checkTicket(appid, secret, ticket.getToken());
		 Assert.assertTrue(TokenStore.token_request_validateresult_ok ==result.getResult());
	}
	
	public static void main(String[] args) throws Exception
	{
		final DBTokenTest s = new DBTokenTest();
		s.init();
		for(int i = 0; i < 10; i ++)
		{
			Thread t = new Thread(){
				public void run()
				{
					while(true)
					{
						try {
							long start = System.currentTimeMillis();
//							mongodbTokenStore.requestStart();
//							s.gendualtokenAndValidate();
							 s.gentempauthortokenAndValidate();
							s.genTemptokenAndValidate();
							
							long end = System.currentTimeMillis();
							System.out.println("耗时:"+(end -start));
							sleep(1000);
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						finally
						{
//							mongodbTokenStore.requestDone();
						}
						
					}
				}
			};
			t.start();
			
					
		}
	}

}
