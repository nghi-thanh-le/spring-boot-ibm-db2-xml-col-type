
<#include "/admin/common/header.ftl">

<h1>Add a company</h1>
<form id="addCompanyForm" name="addCompanyForm" method="post" action="/admin/companies/add">
    <table cellpadding="4" cellspacing="4">
        <tr>
            <td align="right">Name:</td>
            <td>
                <input type="text" name="name">
            </td>
        </tr>
        <tr>
            <td align="right">Url:</td>
            <td>
                <input type="text" name="url">
            </td>
        </tr>
        <tr>
            <td align="right">Email:</td>
            <td>
                <input type="text" name="email">
            </td>
        </tr>
        <tr>
            <td align="right">Phone:</td>
            <td>
                <input type="text" name="phone">
            </td>
        </tr>
        <tr>
            <td align="right">Address:</td>
            <td>
                <input type="text" name="address">
            </td>
        </tr>
        <tr>
            <td align="right">Postcode:</td>
            <td>
                <input type="text" name="postalCode">
            </td>
        </tr>
        <tr>
            <td align="right">Post office:</td>
            <td>
                <input type="text" name="postOffice">
            </td>
        </tr>
        <tr>
            <td align="right">Country:</td>
            <td>
                <input type="text" name="country">
            </td>
        </tr>
        <tr>
            <td align="right">Contact person:</td>
            <td>
                <input type="text" name="contactPerson">
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                <input type="submit" name="button" value="Add Company" tabindex="12">
            </td>
        </tr>
    </table>
</form>

<#include "/admin/common/footer.ftl">