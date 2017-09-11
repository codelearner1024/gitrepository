<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="../../baselist.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title></title>
	<link rel="stylesheet" href="${ctx }/components/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css" />

	<script type="text/javascript" src="${ctx }/components/zTree/js/jquery-1.4.4.min.js"></script>
	<script type="text/javascript" src="${ctx }/components/zTree/js/jquery.ztree.core-3.5.min.js"></script>
	<script type="text/javascript" src="${ctx }/components/zTree/js/jquery.ztree.excheck-3.5.min.js"></script>
	<script type="text/javascript">
		var zTreeObj;
		var setting = {
			check : {
				enable : true
			},
			data : {
				simpleData : {
					enable : true
				}
			}
		};
		
		//ajax请求的地址：   roleAction_jsonTreeNodes.action?id=${id}   页面加载完成后发送ajax请求到action
		 $(function(){
			$.ajax({
				type:"GET",
				url:"${ctx}/sysadmin/roleAction_jsonTreeNodes.action?id=${id}",
				dataType:"json",
				success:initzTree
			});
		});
		
		function initzTree(data){
			var t = $("#jkTree");  //zTree树在什么位置
			
			//第二个参数：说明树的样子
			//var dataNodes = eval("("+data+")");//json对象
			//第三个参数：树上的结点
			zTreeObj = $.fn.zTree.init(t, setting, data);//初始化树
			zTreeObj.expandAll(true);//展开所有树结点
		} 
		
		
		/* $(document).ready(function() {
			$.ajax( {
				url : "${ctx}/sysadmin/roleAction_jsonTreeNodes.action?id=${id}",
				type : "get",
				dataType : "json",
				success : initZtree
			});
		});
		
		//初始化ZTree树
		function initZtree(data) {
			//var zNodes = eval("(" + data + ")");		//动态js语句
			zTreeObj = $.fn.zTree.init($('#jkTree'), setting, zNodes);	//jkTree 树的id，支持多个树
			zTreeObj.expandAll(true);		//展开所有树节点
		} */
		
		
		//获取所有选择的节点
		function submitCheckedNodes() {
			var nodes = new Array();
			nodes = zTreeObj.getCheckedNodes(true);		//取得选中的结点
			var str = "";
			for (i = 0; i < nodes.length; i++) {
				if (str != "") {
					str += ",";
				}
				str += nodes[i].id;
			}
			$('#moduleIds').val(str);
		}
	</script>
</head>

<body>
<form name="icform" method="post">
	<input type="hidden" name="id" value="${id}"/>
	<!-- 拼接字符串发送回action中，为 -->
	<input type="hidden" id="moduleIds" name="moduleIds" value="" />
<div id="menubar">
<div id="middleMenubar">
<div id="innerMenubar">
  <div id="navMenubar">
<ul>
<li id="save"><a href="#" onclick="submitCheckedNodes();formSubmit('roleAction_module','_self');this.blur();">保存</a></li>
<li id="back"><a href="#" onclick="formSubmit('roleAction_list','_self');this.blur();">返回</a></li>
</ul>
  </div>
</div>
</div>
</div>
   
<div class="textbox" id="centerTextbox">
  <div class="textbox-header">
  <div class="textbox-inner-header">
  <div class="textbox-title">
    配置 [${name}] 角色的模块  
  </div> 
  </div>
  </div>
  
<div>
<%-- <div style="text-align:left">
	<c:forEach items="${moduleList}" var="o">
		<div style="padding:3px;">
		<!-- 回显信息：页面传递过来roleModuleStr中包含了用户的所有模块id数组格式的字符串，遍历的时候模块匹配上则打钩（checked）,不匹配则不打钩 -->
		<input type="checkbox" name="moduleIds" value="${o.id}" class="input"
			<c:if test="${fn:contains(roleModuleStr,o.id)}">checked</c:if>
		>
		${o.name}
		</div>
	</c:forEach>
</div> --%>
 
</div>
<div>  
	<ul id="jkTree" class="ztree"></ul>  
</div>
 
 
</form>
</body>
</html>

