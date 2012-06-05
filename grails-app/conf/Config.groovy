// GORM
grails.gorm.failOnError = true

log4j = {
	appenders {
		console name: 'stdout', layout: pattern(conversionPattern: '%d{dd.MM. HH:mm:ss,SSS} %t[%X{user}] %p %c %m%n')
	}
	//info stdout: 'grails.app'
	//warn stdout: 'grails.app'
	//error stdout: 'grails.app'
	//debug stdout: 'grails.app'
	//trace stdout: 'grails.app'
}
log4j.additivity.default = false

// voip
voip {
	users {
		'sipgate@bensmann.com' {
			password = ""
			localUri = "49123456789"
		}
	}
}

// The following properties have been added by the Upgrade process...
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
