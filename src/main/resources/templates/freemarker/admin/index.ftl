<#include "/admin/common/header.ftl">

<h1>Admin Area</h1>
<h2>System Info</h2>
<ul>
    <li>System path : <% out.println(System.getProperty("user.dir"));%></li>

    <li>Session Root path : <% out.println(util.getAppRootPath());%></li>
    <li>Session URL path : <% out.println(util.getAppRootURL());%></li>


    <li>Java Home : <% out.println(System.getProperty("java.home"));%></li>

    <li>OS Name : <% out.println(System.getProperty("os.name"));%></li>
    <li>OS Version : <% out.println(System.getProperty("os.version"));%></li>
    <li>OS Arch : <% out.println(System.getProperty("os.arch"));%></li>

    <li>HTTP resuest / context path : <% out.println(request.getContextPath());%></li>
    <li>HTTP resuest / Path info : <% out.println(request.getPathInfo());%></li>
    <li>HTTP resuest / Local Addr : <% out.println(request.getLocalAddr());%></li>
    <li>HTTP resuest / Server Name : <% out.println(request.getServerName());%></li>
    <li>HTTP resuest / Request URI : <% out.println(request.getRequestURI());%></li>
    <li>HTTP resuest / Remote Addr : <% out.println(request.getRemoteAddr());%></li>
    <li>HTTP resuest / Request URL : <% out.println(request.getRequestURL());%></li>
    <li>HTTP resuest / Servlet Path : <% out.println(request.getServletPath());%></li>
    <li>HTTP resuest / Scheme : <% out.println(request.getScheme());%></li>
</ul>

<#include "/admin/common/footer.ftl">