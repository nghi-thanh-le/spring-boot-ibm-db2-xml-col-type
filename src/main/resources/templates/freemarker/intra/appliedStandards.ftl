<#include "/intra/common/header.ftl">

<header>Applied Standards in ARDs and RDs</header>

<section>
    <p>The following table lists all the standards, which are used by the <i>Abstract Resource Descriptions (ARD)</i>
        and/or <i>Resource Descriptions (RD)</i> available from this service. The <i>label</i> defines the ID that is used to
        reference this standard within the specifications.
    </p>
    <p>
        1) Used in N times in ARD/Profile specifications.<br/>
        2) Used in N times in RD specifications.
    </p>
    <p>
        The amount of standards listed in the database is ${totalStandards}.<br/>
    </p>
    <table border="1" width="100%">
        <tr>
            <th colspan="4">Standard</th>
            <th colspan="3">Standardisation Body</th>
            <th rowspan="2">In N ARD 1)</th>
            <th rowspan="2">In N RD 2)</th>
        </tr>
        <tr>
            <th>Label</th>
            <th>Code</th>
            <th>Name</th>
            <th>Description</th>
            <#-- Body related -->
            <th>Name</th>
            <th>Description</th>
            <th>Label</th>
        </tr>
        <#-- Content -->
        <#list standards as standard>
            <tr>
                <td>${standard.stdId}</td>
                <td>
                    <#--
                    <c:choose>
                        <c:when test="standard.stdUrl.lenght > 0"><a href='${standard.stdUrl}'>${standard.bodyName}</a></c:when>
                        <c:otherwise>${standard.bodyName}</c:otherwise>
                    </c:choose>
                    -->
                    <a href='${standard.stdUrl!"#"}'>${standard.stdCode!""}</a>
                </td>
                <td>${standard.stdName}</td>
                <td>${standard.stdDescription!""}</td>
                <td>
                    <a href='${standard.standardBody.bodyUrl!"#"}'>
                        ${standard.standardBody.bodyName!""}
                    </a>
                </td>
                <td>${standard.standardBody.bodyDescription!""}</td>
                <td>${standard.bodyId!""}</td>
                <td>
                    <a href="#">
                        ${standard.nbrEmpl!""}
                    </a>
                </td>
                <td>
                    <a href="#">
                        ${standard.nbrBP!""}
                    </a>
                </td>
            </tr>
        </#list>
    </table>

</section>

<#include "/intra/common/footer.ftl">