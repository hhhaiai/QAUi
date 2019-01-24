/**
 * 主要逻辑
 */
var red="#ec6262";
var blue="#678cec";//#e6f3ff    #0084ff
var backgroudcolor="#f6f5ff";
var blank="&nbsp;";
//预加载
window.onload = function() {
	main.init();
}
var main = {
		init : function(){
			var self = this;
			showCatalog("Android","QAUiReport");
			self.bindEvent();
			//判断滚动位置
			$(window).bind('scroll', function () {
				if($(this).scrollTop()>20) {
					$(".scrollTop").show();
				}else{
					$(".scrollTop").hide();
				}
			});
			//回到顶部
			$(".scrollTop").on("click",function(){
				$(window).scrollTop(0);
			});
			//强制更新
			$(".refreshCataInfo").on("click",function(){
				//refreshCataInfo("QAUiReport");
			});
		},
		bindEvent : function(){
			var self = this;
			$(".AndroidButton").on("click",function(){
				console.log("click android button");
				$(this).find("img").attr('src',"./pic/android.png");
				$(this).siblings().find("img").attr("src","./pic/ios_disable.png");
				showCatalog("Android","QAUiReport");
			});
			$(".iOSButton").on("click",function(){
				console.log("click ios button");
				$(this).find("img").attr('src',"./pic/ios.png");
				$(this).siblings().find("img").attr("src","./pic/android_disable.png");
				showCatalog("iOS","QAUiReport");
				
			});
		}
		
};
/**
 * 显示报告
 * @param foldername
 * @param type
 * @returns
 */
function showCatalog(foldername,type){
	var html="<div style='color:#999999;width:100%;height:100%;text-align:center;position: absolute;'>加载中...</div>";
	$(".searchlistshow").html(html);
	getCataInfo(foldername,type);
}

/**
 * 展示报告列表
 * @param data
 * @param foldername
 * @returns
 */
