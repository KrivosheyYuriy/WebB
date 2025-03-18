<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Форма была отправлена</title>
    <link href="../styles/formSuccess1.css" rel="stylesheet">
    <link rel="stylesheet" href="../styles/footer.css">
    <script src="../js/copyToClipboard.js"></script>
</head>
<body>
<div class="container">
    <h2>Регистрация прошла успешно!</h2>
    <div class="info-item">
        <div class="info-label">Ваш логин:</div>
        <div class="info-value" id="loginValue">${generatedLogin}</div>
        <button class="copy-button" onclick="copyToClipboard('loginValue')">Копировать</button>
    </div>

    <div class="info-item">
        <div class="info-label">Ваш пароль:</div>
        <div class="info-value" id="passwordValue">${generatedPassword}</div>
        <button class="copy-button" onclick="copyToClipboard('passwordValue')">Копировать</button>
    </div>

    <p>Пожалуйста, сохраните ваши логин и пароль, эта страница видна только 1 раз.</p>
    <a href="/" class="main-page-button">Перейти на главную страницу</a>
</div>
</body>
</html>