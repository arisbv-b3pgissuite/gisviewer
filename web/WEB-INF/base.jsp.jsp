<%@ taglib uri="/WEB-INF/tiles.tld" prefix="tiles" %>

 
<tiles:insert page="/templates/template.jsp" flush="true">

  <tiles:put name="title"  value="My first page" />

  <tiles:put name="header" value="/tutorial/common/header.jsp" />

  <tiles:put name="footer" value="/tutorial/common/footer.jsp" />

  <tiles:put name="menu"   value="/tutorial/basic/menu.jsp" />

  <tiles:put name="body"   value="/tutorial/basic/helloBody.jsp" />

</tiles:insert>
