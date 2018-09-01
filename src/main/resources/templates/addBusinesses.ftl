<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>Document</title>
    <link href="/css/index.css" rel="stylesheet"/>
</head>
<body>
<div class="container">

    <form action="/freemarker/addNewBusiness" method="post">
        <table>
            <tr>
                <td>orgid :<input type="text" name="orgid" value=""></td>
            </tr>
            <tr>
                <td>调用量 :<input type="text" name="callCount" value="0"></td>
            </tr>
            <tr>
                <td>成功调用量 :<input type="text" name="successCount" value="0"></td>
            </tr>
            <tr>
                <td>拉取量 :<input type="text" name="pullCount" value="0"></td>
            </tr>
            <tr>
                <td>芝麻分的数量 :<input type="text" name="zmfCount" value="0"></td>
            </tr>
            <tr>
                <td>行业关注的数量 :<input type="text" name="hygzCount" value="0"></td>
            </tr>
            <tr>
                <td>剩余金额 :<input type="text" name="remainAmount" value="0"></td>
            </tr>
            <tr>
                <td>总金额 :<input type="text" name="totalAmount" value="0"></td>
            </tr>
            <tr>
                <td>芝麻分单价 :<input type="text" name="zmfPrice" value="0.5"></td>
            </tr>
            <tr>
                <td>行业关注单价 :<input type="text" name="hygzPrice" value="0.5"></td>
            </tr>
            <tr>
                <td>returnUrl :<input type="text" name="returnUrl" value="www.baidu.com"></td>
            </tr>
        </table>
        <input type="submit" value="添加">
    </form>

</div>
</body>
</html>