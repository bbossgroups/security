/**
 * 
 */
package org.frameworkset.security.session.impl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.frameworkset.security.session.SessionIDGenerator;

/**
 * @author yinbp
 *
 * @Date:2016-11-21 16:26:34
 */
public abstract class SessionIdGeneratorBase implements SessionIDGenerator {

	 

		    private static final Logger log = Logger.getLogger(SessionIdGeneratorBase.class);


		    

		    /**
		     * Queue of random number generator objects to be used when creating session
		     * identifiers. If the queue is empty when a random number generator is
		     * required, a new random number generator object is created. This is
		     * designed this way since random number generators use a sync to make them
		     * thread-safe and the sync makes using a a single object slow(er).
		     */
		    private final Queue<SecureRandom> randoms = new ConcurrentLinkedQueue<SecureRandom>();

		    private String secureRandomClass = null;

		    private String secureRandomAlgorithm = "SHA1PRNG";

		    private String secureRandomProvider = null;


		


		    /** Number of bytes in a session ID. Defaults to 16. */
		    private int sessionIdLength = 16;


		    /**
		     * Get the class name of the {@link SecureRandom} implementation used to
		     * generate session IDs.
		     *
		     * @return The fully qualified class name. {@code null} indicates that the
		     *         JRE provided {@link SecureRandom} implementation will be used
		     */
		    public String getSecureRandomClass() {
		        return secureRandomClass;
		    }


		    /**
		     * Specify a non-default {@link SecureRandom} implementation to use. The
		     * implementation must be self-seeding and have a zero-argument constructor.
		     * If not specified, an instance of {@link SecureRandom} will be generated.
		     *
		     * @param secureRandomClass The fully-qualified class name
		     */
		    public void setSecureRandomClass(String secureRandomClass) {
		        this.secureRandomClass = secureRandomClass;
		    }


		    /**
		     * Get the name of the algorithm used to create the {@link SecureRandom}
		     * instances which generate new session IDs.
		     *
		     * @return The name of the algorithm. {@code null} or the empty string means
		     *         that platform default will be used
		     */
		    public String getSecureRandomAlgorithm() {
		        return secureRandomAlgorithm;
		    }


		    /**
		     * Specify a non-default algorithm to use to create instances of
		     * {@link SecureRandom} which are used to generate session IDs. If no
		     * algorithm is specified, SHA1PRNG is used. To use the platform default
		     * (which may be SHA1PRNG), specify {@code null} or the empty string. If an
		     * invalid algorithm and/or provider is specified the {@link SecureRandom}
		     * instances will be created using the defaults for this
		     * {@link SessionIdGenerator} implementation. If that fails, the
		     * {@link SecureRandom} instances will be created using platform defaults.
		     *
		     * @param secureRandomAlgorithm The name of the algorithm
		     */
		    public void setSecureRandomAlgorithm(String secureRandomAlgorithm) {
		        this.secureRandomAlgorithm = secureRandomAlgorithm;
		    }


		    /**
		     * Get the name of the provider used to create the {@link SecureRandom}
		     * instances which generate new session IDs.
		     *
		     * @return The name of the provider. {@code null} or the empty string means
		     *         that platform default will be used
		     */
		    public String getSecureRandomProvider() {
		        return secureRandomProvider;
		    }


		    /**
		     * Specify a non-default provider to use to create instances of
		     * {@link SecureRandom} which are used to generate session IDs.  If no
		     * provider is specified, the platform default is used. To use the platform
		     * default specify {@code null} or the empty string. If an invalid algorithm
		     * and/or provider is specified the {@link SecureRandom} instances will be
		     * created using the defaults for this {@link SessionIdGenerator}
		     * implementation. If that fails, the {@link SecureRandom} instances will be
		     * created using platform defaults.
		     *
		     * @param secureRandomProvider  The name of the provider
		     */
		    public void setSecureRandomProvider(String secureRandomProvider) {
		        this.secureRandomProvider = secureRandomProvider;
		    }


		   

		    /**
		     * Return the number of bytes for a session ID
		     */
		    
		    public int getSessionIdLength() {
		        return sessionIdLength;
		    }


		    /**
		     * Specify the number of bytes for a session ID
		     *
		     * @param sessionIdLength   Number of bytes
		     */
		   
		    public void setSessionIdLength(int sessionIdLength) {
		        this.sessionIdLength = sessionIdLength;
		    }


		    /**
		     * Generate and return a new session identifier.
		     */
		   
		    public abstract String generateID();


		    protected void getRandomBytes(byte bytes[]) {

		        SecureRandom random = randoms.poll();
		        if (random == null) {
		            random = createSecureRandom();
		        }
		        random.nextBytes(bytes);
		        randoms.add(random);
		    }


		    /**
		     * Create a new random number generator instance we should use for
		     * generating session identifiers.
		     */
		    private SecureRandom createSecureRandom() {

		        SecureRandom result = null;

		        long t1 = System.currentTimeMillis();
		        if (secureRandomClass != null) {
		            try {
		                // Construct and seed a new random number generator
		                Class<?> clazz = Class.forName(secureRandomClass);
		                result = (SecureRandom) clazz.newInstance();
		            } catch (Exception e) {
		                log.error("sessionIdGeneratorBase.random", e);
		            }
		        }

		        if (result == null) {
		            // No secureRandomClass or creation failed. Use SecureRandom.
		            try {
		                if (secureRandomProvider != null &&
		                        secureRandomProvider.length() > 0) {
		                    result = SecureRandom.getInstance(secureRandomAlgorithm,
		                            secureRandomProvider);
		                } else if (secureRandomAlgorithm != null &&
		                        secureRandomAlgorithm.length() > 0) {
		                    result = SecureRandom.getInstance(secureRandomAlgorithm);
		                }
		            } catch (NoSuchAlgorithmException e) {
		                log.error("sessionIdGeneratorBase.randomAlgorithm", e);
		            } catch (NoSuchProviderException e) {
		                log.error("sessionIdGeneratorBase.randomProvider", e);
		            }
		        }

		        if (result == null) {
		            // Invalid provider / algorithm
		            try {
		                result = SecureRandom.getInstance("SHA1PRNG");
		            } catch (NoSuchAlgorithmException e) {
		                log.error("sessionIdGeneratorBase.randomAlgorithm", e);
		            }
		        }

		        if (result == null) {
		            // Nothing works - use platform default
		            result = new SecureRandom();
		        }

		        // Force seeding to take place
		        result.nextInt();

		        long t2=System.currentTimeMillis();
		        if( (t2-t1) > 100 )
		            log.info("sessionIdGeneratorBase.createRandom:"+result.getAlgorithm()+"="+ Long.valueOf(t2-t1));
		        return result;
		    }

 

}
