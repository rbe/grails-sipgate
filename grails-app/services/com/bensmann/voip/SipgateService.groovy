package com.bensmann.voip

import com.bensmann.sipgate.client.*

/**
 * 
 */
class SipgateService {
	
	/**
	 * The scope. See http://www.grails.org/Services.
	 */
	def scope = "prototype" // prototype request flash flow conversation session singleton
	
	/**
	 * Transactional?
	 */
	boolean transactional = true
	
	/**
	 * 
	 */
	def grailsApplication
	
	/**
	 * The SipgateClient.
	 */
	def factory = [:]
	
	/**
	 * Success.
	 */
	def logSuccess(arg) {
		try {
			def v = new VoipLog(voipType: arg.voipType, status: "SUCCESS",
				voipAccount: arg.voipAccount as String, remoteUri: arg.remoteUri as String,
				sessionId: arg.sessionId as String,
				message: arg.message)
			v.save()
		} catch (e) {
			log.error e
		}
	}
	
	/**
	 * Warning.
	 */
	def logWarning(arg) {
		try {
			def v = new VoipLog(voipType: arg.voipType, status: "WARNING",
				voipAccount: arg.voipAccount as String, remoteUri: arg.remoteUri as String,
				sessionId: arg.sessionId as String,
				message: arg.message)
			v.save()
		} catch (e) {
			log.error e
		}
	}
	
	/**
	 * Failure.
	 */
	def logFailure(arg) {
		try {
			def v = new VoipLog(voipType: arg.voipType, status: "FAILURE",
				voipAccount: arg.voipAccount as String, remoteUri: arg.remoteUri as String,
				sessionId: arg.sessionId as String,
				message: arg.message)
			v.save()
		} catch (e) {
			log.error e
		}
	}
	
	/**
	 * If present, remove + and replace leading 0 with 49
	 * Test: println checkUri("0251") checkUri(["0151", "0049162", "+49171"])
	 */
	def checkUri(uri) {
		// No URI?
		if (!uri) return
		//
		def closure = { u ->
			def buf = new StringBuilder(u)
			if (buf.charAt(0) == '+') {
				buf.deleteCharAt(0)
			}
			2.times {
				if (buf.charAt(0) == '0') {
					buf.deleteCharAt(0)
				}
			}
			if (!(buf[0..1] == "49")) {
				buf.insert(0, "49")
			}
			buf.toString().replaceAll(" ", "")
		}
		if (uri.class == String.class) {
			closure(uri)
		} else {
			uri.collect {
				closure(it)
			}
		}
	}
	
	/**
	 * Get local URI from configuration.
	 */
	def private getLocalUri(username) {
		def users = grailsApplication.config.voip?.users
		def localUri = users."${username}"?.localUri
		localUri
	}
	
	/**
	 * 
	 */
	def private login(cred) {
		if (!cred) throw new IllegalArgumentException("No cred!")
		// Get username
		def username = cred.username
		// Get sipgate client factory for that user
		def client = factory.username
		// Create a new factory if none exists
		if (!client) {
			if (log.debugEnabled) log.debug "creating new client through factory for ${username}"
			client = SipgateClientFactory.create(cred.username, cred.password)
			factory.username = client
		} else {
			if (log.debugEnabled) log.debug "using existing client for ${username}"
		}
		// Return client factory
		client
	}
	
	/**
	 * Process a request/response sequence with Sipgate.
	 */
	def private requestResponse(arg) {
		def ret = [:]
		// Mandatory parameters for a communication with sipgate
		if (arg.cred.username && arg.cred.password && arg.cred.localUri) {
			// Login and execute command
			arg.response = arg.request.call(login(arg.cred))
			// Process response
			if (log.debugEnabled) log.debug "processing response: ${arg.response} for account ${arg.cred}"
			if (arg.response?.StatusCode == 200) {
				if (arg.success) {
					arg.success(arg.response)
				}
			} else if (arg.failure) {
				arg.failure(arg.response)
			} else {
				log.error "requestResponse(${arg.inspect()}): No failure closure"
			}
		} else {
			log.error "requestResponse(${arg.inspect()}): Insufficient parameters"
		}
		// Standard return value
		[statusCode: arg.response?.StatusCode, sessionId: arg.response?.SessionID]
	}
	
