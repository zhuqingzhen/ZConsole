/*! 
Math.uuid.js (v1.4) 
http://www.broofa.com 
mailto:robert@broofa.com 
  
Copyright (c) 2010 Robert Kieffer 
Dual licensed under the MIT and GPL licenses. 
*/ 
/* 
 * Generate a random uuid. 
 * 
 * USAGE: Math.uuid(length, radix) 
 *   length - the desired number of characters 
 *   radix  - the number of allowable values for each character. 
 * 
 * EXAMPLES: 
 *   // No arguments  - returns RFC4122, version 4 ID 
 *   >>> Math.uuid() 
 *   "92329D39-6F5C-4520-ABFC-AAB64544E172" 
 * 
 *   // One argument - returns ID of the specified length 
 *   >>> Math.uuid(15)     // 15 character ID (default base=62) 
 *   "VcydxgltxrVZSTV" 
 * 
 *   // Two arguments - returns ID of the specified length, and radix. (Radix must be <= 62) 
 *   >>> Math.uuid(8, 2)  // 8 character ID (base=2) 
 *   "01001010" 
 *   >>> Math.uuid(8, 10) // 8 character ID (base=10) 
 *   "47473046" 
 *   >>> Math.uuid(8, 16) // 8 character ID (base=16) 
 *   "098F4D35" 
 */  
(function() {  
  // Private array of chars to use  
  var CHARS = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');  
   
  Math.uuid = function (len, radix) {  
    var chars = CHARS, uuid = [], i;  
    radix = radix || chars.length;  
   
    if (len) {  
      // Compact form  
      for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];  
    } else {  
      // rfc4122, version 4 form  
      var r;  
   
      // rfc4122 requires these characters  
      uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';  
      uuid[14] = '4';  
   
      // Fill in random data.  At i==19 set the high bits of clock sequence as  
      // per rfc4122, sec. 4.1.5  
      for (i = 0; i < 36; i++) {  
        if (!uuid[i]) {  
          r = 0 | Math.random()*16;  
          uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];  
        }  
      }  
    }  
   
    return uuid.join('');  
  };  
   
  // A more performant, but slightly bulkier, RFC4122v4 solution.  We boost performance  
  // by minimizing calls to random()  
  Math.uuidFast = function() {  
    var chars = CHARS, uuid = new Array(36), rnd=0, r;  
    for (var i = 0; i < 36; i++) {  
      if (i==8 || i==13 ||  i==18 || i==23) {  
        uuid[i] = '-';  
      } else if (i==14) {  
        uuid[i] = '4';  
      } else {  
        if (rnd <= 0x02) rnd = 0x2000000 + (Math.random()*0x1000000)|0;  
        r = rnd & 0xf;  
        rnd = rnd >> 4;  
        uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];  
      }  
    }  
    return uuid.join('');  
  };  
   
  // A more compact, but less performant, RFC4122v4 solution:  
  Math.uuidCompact = function() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {  
      var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);  
      return v.toString(16);  
    });  
  };  
})();


/**
 * 获取url地址中的参数
 * @param name
 * @returns
 */
function gerUrlParam(name){
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     return  r!=null ?unescape(r[2]):null;
}


/**
使用方法，jsonFormat(json)这样为格式化代码。
jsonFormat(json,true)为开启压缩模式。
 */
function jsonFormat(txt,compress/*是否为压缩模式*/){/* 格式化JSON源码(对象转换为JSON文本) */  
    var indentChar = '    ';   
    if(/^\s*$/.test(txt)){   
        alert('数据为空,无法格式化! ');   
        return;   
    }   
    try{var data=eval('('+txt+')');}   
    catch(e){   
        alert('数据源语法错误,格式化失败! 错误信息: '+e.description,'err');   
        return;   
    };   
    var draw=[],last=false,This=this,line=compress?'':'\n',nodeCount=0,maxDepth=0;   
       
    var notify=function(name,value,isLast,indent/*缩进*/,formObj){   
        nodeCount++;/*节点计数*/  
        for (var i=0,tab='';i<indent;i++ )tab+=indentChar;/* 缩进HTML */  
        tab=compress?'':tab;/*压缩模式忽略缩进*/  
        maxDepth=++indent;/*缩进递增并记录*/  
        if(value&&value.constructor==Array){/*处理数组*/  
            draw.push(tab+(formObj?('"'+name+'":'):'')+'['+line);/*缩进'[' 然后换行*/  
            for (var i=0;i<value.length;i++)   
                notify(i,value[i],i==value.length-1,indent,false);   
            draw.push(tab+']'+(isLast?line:(','+line)));/*缩进']'换行,若非尾元素则添加逗号*/  
        }else   if(value&&typeof value=='object'){/*处理对象*/  
                draw.push(tab+(formObj?('"'+name+'":'):'')+'{'+line);/*缩进'{' 然后换行*/  
                var len=0,i=0;   
                for(var key in value)len++;   
                for(var key in value)notify(key,value[key],++i==len,indent,true);   
                draw.push(tab+'}'+(isLast?line:(','+line)));/*缩进'}'换行,若非尾元素则添加逗号*/  
            }else{   
                    if(typeof value=='string')value='"'+value+'"';   
                    draw.push(tab+(formObj?('"'+name+'":'):'')+value+(isLast?'':',')+line);   
            };   
    };   
    var isLast=true,indent=0;   
    notify('',data,isLast,indent,false);   
    return draw.join('');   
}

