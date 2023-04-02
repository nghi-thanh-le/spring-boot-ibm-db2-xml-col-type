<#include "/intra/user/common/header.ftl">

<h2>${rd.fileName}</h2>

<h4>Supplement files</h4>
<table border="0" cellspacing="5">
    <form action="/intra/user/resources/${rd.id}"
          method="post" enctype="multipart/form-data">
        <tr>
            <td>
                <label for="file">Supplement files:</label>
                <input id="file" type="file" name="file" size="40">
            </td>
        </tr>
        <tr>
            <td>
                <input type="submit" value="Upload">
            </td>
        </tr>
    </form>
</table>

<table border="0" cellspacing="5">
    <thead>
    <tr>
        <th>Id</th>
        <th>Name</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <#list files as file>
        <tr>
            <td>${file.id}</td>
            <td>
                <p>${file.name}</p>
            </td>
            <td>
                <a href="/intra/user/resources/${rd.id}/file/${file.id}/delete">
                    Delete
                </a>
            </td>
        </tr>
    </#list>
    </tbody>
</table>

<pre lang="xml">
    ${rdXml}
</pre>


<#include "/intra/user/common/footer.ftl">