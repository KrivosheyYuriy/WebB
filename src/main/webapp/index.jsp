<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Главная страница</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/footer.css">
</head>
<body>

<div class="navbar">
    <a href="${pageContext.request.contextPath}/">Home</a>

    <%
        // Получаем имя пользователя из сессии
        String username = (String) session.getAttribute("user");

        if (username != null) {
    %>
    <a href="logout">Log out</a>
    <%
    } else {
    %>
    <a href="login">Log in</a>
    <%
        }
    %>

    <a href="form">Form</a>
    <!-- Add more links here -->
</div>

<div class="content">
    <h1><%= "Hello World!" %></h1>
    <p>Welcome to my JSP application!</p>
    <!-- More content can go here -->

    <%
        if (username != null) {
    %>
    <p>Welcome, <%= username %>!</p>
    <%
    } else {
    %>
    <p>Please log in to access more features.</p>
    <%
        }
    %>

</div>

<footer>
    (C) Кривошей Юрий, 25.2
</footer>
</body>
</html>