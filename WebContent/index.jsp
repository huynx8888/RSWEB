<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<html>
<head>
	<%@ include file="../common/meta.jsp" %>
	<%@ include file="../common/link.jsp" %>
	<%@ include file="../common/scri.jsp" %>
	<link href="../css/ListAndMap.css" rel="stylesheet" type="text/css" media="all,screen" />
	<title>Recommendation System</title>
	<%
		Map formDataMap = (Map)request.getAttribute("formDataMap");
		List<String> formImage = (ArrayList<String>)request.getAttribute("formImage");
		if (formImage == null) {
			formImage = new ArrayList<String>();
		}
		if (formDataMap==null) {
			formDataMap = new HashMap();
		}
	%>
	<script type="text/javascript" language="javascript">

		function refreshdData() {
			window.focus();
		}

		function executeAction(type) {
			if (type == 11) {
				frm.para1.value = "11";
				frm.para2.value = "12";
				//frm.submit();
			}
			frm.submit();

		}

	</script>

</head>
<body onload="refreshdData()">
    <form method="post" action="/rs/SearchTreeAction"  name="frm">
    <input type="hidden" name="para1" value="">
    <input type="hidden" name="para2" value="">
    <div id="contentbox">
	<table>
		<tr>
			<td>
				<div class="gaiyou">
					<table>
					<tr>
					<th class="TIL">
						Recommendation Home page
					</th>
					</tr>
					</table>
				</div>
			</td>
		</tr>
	</table>

	<br>
    <input type="radio" name="methodType" value="RankSVM" <%= formDataMap.get("radio1") %> >Rank SVM
    <br>
    <input type="radio" name="methodType" value="RankBoost" <%= formDataMap.get("radio2") %> >Rank Boost
	<br>
	<br>


	<div class="serch">
		<table class="frame">
			<tr>
				<td class="title">
					<%= formDataMap.get("titleName")!=null?formDataMap.get("titleName"):""%>
				</td>
			</tr>

			<%

				List<String> arrList = new ArrayList<String>();
				arrList = (ArrayList)request.getAttribute("arrList");
				if (arrList==null || arrList.size()==0) {
			%>

				<%
					} else {
				%>

				<%
					for (int i= 0; i<arrList.size(); i++ ) {
						String element = "";
						element = arrList.get(i);

						if (i == arrList.size() -1) {

				%>

				<tr>
					<td><%= element %>
					<% if (element.contains("(Y/N)")) { %>
						<input type="text" name="answer" size="10" maxlength="10"  value="<%= formDataMap.get("answer")!=null?formDataMap.get("answer"):""%>">
					<% } %>
					</td>
				</tr>

				<%
						} else {
				%>

				<tr>
					<td><%= element %><br></td>
				</tr>

				<%
						}

					}
				%>


				<%
					}
				%>

		</table>

		<!-- load image picture -->
		<table>

			<%
			String[] arryAttributes1;
			String[] arryAttributes2;
			String[] arryAttributes3;
			String[] arryAttributes4;
			String[] arryAttributes5;
			String[] arryAttributes6;
			String[] arryAttributes7;
			String[] arryAttributes8;
			String[] arryAttributes9;
			String[] arryAttributes10;

			if (formImage.size() > 0) {
				arryAttributes1 = formImage.get(0).split(",");
				arryAttributes2 = formImage.get(1).split(",");
				arryAttributes3 = formImage.get(2).split(",");
				arryAttributes4 = formImage.get(3).split(",");
				arryAttributes5 = formImage.get(4).split(",");
				arryAttributes6 = formImage.get(5).split(",");
				arryAttributes7 = formImage.get(6).split(",");
				arryAttributes8 = formImage.get(7).split(",");
				arryAttributes9 = formImage.get(8).split(",");
				arryAttributes10 = formImage.get(9).split(",");
			%>

				<tr>
				    <td><img src="../images/1.jpg" /><br><em>ID <%=arryAttributes1[0]%></em><br>
				    <% for (int k = 1; k<arryAttributes1.length; k++) {
				    %>
				    	<%=arryAttributes1[k] %><br>
				    <%
				       }
				    %>
				    <td>
				    <td><img src="../images/2.jpg" /><br><em>ID <%=arryAttributes2[0]%></em><br>
				    <% for (int k = 1; k<arryAttributes2.length; k++) {
				    %>
				    	<%=arryAttributes2[k] %><br>
				    <%
				       }
				    %>
				    <td>
				    <td><img src="../images/3.jpg" /><br><em>ID <%=arryAttributes3[0]%></em><br>
				    <% for (int k = 1; k<arryAttributes3.length; k++) {
				    %>
				    	<%=arryAttributes3[k] %><br>
				    <%
				       }
				    %>
				    <td>
			    </tr>
				<tr>
				    <td><img src="../images/4.jpg" /><br><em>ID <%=arryAttributes4[0]%></em><br>
				    <% for (int k = 1; k<arryAttributes4.length; k++) {
				    %>
				    	<%=arryAttributes4[k] %><br>
				    <%
				       }
				    %>
				    <td>
				    <td><img src="../images/5.jpg" /><br><em>ID <%=arryAttributes5[0]%></em><br>
				    <% for (int k = 1; k<arryAttributes5.length; k++) {
				    %>
				    	<%=arryAttributes5[k] %><br>
				    <%
				       }
				    %>
				    <td>
				    <td><img src="../images/6.jpg" /><br><em>ID <%=arryAttributes6[0]%></em><br>
				    <% for (int k = 1; k<arryAttributes6.length; k++) {
				    %>
				    	<%=arryAttributes6[k] %><br>
				    <%
				       }
				    %>
				    <td>
			    </tr>

				<tr>
				    <td><img src="../images/7.jpg" /><br><em>ID <%=arryAttributes7[0]%></em><br>
				    <% for (int k = 1; k<arryAttributes7.length; k++) {
				    %>
				    	<%=arryAttributes7[k] %><br>
				    <%
				       }
				    %>
				    <td>
				    <td><img src="../images/8.jpg" /><br><em>ID <%=arryAttributes8[0]%></em><br>
				    <% for (int k = 1; k<arryAttributes8.length; k++) {
				    %>
				    	<%=arryAttributes8[k] %><br>
				    <%
				       }
				    %>
				    <td>
				    <td><img src="../images/9.jpg" /><br><em>ID <%=arryAttributes9[0]%></em><br>
				    <% for (int k = 1; k<arryAttributes9.length; k++) {
				    %>
				    	<%=arryAttributes9[k] %><br>
				    <%
				       }
				    %>
				    <td>
			    </tr>
			    <tr>
				    <td><img src="../images/10.jpg" /><br><em>ID <%=arryAttributes10[0]%></em><br>
				    <% for (int k = 1; k<arryAttributes10.length; k++) {
				    %>
				    	<%=arryAttributes10[k] %><br>
				    <%
				       }
				    %>
				    <td>
			    </tr>

			<%}%>


		</table>

	</div>


	<div class="serch">
	<table class="frame">
		<tr>
			<td class="fr_timestamp" align="left">
					<input type="button" value="Continue"  onclick="executeAction(11);">
			</td>
		</tr>
	</table>

	</div>
	</div>
</form>

</body>
</html>
