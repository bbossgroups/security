var DesktopMenus = function() {

    var gotoworkspace = function(divid,url) {
    	// var reg=new RegExp("^#");     
    	//if(!reg.test(divid)) divid = '#'+divid;
        $(".page-content").load(url,function(){
        	
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
	
	    }

    };

}();

