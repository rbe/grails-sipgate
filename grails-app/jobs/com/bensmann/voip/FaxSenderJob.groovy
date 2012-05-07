package com.bensmann.voip

/*
import org.quartz.Job
import org.quartz.JobExecutionContext
*/

/**
 * Send a fax.
 */
class FaxSenderJob /*implements Job*/ {
	
	def name = "faxSender"
	def group = "com.bensmann.voip"
	def concurrent = false
	//def startDelay = 30000 // Wait 30 second(s) before execution
	//def timeout = 5000 // Execute job once in 5 seconds
	
	// CronTrigger Tutorial: http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html
	static triggers = {
		//simple name: 'simpleTrigger'
		//cron name: 'cronTrigger', cronExpression: 'sec min hr dom mon dow y'
		//cron name: 'faxSenderCronTrigger', startDelay: 30000, cronExpression: '0 5/15 * * * ?'
	}
	
	/**
	 * 
	 */
	def sipgateService
	
	/**
	 * Execute the job.
	void execute(JobExecutionContext jobContext) {
		// Get job
		def job = VoipFaxJob.get(jobContext.mergedJobDataMap.voipFaxJobId)
		// Send fax
		if (log.debugEnabled) log.debug "execute for account ${job.voipAccount} at ${new Date()}..."
		def sgr = sipgateService.sendFax(cred: job.voipAccount, remoteUri: [job.toUri] as String[], bytes: job.document)
		// TODO Check Sipgate EventList: log success/failure and reschedule job
		//sipgateService.getEventList(cred: job.voipAccount, eventIds: [sgr.sessionId])
		//VoipFaxJob.withTransaction {
		//	job.success = true
		//	job.save()
		//}
	}
	 */
	
}
