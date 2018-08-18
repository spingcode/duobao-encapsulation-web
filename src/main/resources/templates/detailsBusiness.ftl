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
            <td>orgid :${msg.orgid}</td>
        </tr>
        <tr>
            <td>调用量 :${msg.callCount}</td>
        </tr>
        <tr>
            <td>成功调用量 :${msg.successCount}</td>
        </tr>
        <tr>
            <td>拉取量 :${msg.pullCount}</td>
        </tr>
        <tr>
            <td>芝麻分的数量 :${msg.zmfCount}</td>
        </tr>
        <tr>
            <td>行业关注的数量 :${msg.hygzCount}</td>
        </tr>
        <tr>
            <td>剩余金额 :${msg.remainAmount}</td>
        </tr>
        <tr>
            <td>总金额 :${msg.totalAmount}</td>
        </tr>
        <tr>
            <td>芝麻分单价 :${msg.zmfPrice}</td>
        </tr>
        <tr>
            <td>行业关注单价 :${msg.hygzPrice}</td>
        </tr>
        <tr>
            <td>是否有效 :
            <#if msg.vaild=0>
                有效
            <#elseif msg.vaild=1>
                无效
            </#if>
            </td>
        </tr>
        <tr>
            <td>returnUrl :${msg.returnUrl}</td>
        </tr>
        <tr>
            <td>创建时间 :${(msg.createTime?string("yyyy-MM-dd hh:mm:ss"))!}</td>
        </tr>
        <tr>
            <td>更新时间 :${(msg.dbUpdateTime?string("yyyy-MM-dd hh:mm:ss"))!}</td>
        </tr>
        <tr>
            <td><a href="/freemarker/getHistoryByOrgid?orgid=${msg.orgid}">该B端用户的历史纪录</a></td>
            <td><a href="/freemarker/edit?businessId=${msg.id}">编辑</a></td>
        </tr>

    </table>


</div>
</body>
</html>