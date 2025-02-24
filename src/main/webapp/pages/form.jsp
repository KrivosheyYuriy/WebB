<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions" %>
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
        <input id="name" name="name" value="${name}" pattern="^([А-ЯЁ][а-яё]+[\-\s]?){3,}$"
               placeholder="Введите ваши ФИО" required type="text">
        <br>

        <label for="phone">Телефон:</label>
        <input id="phone" name="phone" value="${phone}" pattern="^(\+7|8)([0-9]{10})$" placeholder="88005553535"
               required type="tel">
        <br>

        <label for="email">e-mail:</label>
        <input id="email" name="email" value="${email}" placeholder="you@example.com" required
               type="email">
        <br>

        <label for="birthday">Дата рождения:</label>
        <input id="birthday" name="birthday" value="${birthday}" required type="date" min="1920-01-01" max="<%= java.time.LocalDate.now() %>">
        <br>
        <label>Пол:</label>
        <div class="gender-options">
            <label>
                <input checked name="gender" type="radio" value="Мужской" ${gender.equalsIgnoreCase('мужской') ? 'checked' : ''}>
                Мужской
            </label>

            <label>
                <input name="gender" type="radio" value="Женский" ${gender.equalsIgnoreCase('женский') ? 'checked' : ''}>
                Женский
            </label>
        </div>
        <br>
        <label for="language">Любимый язык программирования:</label>
        <select id="language" name="language" required multiple>
            <option value="1" ${fn:contains(language, ', 1, ') ? 'selected' : ''}>Pascal</option>
            <option value="2" ${fn:contains(language, ', 2, ') ? 'selected' : ''}>C</option>
            <option value="3" ${fn:contains(language, ', 3, ') ? 'selected' : ''}>C++</option>
            <option value="4" ${fn:contains(language, ', 4, ') ? 'selected' : ''}>JavaScript</option>
            <option value="5" ${fn:contains(language, ', 5, ') ? 'selected' : ''}>PHP</option>
            <option value="6" ${fn:contains(language, ', 6, ') ? 'selected' : ''}>Python</option>
            <option value="7" ${fn:contains(language, ', 7, ') ? 'selected' : ''}>Java</option>
            <option value="8" ${fn:contains(language, ', 8, ') ? 'selected' : ''}>Haskel</option>
            <option value="9" ${fn:contains(language, ', 9, ') ? 'selected' : ''}>Clojure</option>
            <option value="10" ${fn:contains(language, ', 10, ') ? 'selected' : ''}>Prolog</option>
            <option value="11" ${fn:contains(language, ', 11, ') ? 'selected' : ''}>Scala</option>
        </select>
        <br>

        <label for="biography">Биография:</label>
        <textarea id="biography" name="biography" placeholder="Расскажите о себе">${biography}</textarea>
        <br>

        <label>
            <input name="agreement" type="checkbox" checked required ${agreement.equals('on') ? 'checked' : ''}>
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