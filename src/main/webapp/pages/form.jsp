<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Form</title>
    <link href="../styles/form.css" rel="stylesheet">
</head>
<body>
<div class="content">
    <form id="form" action="form" method="post">
        <h1>Пожалуйста, заполните форму</h1>

        <%-- Отображение сообщения об ошибке --%>
        <p style="color: red;">${errorMessage}</p>

        <label for="name">ФИО:</label>
        <input id="name" name="name" placeholder="Введите ваши ФИО" required type="text">
        <br>

        <label for="phone">Телефон:</label>
        <input id="phone" name="phone" pattern="^(\+7|8)([0-9]{10})$" placeholder="88005553535"
               required type="tel">
        <br>

        <label for="email">e-mail:</label>
        <input id="email" name="email" placeholder="you@example.com" required
               type="email">
        <br>

        <label for="birthday">Дата рождения:</label>
        <%-- Добавляем JavaScript для установки максимальной даты --%>
        <input id="birthday" name="birthday" required type="date" min="1920-01-01" max="<%= java.time.LocalDate.now() %>">
        <br>

        <label>Пол:</label>
        <div class="gender-options">
            <label>
                <input checked name="gender" type="radio" value="Мужской">
                Мужской
            </label>

            <label>
                <input name="gender" type="radio" value="Женский">
                Женский
            </label>
        </div>
        <br>

        <label for="language">Любимый язык программирования:</label>
        <select id="language" name="language" required multiple>
            <option value="">Любимый язык программирования</option>
            <option value="1">Pascal</option>
            <option value="2">C</option>
            <option value="3">C++</option>
            <option value="4">JavaScript</option>
            <option value="5">PHP</option>
            <option value="6">Python</option>
            <option value="7">Java</option>
            <option value="8">Haskel</option>
            <option value="9">Clojure</option>
            <option value="10">Prolog</option>
            <option value="11">Scala</option>
        </select>
        <br>

        <label for="biography">Биография:</label>
        <textarea id="biography" name="biography" placeholder="Расскажите о себе"></textarea>
        <br>

        <label>
            <input name="agreement" type="checkbox" required>
            С контрактом ознакомлен(а)
        </label>
        <br>

        <button type="submit">Сохранить</button>
    </form>
</div>

<footer>
    (C) Кривошей Юрий, 25.2
</footer>
</body>
</html>