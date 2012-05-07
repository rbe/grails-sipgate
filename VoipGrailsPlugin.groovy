import grails.util.GrailsUtil

/**
 * 
 */
class VoipGrailsPlugin {
	
	/**
	 * Author and plugin.
	 */
	def author = "Ralf Bensmann"
	def authorEmail = "grails@bensmann.com"
	def title = "Voice-over-IP plugin for Grails."
	def description = """Use VoIP provider sipgate within Grails."""
	
	/**
	 * URL to the plugin's documentation.
	 */
	def documentation = "http://grails.org/voip+Plugin"
	
	/**
	 * The plugin version.
	 */
	def version = "1.3.0"
	
	/**
	 * The version or versions of Grails the plugin is designed for.
	 */
	def grailsVersion = "1.2.0> *"
	
	/**
	 * Other plugins this plugin depends on.
	 */
	def dependsOn = [
		controllers: GrailsUtil.grailsVersion,
		core: GrailsUtil.grailsVersion,
		hibernate: GrailsUtil.grailsVersion,
		// TODO can't release plugin in 1.3.1 with dependency, see excludes below: quartz: "0.4.2 > *"
	]
	
	/**
	 * 
	 */
	def loadAfter = [
		"controllers",
		"hibernate"
	]
	
	/**
	 * Other plugins influenced by this plugin.
	 * See http://www.grails.org/Auto+Reloading+Plugins
	 */
	def influences = []
	
	/**
	 * Plugins to observe for changes.
	 * See http://www.grails.org/Auto+Reloading+Plugins
	 */
	def observe = []
	
	/**
	 * Resources to watch.
	 * See http://www.grails.org/Auto+Reloading+Plugins
	 */
	def watchedResources = []
	
	/**
	 * Resources that are excluded from plugin packaging.
	 */
	def pluginExcludes = [
		"grails-app/views/",
		"grails-app/jobs/" // TODO
	]
	
	/**
	 * Implement runtime spring config (optional).
	 * See http://www.grails.org/Runtime+Configuration+Plugins
	 */
	def doWithSpring = {
		//ConstrainedProperty.registerNewConstraint(BestFrameworkConstraint.NAME, BestFrameworkConstraint.class);
	}
	
	/**
	 * Implement post initialization spring config (optional).
	 * See http://www.grails.org/Runtime+Configuration+Plugins
	 */
	def doWithApplicationContext = { applicationContext ->
	}
	
	/**
	 * Implement additions to web.xml (optional).
	 * See http://www.grails.org/Runtime+Configuration+Plugins
	 */
	def doWithWebDescriptor = { xml ->
	}
	
	/**
	 * Implement registering dynamic methods to classes (optional).
	 * See http://www.grails.org/Plugin+Dynamic+Methods
	 */
	def doWithDynamicMethods = { ctx ->
		/*
		// Controllers
		application.controllerClasses.each { c ->
			println "${this.class.getName()}: controller: ${c}"
		}
		// Domain Classes
		application.domainClasses.each { d ->
			println "${this.class.getName()}: domain class: ${d}"
		}
		*/
	}
	
	/**
	 * Implement code that is executed when any artefact that this plugin is
	 * watching is modified and reloaded. The event contains: event.source,
	 * event.application, event.manager, event.ctx, and event.plugin.
	 */
	def onChange = { event ->
		println "${this.class.name}: onChange"
		/*
		if (application.isArtefactOfType(ControllerArtefactHandler.TYPE, event.source)) {
			def c = application.getControllerClass(event.source?.name)
			methodToCall(c)
		}
		*/
	}
	
	/**
	 * Implement code that is executed when the project configuration changes.
	 * The event is the same as for 'onChange'.
	 */
	def onConfigChange = { event ->
		println "${this.class.name}: onConfigChange"
	}
	
}
