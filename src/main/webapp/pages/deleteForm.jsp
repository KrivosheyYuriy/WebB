<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>DELETE Request Form</title>
</head>
<body>

<form id="deleteForm" action="/admin" method="POST">
    <input type="hidden" name="_method" value="DELETE">  <!-- Скрытое поле для эмуляции DELETE -->
    <input name="item_id" value="123"> <!-- ID элемента для удаления -->
    <button type="submit">Удалить</button>
</form>

<script>
    document.getElementById('deleteForm').addEventListener('submit', function(event) {
        event.preventDefault(); // Предотвратить стандартную отправку формы

        const form = event.target;
        const action = form.action;
        const itemId = form.item_id.value; // Получаем ID элемента

        fetch(action + '/' + itemId, { // или fetch(action, ...) если передается ID в теле запроса
            method: 'DELETE',
        })
            .then(response => {
                if (response.ok) {
                    // Обработка успешного удаления
                    console.log('Удаление прошло успешно!');
                    // Можно, например, перенаправить пользователя на другую страницу
                    window.location.href = '/';
                } else {
                    // Обработка ошибки
                    console.error('Произошла ошибка при удалении:', response.status);
                    alert('Произошла ошибка при удалении');
                }
            })
            .catch(error => {
                // Обработка сетевой ошибки
                console.error('Ошибка сети:', error);
                alert('Произошла ошибка сети');
            });
    });
</script>

</body>
</html>