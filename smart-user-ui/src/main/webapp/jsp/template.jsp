<%-- 
    Document   : dashboard
    Created on : Aug 4, 2010, 11:19:15 AM
    Author     : uzzal
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>aponn for you</title>
        <link rel="Stylesheet" href="../css/style.css">
        <link rel="Stylesheet" href="../css/dashboardstyle.css">
        <link rel="Stylesheet" href="../css/organization-style.css">
    </head>
    <body>
        <div id="main" >
            <div id="header"><label>aponn.com</label></div>

            <div id="options">
                <form action="#" method="post">
<%--                <table>
                    <tr>
                        <td>
                             <input type="text" id="searchbox" name="search" size="75">
                        </td>

                        <td>
                             <input type="submit" id="btnSearch" value="search">
                             <input type="image" src="../images/butup.gif" id="btnSearch" value="Search" alt="[Submit]" name="submit">
                        </td>
                    </tr>
                </table>--%>
                </form>

            </div>

            <div style="clear: both">
            </div>

            <div id="content">
                    <%--<jsp:include page="superadminaccess.jsp"></jsp:include>--%>
                    <%--<jsp:include page="orgsadminaccess.jsp"></jsp:include>--%>
                    <%--<jsp:include page="enduseraccess.jsp"></jsp:include>--%>
                    <jsp:include page="organization-creation.jsp"></jsp:include>
            </div>

            <div id="right">Right</div>

            <div style="clear: both">
            </div>

            <div id="footer">Footer</div>

        </div>
        
    </body>
</html>