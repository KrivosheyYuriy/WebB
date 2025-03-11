<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Главная страница</title>
    <link rel="stylesheet" href="./styles/index.css">
    <link rel="stylesheet" href="./styles/footer.css">
</head>
<body>

<div class="navbar">
    <a href="${pageContext.request.contextPath}/">Home</a>
    <a href="pages/form">Form</a>
    <!-- Add more links here -->
</div>

<div class="content">
    <h1><%= "Hello World!" %></h1>
    <p>Welcome to my JSP application!</p>
    <!-- More content can go here -->
</div>
<footer>
    (C) Кривошей Юрий, 25.2
</footer>
</body>
</html>