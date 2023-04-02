<#include "/common/header.ftl">
<header>Resource Description Concept</header>

<section>
    This section introduces the concept and the different descriptions this concept is composed of.

    <article>
        <h2>Introduction to Resource Description Concept</h2>
        <p>
            <i><b>Resource Description Concept</b></i> is descriptive grand total as representation of a technical entity that integrates
            all aspects together with relation to geometrical, mechanical and functional point of view. Resource Description Concept represents
            a comprehensive description of all aspects of a module functionality including all implications from product side, from
            process side and from interaction with other modules.
        </p>
        <p>
            The format of all files bases on XML and are standardised with XML Schemas. Information is stored into single location
            and used and viewed in many. As example the human readable documentation of a Abstract Resource Description (ARD) or
            Resource Description (RD) can be generated on fly upon the request. This ensures that the information remains always
            valid and e.g. documentation is not becoming obsolete.
        </p>
        <p align="center">
            <img src="#"
                 height="300" align="middle" alt="Files and objects associated with Resource Description Concept."><br/>
            Fig.1: Files and objects associated with Resource Description Concept.
        </p>
    </article>

    <article>
        <h2>Abstract Resource Description</h2>
        <p>
            The <i>Abstract Resource Description (ARD) file</i> is an electronic specification of a collection of similar kind of modules as a meta-module and the
            content of these files are controlled by the <i>User Group</i> promoting and developing the technology. The ARD offers
            the standardisation and harmonisation over the content of a module, but it always remains at higher abstraction and
            generalisation levels than a hardware module and its associated RD file. The ARD file presents all the available
            interfaces, specifies the processes present, and the names of the offered properties. In other words ARD
            specifies the standardised minimum set of requirements and abstractions, which are fulfilled and specified later in details
            by the RD file. The ARD is composed of one or more <i>Profiles</i>, which contain the specifications for the modules.
        </p>
        <h3>Profile</h3>
        <p>
            The <i>Profile</i> is a reusable building block for module specifications. The module description i.e. RD will be instantiation
            of one Profile and ARD.
        </p>
    </article>

    <article>
        <h2>Resource Description</h2>
        <p>
            The <i>Resource Description (RD)</i> file (aka Blue Print) is an electronic specification of a real implemented module i.e. virtual representation of the
            module and its features. The file is created and supplied by the vendor of the module and it is supposed to travel along
            the module throughout the module's lifetime. The very same RD file is shared by all the modules of the same type.
        </p>

    </article>

    <article>
        <h2>Resource Instance Description</h2>
        <p>
            The <i>Resource Instance Description (RID)</i> (aka History Container) is the file containing the module instance specific information. It is as well created by
            the module provider and shall travel together with the module having even tighter relationship with it than the RD. The RID
            will be updated during the use of the module at important events like change of owner of the module, after test and
            calibration runs, usage hours, service events, etc.
        </p>
    </article>
</section>

<section>
    <h2>References</h2>
    <ol>
        <li>
            Siltala, N.: Formal Digital Description of Production Equipment Modules for supporting
            System Design and Deployment. PhD (Tech.) Thesis. 12th July, 2016. p.211.
            <a href="http://urn.fi/URN:ISBN:978-952-15-3783-7">http://urn.fi/URN:ISBN:978-952-15-3783-7</a>
        </li>
        <li>
            Siltala, N., Tuokko, R.: A web tool supporting the management and use of electronic module descriptions for
            Evolvable Production Systems. In Proc. of IEEE International Symposium on Industrial Electronics (ISIE 2010).
            4-7 July 2010. Bari, Italy. pp. 2641-2646. ISBN: 978-1-4244-6391-6.
            DOI:<a href="http://dx.doi.org/10.1109/ISIE.2010.5637859">10.1109/ISIE.2010.5637859</a>.
            [<a href="http://ieeexplore.ieee.org/xpl/mostRecentIssue.jsp?punumber=5609073">Conference in IEEE</a>]
            [<a href="http://www.isie2010.it/">ISIE2010 Home page</a>]<br>
        </li>
        <li>
            Siltala, N., Hofmann, A. F., Tuokko, R. &amp; Bretthauer, G.; Emplacement and blue print - An
            approach to handle and describe modules for evolvable assembly systems
            In: <i>9th IFAC Symposium on Robot Control, SYROCO '09</i>,
            Nagaragawa Convention Center, September 9-12, 2009, Gifu, Japan; pp. 183-188.
            DOI:<a href="http://dx.doi.org/10.3182/20090909-4-JP-2010.00017">10.3182/20090909-4-JP-2010.00017</a>.
            <br>
            [<a href="http://www.ifac-papersonline.net/">Paper online from IFAC</a>]
        </li>
        <li>
            Siltala, N., Tuokko, R.: Use of Electronic Module Descriptions for Modular and Reconfigurable Assembly Systems.
            In Proc. of IEEE International Symposium on Assembly and Manufacturing (IEEE ISAM 2009).
            Suwon, Korea. 17-20 November 2009. pp. 214-219.
            DOI:<a href="http://dx.doi.org/10.1109/ISAM.2009.5376903">10.1109/ISAM.2009.5376903</a>.
        </li>
        <li>
            Siltala, N., Tuokko, R.: Emplacement and Blue Print - Electronic Module Description supporting Evolvable
            Assembly Systems Design, Deployment and Execution. In Huang, G. Q., Mak, K. L. &amp; Maropoulos, P. G. (eds.).
            Proc. of 6th CIRP-sponsored International Conference on Digital Enterprise Technology (DET09).
            Hong Kong, 14-16 December 2009. Advances in Soft Computing vol. 66, pp. 773-788.
            DOI:<a href="http://dx.doi.org/10.1007/978-3-642-10430-5_60">10.1007/978-3-642-10430-5_60</a>.
        </li>
    </ol>
</section>

<#include "/common/footer.ftl">