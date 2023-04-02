<#import "/spring.ftl" as spring />

<#include "/admin/common/header.ftl">

<h1>Modify Current User</h1>
<form id="modifyUser" name="modifyUser" method="post" action="/admin/users/modify">
    <table cellpadding="4" cellspacing="4">
        <input type="hidden" name="id" value="${currentUser.id}" />
            <tr>
                <td align="right">Username:</td>
                <td>
                    <input type="text" disabled name="userName" id="userName" value="${currentUser.username}">
                </td>
            </tr>
            <tr>
                <td align="right">Firstname:</td>
                <td>
                    <@spring.formInput "currentUser.firstName"/>
                </td>
            </tr>
            <tr>
                <td align="right">Lastname:</td>
                <td>
                    <@spring.formInput "currentUser.lastName"/>
                </td>
            </tr>
            <tr>
                <td align="right">Email:</td>
                <td>
                    <@spring.formInput "currentUser.email"/>
                </td>
            </tr>
            <tr>
                <td align="right">Phone:</td>
                <td>
                    <@spring.formInput "currentUser.phone"/>
                </td>
            </tr>
            <tr>
                <td align="right">Company:</td>
                <td>
                    <select name="company" id="company">
                        <option value="" ${(!currentUser.company??)?then('selected','')}></option>
                        <#list companies as company>
                            <option value="${company.id}" ${(currentUser.company?? && currentUser.company.id == company.id)?then('selected','')}>${company.name}</option>
                        </#list>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Roles:</td>
                <td>
                    <select name="roles" id="roles" multiple>
                        <#list roles as role>
                            <option value="${role.id}">${role.name}</option>
                        </#list>
                    </select>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <input type="submit" name="button" value="Modify User" tabindex="12">
                    <a href="/admin/users/delete/${currentUser.id}">
                        Delete User
                    </a>
                </td>
            </tr>
    </table>
</form>

<script>
    <#list currentUser.roles as currentUserRole>
    (() => {
        const theRole = document.querySelector('form#modifyUser select#roles option[value="${currentUserRole.id}"]');
        if (theRole) {
            theRole.setAttribute('selected', 'selected');
        }
    })();
    </#list>


</script>

<#include "/admin/common/footer.ftl">