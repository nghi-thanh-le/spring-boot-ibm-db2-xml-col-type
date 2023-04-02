<#include "/intra/user/common/header.ftl">

<h2>Upload new Resource Description</h2>
<table border="0" cellspacing="5">
    <form action="/intra/user/resources"
          method="post" enctype="multipart/form-data">
        <tr>
            <td>
                <label for="file">The Resource Description File:</label>
                <input id="file" type="file" name="file" size="40">
            </td>
        </tr>
        <tr>
            <td>
                <label for="description">Description:</label>
                <textarea id="description" name="description"></textarea>
            </td>
        </tr>
        <tr>
            <td>
                <input type="submit" value="Upload new Resource Description">
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
    <#list resources as resource>
        <tr>
            <td>${resource.id}</td>
            <td>
                <a href="/intra/user/resources/${resource.id}">
                    ${resource.fileName}
                </a>
            </td>
            <td>
                <a href="/intra/user/resources/${resource.id}/delete">
                    Delete
                </a>
            </td>
        </tr>
    </#list>
    </tbody>
</table>

<br/>
<hr/>
<h2>Instructions for use</h2>
<h3>Resource Description Documentation:</h3>
<p>
    Step by step instructions:
<ol type="a">
    <li>Select the local file from which the Resource Description documentation will be generated from. One can type the
        full filepath to the given text field OR use the GUI for file selection by pressing the
        <i>Browse...</i> -button</li>
    <ul>
        <li>The file must have <b>xml</b> as type.</li>
        <li>The file must be valid Resource Description description in XML format.</li>
    </ul>
    <li>Press the button <i>Generate Resource Description Documentation</i> for generating the Resource Description
        documentation in HTML format.</li>
    <li>The output can be saved as local file with the save function of your browser. (Usually from
        Browser's menu: File->Save as..). Rename the file and use the file extension <i>.html</i>.</li>
</ol>
</p>

<p>
    <i><font size="2">Last updated : 2009-03-04</font></i>
</p>

<#include "/intra/user/common/footer.ftl">