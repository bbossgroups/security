package org.frameworkset.platform.security.authentication;


public class PasswordCallback  implements Callback, java.io.Serializable {

    private static final long serialVersionUID = 2267422647454909926L;

    /**
     * @serial
     * @since 1.4
     */
    private String prompt;
    /**
     * @serial
     * @since 1.4
     */
    private boolean echoOn;
    /**
     * @serial
     * @since 1.4
     */
    private String inputPassword;

    /**
     * Construct a <code>PasswordCallback</code> with a prompt
     * and a boolean specifying whether the password should be displayed
     * as it is being typed.
     *
     * <p>
     *
     * @param prompt the prompt used to request the password. <p>
     *
     * @param echoOn true if the password should be displayed
     *                  as it is being typed.
     *
     * @exception IllegalArgumentException if <code>prompt</code> is null or
     *                  if <code>prompt</code> has a length of 0.
     */
    public PasswordCallback(String prompt, boolean echoOn) {
        if (prompt == null || prompt.length() == 0)
            throw new IllegalArgumentException();

        this.prompt = prompt;
        this.echoOn = echoOn;
    }

    /**
     * Get the prompt.
     *
     * <p>
     *
     * @return the prompt.
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * Return whether the password
     * should be displayed as it is being typed.
     *
     * <p>
     *
     * @return the whether the password
     *          should be displayed as it is being typed.
     */
    public boolean isEchoOn() {
        return echoOn;
    }

    /**
     * Set the retrieved password.
     *
     * <p> This method makes a copy of the input <i>password</i>
     * before storing it.
     *
     * <p>
     *
     * @param password the retrieved password, which may be null.
     *
     * @see #getPassword
     */
    public void setPassword(String password) {
        this.inputPassword = password;
    }

    /**
     * Get the retrieved password.
     *
     * <p> This method returns a copy of the retrieved password.
     *
     * <p>
     *
     * @return the retrieved password, which may be null.
     *
     * @see #setPassword
     */
    public String getPassword() {
        return inputPassword;
    }

    /**
     * Clear the retrieved password.
     */
    public void clearPassword() {
    	inputPassword = "";
    }

}
