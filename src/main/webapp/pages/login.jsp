<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Авторизация</title>
    <link rel="stylesheet" href="../styles/login.css">
</head>
<body>

<div class="login-container">
    <h2>Авторизация</h2>
    <p style="color: red;">${errorMessage}</p>
    <form action="login" method="post">
        <div class="form-group">
            <label for="username">Логин:</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div class="form-group">
            <label for="password">Пароль:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
        <button type="submit">Войти</button>
    </form>
</div>
</body>
</html>
