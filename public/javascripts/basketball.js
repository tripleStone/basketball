/**
 * 
 */

$(function(){
	
	
	$(".query_btn_ana").click(function(){
		
		var gameId = $("#gameId").val();
		var teamId = $(this).attr("rel");
		var url = $(this).attr("url");
		var destID = $(this).attr("data");
		if (destID == 'recent_games_compare_home'){
			var home_away = $("#home_select").val();
			var games_deadline = $("#home_games_deadline").val();
		}else{
			var home_away = $("#away_select").val();
			var games_deadline = $("#away_games_deadline").val();			
		}		
			
		$.ajax({
			type:'get',
			url:url,
			data:{gameId:gameId,teamId:teamId,
				homeAway:home_away,gameDeadLine:games_deadline},
			dataType:'html',
			success:function(msg){
				removeLoad();
				$("#"+destID).html(msg);
				aclose();
			},
			error:function(msg){
				removeLoad();
			}
		});	
		
	});	
	
	$("#linav li").live('click',function(){
		var data = $(this).attr("data");
		var rel = $(this).parent().attr("rel");
		$("div[name="+rel+"]").each(function (i) {
	        if ($(this).attr("id") == data){
	        	$(this).show();
	        }else{
	        	$(this).hide();
	        }
	      });
	});
	

 	
 	// import_lottery , analysis_mutual
 	$("a[name='lottery_his']").live('click',function(){
		var gameId = $(this).attr("data");
		var ltype = $(this).attr("ltype");
 		var url = "/basedata/ajax/lotterHis?gameId="+gameId +"&ltype="+ltype;
 		var tip = "盘口历史";
		$.post(url,function(msg){
			my_alertWidth(1,tip,msg,900);	
		},'html');
	});
	
 	// import_lottery , analysis_mutual
	$("a[name='lottery_exp']").live('click',function(){
		var lotteryId = $(this).attr("data");
		var lotteryType = $(this).attr("rel");
 		var url = "/datamanage/lottery/callLotteryExp";
 		var tip = "买盘经验";
		$.post(url,{lotteryId:lotteryId,lotteryType:lotteryType},function(msg){
			my_alertWidth(1,tip,msg,900);	
		},'html');
	});	
	
	//seasonBoxScore recentPlayerInfos
	$("a[name='rivalPlayer']").bind("click",function(){
		var id = $(this).attr("data");
		var span = $(this).attr("span");
		$.ajax({
			url:'/analysis/player/ajax/rivals',
			data:{"id": id},
			dataType:'html',
			type:'POST',
			beforeSend:function(){
				loading('加载中');
			},
			success: function(data) {
				if($("#rival"+id).length>0){
					$("#rival"+id).remove();
				}
				$("#"+id).after("<tr id=\"rival"+id+"\"><td colspan=\""+span+"\">" + data + "</td></tr>");
				removeLoad();
			},
			error:function() {
				$("#"+id).after("<font color='red'>加载失败</font>");
				 removeLoad();
			}
			
		});				
	});	
	
});

function showReport(gameId,reportType){
	var url = "/basedata/game/ajaxreport/"+gameId;
	
	$.post(url,{reportType:reportType},function(msg){
		my_alertWidth(1,'测试',msg,1200);	
	},'html');
}

/*保存经验*/
function saveExperience(gameId,url){
	var report = $("#game_report").val();
	$.ajax({
		type:'POST',
		url:url,
		data:{gameId:gameId,content:report},
		dataType:'json',
		success:function(msg){
//			removeLoad();
//			$("#report_query").click();
//			aclose();
			window.reload();
		},
		error:function(msg){
//			removeLoad();
			window.reload();
		}
	});	
}

/*最近战绩详细*/
function recentStatistic(gameId,teamId,homeAway,gameDeadLine,type){
	$.ajax({
		type:'GET',
		url:'/analysis/ajax/recnet/gamestat',
		data:{gameId:gameId,teamId:teamId,homeAway:homeAway,gameDeadLine:gameDeadLine},
		dataType:'html',
		success:function(data){
			$("#"+type+"Recent").html(data);
		},
		error:function(data){
			msgTips('error',data.msg);
			removeLoad();
		}
	});
}

/*分析首页*/
function forecastPlayers(gameId){
	$.ajax({
		url:'/analysis/ajax/forecastPlayers/'+gameId,
		data:{},
		dataType:'html',
		type:'POST',
		beforeSend:function(){
			$("#players").html("正在加载数据...");
		},
		success: function(data) {
			 $("#players").html(data);
			 setInjury();
		},
		error:function() {
			$("#players").html("<font color='red'>加载失败</font>");
		}
	});
}



/*分析首页*/
function teamPlayers(gameId){
	$.ajax({
		url:'/analysis/ajax/teamPlayers/'+gameId,
		data:{},
		dataType:'html',
		type:'POST',
		beforeSend:function(){
			$("#players").html("正在加载数据...");
		},
		success: function(data) {
			 $("#players").html(data);
			 setInjury();
		},
		error:function() {
			$("#players").html("<font color='red'>加载失败</font>");
		}
	});
}

//seasonBoxScore recentPlayerInfos
function closeRP(id){
	if($("#rival"+id).length>0){
		$("#rival"+id).remove();
	}
}