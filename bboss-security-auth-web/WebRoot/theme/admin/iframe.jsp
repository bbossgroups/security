<%@ page session="false" language="java"
	contentType="text/html; charset=utf-8"%>
<iframe allowfullscreen id="workspace" 
	frameborder="0"  
	src="${workspaceurl}" 
	scrolling="auto" 
	width="100%"  
	onLoad="javascript:DesktopMenus.changeFrameHeight('workspace');">
	</iframe>
   <script   type="text/javascript"> 
        jQuery(document).ready(function() {
        	 window.onresize=function(){  
         		DesktopMenus.changeFrameHeight("workspace");  
         	}
        });</script>             