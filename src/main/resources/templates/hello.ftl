<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>Document</title>
    <link href="/css/index.css" rel="stylesheet"/>
</head>
<body>
<div class="container">
    <table>
        <tr>
            <td width="100">orgid</td>
            <td>调用量</td>
            <td>成功调用量</td>
            <td>拉取量</td>
            <td>芝麻分的数量</td>
            <td>行业关注的数量</td>
            <td>剩余金额</td>
            <td>总金额</td>
            <td>芝麻分单价</td>
            <td>行业关注单价</td>
            <td width="100">是否有效</td>
            <td>操作</td>
        </tr>
    <#list msg as being>
        <tr>
            <td>${being.orgid}</td>
            <td>${being.callCount}</td>
            <td>${being.successCount}</td>
            <td>${being.pullCount}</td>
            <td>${being.zmfCount}</td>
            <td>${being.hygzCount}</td>
            <td>${being.remainAmount}</td>
            <td>${being.totalAmount}</td>
            <td>${being.zmfPrice}</td>
            <td>${being.hygzPrice}</td>
            <td>
                <#if being.vaild=0>
                    有效
                <#elseif being.vaild=1>
                    无效
                </#if>
            <td>
            <td>
                <a href="/freemarker/details?businessId=${being.id}">详情</a>
            <td>
        </tr>
    </#list>
    </table>
    <tr>
        <td><a href="/freemarker/newPage">添加新的orgid</a></td>
    </tr>


</div>
</body>
</html>