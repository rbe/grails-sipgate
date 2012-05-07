package com.bensmann.voip

/**
 * 
 */
class VoipLog {
	
	/**
	 * Date of creation.
	 */
	Date dateCreated
	
	/**
	 * Date of last update.
	 */
	Date lastUpdated
	
	/**	
	 * Type: voice, fax, sms.
	 */
	String voipType
	
	/**
	 * The status: SUCCESS, WARNING, ERROR.
	 */
	String status
	
	/**
	 * The VoIP account.
	 */
	String voipAccount
	
	/**
	 * Remote URI.
	 */
	String remoteUri
	
	/**
	 * Session ID.
	 */
	String sessionId
	
	/**
	 * The message.
	 */
	String message
	
	static mapping = {
		table "T3_LOG"
	}
	
	static constraints = {
		lastUpdated(nullable: true)
		voipType(nullable: false, inList: ["voice", "fax", "sms"])
		status(nullable: false, inList: ["SUCCESS", "WARNING", "FAILURE"])
		voipAccount(nullable: false)
		remoteUri(nullable: true)
		sessionId(nullable: true, maxSize: 500)
		message(nullable: false, maxSize: 4000)
		// validator: { val, obj, errors -> }
	}
	
}
