function copyToClipboard(elementId) {
    var element = document.getElementById(elementId);
    var text = element.textContent;

    navigator.clipboard.writeText(text)
        .then(() => {
            element.text = 'Скопировано!';
        })
        .catch(err => {
            console.error('Ошибка при копировании: ', err);
            alert('Не удалось скопировать в буфер обмена');
        });
}