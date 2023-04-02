<#include "/intra/common/header.ftl">

<header>Resource Description Processor</header>

<section>
    <p>This is a <i>Resource Description (RD)</i> aka. Blue Print processor. Select the RD file to be processed.</p>

    <article>
        <h3>Resource Description (RD) Documentation:</h3>
        <form
            action="/intra/process/rds"
            method="post" enctype="multipart/form-data">
            <table border="0" cellspacing="5">
                <tr>
                    <td>Select a RD file: <input type="file" name="file" size="40">
                    </td>
                </tr>
                <tr>
                    <td><input type="submit" value="Generate RD Documentation">
                    </td>
                </tr>
            </table>
        </form>
    </article>
</section>

<section>
    <h2>Instructions for use</h2>
    <h3>Resource Description (RD) Documentation:</h3>
    <p>Step by step instructions:
    <ol type="a">
        <li>Select the local file from which the RD documentation will be generated from. One can type the
            full filepath to the given text field OR use the GUI for file selection by pressing the
            <i>Browse...</i> -button</li>
        <ul>
            <li>The file must have <b>xml</b> as type.</li>
            <li>The file must be valid RD description in XML format.</li>
        </ul>
        <li>Press the button <i>Generate RD Documentation</i> for generating the RD
            documentation in HTML format.</li>
        <li>The output can be saved as local file with the save function of your browser. (Usually from
            Browser's menu: File->Save as..). Rename the file and use the file extension <i>.html</i>.</li>
    </ol>
    </p>
</section>

<#include "/intra/common/footer.ftl">