function showListData(data,foldername){
	var html="";
	if(data!=null&&data!=""){
		html+="<div style='color:#999999;width:100%;text-align:center;'>共搜索到"+data.length+"份测试报告</p></div>";	
		html +="<hr style='color='"+backgroudcolor+"'><div><table class='searchTable'>";	
		html+="<th style='width:5%;'>序号</th><th style='width:20%;'>报告名称</th><th th style='width:15%;'>标题</th><th style='width:15%;'>测试结果</th><th style='width:15%;'>创建时间</th><th style='width:10%;'>场景列表</th><th style='width:20%;'></th>";	
		var no=0;
		$.each(data,function(i,item){
			no++;
			html+="<tr>";
			html+="<td style='width:5%;' name='"+item.folder+"' id='no'>"+no+"</td>";
			if(item.done=="false"){
				html+="<td style='width:20%;color:red;'>"+item.folder+"</td>";	
			}else{
				html+="<td style='width:20%;color:#678cec;'>"+item.folder+"</td>";
			}	
			html+="<td style='width:15%;' name='"+item.folder+"' id='title'>"+item.title+"</td>";
			html+="<td style='width:15%;'>"+item.summary+"</td>";
			html+="<td style='width:15%;'>"+item.createtime+"</td>";
			html+="<td style='width:10%;'>";
			html+="<a name='"+i+"' id='show_subscene_button' class='toggle toggle--on'></a>";
			html+="</td>";
			
			html+="<td style='width:20%;'>";
			//html+="<button  name='"+item.name+"_"+i+"' class='open_scene_button'>详情</button>";
			html+="<a  name='"+item.folder+"' id='open_scene_button' class='btn-slide'>";
			html+="<span class='circle'><i class='fa fa-rocket'></i></span>";
			html+="<span class='title'>报告详情</span>";
			html+="<span class='title-hover'>点击打开</span></a>";
			html+="</td>";
			
			html+="</tr>";
			html+="<tr id='subscene_"+i+"' style='display: none;'>";
			html+="<td style='text-align:left;' colspan='6'><hr><ul>";
			$.each(item.items_scene,function(j,subitem){
				if(subitem.success=="false"){
					html+="<li style='color:red;'>";	
				}else{
					html+="<li>";	
				}
				html+="<span>"+subitem.folder+"</span>"+blank+blank+blank+blank+blank;
				//html+="<button name='"+item.name+"_"+j+"' class='open_subscene_button'>详情</button><br>";
				html+="<span>"+subitem.summary+"</span>"+blank+blank+blank+blank+blank;
				html+="<span name='"+item.folder+"/"+subitem.folder+"' id='open_subscene_button' class='btn btn-small submit'>"+blank+"点击打开"+blank+"</span>";
				html+="</li>";
			});
			$.each(item.items_monkey_android_sys,function(j,subitem){
				if(subitem.success=="false"){
					html+="<li style='color:red;'>";	
				}else{
					html+="<li>";	
				}
				html+="<span>"+subitem.folder+"</span>"+blank+blank+blank+blank+blank;
				//html+="<button name='"+item.name+"_"+j+"' class='open_subscene_button'>详情</button><br>";
				html+="<span>"+subitem.summary+"</span>"+blank+blank+blank+blank+blank;
				html+="<span name='"+item.folder+"/"+subitem.folder+"' id='open_subscene_button' class='btn btn-small submit'>"+blank+"点击打开"+blank+"</span>";
				html+="</li>";
			});
			html+="<hr></ul></td>";
			html+="</tr>";
			
		});
		html+="</table></div>";
	}else{
		html +="<div style='color:#999999;width:100%;height:100%;text-align:center;position: absolute;'>无查询结果</div>";
	}
	$(".searchlistshow").html(html);
	//更改标题
	$(".searchlistshow #title").click(function() {
		var path=foldername+"/"+$(this).attr("name");
		console.log("change title "+path);
		var title = prompt("请输入新标题?","");
		if (title != null&&title!=""){
			changeTitle(path,"QAUiReport",title);
			setTimeout(function() {
				showCatalog(foldername,"QAUiReport");
			}, 1000);
		}
	});
	//删除场景
	$(".searchlistshow #no").click(function() {
		var path=foldername+"/"+$(this).attr("name");
		console.log("del cata "+path);
		var token = prompt("删除该报告?","");
		if ($(this).attr("name")!=''&&token != null&&token=='del'){
			delCata(path,"QAUiReport");
			setTimeout(function() {
				showCatalog(foldername,"QAUiReport");
			}, 1000);
		}
	});
	//展开子场景
	$(".searchlistshow #show_subscene_button").click(function() {
		var toggle=this;
		if($(this).attr("name")!=null){
			$(this).parent().parent().css({"background-color":backgroudcolor});//点击的设置
			$(".searchlistshow tr").not($(this).parent().parent()).css({"background-color":"rgba(0, 0, 0, 0)"});//其他的全部设置	
			$(this).toggleClass('toggle--on').toggleClass('toggle--off').addClass('toggle--moving');
			setTimeout(function() {
			   $(toggle).removeClass('toggle--moving');
			}, 200);
			//$(this).removeClass('toggle--moving');
			if($("#subscene_"+$(this).attr("name")).is(':hidden')){
				$("#subscene_"+$(this).attr("name")).show();	
			}else{
				$("#subscene_"+$(this).attr("name")).hide();
			}
		}
	});
	//打开场景
	$(".searchlistshow #open_scene_button").click(function() {
		if($(this).attr("name")!=null){
			$(this).parent().parent().css({"background-color":backgroudcolor});//点击的设置
			$(".searchlistshow tr").not($(this).parent().parent()).css({"background-color":"rgba(0, 0, 0, 0)"});//其他的全部设置	
			var path=$(this).attr("name")+"/测试结果汇总-Report.html";
			console.log("open page "+path);
			window.open("./QAUiReport/"+foldername+"/"+path);
		}
	});
	//打开子场景
	$(".searchlistshow #open_subscene_button").click(function() {
		if($(this).attr("name")!=null){
			$(this).parent().css({"background-color":backgroudcolor});//点击的设置
			$(".searchlistshow  tr td li").not($(this).parent()).css({"background-color":"rgba(0, 0, 0, 0)"});//其他的全部设置	
			//var subname=$(this).prev().prev().text();
			var path=$(this).attr("name")+"/测试结果-Report.html";
			//	+subname.substring(subname.indexOf("-")+1,subname.indexOf("-",subname.indexOf("-")+1)>0?subname.indexOf("-",subname.indexOf("-")+1):subname.length)+"-Report.html";
			console.log("open page "+path);
			window.open("./QAUiReport/"+foldername+"/"+path);
		}
	});
}
/**
 * 
 * @param foldername
 * @param type
 * @returns
 */
