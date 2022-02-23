
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<%--  Default title --%>

<%-- 
	<c:if test="${ empty pageTitle }" >

		<c:set var="pageTitle" value="metaGOmics - Quantitative Gene Ontology Analysis of Meta-omics data" ></c:set>
	
	</c:if>
--%>

<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
response.addHeader("Cache-control", "no-store"); // tell proxy not to cache
response.addHeader("Cache-control", "max-age=0"); // stale right away
%>

<%--
	HTML5 DOCTYPE
	
	The DOCTYPE is partially put in to make IE not go into quirks mode (the default when there is no DOCTYPE).

--%>

<!DOCTYPE html>

<html class="no-js"> <!--  Modernizr will change "no-js" to "js" if Javascript is enabled -->

<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>

	<title>metaGOmics - <c:out value="${ pageTitle }" ></c:out></title>

	<link rel="stylesheet" href="${ contextPath }/css/global.css" type="text/css" media="print, projection, screen" />

	<c:out value="${ headerAdditions }" escapeXml="false" ></c:out>

	<script type="text/javascript" src="${ contextPath }/js/fasta-file-upload-validator.js"></script>


</head>
 

<body class="metagomics-page-main">

<div class="metagomics-page-main-outermost-div ">  <%--  This div is closed in footer_main.jsp --%>


<div class="header-outer-container">

	<div class="header-logo-container">
		<img src="${ contextPath }/images/header-logo.png" />
	</div>
	
	<div class="header-right bold-black">
		<p>Version: 0.2.0</p>
		<p style="margin-top:100px;"><a style="color:#000000;" href="https://meta.yeastrc.org/metagomics/demo/">View Demo Results</a></p>
	</div>

</div>

<div class="overall-enclosing-block">
