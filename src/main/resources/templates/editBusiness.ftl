<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>Document</title>
    <link href="/css/index.css" rel="stylesheet"/>
</head>
<body>
<div class="container">

    <form action="/freemarker/saveBusiness" method="post">
        <table>
            <tr>
                <td>orgid :<input type="text" name="orgid" value="${msg.orgid}"></td>
            </tr>
            <tr>
                <td>调用量 :<input type="text" name="callCount" value="${msg.callCount}"></td>
            </tr>
            <tr>
                <td>成功调用量 :<input type="text" name="successCount" value="${msg.successCount}"></td>
            </tr>
            <tr>
                <td>拉取量 :<input type="text" name="pullCount" value="${msg.pullCount}"></td>
            </tr>
            <tr>
                <td>芝麻分的数量 :<input type="text" name="zmfCount" value="${msg.zmfCount}"></td>
            </tr>
            <tr>
                <td>行业关注的数量 :<input type="text" name="hygzCount" value="${msg.hygzCount}"></td>
            </tr>
            <tr>
                <td>剩余金额 :<input type="text" name="remainAmount" value="${msg.remainAmount}"></td>
            </tr>
            <tr>
                <td>总金额 :<input type="text" name="totalAmount" value="${msg.totalAmount}"></td>
            </tr>
            <tr>
                <td>芝麻分单价 :<input type="text" name="zmfPrice" value="${msg.zmfPrice}"></td>
            </tr>
            <tr>
                <td>行业关注单价 :<input type="text" name="hygzPrice" value="${msg.hygzPrice}"></td>
            </tr>
            <tr>
                <td>是否有效(0 表示有效，1表示无效) :<input type="text" name="vaild" value="${msg.vaild}"></td>
            </tr>
            <tr>
                <td>returnUrl :<input type="text" name="returnUrl" value="${msg.returnUrl}"></td>
            </tr>
            <tr>
                <td>创建时间 :${(msg.createTime?string("yyyy-MM-dd hh:mm:ss"))!}</td>
            </tr>
            <tr>
                <td>更新时间 :${(msg.dbUpdateTime?string("yyyy-MM-dd hh:mm:ss"))!}</td>
            </tr>
        </table>
        <input type="hidden" name="id" value="${msg.id}">
        <input type="submit" value="保存">
    </form>



</div>
</body>
</html>