/**
 * 
 */
package org.frameworkset.security.session.impl;

/**
 * @author yinbp
 *
 * @Date:2016-11-21 16:31:53
 */
public class StandardSessionIdGenerator  extends SessionIdGeneratorBase {

    @Override
    public String generateID() {

        byte random[] = new byte[16];
        int sessionIdLength = getSessionIdLength();

        // Render the result as a String of hexadecimal digits
        // Start with enough space for sessionIdLength and medium route size
        StringBuilder buffer = new StringBuilder(2 * sessionIdLength + 20);

        int resultLenBytes = 0;

        while (resultLenBytes < sessionIdLength) {
            getRandomBytes(random);
            for (int j = 0;
            j < random.length && resultLenBytes < sessionIdLength;
            j++) {
                byte b1 = (byte) ((random[j] & 0xf0) >> 4);
                byte b2 = (byte) (random[j] & 0x0f);
                if (b1 < 10)
                    buffer.append((char) ('0' + b1));
                else
                    buffer.append((char) ('A' + (b1 - 10)));
                if (b2 < 10)
                    buffer.append((char) ('0' + b2));
                else
                    buffer.append((char) ('A' + (b2 - 10)));
                resultLenBytes++;
            }
        }

        

        return buffer.toString();
    }

}
