package com.bensmann.voip

/**
 * 
 */
class VoipTagLib {
	
	/**
	 * Our namespace.
	 */
	static namespace = "voip"
	
	/**
	 * Which tags return objects?
	 */
	static returnObjectForTags = []
	
	/**
	 * 
	 */
	def grailsApplication
	
	/**
	 * Create a link to send a SMS.
	 */
	def sendSms = { attr, body ->
		// Parameters
		def actionMap = [
			controller: "sipgate",
			action: "sendSms",
			params: [:]
		]
		if (attr.username) actionMap.params.username = attr.username
		if (attr.password) actionMap.params.password = attr.password
		if (attr.remoteUri) actionMap.params.remoteUri = attr.remoteUri
		if (attr.text) actionMap.params.text = attr.text
		// Output
		def w = attr.widget ?: "button"
		if (w == "link") {
			out << g."${attr.remote ? 'remoteLink' : 'link'}"(map) { body() }
		} else if (w == "button") {
			if (attr.update) {
				out << g.submitToRemote([class: attr.class, url: actionMap, update: attr.update, value: attr.value]) { body() }
			} else {
				out << "<input type='submit' ${attr.each { k, v -> "${k}='${v}'"}} />"
			}
		}
	}
	
	/**
	 * Create an input tag and button to send a document by fax.
	 */
	def sendFax = { attr ->
	}
	
	/**
	 * Create a link to call somebody.
	 */
	def initiateCall = { attr, body ->
		// Parameters
		def actionMap = [
			controller: "sipgate",
			action: "initiateCall",
			params: [:]
		]
		if (attr.username) actionMap.params.username = attr.username
		if (attr.password) actionMap.params.password = attr.password
		if (attr.remoteUri) actionMap.params.remoteUri = attr.remoteUri
		// Output
		def w = attr.widget ?: "button"
		if (w == "link") {
			out << g."${attr.remote ? 'remoteLink' : 'link'}"(actionMap) { body() }
		} else if (w == "button") {
			if (attr.update) {
				out << g.submitToRemote([class: attr.class, url: actionMap, update: attr.update, value: attr.value]) { body() }
			} else {
				out << "<input type='submit' ${attr.each { k, v -> "${k}='${v}'"}} />"
			}
		}
	}
	
	/**
	 * Hangup a call. If no session ID is given the last call opened by call() will be ended.
	 */
	def hangup = { attr, body ->
		// Parameters
		def actionMap = [
			controller: "sipgate",
			action: "hangupCall",
			params: [:]
		]
		if (attr.username) actionMap.params.username = attr.username
		if (attr.password) actionMap.params.password = attr.password
		if (attr.sessionId) actionMap.params.sessionId = attr.sessionId
		// Output
		def w = attr.widget ?: "button"
		if (w == "link") {
			out << g."${attr.remote ? 'remoteLink' : 'link'}"(map) { body() }
		} else if (w == "button") {
			if (attr.update) {
				out << g.submitToRemote([class: attr.class, url: actionMap, update: attr.update, value: attr.value]) { body() }
			} else {
				out << "<input type='submit' ${attr.each { k, v -> "${k}='${v}'"}} />"
			}
		}
	}
	
}
