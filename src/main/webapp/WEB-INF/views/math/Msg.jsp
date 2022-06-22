<%@ page language="java" contentType="text/html; charset=EUC-KR"
         pageEncoding="EUC-KR" %>
<%@ page import="kopo.poly.dto.UserInfoDTO" %>
<%@ page import="kopo.poly.util.CmmUtil" %>
<%
  //Controller로부터 전달받은 데이터
   String msg = CmmUtil.nvl((String) request.getAttribute("msg"));
    String url = CmmUtil.nvl((String) request.getAttribute("url"));

  //Controller로부터 전달받은 웹(회원정보 입력화면)으로부터 입력받은 데이터(회원아이디, 이름, 이메일, 주소 등)
   UserInfoDTO pDTO = (UserInfoDTO) request.getAttribute("pDTO");
    if (pDTO == null) {
    pDTO = new UserInfoDTO();
    }
%>

<!DOCTYPE html>
<html>
<head>
  <meta charset="EUC-KR">
  <title>Sign Up Result</title>
  <script type="text/javascript">
    alert("<%=msg%>");
    window.location= '<%=url%>';
  </script>
</head>
<body>
</body>
</html>