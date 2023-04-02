<table border="1" cellpadding="5">
    <tr>
        <th>Filename</th>
        <th>Size</th>
        <th>Modified</th>
        <#-- TODO: if bRDDocuemntation, add one more col here -->
        <#-- <th>Documentation </th> -->
    </tr>
    <#list files as file>
        <tr>
            <td>
                <a href="#">
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