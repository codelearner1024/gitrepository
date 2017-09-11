<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<ul>


	<!-- 所有的左侧菜单也可以引入动态菜单，如何获取到对应的左侧菜单： 
	根据登录用户的信息获取role获取module
	
	-->
            
            <c:set var="aaa" value=""/>
            <!-- 遍历当前登录用户的角色列表 -->
			<c:forEach items="${_CURRENT_USER.roles }" var="role">
			       <!-- 遍历每个角色下的模块 -->
			       <c:forEach items="${role.modules }" var="module">
			            <!-- 如果该模块没有输出过，则要进行输出，否则这个模块就不输出    
			            	主菜单 moduleName是由登陆后页面默认加载或者title.jsp中获取动态参数
			           		 传递给action再请求转发回页面，间接获取，（类似于数据回显的功能属性驱动和模型驱动都可以获取到）
			           		 判断ctype是判断是否是二级菜单（左则菜单）并且remark属性是父菜单的英文名
			           		 
			            -->
			            <c:if test="${(moduleName eq module.remark) and module.ctype==1  }">
			            		<!-- aaa集合中是否包含模块名，不包含就添加，并且加到aaa中 -->
				               <c:if test="${fn:contains(aaa,module.cpermission) eq false }">
				                  <c:set var="aaa" value="${aaa},${module.cpermission }"/>
			                      <li><a href="${ctx}/${module.curl}" onclick="linkHighlighted(this)" target="main" id="aa_1">${module.cpermission }</a></li>
			                 </c:if>  
			            </c:if>
			       </c:forEach>
			</c:forEach>
</ul>