package com.nexmo.messaging.sdk;

import java.math.BigDecimal;

/**
 * SmsSubmissionResult.java<br><br>
 *
 * Created on 5 January 2011, 17:34<br><br>
 *
 * Represents the result of the submission of a single sms message,
 * or a single part of a long / multi-part message
 *
 * @author  Paul Cook
 * @version 1.0
 */
public class SmsSubmissionResult {

    /**
     * Message was successfully submitted to the Nexmo service
     */
    public static final int STATUS_OK = 0;

    /**
     * Message was rejected due to exceeding the maximum throughput allowed for this account.<br>
     * Message can be re-submitted after a short delay
     */
    public static final int STATUS_THROTTLED = 1;

    /**
     * Message was rejected due to incomplete data in the submission request
     */
    public static final int STATUS_MISSING_PARAMS = 2;

    /**
     * Message was rejected due to an illegal value in one or more elements of the submission request
     */
    public static final int STATUS_INVALID_PARAMS = 3;

    /**
     * Message was rejected due to receiving invalid account username and/or password
     */
    public static final int STATUS_INVALID_CREDENTIALS = 4;

    /**
     * Message was rejected due to a failure within the Nexmo systems.<br>
     * Message can be re-submitted after a short delay
     */
    public static final int STATUS_INTERNAL_ERROR = 5;

    /**
     * Message was rejected because the Nexmo service was unable to handle this request. eg, the destination was un-routable.
     */
    public static final int STATUS_INVALID_MESSAGE = 6;

    /**
     * Message was rejected because the phone number you tried to submit to has been blacklisted.
     */
    public static final int STATUS_NUMBER_BARRED = 7;

    /**
     * Message was rejected because your account has been barred, or has not yet been activated
     */
    public static final int STATUS_PARTNER_ACCOUNT_BARRED = 8;

    /**
     * Message was rejected because your pre-paid balance does not contain enough credit to handle this request.<br>
     * Please top up your balance before re-submitting this request or subsequent requests.
     */
    public static final int STATUS_PARTNER_QUOTA_EXCEEDED = 9;

    /**
     * Message was rejected because already have the maximum number of concurrent connections allowed for your account.
     */
    public static final int STATUS_TOO_MANY_BINDS = 10;

    /**
     * Message was rejected because your account is not provisioned for submitting via the REST interface.
     */
    public static final int STATUS_ACCOUNT_NOT_HTTP = 11;

    /**
     * Message was rejected because it exceeds the allowable maximum length (140 octets for a binary message, or 3200 chars for a text message)
     */
    public static final int STATUS_MESSAGE_TOO_LONG = 12;

    /**
     * Message was not submitted because there was a communications failure.
     */
    public static final int STATUS_COMMS_FAILURE = 13;

    /**
     * Message was not submitted due to a verification failure in the submitted signature
     */
    public static final int STATUS_INVALID_SIGNATURE = 14;

    private final int status;
    private final String messageId;
    private final String errorText;
    private final String clientReference;
    private final BigDecimal remainingBalance;
    private final BigDecimal messagePrice;
    private final boolean temporaryError;

    protected SmsSubmissionResult(final int status,
                                  final String messageId,
                                  final String errorText,
                                  final String clientReference,
                                  final BigDecimal remainingBalance,
                                  final BigDecimal messagePrice,
                                  final boolean temporaryError) {
        this.status = status;
        this.messageId = messageId;
        this.errorText = errorText;
        this.clientReference = clientReference;
        this.remainingBalance = remainingBalance;
        this.messagePrice = messagePrice;
        this.temporaryError = temporaryError;
    }

    /**
     * @return int status code representing either the success of the message submission or a reason for failure
     */
    public int getStatus() {
        return this.status;
    }

    /**
     * @return String a unique identifier associated with the submitted message.
     *         This value will be returned in any subsequent delivery notifications in order that they may be correlated with the appropriate message submission request.
     */
    public String getMessageId() {
        return this.messageId;
    }

    /**
     * @return String a human readable error message giving further description of the value of {see #getStatus()}
     */
    public String getErrorText() {
        return this.errorText;
    }

    /**
     * @return String the client-reference that was supplied as part of the original message submission
     */
    public String getClientReference() {
        return this.clientReference;
    }

    /**
     * @return BigDecimal The account balance that remains after the submission of this message.
     */
    public BigDecimal getRemainingBalance() {
        return this.remainingBalance;
    }

    /**
     * @return BigDecimal The amount that was debited from your account balance upon submission of this message
     */
    public BigDecimal getMessagePrice() {
        return this.messagePrice;
    }

    /**
     * @return boolean Indicates if the failure was due to a temporary condition. If so, then the message submission may be re-attempted after a short delay
     */
    public boolean getTemporaryError() {
        return this.temporaryError;
    }

}
