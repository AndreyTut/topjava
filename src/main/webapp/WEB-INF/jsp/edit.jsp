<%--
  Created by IntelliJ IDEA.
  User: Андрій
  Date: 17.01.2020
  Time: 23:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <%--<jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>--%>
    <link rel="stylesheet" href="style.css">
        <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
    <title>${meal.id}</title>
</head>
<body>
<h1>${meal.id==-1?'Create new meal':'Edit meal'}</h1>
<h1>${meal.description}</h1>
<div class="mealform">
    <form method="post" action="meals">
        <input type="hidden" name="id" value="${meal.id}">
        <label for="desc">Описание еды:</label><input type="text" id="desc" name="description" value="${meal.description}"/><br>
        <label for="datetime">Время приема:</label><input type="datetime-local" id="datetime" name="time" value="${meal.dateTime}"/><br>
        <label for="calories">Калории:</label><input type="text" id="calories" name="calories" value="${meal.calories}"/><br>
        <span><input type="submit"/></span>
    </form>
</div>
</body>
</html>
