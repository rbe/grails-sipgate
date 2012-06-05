// Grails Plugin distribution
grails.plugin.repos.discovery.irbdistrib = "https://rbe:Sowujax44@svn.bensmann.com/grails-plugin-distrib"
grails.plugin.repos.distribution.irbdistrib = "https://rbe:Sowujax44@svn.bensmann.com/grails-plugin-distrib"
//
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
	// inherit Grails' default dependencies
	inherits( "global" ) {
		// uncomment to disable ehcache
		// excludes 'ehcache'
	}
	log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'if (log.debugEnabled) log.trace' or 'verbose'
	repositories {
		grailsRepo "http://svn.bensmann.com/grails-plugin-distrib"
		credentials {
			realm = "vcs.bensmann.com"
			host = "svn.bensmann.com"
			username = "rbe"
			password = "Sowujax44"
		}
		grailsCentral()
		grailsPlugins()
		grailsHome()
		// uncomment the below to enable remote dependency resolution
		// from public Maven repositories
		//mavenLocal()
		//mavenCentral()
		//mavenRepo "http://snapshots.repository.codehaus.org"
		//mavenRepo "http://repository.codehaus.org"
		//mavenRepo "http://download.java.net/maven/2/"
		//mavenRepo "http://repository.jboss.com/maven2/"
	}
	dependencies {
		// specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		// runtime 'mysql:mysql-connector-java:5.1.5'
	}
}
