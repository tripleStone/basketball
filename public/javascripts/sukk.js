$(function(){
	$("#query_submit").click(function(){
		var url=$(this).attr('rel');
		var start_time=$("#start_time").val();
		var end_time=$("#end_time").val();
		var key_card=$("#key_card").val();
		var author=$("#author").val();
		var article_id=$("#article_id").val();
		if(start_time=='' && end_time=='' && key_card=='' && author=='' && article_id=='' ){
			msgTips('warn','查询条件不能全为空');
		}else{
			window.location=url+'&start_time='+start_time+"&end_time="+end_time+"&key_card="+key_card+"&author="+author+"&article_id="+article_id;
		}							  
	});
	$("span.sub_edit").click(function(){//推荐
		var name=$(this).text();
		var data={};
		var _this=$(this);
		var url=$("#set_best_action").val();
		data.section_id=$("ul.sub_nar").find("li.cur").attr("sectionid");
		data.article_id=$(this).parents('tr').find("td").eq(1).text();
		$.post(url,data,function(msg){
			if(msg.error==1){
				msgTips('success',name+'成功');
				_this.parent().find('span.sub_recommend').remove();
				_this.parent().prepend('<span class="sub_recommend" title="推荐时间：'+msg.dated+'">');
 			}else{
				msgTips('error',msg.error);
			}								 
		},'json');
	});
	$("span.sub_del").click(function(){//删除
		var id=$(this).attr('rel');
		var hid=$(this).attr('data');
		var msg='确认要删除文章:'+$(this).parents('tr').find("td").eq(2).text();
		msgconFirm('删除文章',msg,'submitDelArticle('+id+','+hid+')');						 
	});
	$("a.up_move,a.domn_move").live('click',function(){
		var url=$("#action_tbody").attr("action").split(",")[1];
		var index=$(this).parents('tr').index("#action_tbody tr");
		var action=$(this).attr('class');
		var maxLen=$("#action_tbody tr").length-1;
		var id=$(this).parents('tr').find('td').eq(1).text();
		var aid=$(this).attr('id');
		if(action=='up_move'){
			 if(index==0){
			 	return false;
			 }else{
				var oid=$("#action_tbody tr").eq(index-1).find('td').eq(1).attr('data');
			 }
		}
		if(action=='domn_move'){
			if(index==maxLen){
				return false;
			}else{
				var oid=$("#action_tbody tr").eq(index+1).find('td').eq(1).attr('data');
			}
		}
		loading('提交中...');
		$.post(url,{action:action,id:aid,oid:oid},function(msg){
			removeLoad();
			if(msg.error==1){
				var html='<tr id="recommended_list_'+id+'">'+$("#recommended_list_"+id).html()+'</tr>';
				if(action=='up_move'){
					var obj=$("#action_tbody tr").eq(index-1).attr('id');
					$('#recommended_list_'+id+'').remove();
					$('#'+obj).before(html);
				}else{
					var obj=$("#action_tbody tr").eq(index+1).attr('id');
					$('#recommended_list_'+id+'').remove();
					$('#'+obj).after(html);
				}
			}else{
				msgTips('error',msg.error);	
			}
 			aclose();
 		},'json');
	});
	$("#query_view,#query_release").click(function(){
		 var url=$(this).attr('rel');
		 var id=$(this).attr('id');
		 if(id=='query_view'){
		 	window.open(url);
		 }else{
			$.post(url,function(msg){
			if(msg.error==1){
				msgTips('success','发布成功');
			}else{
				msgTips('error',msg.error);
			}					 
			},'json');
		 }
	});
	rename.init();
})
function submitDelArticle(id,hid){
	loading('提交中...');
	var url=$("#action_tbody").attr("action").split(",")[0];
	$.post(url,{id:id},function(msg){
		removeLoad();
		if(msg.error==1){
			$("#recommended_list_"+hid).remove();
			msgTips('success','删除成功');
		}else{
			msgTips('error',msg.error);	
		}
		aclose();							
	},'json');
}
rename={//重命名	
	init:function(){
		$("a.sub_edit").click(function(){//资源列表更多>>重命名
			var id=$(this).attr('rel');
			var file=$(this).parents("tr").find("a.file_tittle").text();
			var len=$(this).parents("tr").find('span.renameArea').length;
			if(len>0){return false;}
			$(this).parents("tr").find("td.file_name .file_tittle").hide();
			$(this).parents("tr").find("td.file_name").append(html);	
			var html='<span class="renameArea"><input type="text" class="renameInput" value="'+file+'"><a href="javascript:void(0)" onclick="rename.save('+id+')">确定</a><a href="javascript:void(0)" onclick="rename.cancel('+id+')">取消</a></span>';
			$(this).parents("tr").find(".file_name").append(html);	
		});	
	},
	cancel:function(id){
		$("#recommended_list_"+id).find('td.file_name a.file_tittle').show();
		$("#recommended_list_"+id).find('td.file_name span.renameArea').remove();
	},
	save:function(id){
		var url=$("#item_set_manage").attr('action');
		var title=$.trim($("#recommended_list_"+id).find('input.renameInput').val());
		if(title==''){
			msgTips('warn','标题名称不能为空!');
			$("#recommended_list_"+id).find('input.renameInput').focus();
			return false;
		}
		$.post(url,{id:id,title:title},function(msg){
			if(msg.error==1){
				msgTips('success','更改成功!');
				rename.cancel(id);
 				$("#recommended_list_"+id).find('a.file_tittle').text(title)
			}else{
				msgTips('error',msg.error);
			}										
		},'json');
	}
}