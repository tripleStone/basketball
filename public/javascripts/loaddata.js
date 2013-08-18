$(function(){
	
	$("div[name=loadtmsg]").each(function(){
		var id = $(this).attr("id");
		$.ajax({
			url:'/basedata/team/ajax/tmsg',
			data:{"teamId": $(this).attr("teamId"),"gameId": $(this).attr("gameId")},
			dataType:'html',
			type:'POST',
			beforeSend:function(){
				$("#"+id).html("正在加载数据...");
			},
			success: function(data) {
				 $("#"+id).html(data);
			},
			error:function() {
				$("#"+id).html("<font color='red'>加载失败</font>");
			}
		});	
	});
	
	$("div[name=loadreport]").each(function(){
		var id = $(this).attr("id");
		$.ajax({
			url:'/basedata/game/ajaxreport/',
			data:{"gameId": $(this).attr("gameId"),"reportType":"1"},
			dataType:'html',
			type:'POST',
			beforeSend:function(){
				$("#"+id).html("正在加载数据...");
			},
			success: function(data) {
				 $("#"+id).html(data);
			},
			error:function() {
				$("#"+id).html("<font color='red'>加载失败</font>");
			}
		});	
	});
	
	$("div[name=loadplayerrecents]").each(function(){
		var id = $(this).attr("id");
		$.ajax({
			url:'/basedata/game/ajax/report',
			data:{"gameId": $(this).attr("gameId"),"reportType":$(this).attr("reportType")},
			dataType:'html',
			type:'POST',
			beforeSend:function(){
				$("#"+id).html("正在加载推荐列表...");
			},
			success: function(data) {
				 $("#"+id).html(data);
			},
			error:function() {
				$("#"+id).html("<font color='red'>加载失败</font>");
			}
		});	
	});
	
	$("div[name=loadgamequarters]").each(function(){
		var id = $(this).attr("id");
		$.ajax({
			url:'/basedata/game/ajax/report',
			data:{"gameId": $(this).attr("gameId"),"reportType":$(this).attr("reportType")},
			dataType:'html',
			type:'POST',
			beforeSend:function(){
				$("#"+id).html("正在加载推荐列表...");
			},
			success: function(data) {
				 $("#"+id).html(data);
			},
			error:function() {
				$("#"+id).html("<font color='red'>加载失败</font>");
			}
		});	
	});
	
	$("tbody[name=loadgamequarters]").each(function(){
		var id = $(this).attr("id");
		$.ajax({
			url:'/basedata/game/ajax/quarters',
			data:{"gameId": $(this).attr("gameId"),"homeId": $(this).attr("homeId"),"awayId": $(this).attr("awayId")},
			dataType:'html',
			type:'POST',
			beforeSend:function(){
				$("#"+id).html("正在加载推荐列表...");
			},
			success: function(data) {
				 $("#"+id).html(data);
			},
			error:function() {
				$("#"+id).html("<font color='red'>加载失败</font>");
			}
		});	
	});	
	
	$("table[name=loadgameplayerinfo]").each(function(){
		var id = $(this).attr("id");
		$.ajax({
			url:'/basedata/game/ajax/gamePlayersInfo',
			data:{"gameId": $(this).attr("gameId"),"teamId": $(this).attr("teamId")},
			dataType:'html',
			type:'POST',
			beforeSend:function(){
				$("#"+id).html("正在加载推荐列表...");
			},
			success: function(data) {
				 $("#"+id).html(data);
			},
			complete:function() {
			},
			error:function() {
				$("#"+id).html("<font color='red'>加载失败</font>");
			}
		});	
	});
	
	$("div[name=loadplayerInfos]").each(function(){
		var id = $(this).attr("id");
		$.ajax({
			url:'/basedata/player/teamPlayers',
			data:{"teamId": $(this).attr("teamId"),"gameId":$(this).attr("gameId")},
			dataType:'html',
			type:'POST',
			beforeSend:function(){
				$("#"+id).html("正在加载推荐列表...");
			},
			success: function(data) {
				 $("#"+id).html(data);
			},
			complete:function() {
			},
			error:function() {
				$("#"+id).html("<font color='red'>加载失败</font>");
			}
		});			
		
	});
	
	
	
	if($("#hiloHistory").length>=1){
		$.ajax({
			url:'/analysis/hilo/ajax/history',
			data:{"gameId": $("#gameId").val()},
			dataType:'html',
			beforeSend:function() {
			    $("#hiloHistory").html("正在加载推荐列表...");
			},
			success: function(data) {
			    $("#hiloHistory").html(data);
			},
			complete:function() {
			},
			error:function() {
			    $('#hiloHistory').html("<font color='red'>加载失败</font>");
			}
		});		
	}

	if($("#hilo").length>=1){
		$.ajax({
			url:'/analysis/hilo/ajax/recent',
			data:{"gameId": $("#gameId").val()},
			dataType:'html',
			beforeSend:function() {
			    $("#hilo").html("正在加载推荐列表...");
			},
			success: function(data) {
			    $("#hilo").html(data);
			},
			complete:function() {
			},
			error:function() {
			    $('#hilo').html("<font color='red'>加载失败</font>");
			}
		});		
		
	}
	
	$("div[name=loadInjury]").each(function(){
		var id = $(this).attr("id");
		$.ajax({
			url:'/basedata/game/ajax/injury',
			data:{"gameId": $(this).attr("gameId")},
			dataType:'html',
			type:'POST',
			beforeSend:function(){
				$("#"+id).html("正在加载推荐列表...");
			},
			success: function(data) {
				$("#"+id).html(data);

			},
			error:function() {
				$("#"+id).html("<font color='red'>加载失败</font>");
			}
		});	
	});
	
});