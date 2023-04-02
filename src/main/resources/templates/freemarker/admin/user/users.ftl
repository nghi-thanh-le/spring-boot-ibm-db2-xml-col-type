<#include "/admin/common/header.ftl">

<h1>All Users</h1>

<ul>
    <#list users as user>
        <li>
            <a href="/admin/users/${user.id}">
                ${(user.firstName)!""} ${(user.lastName)!""}
            </a>
        </li>
    </#list>
</ul>

<#include "/admin/common/footer.ftl">