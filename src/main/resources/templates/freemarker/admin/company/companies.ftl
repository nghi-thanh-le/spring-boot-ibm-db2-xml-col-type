<#include "/admin/common/header.ftl">

<h1>All Users</h1>

<ul>
    <#list companies as company>
        <li>
            <a href="/admin/companies/${company.id}">
                ${company.name}
            </a>
        </li>
    </#list>
</ul>

<#include "/admin/common/footer.ftl">