package com.bensmann.voip

/*
import org.quartz.Job
import org.quartz.JobExecutionContext
*/

/**
 * 
 */
class SipgateEventListJob /*implements Job*/ {
	
	def name = "sipgateEventList"
	def group = "com.bensmann.voip"
	//def sessionRequired = true
	//def dataSource
	def concurrent = false
	//def startDelay = 30000 // Wait 30 second(s) before execution
	//def timeout = 5000 // Execute job once in 5 seconds
	
	// CronTrigger Tutorial: http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html
	static triggers = {
		//simple name: 'simpleTrigger'
		//cron name: 'cronTrigger', cronExpression: 'sec min hr dom mon dow y'
		cron name: 'sipgateEventListCronTrigger', startDelay: 30000, cronExpression: '0 0/15 * * * ?'
	}
	
	/**
	 * 
	 */
	def sipgateService
	
	/**
	 * Execute the job.
	void execute(JobExecutionContext jobContext) {
		try {
			VoipAccount.list().each {
				if (log.debugEnabled) log.debug "Fetching event list for account ${it} at ${new Date()}..."
				// Get event list
				sipgateService.getEventList(cred: it)
				// Get recommended interval
				sipgateService.getRecommendedInterval(cred: it)
			}
		} catch (e) {
			log.error e
		}
	}
	 */
	
}
