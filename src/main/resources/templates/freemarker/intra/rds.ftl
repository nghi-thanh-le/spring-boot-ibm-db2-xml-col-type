<#include "/intra/common/header.ftl">

<header>${pageTitle}</header>

<section>
    <table border="1" cellpadding="5">
        <tr>
            <th>Filename</th>
            <th>Size</th>
            <th>Modified</th>
            <th>Documentation</th>
        </tr>
        <#list files as file>
            <tr>
                <td>
                    <a href="/${namespace}/${file.name}">
                        ${file.name}
                    </a>
                </td>
                <td align="right">
                    ${file.size}
                </td>
                <td align="right">
                    ${file.formattedLastModified}
                </td>
                <td>
                    <a href="https://resourcedescription.rd.tuni.fi/resourcedesc/intra/RDDocumentFromURL.jsp?URL=http://resourcedescription.rd.tuni.fi/resourcedesc/rds/${file.name}">Doc</a>
                </td>
            </tr>
        </#list>
    </table>

</section>

<#include "/intra/common/footer.ftl">