<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Resource Description Web Service - login</title>
    <!--[if lt IE 9]>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html5shiv/3.7.3/html5shiv.js"></script>
    <![endif]-->
</head>
<body>

<h1>Login to Resource Description Web Service</h1>
<p>This is the login page to enter the Resource Description Web Service.</p>

<form method="POST" name="loginform" action="/login">
    <table cellpadding="4" cellspacing="4"
           style="background-color:#CCCCEE;border:1px solid #3333CC;width:20em">
        <tr><td align="right">Username:</td>
            <td><input type="text" name="username" value="" tabindex="1"></td></tr>
        <tr><td align="right">Password:</td>
            <td><input type="password" name="password" value="" tabindex="2"></td></tr>
        <tr><td colspan="2" align="right"><input type="submit" name="login" value="Login" tabindex="0"></td></tr>
    </table>
</form>

<p>The additional information about the project and resources can be found from:
<ul>
    <!--
                    <li><a href="http://www.eupass-fp6.org">EUPASS public home page</a> (EU FP6)</li>
                    <li><a href="http://www.eas-env.org">Evolvable Assembly Systems (EAS) environment</a></li>
    -->
    <li>This web application is created and hosted by <a href="http://www.tut.fi/mei/">Tampere University of Technology / Department of Mechanical Engineering and Industrial Systems</a></li>
    <li><a href="http://www.recam-project.eu/">Home page of ReCaM project</a> (EU Hz2020)</li>
</ul>
</p>
<p><font size="2">Maintenance and support: Niko Siltala, niko.siltala(at)tut.fi</font></p>
</body>
</html>