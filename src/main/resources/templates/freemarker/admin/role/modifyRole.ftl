<#include "/admin/common/header.ftl">

<h1>Modify Current Role</h1>
<table cellpadding="4" cellspacing="4">
    <tr>
        <td align="right">Name:</td>
        <td>
            <span>${currentRole.name}</span>
        </td>
    </tr>
    <tr>
        <td align="right">Description:</td>
        <td>
            <div>
                ${currentRole.description}
            </div>
        </td>
    </tr>
</table>
<div id="users-list">
    <p>List of users belonging to this role:</p>
    <ul>
        <#list currentRole.users as user>
            <li>
                <a href="/admin/users/${user.id}">
                    ${user.firstName} ${user.lastName}
                </a>
            </li>
        </#list>
    </ul>
</div>

<#include "/admin/common/footer.ftl">