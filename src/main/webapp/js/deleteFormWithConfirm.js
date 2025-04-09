function deleteFormWithConfirm(formId) {
    if (confirm("Вы уверены, что хотите удалить эту форму?")) {
        const id = window.location.pathname.split('/').pop();
        console.log(id)
        let xhr = new XMLHttpRequest();
        xhr.open("DELETE", "/admin/form/" + formId, true);
        xhr.onload = function() {
            if (xhr.status === 200) {
                alert("Форма успешно удалена.");
                window.location.href = "/admin/langstat";
            } else {
                alert("Ошибка при удалении формы: " + xhr.responseText);
            }
        };
        xhr.onerror = function() {
            alert("Ошибка сети при удалении формы.");
        };
        xhr.send();
    }
}