/**
使用方法，jsonFormat(json)这样为格式化代码。
jsonFormat(json,true)为开启压缩模式。
 */
function jsonFormatToHtml(txt,compress/*是否为压缩模式*/){/* 格式化JSON源码(对象转换为JSON文本) */  
    var indentChar = '&nbsp;&nbsp;&nbsp;&nbsp;';   
    if(/^\s*$/.test(txt)){   
        alert('数据为空,无法格式化! ');   
        return;   
    }   
    try{var data=eval('('+txt+')');}   
    catch(e){   
        alert('数据源语法错误,格式化失败! 错误信息: '+e.description,'err');   
        return;   
    };   
    var draw=[],last=false,This=this,line=compress?'':'<br>',nodeCount=0,maxDepth=0;   
       
    var notify=function(name,value,isLast,indent/*缩进*/,formObj){   
        nodeCount++;/*节点计数*/  
        for (var i=0,tab='';i<indent;i++ )tab+=indentChar;/* 缩进HTML */  
        tab=compress?'':tab;/*压缩模式忽略缩进*/  
        maxDepth=++indent;/*缩进递增并记录*/  
        if(value&&value.constructor==Array){/*处理数组*/  
            draw.push(tab+(formObj?('"'+name+'":'):'')+'['+line);/*缩进'[' 然后换行*/  
            for (var i=0;i<value.length;i++)   
                notify(i,value[i],i==value.length-1,indent,false);   
            draw.push(tab+']'+(isLast?line:(','+line)));/*缩进']'换行,若非尾元素则添加逗号*/  
        }else   if(value&&typeof value=='object'){/*处理对象*/  
                draw.push(tab+(formObj?('"'+name+'":'):'')+'{'+line);/*缩进'{' 然后换行*/  
                var len=0,i=0;   
                for(var key in value)len++;   
                for(var key in value)notify(key,value[key],++i==len,indent,true);   
                draw.push(tab+'}'+(isLast?line:(','+line)));/*缩进'}'换行,若非尾元素则添加逗号*/  
            }else{   
                    if(typeof value=='string')value='"'+value+'"';   
                    draw.push(tab+(formObj?('"'+name+'":'):'')+value+(isLast?'':',')+line);   
            };   
    };   
    var isLast=true,indent=0;   
    notify('',data,isLast,indent,false);   
    return draw.join('');   
}  

/**
 * new Date().format("yyyy-MM-dd hh:mm:ss")
 * new Date().format("yyyy-MM-dd hh:mm:ss:S")
 */
Date.prototype.format = function (format) {
    var args = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),  //quarter
        "S": this.getMilliseconds()
    };
    if (/(y+)/.test(format))
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var i in args) {
        var n = args[i];
        if (new RegExp("(" + i + ")").test(format))
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? n : ("00" + n).substr(("" + n).length));
    }
    return format;
};

/**字符串startWith**/
String.prototype.startWith=function(str){     
	var reg=new RegExp("^"+str);     
	return reg.test(this);        
}
/**字符串endWith**/
String.prototype.endWith=function(str){     
	var reg=new RegExp(str+"$");     
	return reg.test(this);        
}

/**
 * 毫秒格式化
 * @param mss
 * @returns
 */
function formatDuring(mss) {
    var days = parseInt(mss / (1000 * 60 * 60 * 24));
    var hours = parseInt((mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    var minutes = parseInt((mss % (1000 * 60 * 60)) / (1000 * 60));
    var seconds = (mss % (1000 * 60)) / 1000;
    return   (days> 0 ? days + " 天 ":"") + (hours>0?hours + " 小时 ":"") + (minutes>0? minutes + " 分钟 ":"") + (seconds>0?seconds + " 秒 ":"");
}

/**
 * @param bytes
 * @returns {String}
 */
function bytesToSize(bytes) {  
    if (bytes === 0) return '0 B';  
    	var k = 1024;  
    	sizes = ['B','KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];  
    	i = Math.floor(Math.log(bytes) / Math.log(k));  
    return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];   
    //toPrecision(3) 后面保留一位小数，如1.0GB  
}