	/**
	 * Send a SMS.
	 */
	def sendSms(arg) {
		if (!arg.cred) throw new IllegalArgumentException("No cred")
		// Return value
		def ret = [:]
		try {
			// Check local and remote URI
			arg.cred.localUri = checkUri(arg.cred.localUri ?: getLocalUri(arg.cred.username))
			arg.remoteUri = checkUri(arg.remoteUri)
			if (log.debugEnabled) log.debug "voipUser=${arg.cred.username}, localUri=${arg.cred.localUri}, remoteUri=${arg.remoteUri}, text=${arg.text}"
			// Execute command and process response
			ret = requestResponse(
				cred: arg.cred,
				remoteUri: arg.remoteUri,
				request: { sgc -> sgc.sendSms(arg.cred.localUri, arg.remoteUri as String[], arg.text) },
				success: { response ->
					if (log.debugEnabled) log.debug "SMS sent, session ID = ${response.SessionList.SessionID}"
					logSuccess(voipType: "sms", voipAccount: arg.cred, remoteUri: arg.remoteUri as String, sessionId: response.SessionList.SessionID as String, message: "Sent SMS: ${arg.text}")
				},
				failure: { response ->
					if (log.debugEnabled) log.debug "ERROR: ${response}"
					logFailure(voipType: "sms", voipAccount: arg.cred, remoteUri: arg.remoteUri as String, message: "Can't send SMS")
				}
			)
		} catch (e) {
			if (log.debugEnabled) e.printStackTrace()
			logFailure(voipType: "voice",
				voipAccount: arg.cred, remoteUri: "${arg.remoteUri}",
				message: "Can't initiate call: ${e}")
		}
		ret
	}
	
	/**
	 * Send a fax.
	 */
	def sendFax(arg) {
		if (!arg.cred) throw new IllegalArgumentException("No cred")
		// Return value
		def ret = [:]
		try {
			// Check local and remote URI
			arg.cred.localUri = checkUri(arg.cred.localUri ?: getLocalUri(arg.cred.username))
			arg.remoteUri = checkUri(arg.remoteUri)
			if (log.debugEnabled) log.debug "voipUser=${arg.cred.username}, localUri=${arg.localUri}, remoteUri=${arg.remoteUri}, byte#=${arg.bytes.length}"
			// Execute command and process response
			ret = requestResponse(
				cred: arg.cred,
				remoteUri: arg.remoteUri,
				request: { sgc -> sgc.sendFax(arg.cred.localUri, arg.remoteUri as String[], arg.bytes) },
				success: { response ->
					if (log.debugEnabled) log.debug "Sent fax, session ID = ${response.SessionList.SessionID}"
					logSuccess(voipType: "fax", voipAccount: arg.cred, remoteUri: arg.remoteUri as String, sessionId: response.SessionList.SessionID, message: "Sent fax with ${arg.bytes.length} bytes")
				},
				failure: { response ->
					if (log.debugEnabled) log.debug "ERROR: ${response}"
					if (response.StatusCode == 402) {
						log.warn "statusCode 402: Maybe wrong local URI for fax was used?!"
					}
					logFilure(voipType: "fax", voipAccount: arg.cred, remoteUri: arg.remoteUri as String, message: "Can't send fax")
				}
			)
		} catch (e) {
			if (log.debugEnabled) e.printStackTrace()
			logFailure(voipType: "voice",
				voipAccount: arg.cred, remoteUri: "${arg.remoteUri}",
				message: "Can't initiate call: ${e}")
		}
		ret
	}
	
	/**
	 * Initiate a call.
	 */
	def initiateCall(arg) {
		if (!arg.cred) throw new IllegalArgumentException("No cred")
		// Return value
		def ret = [:]
		try {
			// Check local and remote URI
			arg.cred.localUri = checkUri(arg.cred.localUri ?: getLocalUri(arg.cred.username))
			arg.remoteUri = checkUri(arg.remoteUri)
			if (log.debugEnabled) log.debug "voipUser=${arg.cred.username}, localUri=${arg.cred.localUri}, remoteUri=${arg.remoteUri}"
			// Execute command and process response
			ret = requestResponse(
				cred: arg.cred,
				remoteUri: arg.remoteUri,
				request: { sgc -> sgc.initiateCall(arg.cred.localUri, arg.remoteUri) },
				success: { response ->
					if (log.debugEnabled) log.debug "Call initiated, session ID = ${response.SessionID}"
					logSuccess(voipType: "voice", voipAccount: arg.cred, remoteUri: arg.remoteUri, sessionId: response.SessionID, message: "Established call")
				},
				failure: { response ->
					if (log.debugEnabled) log.debug "ERROR: ${response}"
					logFailure(voipType: "voice", voipAccount: arg.cred, remoteUri: "${arg.remoteUri}", sessionId: sessionId, message: "Can't initiate call")
				}
			)
		} catch (e) {
			if (log.debugEnabled) e.printStackTrace()
			logFailure(voipType: "voice", voipAccount: arg.cred, remoteUri: "${arg.remoteUri}", message: "Can't initiate call: ${e}")
		}
		ret
	}
	
