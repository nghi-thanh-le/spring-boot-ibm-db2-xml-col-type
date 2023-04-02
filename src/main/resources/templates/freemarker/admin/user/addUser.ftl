<#import "/spring.ftl" as spring />

<#include "/admin/common/header.ftl">

<h1>Add New User</h1>
<p>You can Create new user account by filling in all the shown fields associated to action
    and pressing the button related to selected action.
</p>
<table cellpadding="4" cellspacing="4">
    <form id="addUser" name="addUser" method="post" action="/admin/users/add">
        <tr>
            <td align="right">Username:</td>
            <td>
                <@spring.formInput "newUser.username"/>
            </td>
        </tr>
        <tr>
            <td align="right">Firstname:</td>
            <td>
                <@spring.formInput "newUser.firstName"/>
            </td>
        </tr>
        <tr>
            <td align="right">Lastname:</td>
            <td>
                <@spring.formInput "newUser.lastName"/>
            </td>
        </tr>
        <tr>
            <td align="right">Email:</td>
            <td>
                <@spring.formInput "newUser.email"/>
            </td>
        </tr>
        <tr>
            <td align="right">Phone:</td>
            <td>
                <@spring.formInput "newUser.phone"/>
            </td>
        </tr>
        <tr>
            <td align="right">New password:</td>
            <td>
                <@spring.formInput "newUser.password"/>
            </td>
        </tr>
        <tr>
            <td align="right">Company:</td>
            <td>
                <select name="company" id="company">
                    <#list companies as company>
                        <option value="${company.id}">${company.name}</option>
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
                <input type="submit" name="button" value="Add User" tabindex="12">
            </td>
        </tr>
    </form>
</table>

<#include "/admin/common/footer.ftl">