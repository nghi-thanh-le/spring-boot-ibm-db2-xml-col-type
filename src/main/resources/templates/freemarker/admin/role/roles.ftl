<#include "/admin/common/header.ftl">

<h1>All Users</h1>

<ul>
    <#list roles as role>
        <li>
            <a href="/admin/roles/${role.id}">
                ${role.name}
            </a>
        </li>
    </#list>
</ul>

<#include "/admin/common/footer.ftl">