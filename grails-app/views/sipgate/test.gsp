<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
	<head>
		<meta name="layout" content="irb">
	</head>
	<body>
		<p>
			<g:form>
				Username: <input type="text" name="username" />
				<br/>
				Password: <input type="password" name="password" /> (if not in Config.groovy)
				<br/>
				Local URI: <input type="text" name="localUri" /> (if not in Config.groovy)
				<br/>
				<voip:initiateCall name="initiateCall" widget="button" value="Call -&gt;" remote="true" update="result"/>
				<input name="remoteUri" type="text" />
				<voip:hangup name="hangup" widget="button" value="&lt;- Hangup" remote="true" update="result"/>
				<br/>
				<voip:sendSms name="sendSms" widget="button" value="Send SMS -&gt;" remote="true" update="result"/>
				<input name="text" type="text" />
			</g:form>
		</p>
		<p>
			<br/>
			<div id="result"></div>
		</p>
	</body>
</html>
