<!doctype html>
<html class="no-js" lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Resource Description Web Service - Public</title>
    <link rel="stylesheet" href="/css/normalize.css">
    <link rel="stylesheet" href="/css/main.css">
    <link href="//cdn.quilljs.com/1.3.6/quill.snow.css" rel="stylesheet">
</head>
<body>
<div class="container" style="padding: 10px;">
    <header>Review a resource</header>

    <style>
        #editor-container {
            height: 375px;
        }
    </style>

    <h3>User List</h3>
    <select name="user" id="user">
        <#list users as user>
            <option value="${user.id}">${user.firstName} ${user.lastName}</option>
        </#list>
    </select>
    <br/>

    <h3>Resources</h3>
    <select name="resource" id="resource">
        <#list resources as resource>
            <option value="${resource.id}">${resource.fileName}</option>
        </#list>
    </select>

    <h3>Stages</h3>
    <select name="stage" id="stage">
        <#list stages as stage>
            <option value="${stage.id}">${stage.id}</option>
        </#list>
    </select>

    <div id="editor-container"></div>

    <button id="review-btn">Review</button>
</div>

<script src="//cdn.quilljs.com/1.3.6/quill.min.js"></script>
<script>
    const quill = new Quill('#editor-container', {
        modules: {
            toolbar: [
                [{ header: [1, 2, false] }],
                ['bold', 'italic', 'underline'],
                ['image', 'code-block']
            ]
        },
        placeholder: 'Compose an epic...',
        theme: 'snow'  // or 'bubble'
    });

    const userSelect = document.querySelector("select#user");
    const resourceSelect = document.querySelector("select#resource");
    const stageSelect = document.querySelector("select#stage");

    const reviewBtn = document.querySelector("button#review-btn");
    reviewBtn.addEventListener("click", evt => {
        const review= document.querySelector('.ql-editor').innerHTML;
        const selectedUserId = userSelect.value;
        const selectedResourceId = resourceSelect.value;
        const selectedStageId = stageSelect.value;

        fetch("/api/v1/rds/" + selectedResourceId + "/review", {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            method: 'POST',
            body: JSON.stringify({
                userId: selectedUserId,
                review: review,
                stage: selectedStageId
            })
        })
        .then(response => {

        })
        .catch(err => console.log(err));
    });
</script>
<script src="js/vendor/modernizr-3.11.2.min.js"></script>
<script src="js/plugins.js"></script>
<script src="js/main.js"></script>
</body>
</html>