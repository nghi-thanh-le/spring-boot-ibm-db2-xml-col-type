<#import "/spring.ftl" as spring />

<#include "/admin/common/header.ftl">

<h1>Modify Current Company</h1>
<form id="modifyCompany" method="post" action="/admin/companies/modify">
    <table cellpadding="4" cellspacing="4">
        <input type="hidden" name="id" value="${currentCompany.id}" />
        <tr>
            <td align="right">Name:</td>
            <td>
                <@spring.formInput "currentCompany.name"/>
            </td>
        </tr>
        <tr>
            <td align="right">Url:</td>
            <td>
                <@spring.formInput "currentCompany.url"/>
            </td>
        </tr>
        <tr>
            <td align="right">Email:</td>
            <td>
                <@spring.formInput "currentCompany.email"/>
            </td>
        </tr>
        <tr>
            <td align="right">Phone:</td>
            <td>
                <@spring.formInput "currentCompany.phone"/>
            </td>
        </tr>
        <tr>
            <td align="right">Address:</td>
            <td>
                <@spring.formInput "currentCompany.address"/>
            </td>
        </tr>
        <tr>
            <td align="right">Postcode:</td>
            <td>
                <@spring.formInput "currentCompany.postalCode"/>
            </td>
        </tr>
        <tr>
            <td align="right">Post office:</td>
            <td>
                <@spring.formInput "currentCompany.postOffice"/>
            </td>
        </tr>
        <tr>
            <td align="right">Country:</td>
            <td>
                <@spring.formInput "currentCompany.country"/>
            </td>
        </tr>
        <tr>
            <td align="right">Contact person:</td>
            <td>
                <@spring.formInput "currentCompany.contactPerson"/>
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                <input type="submit" name="button" value="Modify Company" tabindex="12">
                <a href="/admin/companies/delete/${currentCompany.id}">
                    Delete Company
                </a>
            </td>
        </tr>
    </table>
</form>
<div id="users-list">
    <ul>
        <#list users as user>
            <li>
                ${user.firstName} ${user.lastName} <a href="/admin/companies/${currentCompany.id}/delete/${user.id}">Unbind</a>
            </li>
        </#list>
    </ul>
</div>

<#include "/admin/common/footer.ftl">