	/**
	 * End a call.
	 */
	def hangupCall(arg) {
		if (!arg.cred) throw new IllegalArgumentException("No cred")
		// Return value
		def ret = [:]
		try {
			if (log.debugEnabled) log.debug "voipUser=${arg.cred.username}, sessionId=${arg.sessionId}"
			// Execute command and process reponse
			ret = requestResponse(
				cred: arg.cred,
				remoteUri: arg.remoteUri,
				request: { sgc -> sgc.closeSession(arg.sessionId) },
				success: { response ->
					if (log.debugEnabled) log.debug "Call hung up"
					logSuccess(voipType: "voice", voipAccount: arg.cred, sessionId: arg.sessionId, message: "Hung up call")
				},
				failure: { response ->
					if (log.debugEnabled) log.debug "ERROR: ${response}"
					logFailure(voipType: "voice", voipAccount: arg.cred, sessionId: arg.sessionId, message: "Can't hang up call")
				}
			)
		} catch (e) {
			if (log.debugEnabled) e.printStackTrace()
			logFailure(voipType: "voice", voipAccount: arg.cred, remoteUri: "${arg.remoteUri}", message: "Can't initiate call: ${e}")
			
		}
		ret
	}
	
	/**
	 * Get the event list from Sipgate.
	def getEventList(arg) {
		if (!arg.cred) throw new IllegalArgumentException("No cred")
		if (log.debugEnabled) log.debug "username=${arg.cred.username}"
		def sgc = login(arg.cred)
		def eventListResponse = sgc.exe("samurai.EventListGet", [
			Labels: arg.labels ?: [],
			EventIDs: arg.eventIds ?: [],
			TOS: (arg.tos ?: ["voice", "fax", "text"]) as String[],
			Limit: 0,
			Offset: 0,
			//put("PeriodStart", null);
			//put("PeriodEnd", null);
			//put("IncrementBaseID", "");
		])
		requestResponse
			response: eventListResponse,
			success: { response ->
				[statusCode: response.StatusCode, sessionId: response.SessionID]
			},
			failure: { response ->
				println "ERROR: ${response}"
				[statusCode: response.StatusCode]
			}
		eventListResponse
	}
	 */
	
	/**
	 * Get the recommended intervalls for calling XML-RPC methods from Sipgate.
	def getRecommendedInterval(arg) {
		if (!arg.cred) throw new IllegalArgumentException("No cred")
		if (log.debugEnabled) log.debug "username=${arg.cred.username}"
		def sgc = login(arg.cred)
		def recommendedIntervalResponse = sgc.exe("samurai.RecommendedIntervalGet", [
			MethodList: [
				"samurai.SessionInitiate",
				"samurai.SessionInitiateMulti",
				"samurai.RecommendedIntervalGet"
			] as String[]
		])
		requestResponse
			response: recommendedIntervalResponse,
			success: { response ->
				[statusCode: response.StatusCode, sessionId: response.SessionID]
			},
			failure: { response ->
				println "ERROR: ${response}"
				[statusCode: response.StatusCode]
			}
		recommendedIntervalResponse
	}
	 */
	
	/**
	 * Add a notification.
	 * @param arg Map: voipAccount, message
	def addNotification(arg) {
		def e = new VoipNotify(voipAccount: arg.voipAccount, message: arg.message)
		e?.save(flush: true)
		e?.refresh()
		e
	}
	 */
	
	/**
	 * Get notifications (for a VoIP account).
	 * @param arg Map: cred: VoipAccount; optional, if empty fetches all notifications.
	def getNotifications(arg) {
		arg.voipAccount ? VoipNotify.findByVoipAccount(arg.voipAccount) : VoipNotify.list()
	}
	 */
	
}
