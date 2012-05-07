package com.bensmann.voip

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH

/**
 * 
 */
class SipgateController {
	
	/**
	 * Glue.
	 */
	def useLiquidGlue = true
	
	/**
	 * 
	 */
	def sipgateService
	
	/**
	 * The default action.
	 */
	def defaultAction = "test"
	
	/**
	 * The before interceptor.
	 * See http://www.grails.org/Controllers+-+Interceptors
	 */
	def beforeInterceptor = {
		init()
		responseNoCache()
	}
	
	/**
	 * Initialize.
	 */
	def init = {
		// Set map for VoIP variables
		if (!session.voip) {
			session.voip = [:]
		}
	}
	
	/**
	 * No-cache response. Taken from Glue.
	 */
	def responseNoCache() {
		response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate,max-age=0")
	}
	
	/**
	 * Check parameter. Taken from Glue.
	 */
	def checkParameter(arg) {
		arg.p.each {
			if (!arg.params."${it}") {
				throw new IllegalArgumentException("Parameter ${it} missing")
			}
		}
	}
	
	/**
	 * Action when an API call failed.
	 */
	def private sipgateCallFailed(params = null, sgr = null, exception = null) {
		if (/*log.debugEnabled &&*/ exception) exception.printStackTrace()
		log.error "sipgateCallFailed: params=${params}, sgr=${sgr}, exception=${exception}"
	}
	
	/**
	 * Get credentials from request parameters and/or configuration.
	 */
	def private getCred(params) {
		def username = params.username
		def password = params.remove("password")
		def localUri = params.localUri
		def users = grailsApplication.config.voip?.users
		def cred = [
			username: users."${username}"?.username ?: username,
			password: password ?: users."${username}"?.password,
			localUri: localUri ?: users."${username}"?.localUri
		]
		cred
	}
	
	/**
	 * Send a SMS.
	 */
	def sendSms = {
		// The sipgate result
		def sgr
		try {
			// Get parameter
			checkParameter params: params, p: ["username", "remoteUri", "text"]
			// Get credentials
			def cred = getCred(params)
			// Send the SMS
			sgr = sipgateService.sendSms(cred: cred, remoteUri: params.list("remoteUri"), text: params.text)
			if (sgr.statusCode == 200) {
				// Save session ID of SMS in session
				session.voip.sessionIdOfLastSMS = sgr.sessionId
				if (log.debugEnabled) log.debug "Sent SMS to ${remoteUri} for ${cred.username}, session ID=${sgr.sessionId}"
				render "SMS SENT"
			} else {
				throw new IllegalStateException("Could not send SMS")
			}
		} catch (e) {
			sipgateCallFailed params, sgr, e
			render "SEND SMS FAILED"
		}
	}
	
	/**
	 * Send a fax.
	 */
	def sendFax = {
		// The sipgate result
		def sgr
		try {
			// Get parameter
			checkParameter params: params, p: ["username", "remoteUri", "file"]
			def remoteUri = params.list("remoteUri")
			// Check file
			def file = new File(params.file)
			if (!file.exists() || !file.canRead()) {
				throw new IllegalStateException("File ${file.getAbsolutePath()} not found")
			} else {
				// Get credentials
				def cred = getCred(params)
				// Send the fax
				sgr = sipgateService.sendFax(cred: cred, remoteUri: remoteUri, bytes: file.readBytes())
				if (sgr.statusCode == 200) {
					// Save session ID of call in session
					session.voip.sessionIdOfLastFax = sgr.sessionId
					if (log.debugEnabled) log.debug "Sent fax to ${remoteUri} for ${cred.username}, session ID=${sgr.sessionId}"
					render "FAX SENT"
				} else {
					throw new IllegalStateException("Could not send fax")
				}
			}
		} catch (e) {
			sipgateCallFailed params, sgr, e
			render "SEND FAX FAILED"
		}
	}
	
	/**
	 * Initiate a call.
	 */
	def initiateCall = {
		// The sipgate result
		def sgr
		try {
			// Get parameter
			checkParameter params: params, p: ["username", "remoteUri"]
			// Get credentials
			def cred = getCred(params)
			// Initiate the call
			sgr = sipgateService.initiateCall(cred: cred, remoteUri: params.remoteUri)
			if (sgr.statusCode == 200) {
				// Save session ID of call in session
				session.voip.sessionIdOfLastCall = sgr.sessionId
				if (log.debugEnabled) log.debug "Initiated call to ${params.remoteUri} for ${cred.username}, session ID=${sgr.sessionId}"
				render "CALL INITIATED"
			} else {
				throw new IllegalStateException("Could not initiate call")
			}
		} catch (e) {
			sipgateCallFailed params, sgr, e
			render "CALL FAILED"
		}
	}
	
	/**
	 * End a call.
	 */
	def hangupCall = {
		// The sipgate result
		def sgr
		try {
			// Get parameter
			checkParameter params: params, p: ["username"]
			// Get session ID from params or from session
			if (!params.sessionId) {
				params.sessionId = session.voip.sessionIdOfLastCall
			}
			// Get credentials
			def cred = getCred(params)
			// Hangup call
			sgr = sipgateService.hangupCall(cred: cred, sessionId: params.sessionId)
			if (sgr.statusCode == 200) {
				if (log.debugEnabled) log.debug "Hung up call for ${cred.username}, session ID ${params.sessionId}"
				render "CALL HUNG UP"
			} else {
				throw new IllegalStateException("Could not hang up call")
			}
		} catch (e) {
			sipgateCallFailed params, sgr, e
			render "HANGUP FAILED"
		}
	}
	
	/**
	 * Simple UI to test.
	 */
	def test = {
	}
	
}
