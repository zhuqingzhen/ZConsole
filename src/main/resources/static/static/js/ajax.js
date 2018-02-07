jQuery.ajax({
	"async":true,
	"url":url,
	"dataType":"json"
	"beforeSend":function(){
		
	},
	"success":function(result){
		
	},
	"complete":function(){
		
	},
	"error":function(xhr,status,error){
		$('.body').toast({
			content:'系统异常',
			duration:1000
		});
	}
})