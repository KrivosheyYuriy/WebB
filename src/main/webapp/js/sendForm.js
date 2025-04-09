function sendForm() {
    // Получаем id формы из URL (или другим способом, например, из скрытого поля)
    const id = window.location.pathname.split('/').pop();

    fetch('/admin/form/' + id, { // Динамически формируем URL
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json' // Указываем тип контента - JSON
        },
        body: JSON.stringify({
            // Собираем данные формы в JSON
            field1: document.getElementById('field1').value,
            field2: document.getElementById('field2').value,
            // ... другие поля
        })
    })
        .then(response => {
            if (response.ok) {
                // Обработка успешного ответа (например, переход на другую страницу)
                alert('Форма успешно обновлена!');
                // window.location.href = '/success.jsp';  // Перенаправление
            } else {
                // Обработка ошибки
                console.error('Ошибка обновления формы:', response.status, response.statusText);
                alert('Ошибка обновления формы. Пожалуйста, попробуйте позже.');
            }
        })
        .catch(error => {
            // Обработка ошибок сети и т.д.
            console.error('Ошибка при отправке запроса:', error);
            alert('Произошла ошибка при отправке запроса.');
        });
}