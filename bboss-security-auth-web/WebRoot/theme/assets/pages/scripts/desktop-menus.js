var DesktopMenus = function() {

    var gotoworkspace = function(divid,url) {
    	// var reg=new RegExp("^#");     
    	//if(!reg.test(divid)) divid = '#'+divid;
    	//App.startPageLoading({message: '努力加载中...'});
    	App.blockUI({message: '努力加载中...'});
        $(".page-content").load(url,function(responseTxt,statusTxt,xhr){
        	//App.stopPageLoading();
        	//App.unblockUI();
        	window.setTimeout(function() {
               // App.stopPageLoading();
        		App.unblockUI();
            }, 10);
             
              if(statusTxt=="error" )
              {
            	  if(xhr.status == 404)
	    		  {
            		  $(".page-content").load('../../jsp/common/404.jsp');
	    		  }
            	  //alert("Error: "+xhr.status+": "+xhr.statusText);
              }
            	  
            });
        
    }
    
    var initTheme = function()
    {
    	Demo.init(); // init metronic core componets
    }

 
  

    return {
        //main function to initiate the module
    	gotoworkspace: function(divid,url) {

    		gotoworkspace(divid,url);

        },
    
        initTheme: function() {
	
        	initTheme();
	
	    },
	    changeFrameHeight:function(id){
	    	//alert($(".page-content").height());
	    	//alert($(".page-content").width());
	    	//alert($(".page-content").outerHeight());
	    	//alert($(".page-content").outerWidth());
	    //	var height = App.getViewPort().height - $('.page-header').outerHeight(true);
//$('#product-source').css('height', height);
	    	$("#"+id).height($(".page-content").height());
	    	// $("#"+id).height(height);
	    	 $("#"+id).width($(".page-content").width()+15);
	    	 
	    	//parent.height=id.document.body.scrollHeight+10;
	    	//id.document.body.scrollWidth = parent.width;
	    }

    };

}();

