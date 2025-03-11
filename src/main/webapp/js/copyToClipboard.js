function copyToClipboard(elementId) {
    var element = document.getElementById(elementId);
    var text = element.textContent;

    navigator.clipboard.writeText(text)
        .then(() => {
            alert('Скопировано!'); // Optional: Show a success message
        })
        .catch(err => {
            console.error('Ошибка при копировании: ', err);
            alert('Не удалось скопировать в буфер обмена');
        });
}