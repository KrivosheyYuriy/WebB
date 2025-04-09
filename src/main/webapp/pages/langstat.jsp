<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Language Statistics</title>
    <link rel="stylesheet" href="../styles/langstat.css">
</head>
<body>
<h1>Language Statistics</h1>

<table>
    <thead>
    <tr>
        <th>Language</th>
        <th>Votes</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="statistic" items="${languageStatistics}">
        <tr>
            <td>${statistic[0]}</td>
            <td>${statistic[1]}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>