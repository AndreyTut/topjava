<%--
  Created by IntelliJ IDEA.
  User: Андрій
  Date: 16.01.2020
  Time: 11:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Meals</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<a href="index.html">home</a>
<table>
    <caption><h2>Еда</h2></caption>
    <tr>
        <th>Description</th>
        <th>Time</th>
        <th>Calories</th>
        <th></th>
        <th></th>
    </tr>
    <c:forEach var="meal" items="${meals}">
        <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.MealTo"/>
        <tr <c:if test="${meal.isExcess()}">class="exceed" </c:if> >
            <td>${meal.description}</td>
            <td>${meal.getDateTime().toLocalDate()} ${meal.getDateTime().toLocalTime()}</td>
            <td>${meal.getCalories()}</td>
            <td><a href="meals?action=edit&id=${meal.getId()}"><img src="img/pencil.png">edit</a></td>
            <td><a href="meals?action=delete&id=${meal.getId()}"><img src="img/delete.png"/>delete</a></td>
        </tr>
    </c:forEach>
</table>
<a href="meals?action=add">Add meal <img src="img/add.png"></a>
</body>
</html>