function delCata(subfoldername,type){
	var jsonstr={//http://www.jb51.net/article/87626.htm
			"subfoldername":subfoldername,
			"type":type,
	}
	$.ajax({
        url: './FileServlet',//ajax提交路径
        type: 'post',//提交方式
        data: {type:"delCata",content:JSON.stringify(jsonstr),token:null},//提交参数
        async: true,
        success: function (result) {//ajax请求完成时执行，result为返回的结果
        	if(result!=null){
				console.log("get delCata ok!");
				if(result.success=="true"){
					console.log(result.value);
				}else{
					console.log("get delCata data false",result);
				}
			}else{
				console.log("get delCata failed!");
			}
        },
        error: function () {
        	console.log("create delCata ajax请求处理错误");
        }
     });
}
/**
 * 从服务器拉数据
 * @param foldername
 * @param type
 * @returns
 */
function getCataInfo(foldername,type){
	var jsonstr={//http://www.jb51.net/article/87626.htm
			"foldername":foldername,
			"type":type,
	}
	$.ajax({
        url: './FileServlet',//ajax提交路径
        type: 'post',//提交方式
        data: {type:"getCataInfo",content:JSON.stringify(jsonstr),token:null},//提交参数
        async: true,
        success: function (result) {//ajax请求完成时执行，result为返回的结果
        	if(result!=null){
				console.log("get getCataInfo ok!");
				if(result.success=="true"){
					var cataloginfo=result.value;
					console.log(cataloginfo);
					showListData(cataloginfo,foldername);
				}else{
					console.log("get getCataInfo data false",result);
					showListData(null,foldername);
				}
			}else{
				console.log("get getCataInfo  failed!");
			}
        },
        error: function () {
        	console.log("create getCataInfo ajax请求处理错误");
        }
     });
}
/**
 * 更改测试报告标题
 * @param subfoldername
 * @param type
 * @param title
 * @returns
 */
function changeTitle(subfoldername,type,title){
		var jsonstr={//http://www.jb51.net/article/87626.htm
				"subfoldername":subfoldername,
				"type":type,
				"title":title,
		}
		$.ajax({
	        url: './FileServlet',//ajax提交路径
	        type: 'post',//提交方式
	        data: {type:"changeTitle",content:JSON.stringify(jsonstr),token:null},//提交参数
	        async: true,
	        success: function (result) {//ajax请求完成时执行，result为返回的结果
	        	if(result!=null){
					console.log("get changeTitle ok!");
					if(result.success=="true"){
						console.log(result.value);
					}else{
						console.log("get changeTitle data false",result);
					}
				}else{
					console.log("get changeTitle failed!");
				}
	        },
	        error: function () {
	        	console.log("create changeTitle ajax请求处理错误");
	        }
	     });
	}

/**
 * 强制更新TXT信息
 * @param foldername
 * @param type
 * @returns
 */
function refreshCataInfo(type){
	var jsonstr={
			"type":type,
	}
	$.ajax({
        url: './FileServlet',//ajax提交路径
        type: 'post',//提交方式
        data: {type:"refreshCataInfo",content:JSON.stringify(jsonstr),token:null},//提交参数
        async: true,
        success: function (result) {//ajax请求完成时执行，result为返回的结果
        	if(result!=null){
				console.log("get refreshCataInfo ok!");
				if(result.success=="true"){
					console.log(result.value);
				}else{
					console.log("get refreshCataInfo data false",result);
				}
			}else{
				console.log("get refreshCataInfo  failed!");
			}
        },
        error: function () {
        	console.log("create refreshCataInfo ajax请求处理错误");
        }
     });
}
/**
 * 获取URL参数
 * @param name
 * @returns
 */
function getUrlParams(name){
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null){
    	 return  unescape(r[2]);
     }
     return null;
}

