<#include "/intra/common/header.ftl">

<header>${pageTitle}</header>

<section>
    <p>The following ${fileType} are published on this service:</p>

    <table border="1" cellpadding="5">
        <tr>
            <th>Filename</th>
            <th>Size</th>
            <th>Modified</th>
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
            </tr>
        </#list>
    </table>

</section>

<#include "/intra/common/footer.ftl">