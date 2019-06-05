

<%--  footer_main.jsp    /WEB-INF/jsp-includes/footer_main.jsp

	  This is included in every page 
--%>


<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>
	
	<div class="footer-outer-container">
	
		<div class="footer-left-container">
			<span><img src="${ contextPath }/images/footer-logo.png" ></span>
		</div>
		<div class="footer-right-container">
			<div style="text-align:center;">
			For help email:<br>
			mriffle@uw.edu
			</div>
		</div>
		
		
		<div class="footer-center-outer-container">
		  <div class="footer-center-container" >
			<%--  'id' used by manage configuration to update this div with admin entered data --%>
			<div id="footer-center-container" >

			&copy; Copyright 2019<br>University of Washington

			</div>
		  </div>
		</div>
	
	</div>


  </div>
 
 </body>
</html>